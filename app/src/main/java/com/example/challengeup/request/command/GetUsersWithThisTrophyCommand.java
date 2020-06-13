package com.example.challengeup.request.command;

import com.example.challengeup.backend.TrophyEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetUsersWithThisTrophyCommand  implements IRequestCommand {

    private final TrophyEntity trophy;

    public GetUsersWithThisTrophyCommand(TrophyEntity trophy) {
        this.trophy = trophy;
    }


    @Override
    public Result request() {
        List<UserEntity> users = trophy.getUsersWithThisTrophy();
        return new Result.Success<>(users);
    }
}
