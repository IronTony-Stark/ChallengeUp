package com.example.challengeup.fragment.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;

import com.example.challengeup.IBlockingLoadable;
import com.example.challengeup.R;

public class BlockingLoaderDialog implements IBlockingLoadable {

    private final AlertDialog mDialog;

    @SuppressLint("InflateParams")
    public BlockingLoaderDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(activity.getLayoutInflater().inflate(R.layout.dialog_blocking_loader, null));
        builder.setCancelable(false);
        mDialog = builder.create();
    }

    @Override
    public void startBlockingLoading(int timeoutMillis) {
        mDialog.show();
        if (timeoutMillis > 0)
            new Handler().postDelayed(this::finishBlockingLoading, timeoutMillis);
    }

    @Override
    public void finishBlockingLoading() {
        mDialog.dismiss();
    }
}
