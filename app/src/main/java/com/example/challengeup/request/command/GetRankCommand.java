package com.example.challengeup.request.command;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.ArrayList;

public class GetRankCommand implements IRequestCommand {

    private final UserEntity mUserEntity;

    public GetRankCommand(UserEntity user) {
        mUserEntity = user;
    }

    @Override
    public Result request() {
        Integer rank = mUserEntity.getRank();
        return new Result.Success<>(rank);
    }
}
