package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.TrophyEntity;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetTrophyByIdCommand;
import com.example.challengeup.request.command.GetUsersWithThisTrophyCommand;


public class AchievementViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public AchievementViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getUsersWithThisTrophy(TrophyEntity trophy, ICallback callback){
        mRequestExecutor.execute(new GetUsersWithThisTrophyCommand(trophy), callback);
    }

    public void getTrophyById(String id, ICallback callback){
        mRequestExecutor.execute(new GetTrophyByIdCommand(id), callback);
    }
}
