package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.AddChallengeCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;

public class CreateChallengeViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public CreateChallengeViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getUserById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetUserByIdCommand(uid), callback);
    }

    public void addChallenge(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new AddChallengeCommand(challenge), callback);
    }

}
