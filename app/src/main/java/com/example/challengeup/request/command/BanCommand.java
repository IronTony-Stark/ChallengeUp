package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class BanCommand implements IRequestCommand {

    private final ChallengeEntity challenge;
    private final boolean ban;

    public BanCommand(ChallengeEntity challenge, boolean ban) {
        this.challenge = challenge;
        this.ban = ban;
    }

    @Override
    public Result request() {
        if (ban)
            challenge.block();
        else challenge.unblock();
        return new Result.Success<>(challenge);
    }
}
