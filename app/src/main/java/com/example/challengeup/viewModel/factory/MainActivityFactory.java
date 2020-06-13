package com.example.challengeup.viewModel.factory;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.viewModel.MainActivityViewModel;

public class MainActivityFactory implements ViewModelProvider.Factory {

    private final RequestExecutor mRequestExecutor;
    private final SharedPreferences mPreferences;

    public MainActivityFactory(final RequestExecutor requestExecutor,
                               final SharedPreferences preferences) {
        mRequestExecutor = requestExecutor;
        mPreferences = preferences;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(mRequestExecutor, mPreferences);
    }
}
