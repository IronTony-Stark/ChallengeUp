package com.example.challengeup.request.command;

import com.example.challengeup.backend.User;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetAllUsersCommand implements IRequestCommand {

    @Override
    public Result request() {
        List<User> users = User.getAllUsers();
        return new Result.Success<>(users);
    }
}
