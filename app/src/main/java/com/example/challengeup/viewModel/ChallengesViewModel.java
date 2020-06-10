package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.AddBookmarkedCommand;
import com.example.challengeup.request.command.GetAllChallengesCommand;
import com.example.challengeup.request.command.GetNumAcceptedCommand;
import com.example.challengeup.request.command.GetNumCompletedCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;
import com.example.challengeup.request.command.RemoveBookmarkedCommand;

public class ChallengesViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public ChallengesViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getAllChallenges(ICallback callback) {
        mRequestExecutor.execute(new GetAllChallengesCommand(), callback);
    }

    public void getUserById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetUserByIdCommand(uid), callback);
    }

    public void getNumAccepted(Challenge challenge, ICallback callback) {
        mRequestExecutor.execute(new GetNumAcceptedCommand(challenge), callback);
    }

    public void getNumCompleted(Challenge challenge, ICallback callback) {
        mRequestExecutor.execute(new GetNumCompletedCommand(challenge), callback);
    }

    public void setBookmarked(User user, Challenge challenge, boolean isBookmarked, ICallback callback) {
        if (!isBookmarked)
            mRequestExecutor.execute(new AddBookmarkedCommand(user, challenge), callback);
        else
            mRequestExecutor.execute(new RemoveBookmarkedCommand(user, challenge), callback);
    }
}
