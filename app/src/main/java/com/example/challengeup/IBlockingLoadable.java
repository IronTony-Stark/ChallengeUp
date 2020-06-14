package com.example.challengeup;

public interface IBlockingLoadable {
    void startBlockingLoading(int timeoutMillis);
    void finishBlockingLoading();
}
