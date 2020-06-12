package com.example.challengeup.request.command;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class LikedCommand implements IRequestCommand {

    private final UserEntity user;
    private final ChallengeEntity challenge;

    public LikedCommand(UserEntity user, ChallengeEntity challenge) {
        this.user = user;
        this.challenge = challenge;
    }

    @Override
    public Result request() {
        user.addChallengeToLiked(challenge);
        user.update();
        challenge.setLikes(challenge.getLikes() + 1);
        challenge.update();
        return new Result.Success<>(challenge);
    }
}
