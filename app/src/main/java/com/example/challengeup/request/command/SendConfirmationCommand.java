package com.example.challengeup.request.command;

import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.ArrayList;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SendConfirmationCommand implements IRequestCommand {

    private VideoConfirmationEntity videoConfirmationEntity;
    private String userID;

    @Override
    public Result request() {
        videoConfirmationEntity.setNumberOfConfirmationLeft(videoConfirmationEntity.getNumberOfConfirmationLeft() - 1);
        ArrayList<String> users = videoConfirmationEntity.getUsersWhoConfirmedOrDenied();
        users.add(userID);
        videoConfirmationEntity.setUsersWhoConfirmedOrDenied(users);
        videoConfirmationEntity.update();
        return new Result.Success<>(videoConfirmationEntity.getNumberOfConfirmationLeft());
    }
}
