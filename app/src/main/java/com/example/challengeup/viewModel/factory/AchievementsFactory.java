package com.example.challengeup.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.viewModel.AchievementsViewModel;

public class AchievementsFactory implements ViewModelProvider.Factory {

    private final RequestExecutor mRequestExecutor;

    public AchievementsFactory(final RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AchievementsViewModel(mRequestExecutor);
    }
}
