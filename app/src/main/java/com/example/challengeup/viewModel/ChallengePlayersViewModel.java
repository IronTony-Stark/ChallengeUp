package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetChallengeByIdCommand;
import com.example.challengeup.request.command.PeopleWhoAcceptedCommand;

public class ChallengePlayersViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public ChallengePlayersViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getChallengeById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetChallengeByIdCommand(uid), callback);
    }

    public void peopleWhoAccepted(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new PeopleWhoAcceptedCommand(challenge), callback);
    }
    //get users method
}
