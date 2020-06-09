package com.example.challengeup.viewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.viewModel.ChallengesViewModel;
import com.example.challengeup.viewModel.SavedChallengesViewModel;

public class SavedChallengesFactory implements ViewModelProvider.Factory {

    private final RequestExecutor mRequestExecutor;

    public SavedChallengesFactory(final RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SavedChallengesViewModel(mRequestExecutor);
    }
}
