package com.example.challengeup.request.command;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class AddBookmarkedCommand implements IRequestCommand {

    private final User user;
    private final Challenge challenge;

    public AddBookmarkedCommand(User user, Challenge challenge) {
        this.user = user;
        this.challenge = challenge;
    }

    @Override
    public Result request() {
        user.addChallengeToSaved(challenge);
        return new Result.Success<>(challenge);
    }
}
