package com.example.challengeup.request.command;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetAllChallengesCommand implements IRequestCommand {

    @Override
    public Result request() {
        List<Challenge> challenges = Challenge.getAllChallenges();
        return new Result.Success<>(challenges);
    }
}
