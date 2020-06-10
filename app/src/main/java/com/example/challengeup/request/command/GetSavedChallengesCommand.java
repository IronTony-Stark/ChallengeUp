package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetSavedChallengesCommand implements IRequestCommand {

    private final UserEntity user;

    public GetSavedChallengesCommand(UserEntity user) {
        this.user = user;
    }

    @Override
    public Result request() {
        List<ChallengeEntity> challenges = user.getSavedChallenges();
        return new Result.Success<>(challenges);
    }
}
