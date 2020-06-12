package com.example.challengeup.request.command;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class GetUserByIdCommand implements IRequestCommand {

    private final String uid;

    public GetUserByIdCommand(String uid) {
        this.uid = uid;
    }

    @Override
    public Result request() {
        UserEntity user = UserEntity.getUserById(uid);
        return new Result.Success<>(user);
    }
}
