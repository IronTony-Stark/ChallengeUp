package com.example.challengeup.request.command;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.ArrayList;

public class GetSubscribersCommand implements IRequestCommand {

    private final UserEntity mUserEntity;

    public GetSubscribersCommand(UserEntity userEntity) {
        mUserEntity = userEntity;
    }

    @Override
    public Result request() {
        ArrayList<UserEntity> subscribers = mUserEntity.getSubscribersAsUsers();
        return new Result.Success<>(subscribers);
    }
}
