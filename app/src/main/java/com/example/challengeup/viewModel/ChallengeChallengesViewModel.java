package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetAllVideosCommand;
import com.example.challengeup.request.command.GetChallengeByIdCommand;

public class ChallengeChallengesViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public ChallengeChallengesViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getChallengeById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetChallengeByIdCommand(uid), callback);
    }

    public void getAllVideos(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new GetAllVideosCommand(challenge), callback);
    }
    //get users method
}
