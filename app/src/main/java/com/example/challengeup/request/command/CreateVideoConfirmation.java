package com.example.challengeup.request.command;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.concurrent.atomic.AtomicReference;

public class CreateVideoConfirmation implements IRequestCommand {

    private final AtomicReference<UserEntity> user;
    private final String challengeID;

    public CreateVideoConfirmation(AtomicReference<UserEntity> user, String challengeID) {
        this.user = user;
        this.challengeID = challengeID;
    }

    @Override
    public Result request() {
//        String fileID = VideoConfirmationEntity.addNewVideo(user.get().getId(), challengeID);
//        user.get().addChallengeToWaitingConfirmation(challengeID);
//        user.get().update();
//        return new Result.Success<>(fileID);
        throw new UnsupportedOperationException();
    }
}
