package com.example.challengeup.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.ILoadable;
import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.StorageFolders;
import com.example.challengeup.databinding.FragmentCreateChallengeBinding;
import com.example.challengeup.dto.ChallengeDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengesViewModel;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.ChallengesFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

public class CreateChallengeFragment extends Fragment {

    private FragmentCreateChallengeBinding mBinding;

    private ChallengesViewModel mViewModel;
    private MainActivityViewModel mMainActivityViewModel;
    private StorageReference mStorage;
    private ILoadable mLoadable;

//    private HashTagHelper mHashTagHelperDescription;
    private ChipGroup mCategories;

    private EditText mInputName;
    private EditText mInputDescription;
    private TextView mPreviewName;
    private TextView mPreviewDescription;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_challenge,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new ChallengesFactory(
                appContainer.mRequestExecutor
        )).get(ChallengesViewModel.class);
        mMainActivityViewModel = new ViewModelProvider(requireActivity())
                .get(MainActivityViewModel.class);

        mStorage = FirebaseStorage.getInstance().getReference(StorageFolders.challenge_photos.toString());
        mLoadable = (ILoadable) requireActivity();

        mInputName = mBinding.inputName;
        mInputDescription = mBinding.inputDescription;
        mPreviewName = mBinding.preview.name;
        mPreviewDescription = mBinding.preview.description;

        setupChallenge();

        setupUser();

        setupCategories();

        setupHashtags();

        setupInputListeners();

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        mBinding.btnLoadImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivityForResult(intent, GET_IMAGE_REQUEST);
        });

        mBinding.btnCreate.setOnClickListener(v -> {
            if (inProgress) {
                Toast.makeText(getContext(), "In Progress", Toast.LENGTH_SHORT).show();
                return;
            }

            inProgress = true;
            mLoadable.startLoading();

            String name = mInputName.getText().toString();
            String description = mInputDescription.getText().toString();
            String uid = Objects.requireNonNull(mMainActivityViewModel.getUser().getValue()).getId();
//            List<String> hashtags = mHashTagHelperDescription.getAllHashTags();
            List<String> categories = mCategories
                    .getCheckedChipIds()
                    .stream()
                    .map(id -> {
                        Chip chip = (Chip) mCategories.getChildAt(id - 1);
                        if (chip == null)
                            Log.e("Chip | Null Pointer", String.valueOf(id));
                        return chip;
                    })
                    .filter(Objects::nonNull)
                    .map(chip -> Objects.requireNonNull(chip).getText().toString())
                    .collect(Collectors.toList());

            ChallengeEntity challenge = new ChallengeEntity(
                    name,
                    description,
                    uid,
                    new ArrayList<>(), // hashtags
                    (ArrayList<String>) categories // TODO remove casts
            );

            mViewModel.addChallenge(challenge, result -> {
                inProgress = false;
                mLoadable.finishLoading();

                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    String challengeId = ((Result.Success<String>) result).data;

                    uploadImage(challengeId, mBinding.getChallengeThumbnail(), (ignored -> {}));

                    reset();
                    Toast.makeText(getContext(),
                            "Challenge Successfully Created!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Exception exception = ((Result.Error) result).exception;
                    Toast.makeText(getContext(),
                            exception.getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void reset() {
        mInputName.setText("");
        mInputDescription.setText("");
        mBinding.setChallengeThumbnail(null);
//        Toast.makeText(getContext(), String.valueOf(mHashTagHelperDescription.getAllHashTags().size()), Toast.LENGTH_SHORT).show();
        mCategories.clearCheck();
    }

    private void setupChallenge() {
        ChallengeDTO challengeDTO = ChallengeDTO
                .builder()
                .name("")
                .description("")
                .accepted("100000")
                .completed("100000")
                .liked("100000")
                .rp("")
                .build();
        mBinding.setChallenge(challengeDTO);
    }

    private void setupUser() {
        mBinding.setUserAvatar(mMainActivityViewModel.getUserAvatar().getValue());
    }

    private void setupHashtags() {
//        mHashTagHelperDescription = HashTagHelper.Creator.create(
//                ContextCompat.getColor(requireContext(), R.color.colorPrimary),
//                null, '_');
//        mHashTagHelperDescription.handle(mBinding.inputDescription);
//
//        HashTagHelper hashTagHelperPreview = HashTagHelper.Creator.create(
//                ContextCompat.getColor(requireContext(), R.color.colorPrimary),
//                null, '_');
//        hashTagHelperPreview.handle(mBinding.preview.description);
    }

    private void setupCategories() {
        mCategories = mBinding.chipFilter;
        mViewModel.getCategories(result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                List<String> categories = ((Result.Success<List<String>>) result).data;
                if (categories != null)
                    mViewModel.inflateChipGroup(mCategories, categories, requireContext());
            }
        });
    }

    private void setupInputListeners() {
        mBinding.inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPreviewName.setText(mInputName.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.inputDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPreviewDescription.setText(mInputDescription.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.addMenuItem).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GET_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            String thumbnailUrl = data.getData().toString();
            mBinding.setChallengeThumbnail(thumbnailUrl);
        }
    }

    private void uploadImage(String challengeId, String imageUri,
                             Consumer<UploadTask.TaskSnapshot> consumer) {
        StorageReference file = mStorage.child(challengeId + "_photo");
        file.putFile(Uri.parse(imageUri))
                .addOnSuccessListener(consumer::accept);
    }

    private static final int GET_IMAGE_REQUEST = 1;
    private boolean inProgress = false;
}
