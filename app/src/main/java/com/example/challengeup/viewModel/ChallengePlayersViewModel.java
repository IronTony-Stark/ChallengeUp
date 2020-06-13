package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetAllUsersCommand;

public class ChallengePlayersViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public ChallengePlayersViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getAllUsers(ICallback callback) {
        mRequestExecutor.execute(new GetAllUsersCommand(), callback);
    }
    //get users method
}
