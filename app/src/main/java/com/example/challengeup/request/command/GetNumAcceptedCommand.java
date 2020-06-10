package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class GetNumAcceptedCommand implements IRequestCommand {

    private final ChallengeEntity mChallenge;

    public GetNumAcceptedCommand(ChallengeEntity challenge) {
        mChallenge = challenge;
    }

    @Override
    public Result request() {
        Long number = mChallenge.numberOfPeopleWhoAccepted();
        return new Result.Success<>(number);
    }
}
