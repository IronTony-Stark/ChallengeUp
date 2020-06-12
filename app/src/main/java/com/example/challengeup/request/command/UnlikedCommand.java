package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class UnlikedCommand implements IRequestCommand {

    private final UserEntity user;
    private final ChallengeEntity challenge;

    public UnlikedCommand(UserEntity user, ChallengeEntity challenge) {
        this.user = user;
        this.challenge = challenge;
    }

    @Override
    public Result request() {
        user.removeChallengeFromLiked(challenge);
        user.update();
        challenge.setLikes(challenge.getLikes() - 1);
        challenge.update();
        return new Result.Success<>(challenge);
    }
}
