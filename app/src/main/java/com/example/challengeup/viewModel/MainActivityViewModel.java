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
import com.example.challengeup.request.command.GetCreatedChallengesCommand;
import com.example.challengeup.request.command.GetRankCommand;
import com.example.challengeup.request.command.GetSubscribersCommand;
import com.example.challengeup.request.command.GetUserByEmailCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;
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

    public void saveUserToSharedPreferences(@NotNull UserDTO user) {
        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putString(USER_ID, user.getId());
        editor.putString(USER_NAME, user.getName());
        editor.putString(USER_USERNAME, user.getUsername());
        editor.putString(USER_INFO, user.getInfo());

        editor.apply();
    }

    public void saveUserIdToSharedPreferences(@NotNull String id) {
        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putString(USER_ID, id);

        editor.apply();
    }

    public void refreshUserFromSharedPreferences() {
        String id = mPreferences.getString(USER_ID, null);
        String name = mPreferences.getString(USER_NAME, null);
        String username = mPreferences.getString(USER_USERNAME, null);
        String info = mPreferences.getString(USER_INFO, null);

        UserDTO userDTO = new UserDTO(id, name, username, info);

        mUser.setValue(userDTO);
    }

    public void setUser(UserDTO dto) {
        mUser.setValue(dto);
    }

    public void setUserAvatar(String photoUrl) {
        mUserAvatar.setValue(photoUrl);
    }

    public void saveUserAvatar(String photoUrl) {
        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putString(USER_AVATAR, photoUrl);

        editor.apply();
    }

    public void refreshUserAvatar() {
        String photoUrl = mPreferences.getString(USER_AVATAR, null);
        if (photoUrl != null)
            mUserAvatar.setValue(photoUrl);
        else
            mUserAvatar.setValue(DEFAULT_AVATAR_URL);
    }

    public void getUserByEmail(String email, ICallback getUserCallback) {
        mRequestExecutor.execute(new GetUserByEmailCommand(email), getUserCallback);
    }

    public void getUserById(String id, ICallback callback) {
        mRequestExecutor.execute(new GetUserByIdCommand(id), callback);
    }

    public void addUser(UserEntity newUser, ICallback callback) {
        mRequestExecutor.execute(new AddUserCommand(newUser), callback);
    }

    public void getCreatedChallenges(UserEntity user, ICallback callback) {
        mRequestExecutor.execute(new GetCreatedChallengesCommand(user), callback);
    }

    public void getSubscribers(UserEntity user, ICallback callback) {
        mRequestExecutor.execute(new GetSubscribersCommand(user), callback);
    }

    public void getRank(UserEntity user, ICallback callback) {
        mRequestExecutor.execute(new GetRankCommand(user), callback);
    }

    public boolean isAdmin(UserEntity user, ICallback callback) {
        return user.isAdmin();
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
    public static final String USER_INFO = "USER_INFO";
    public static final String USER_AVATAR = "USER_AVATAR";

    public static final String DEFAULT_AVATAR_URL = "https://firebasestorage.googleapis.com/v0/b/challengeup-49057.appspot.com/o/user_photos%2Fdefault.jpg?alt=media";
}