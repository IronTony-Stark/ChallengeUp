package com.example.challengeup.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.IBlockingLoadable;
import com.example.challengeup.IUIConfig;
import com.example.challengeup.R;
import com.example.challengeup.backend.StorageFolders;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.databinding.FragmentFtueBinding;
import com.example.challengeup.dto.UserDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengesViewModel;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.ChallengesFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

public class FTUEFragment extends Fragment {

    private FragmentFtueBinding mBinding;
    private MainActivityViewModel mMainActivityViewModel;
    private ChallengesViewModel mChallengeViewModel;
    private IBlockingLoadable mBlockingLoadable;
    private IUIConfig mUIConfig;
    private StorageReference mStorage;

    private ImageView mAvatar;
    private String mAvatarUrl;
    private EditText mName;
    private EditText mUsername;
    private EditText mInfo;
    private ChipGroup mChipGroup;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ftue,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mMainActivityViewModel = new ViewModelProvider(requireActivity())
                .get(MainActivityViewModel.class);
        mChallengeViewModel = new ViewModelProvider(this, new ChallengesFactory(
                appContainer.mRequestExecutor
        )).get(ChallengesViewModel.class);

        Activity activity = requireActivity();
        mBlockingLoadable = (IBlockingLoadable) activity;
        mUIConfig = (IUIConfig) activity;

        mUIConfig.setAppBarVisibility(false);

        mStorage = FirebaseStorage.getInstance().getReference(StorageFolders.user_photos.toString());

        mAvatar = mBinding.avatar;
        mName = mBinding.editTextName;
        mUsername = mBinding.editTextUsername;
        mInfo = mBinding.editTextInfo;
        Button btnLoadPhoto = mBinding.btnLoadPhoto;
        Button btnChallengeUp = mBinding.btnChallengeUp;

        mAvatarUrl = mMainActivityViewModel.getUserAvatar().getValue();
        if (mAvatarUrl == null)
            mAvatarUrl = MainActivityViewModel.DEFAULT_AVATAR_URL;
        updateAvatar();

        mName.setText(Objects.requireNonNull(mMainActivityViewModel.getUser().getValue()).getName());

        mChipGroup = mBinding.chipGroup;
        mChallengeViewModel.getCategories(result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                List<String> categories = ((Result.Success<List<String>>) result).data;
                if (categories != null)
                    mChallengeViewModel.inflateChipGroup(mChipGroup, categories, requireContext());
            }
        });

        btnLoadPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivityForResult(intent, GET_IMAGE_REQUEST);
        });

        btnChallengeUp.setOnClickListener(v -> {
            mBlockingLoadable.startBlockingLoading(0);

            String name = mName.getText().toString();
            String username = mUsername.getText().toString();
            String info = mInfo.getText().toString();
            ArrayList<String> categories = (ArrayList<String>) mChipGroup
                    .getCheckedChipIds()
                    .stream()
                    .map(id -> ((Chip) mChipGroup.getChildAt(id - 1)).getText().toString())
                    .collect(Collectors.toList());

            UserEntity user = new UserEntity(username, name,
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            user.setInfo(info);
            user.setCategories(categories);

            mMainActivityViewModel.addUser(user, addUserResult -> {
                mBlockingLoadable.finishBlockingLoading();

                if (addUserResult instanceof Result.Success) {
                    //noinspection unchecked
                    String userId = ((Result.Success<String>) addUserResult).data;

                    if (!mAvatarUrl.equals(MainActivityViewModel.DEFAULT_AVATAR_URL)) {
                        String avatarDbUrl = userAvatarsDbUrl +
                                StorageFolders.user_photos.toString() +
                                "%2F" +
                                userId +
                                "_photo" +
                                "?alt=media";

                        uploadImage(userId, mAvatarUrl, taskSnapshot -> {
                            mMainActivityViewModel.saveUserAvatar(avatarDbUrl);
                            mMainActivityViewModel.setUserAvatar(avatarDbUrl);
                        });
                    }

                    UserDTO userDTO = new UserDTO(userId, name, username, info);

                    mMainActivityViewModel.saveUserToSharedPreferences(userDTO);
                    mMainActivityViewModel.setUser(userDTO);

                    NavDirections action = FTUEFragmentDirections.actionFtueToNewsFeed();
                    Navigation.findNavController(view).navigate(action);
                } else {
                    Exception exception = ((Result.Error) addUserResult).exception;
                    Snackbar.make(requireView(),
                            Objects.requireNonNull(exception.getMessage()),
                            BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mUIConfig.setAppBarVisibility(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GET_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            mAvatarUrl = data.getData().toString();
            updateAvatar();
        }
    }

    private void uploadImage(String userId, String imageUri,
                             Consumer<UploadTask.TaskSnapshot> consumer) {
        StorageReference file = mStorage.child(userId + "_photo");
        file.putFile(Uri.parse(imageUri))
                .addOnSuccessListener(consumer::accept);
    }

    private void updateAvatar() {
        Glide.with(this).load(mAvatarUrl).circleCrop().into(mAvatar);
    }

    private static final int GET_IMAGE_REQUEST = 1;
    private static final String userAvatarsDbUrl = "https://firebasestorage.googleapis.com/v0/b/challengeup-49057.appspot.com/o/";
}