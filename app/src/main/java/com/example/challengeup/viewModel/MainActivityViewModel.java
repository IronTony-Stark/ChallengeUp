package com.example.challengeup.viewModel;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.User;
import com.example.challengeup.result.ICallback;
import com.example.challengeup.result.Result;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivityViewModel extends ViewModel {

    private FirebaseUser mUser;
    private final ExecutorService mExecutor;
    private final Handler mMainThreadHandler;

    public MainActivityViewModel(final ExecutorService executor,
                                 final Handler mainThreadHandler) {
        mExecutor = executor;
        mMainThreadHandler = mainThreadHandler;
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

    public void getUserById(String uid, ICallback callback) {
        mExecutor.execute(() -> {
            try {
                Result result = getUserByIdSync(uid);
                notifyResult(result, callback);
            } catch (Exception e) {
                Result errorResult = new Result.Error(e);
                notifyResult(errorResult, callback);
            }
        });
    }

    public void addUser(User user, ICallback callback) {
        mExecutor.execute(() -> {
            try {
                Result result = addUserSync(user);
                notifyResult(result, callback);
            } catch (Exception e) {
                Result errorResult = new Result.Error(e);
                notifyResult(errorResult, callback);
            }
        });
    }

    public Result getUserByIdSync(String uid) {
        User user = User.getUserById(uid);
        return new Result.Success<>(user);
    }

    public Result addUserSync(User newUser) {
        String userId = User.addNewUser(newUser);
        return new Result.Success<>(userId);
    }

    private void notifyResult(Result result, ICallback callback) {
        mMainThreadHandler.post(() -> callback.onComplete(result));
    }
}
