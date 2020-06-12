package com.example.challengeup.request.command;

import com.example.challengeup.backend.TrophyEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetAchievementsAsTrophiesCommand  implements IRequestCommand {

    private final UserEntity user;

    public GetAchievementsAsTrophiesCommand(UserEntity user) {
        this.user = user;
    }


    @Override
    public Result request() {
        List<TrophyEntity> trophies = user.getAchievementsAsTrophies();
        return new Result.Success<>(trophies);
    }
}
