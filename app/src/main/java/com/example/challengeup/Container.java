package com.example.challengeup;

import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Container {
    public final ExecutorService mExecutor =
            Executors.newFixedThreadPool(5);
    public final Handler mainThreadHandler =
            HandlerCompat.createAsync(Looper.getMainLooper());
}
