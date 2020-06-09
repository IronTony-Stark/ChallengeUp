package com.example.challengeup;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import com.example.challengeup.request.RequestExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Container {

    public final ExecutorService mExecutor;
    public final Handler mMainThreadHandler;
    public final RequestExecutor mRequestExecutor;

    public Container() {
        mExecutor = Executors.newFixedThreadPool(5);
        mMainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        mRequestExecutor = new RequestExecutor(mExecutor, mMainThreadHandler);
    }
}
