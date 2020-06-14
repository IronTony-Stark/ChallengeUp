package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetAllConfirmedVideosCommand;
import com.example.challengeup.request.command.GetAllUnconfirmedVideosCommand;
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

    public void getAllConfirmedVideos(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new GetAllConfirmedVideosCommand(challenge), callback);
    }

    public void getAllUnconfirmedVideos(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new GetAllUnconfirmedVideosCommand(challenge), callback);
    }
}
