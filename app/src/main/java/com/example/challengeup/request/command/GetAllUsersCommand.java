package com.example.challengeup.request.command;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetAllUsersCommand implements IRequestCommand {

    @Override
    public Result request() {
        List<UserEntity> users = UserEntity.getAllUsers();
        return new Result.Success<>(users);
    }
}
