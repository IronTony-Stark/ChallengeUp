package com.example.challengeup.request.command;

import com.example.challengeup.backend.TrophyEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.util.List;

public class GetUndoneAchievementsAsTrophiesCommand  implements IRequestCommand {

    private final UserEntity user;

    public GetUndoneAchievementsAsTrophiesCommand(UserEntity user) {
        this.user = user;
    }


    @Override
    public Result request() {
        List<TrophyEntity> trophies = user.getUndoneAchievementsAsTrophies();
        return new Result.Success<>(trophies);
    }
}
