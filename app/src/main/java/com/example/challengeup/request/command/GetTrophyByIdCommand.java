package com.example.challengeup.request.command;

import com.example.challengeup.backend.TrophyEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

public class GetTrophyByIdCommand implements IRequestCommand {

    private final String tid;

    public GetTrophyByIdCommand(String tid) {
        this.tid = tid;
    }

    @Override
    public Result request() {
        TrophyEntity trophy = TrophyEntity.getTrophyById(tid);
        return new Result.Success<>(trophy);
    }
}
