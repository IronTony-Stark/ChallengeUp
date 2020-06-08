package com.example.challengeup.request;

import android.os.Handler;

import java.util.concurrent.ExecutorService;

public class RequestExecutor {

    private final ExecutorService mExecutor;
    private final Handler mMainThreadHandler;

    public RequestExecutor(ExecutorService executor, Handler mainThreadHandler) {
        mExecutor = executor;
        mMainThreadHandler = mainThreadHandler;
    }

    public void execute(IRequestCommand command, ICallback callback) {
        mExecutor.execute(() -> {
            Result result;
            try {
                result = command.request();
            } catch (Exception e) {
                result = new Result.Error(e);
            }
            notifyResult(result, callback);
        });
    }

    private void notifyResult(final Result result, ICallback callback) {
        mMainThreadHandler.post(() -> callback.onComplete(result));
    }
}
