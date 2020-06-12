package com.example.challengeup.request.command;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class GetUserByEmailCommand implements IRequestCommand {

    private final String email;

    public GetUserByEmailCommand(String email) {
        this.email = email;
    }

    @Override
    public Result request() {
        UserEntity user = UserEntity.getUserByEmail(email);
        return new Result.Success<>(user);
    }
}
