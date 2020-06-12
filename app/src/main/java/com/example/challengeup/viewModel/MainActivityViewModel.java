package com.example.challengeup.viewModel;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.dto.UserDTO;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.AddUserCommand;
import com.example.challengeup.request.command.GetUserByEmailCommand;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivityViewModel extends ViewModel {

    private FirebaseUser mFirebaseUser;
    private final MutableLiveData<UserDTO> mUser;
    private final MutableLiveData<String> mUserAvatar;
    private final RequestExecutor mRequestExecutor;
    private final SharedPreferences mPreferences;

    public MainActivityViewModel(final RequestExecutor requestExecutor,
                                 final SharedPreferences preferences) {
        mUser = new MutableLiveData<>();
        mUserAvatar = new MutableLiveData<>();
        mRequestExecutor = requestExecutor;
        mPreferences = preferences;
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

    public void saveUserToSharedPreferences(@NotNull UserEntity user) {
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

    public void setUserAvatar(String photo) {
        mUserAvatar.setValue(photo);
    }

    public void saveUserAvatar(String photo) {
        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putString(USER_AVATAR, photo);

        editor.apply();
    }

    public void refreshUserAvatar() {
        String avatar = mPreferences.getString(USER_AVATAR, null);
        if (avatar != null)
            setUserAvatar(avatar);
    }

    public void getUserByEmail(String email, ICallback getUserCallback) {
        mRequestExecutor.execute(new GetUserByEmailCommand(email), getUserCallback);
    }

    public void addUser(UserEntity newUser, ICallback callback) {
        mRequestExecutor.execute(new AddUserCommand(newUser), callback);
    }

    public LiveData<UserDTO> getUser() {
        return mUser;
    }

    public LiveData<String> getUserAvatar() {
        return mUserAvatar;
    }

    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_USERNAME = "USER_USERNAME";
    public static final String USER_AVATAR = "USER_AVATAR";
}