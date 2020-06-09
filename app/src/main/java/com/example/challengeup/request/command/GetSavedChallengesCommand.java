package com.example.challengeup.request.command;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetSavedChallengesCommand implements IRequestCommand {

    private final User user;

    public GetSavedChallengesCommand(User user) {
        this.user = user;
    }

    @Override
    public Result request() {
        List<Challenge> challenges = user.getSavedChallenges();
        return new Result.Success<>(challenges);
    }
}
