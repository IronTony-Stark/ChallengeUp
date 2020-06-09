package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetAllChallengesCommand;
import com.example.challengeup.request.command.GetNumAcceptedCommand;
import com.example.challengeup.request.command.GetNumCompletedCommand;
import com.example.challengeup.request.command.GetSavedChallengesCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;

public class SavedChallengesViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public SavedChallengesViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getSavedChallenges(User user, ICallback callback) {
        mRequestExecutor.execute(new GetSavedChallengesCommand(user), callback);
    }

    public void getUserById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetUserByIdCommand(uid), callback);
    }

    public void getNumAccepted(Challenge challenge, ICallback callback) {
        mRequestExecutor.execute(new GetNumAcceptedCommand(challenge), callback);
    }

    public void getNumCompleted(Challenge challenge, ICallback callback) {
        mRequestExecutor.execute(new GetNumCompletedCommand(challenge), callback);
    }

}
