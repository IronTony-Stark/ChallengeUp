package com.example.challengeup.request.command;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.ArrayList;

public class RemoveBookmarkedCommand implements IRequestCommand {

    private final User user;
    private final Challenge challenge;

    public RemoveBookmarkedCommand(User user, Challenge challenge) {
        this.challenge = challenge;
        this.user = user;
    }

    @Override
    public Result request() {
        ArrayList<String> saved = user.getSaved();
        saved.add(challenge.getId());
        user.setSaved(saved);
        user.update();
        return new Result.Success<>(challenge);
    }
}
