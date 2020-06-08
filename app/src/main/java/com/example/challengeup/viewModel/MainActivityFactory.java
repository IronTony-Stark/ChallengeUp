package com.example.challengeup.viewModel;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.ExecutorService;

public class MainActivityFactory implements ViewModelProvider.Factory {

    private final ExecutorService mExecutor;
    private final Handler mMainThreadHandler;

    public MainActivityFactory(final ExecutorService executor,
                               final Handler mainThreadHandler) {
        mExecutor = executor;
        mMainThreadHandler = mainThreadHandler;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(mExecutor, mMainThreadHandler);
    }
}
