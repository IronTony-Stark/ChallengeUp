package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetChallengeByIdCommand;
import com.example.challengeup.request.command.GetUserByEmailCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;

public class ChallengeViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public ChallengeViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getChallengeById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetChallengeByIdCommand(uid), callback);
    }

    public void getUserById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetUserByIdCommand(uid), callback);
    }

    public void getUserByEmail(String email, ICallback callback) {
        mRequestExecutor.execute(new GetUserByEmailCommand(email), callback);
    }

}
