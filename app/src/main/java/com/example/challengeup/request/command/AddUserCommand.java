package com.example.challengeup.request.command;

import com.example.challengeup.backend.User;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class AddUserCommand implements IRequestCommand {

    private final User user;

    public AddUserCommand(User user) {
        this.user = user;
    }

    @Override
    public Result request() {
        String userId = User.addNewUser(user);
        return new Result.Success<>(userId);
    }
}
