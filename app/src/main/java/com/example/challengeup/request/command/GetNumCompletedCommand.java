package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class GetNumCompletedCommand implements IRequestCommand {

    private final ChallengeEntity mChallenge;

    public GetNumCompletedCommand(ChallengeEntity challenge) {
        mChallenge = challenge;
    }

    @Override
    public Result request() {
        Long number = mChallenge.numberOfPeopleWhoComplete();
        return new Result.Success<>(number);
    }
}
