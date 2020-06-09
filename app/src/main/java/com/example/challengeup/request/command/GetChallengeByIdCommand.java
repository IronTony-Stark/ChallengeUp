package com.example.challengeup.request.command;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class GetChallengeByIdCommand implements IRequestCommand {

    private final String cid;

    public GetChallengeByIdCommand(String cid) {
        this.cid = cid;
    }

    @Override
    public Result request() {
        Challenge challenge = Challenge.getChallengeById(cid);
        return new Result.Success<>(challenge);
    }
}
