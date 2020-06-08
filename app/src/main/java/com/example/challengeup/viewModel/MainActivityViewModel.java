package com.example.challengeup.viewModel;

import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.User;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.AddUserCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivityViewModel extends ViewModel {

    private FirebaseUser mUser;
    private final RequestExecutor mRequestExecutor;
    private final SharedPreferences mPreferences;

    public MainActivityViewModel(final RequestExecutor requestExecutor,
                                 final SharedPreferences preferences) {
        mRequestExecutor = requestExecutor;
        mPreferences = preferences;
    }

    public boolean isAuthenticated() {
        if (mUser == null)
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        return mUser != null;
    }

    public FirebaseUser getFirebaseUser() {
        if (mUser == null)
            mUser = FirebaseAuth.getInstance().getCurrentUser();
        return mUser;
    }

    public void getUserById(String uid, ICallback getUserCallback) {
        mRequestExecutor.execute(new GetUserByIdCommand(uid), getUserCallback);
    }

    public void addUser(User newUser, ICallback addUserCallback) {
        mRequestExecutor.execute(new AddUserCommand(newUser), addUserCallback);
    }
}
