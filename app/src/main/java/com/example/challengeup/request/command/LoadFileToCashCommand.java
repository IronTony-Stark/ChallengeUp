package com.example.challengeup.request.command;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.challengeup.backend.Utilities;
import com.example.challengeup.request.IRequestCommand;
import com.example.challengeup.request.Result;

import java.io.IOException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoadFileToCashCommand implements IRequestCommand {

    private String url;
    private String fileName;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Result request() {
        try {
            Utilities.downloadFile(url, fileName);
            return new Result.Success<>(fileName);
        } catch (IOException e) {
            return new Result.Error(e);
        }
    }
}
