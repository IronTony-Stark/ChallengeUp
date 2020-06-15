package com.example.challengeup.viewModel;

import android.os.Environment;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.GetAllConfirmedVideosCommand;
import com.example.challengeup.request.command.GetAllUnconfirmedVideosCommand;
import com.example.challengeup.request.command.GetAllVideosCommand;
import com.example.challengeup.request.command.GetChallengeByIdCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;
import com.example.challengeup.request.command.LoadFileToCashCommand;

import java.io.File;

public class ChallengeChallengesViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    private String TAG = "VideoDownloader";

    private boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public File getSdcardFileForVideo(String fileName) {
        if (!isExternalStorageWritable()) {
            throw new RuntimeException("Unable to access external storage");
        }

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            throw new RuntimeException("Unable to access external storage");
        } else {
            File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ChallengeUp/Videos/");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            return new File(appDir, fileName);
        }
    }

    public ChallengeChallengesViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getChallengeById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetChallengeByIdCommand(uid), callback);
    }

    public void getAllVideos(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new GetAllVideosCommand(challenge), callback);
    }
    //get users method

    public void getUserByID(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetUserByIdCommand(uid), callback);
    }

    public void getAllConfirmedVideos(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new GetAllConfirmedVideosCommand(challenge), callback);
    }

    public void getAllUnconfirmedVideos(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new GetAllUnconfirmedVideosCommand(challenge), callback);
    }

    public void loadVideoToCash(String url, String fileName, ICallback callback) {
        mRequestExecutor.execute(new LoadFileToCashCommand(url, fileName), callback);
    }
}
