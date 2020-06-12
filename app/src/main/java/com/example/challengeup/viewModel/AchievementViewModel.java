package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetAchievementsAsTrophiesCommand;
import com.example.challengeup.request.command.GetUndoneAchievementsAsTrophiesCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;


public class AchievementViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public AchievementViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getUserById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetUserByIdCommand(uid), callback);
    }

    public void getAchievementsAsTrophies(UserEntity user, ICallback callback){
        mRequestExecutor.execute(new GetAchievementsAsTrophiesCommand(user),callback);
    }

    public void getUndoneAchievementsAsTrophies(UserEntity user, ICallback callback){
        mRequestExecutor.execute(new GetUndoneAchievementsAsTrophiesCommand(user),callback);
    }

}
