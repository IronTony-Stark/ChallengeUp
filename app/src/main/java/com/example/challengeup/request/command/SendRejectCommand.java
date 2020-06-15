package com.example.challengeup.request.command;

import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SendRejectCommand implements IRequestCommand {

    private VideoConfirmationEntity videoConfirmationEntity;

    @Override
    public Result request() {
        videoConfirmationEntity.setNumberOfRejectionLeft(videoConfirmationEntity.getNumberOfRejectionLeft() - 1);
        videoConfirmationEntity.update();
        return new Result.Success<>(videoConfirmationEntity.getNumberOfRejectionLeft());
    }
}
