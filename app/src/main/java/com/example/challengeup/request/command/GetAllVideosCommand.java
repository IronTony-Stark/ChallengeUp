package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetAllVideosCommand implements IRequestCommand {

    private final ChallengeEntity challenge;

    public GetAllVideosCommand(ChallengeEntity challenge) {
        this.challenge = challenge;
    }

    @Override
    public Result request() {
        List<VideoConfirmationEntity> users = challenge.getAllVideos();
        return new Result.Success<>(users);
    }
}
