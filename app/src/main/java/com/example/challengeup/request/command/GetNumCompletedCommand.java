package com.example.challengeup.request.command;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class GetNumCompletedCommand implements IRequestCommand {

    private final Challenge mChallenge;

    public GetNumCompletedCommand(Challenge challenge) {
        mChallenge = challenge;
    }

    @Override
    public Result request() {
        Long number = mChallenge.numberOfPeopleWhoComplete();
        return new Result.Success<>(number);
    }
}
