package com.example.challengeup.viewModel;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.User;
import com.example.challengeup.dto.UserDTO;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.AddUserCommand;
import com.example.challengeup.request.command.GetUserByEmailCommand;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivityViewModel extends ViewModel {

    private FirebaseUser mFirebaseUser;
    private final MutableLiveData<UserDTO> mUser;
    private final MutableLiveData<Bitmap> mUserAvatar;
    private final RequestExecutor mRequestExecutor;
    private final SharedPreferences mPreferences;
    private final File mAvatarFile;

    public MainActivityViewModel(final RequestExecutor requestExecutor,
                                 final SharedPreferences preferences,
                                 final File avatarFile) {
        mUser = new MutableLiveData<>();
        mUserAvatar = new MutableLiveData<>();
        mRequestExecutor = requestExecutor;
        mPreferences = preferences;
        mAvatarFile = avatarFile;
    }

    public boolean isAuthenticated() {
        if (mFirebaseUser == null)
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return mFirebaseUser != null;
    }

    public FirebaseUser getFirebaseUser() {
        if (mFirebaseUser == null)
            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return mFirebaseUser;
    }

    public void saveUserToSharedPreferences(@NotNull User user) {
        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putString(USER_ID, user.getId());
        editor.putString(USER_NAME, user.getNick());
        editor.putString(USER_USERNAME, user.getTag());

        editor.apply();
    }

    public void refreshUserFromSharedPreferences() {
        String id = mPreferences.getString(USER_ID, null);
        String name = mPreferences.getString(USER_NAME, null);
        String username = mPreferences.getString(USER_USERNAME, null);

        UserDTO userDTO = new UserDTO(id, name, username);

        mUser.setValue(userDTO);
    }

    public void setLoadingUser() {
        UserDTO temp = new UserDTO("-1", "Loading...", "Loading...");
        mUser.setValue(temp);
    }

    public void setUserAvatar(Bitmap photo) {
        mUserAvatar.setValue(photo);
    }

    public void saveUserAvatar(Bitmap photo) {
        try (FileOutputStream fos = new FileOutputStream(mAvatarFile)) {
            photo.compress(Bitmap.CompressFormat.PNG, 0, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshUserAvatar() {
        Bitmap bitmap = BitmapFactory.decodeFile(mAvatarFile.getAbsolutePath());
        if (bitmap != null)
            setUserAvatar(bitmap);
    }

    public void getUserByEmail(String email, ICallback getUserCallback) {
        mRequestExecutor.execute(new GetUserByEmailCommand(email), getUserCallback);
    }

    public void addUser(User newUser, ICallback callback) {
        mRequestExecutor.execute(new AddUserCommand(newUser), callback);
    }

    public LiveData<UserDTO> getUser() {
        return mUser;
    }

    public LiveData<Bitmap> getUserAvatar() {
        return mUserAvatar;
    }

    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_USERNAME = "USER_USERNAME";
}