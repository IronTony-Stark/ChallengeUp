package com.example.challengeup.request.command;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.concurrent.atomic.AtomicReference;

public class CreateVideoConfirmation implements IRequestCommand {

    private final UserEntity user;
    private final String challengeID;

    public CreateVideoConfirmation(UserEntity user, String challengeID) {
        this.user = user;
        this.challengeID = challengeID;
    }

    @Override
    public Result request() {
        String fileID = VideoConfirmationEntity.addNewVideo(user.getId(),
                challengeID, 3, 3);
        user.addChallengeToWaitingConfirmation(challengeID);
        user.update();
        return new Result.Success<>(fileID);
    }
}
