package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class ReportCommand implements IRequestCommand {

    private final ChallengeEntity challenge;

    public ReportCommand(ChallengeEntity challenge) {
        this.challenge = challenge;
    }

    @Override
    public Result request() {
        challenge.report();
        return new Result.Success<>(challenge);
    }
}
