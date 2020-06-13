package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class PeopleWhoAcceptedCommand implements IRequestCommand {

    private final ChallengeEntity challenge;

    public PeopleWhoAcceptedCommand(ChallengeEntity challenge) {
        this.challenge = challenge;
    }

    @Override
    public Result request() {
        List<UserEntity> users = challenge.peopleWhoAccepted();
        return new Result.Success<>(users);
    }
}
