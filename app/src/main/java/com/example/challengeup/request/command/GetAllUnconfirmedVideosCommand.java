package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetAllUnconfirmedVideosCommand implements IRequestCommand {

    private final ChallengeEntity challenge;

    public GetAllUnconfirmedVideosCommand(ChallengeEntity challenge) {
        this.challenge = challenge;
    }

    @Override
    public Result request() {
        List<VideoConfirmationEntity> users = challenge.getAllUnconfirmedVideos();
        return new Result.Success<>(users);
    }
}
