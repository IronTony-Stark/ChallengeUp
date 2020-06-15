package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class AddChallengeCommand implements IRequestCommand {

    private final ChallengeEntity challenge;

    public AddChallengeCommand(ChallengeEntity challenge) {
        this.challenge=challenge;
    }

    @Override
    public Result request() {
        String challengeId = ChallengeEntity.addNewChallenge(challenge);
        return new Result.Success<>(challengeId);
    }
}
