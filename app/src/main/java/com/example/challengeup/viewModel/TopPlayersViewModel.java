package com.example.challengeup.viewModel;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.result.ICallback;
import com.example.challengeup.result.Result;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TopPlayersViewModel extends ViewModel {

    private ExecutorService mExecutor = Executors.newFixedThreadPool(4);
    private Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

    public void getAllUsers(ICallback callback) {
        mExecutor.execute(() -> {
            try {
                Result result = getAllUsersSync();
                notifyResult(result, callback);
            } catch (Exception e) {
                Result errorResult = new Result.Error(e);
                notifyResult(errorResult, callback);
            }
        });
    }


    public Result getAllUsersSync() {
        List<User> users = User.getAllUsers();
        return new Result.Success<>(users);
    }

    private void notifyResult(Result result, ICallback callback) {
        mainThreadHandler.post(() -> callback.onComplete(result));
    }
}
