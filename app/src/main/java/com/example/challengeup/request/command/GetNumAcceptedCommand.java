package com.example.challengeup.request.command;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class GetNumAcceptedCommand implements IRequestCommand {

    private final Challenge mChallenge;

    public GetNumAcceptedCommand(Challenge challenge) {
        mChallenge = challenge;
    }

    @Override
    public Result request() {
        Long number = mChallenge.numberOfPeopleWhoAccepted();
        return new Result.Success<>(number);
    }
}
