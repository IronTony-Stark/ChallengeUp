package com.example.challengeup.viewModel.factory;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.viewModel.MainActivityViewModel;

import java.io.File;

public class MainActivityFactory implements ViewModelProvider.Factory {

    private final RequestExecutor mRequestExecutor;
    private final SharedPreferences mPreferences;
    private final File mAvatarFile;

    public MainActivityFactory(final RequestExecutor requestExecutor,
                               final SharedPreferences preferences,
                               final File avatarFile) {
        mRequestExecutor = requestExecutor;
        mPreferences = preferences;
        mAvatarFile = avatarFile;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(mRequestExecutor, mPreferences, mAvatarFile);
    }
}
