package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.ArrayList;

public class RemoveBookmarkedCommand implements IRequestCommand {

    private final UserEntity user;
    private final ChallengeEntity challenge;

    public RemoveBookmarkedCommand(UserEntity user, ChallengeEntity challenge) {
        this.challenge = challenge;
        this.user = user;
    }

    @Override
    public Result request() {
        user.removeChallengeFromLiked(challenge);
        user.update();
        return new Result.Success<>(challenge);
    }
}
