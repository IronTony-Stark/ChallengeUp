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
import com.example.challengeup.IBlockingLoadable;
import com.example.challengeup.IUIConfig;
import com.example.challengeup.R;
import com.example.challengeup.backend.StorageFolders;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.databinding.FragmentFtueBinding;
import com.example.challengeup.dto.UserDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FTUEFragment extends Fragment {

    private FragmentFtueBinding mBinding;
    private MainActivityViewModel mMainActivityViewModel;
    private IBlockingLoadable mBlockingLoadable;
    private IUIConfig mUIConfig;
    private StorageReference mStorage;

    private ImageView mAvatar;
    private String mAvatarUrl;
    private EditText mName;
    private EditText mUsername;
    private EditText mInfo;

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

        mMainActivityViewModel = new ViewModelProvider(requireActivity())
                .get(MainActivityViewModel.class);

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

            UserEntity user = new UserEntity(username, name,
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            user.setInfo(info);

            mMainActivityViewModel.addUser(user, addUserResult -> {
                mBlockingLoadable.finishBlockingLoading();

                if (addUserResult instanceof Result.Success) {
                    //noinspection unchecked
                    String userId = ((Result.Success<String>) addUserResult).data;

                    if (!mAvatarUrl.equals(MainActivityViewModel.DEFAULT_AVATAR_URL)) {
                        uploadImage(userId, mAvatarUrl);
                        mAvatarUrl = avatarDbUrl +
                                StorageFolders.user_photos.toString() +
                                "%2F" +
                                userId +
                                "_photo" +
                                "?alt=media";
                    }

                    UserDTO userDTO = new UserDTO(userId, name, username, info);

                    mMainActivityViewModel.saveUserToSharedPreferences(userDTO);
                    mMainActivityViewModel.saveUserAvatar(mAvatarUrl);

                    mMainActivityViewModel.setUser(userDTO);
                    mMainActivityViewModel.setUserAvatar(mAvatarUrl);

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

    private void uploadImage(String userId, String imageUri) {
        StorageReference file = mStorage.child(userId + "_photo");
        file.putFile(Uri.parse(imageUri));
    }

    private void updateAvatar() {
        Glide.with(this).load(mAvatarUrl).circleCrop().into(mAvatar);
    }

    private static final int GET_IMAGE_REQUEST = 1;
    private static final String avatarDbUrl = "https://firebasestorage.googleapis.com/v0/b/challengeup-49057.appspot.com/o/";
}