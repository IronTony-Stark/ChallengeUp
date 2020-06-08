package com.example.challengeup.viewModel;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.User;
import com.example.challengeup.dto.UserDTO;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.AddUserCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivityViewModel extends ViewModel {

    private FirebaseUser mFirebaseUser;
    public MutableLiveData<UserDTO> mUser;
    private final RequestExecutor mRequestExecutor;
    private final SharedPreferences mPreferences;

    public MainActivityViewModel(final RequestExecutor requestExecutor,
                                 final SharedPreferences preferences) {
        mUser = new MutableLiveData<>();
        UserDTO temp = new UserDTO("-1", "Loading...", "Loading...");
        mUser.setValue(temp);

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

    public void getUserById(String uid, ICallback getUserCallback) {
        mRequestExecutor.execute(new GetUserByIdCommand(uid), getUserCallback);
    }

    public void addUser(User newUser, ICallback addUserCallback) {
        mRequestExecutor.execute(new AddUserCommand(newUser), addUserCallback);
    }

    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_USERNAME = "USER_USERNAME";
}
