package com.example.challengeup.request.command;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class AddUserCommand implements IRequestCommand {

    private final UserEntity user;

    public AddUserCommand(UserEntity user) {
        this.user = user;
    }

    @Override
    public Result request() {
        String userId = UserEntity.addNewUser(user);
        return new Result.Success<>(userId);
    }
}
