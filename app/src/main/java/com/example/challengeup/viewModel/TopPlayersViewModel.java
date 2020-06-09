package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetAllUsersCommand;

public class TopPlayersViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public TopPlayersViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getAllUsers(ICallback callback) {
        mRequestExecutor.execute(new GetAllUsersCommand(), callback);
    }
}
