package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.ArrayList;

public class GetCreatedChallengesCommand implements IRequestCommand {

    private final UserEntity mUserEntity;

    public GetCreatedChallengesCommand(UserEntity userEntity) {
        mUserEntity = userEntity;
    }

    @Override
    public Result request() {
        ArrayList<ChallengeEntity> createdChallenges = mUserEntity.getAllCreatedChallenges();
        return new Result.Success<>(createdChallenges);
    }
}
