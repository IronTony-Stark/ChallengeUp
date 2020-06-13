package com.example.challengeup.request.command;

import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class CreateVideoConfirmation implements IRequestCommand {

    private final String user;
    private final String challenge;

    public CreateVideoConfirmation(String user, String challenge) {
        this.user = user;
        this.challenge = challenge;
    }

    @Override
    public Result request() {
        String fileID = VideoConfirmationEntity.addNewVideo(user, challenge);
        return new Result.Success<>(fileID);
    }
}
