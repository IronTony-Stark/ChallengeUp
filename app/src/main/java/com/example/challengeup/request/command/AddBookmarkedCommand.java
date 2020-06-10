package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class AddBookmarkedCommand implements IRequestCommand {

    private final UserEntity user;
    private final ChallengeEntity challenge;

    public AddBookmarkedCommand(UserEntity user, ChallengeEntity challenge) {
        this.user = user;
        this.challenge = challenge;
    }

    @Override
    public Result request() {
        user.addChallengeToSaved(challenge);
        return new Result.Success<>(challenge);
    }
}
