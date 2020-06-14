package com.example.challengeup.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.AddBookmarkedCommand;
import com.example.challengeup.request.command.GetAllChallengesCommand;
import com.example.challengeup.request.command.GetCategories;
import com.example.challengeup.request.command.GetNumAcceptedCommand;
import com.example.challengeup.request.command.GetNumCompletedCommand;
import com.example.challengeup.request.command.GetUserByEmailCommand;
import com.example.challengeup.request.command.GetUserByIdCommand;
import com.example.challengeup.request.command.LikedCommand;
import com.example.challengeup.request.command.RemoveBookmarkedCommand;
import com.example.challengeup.request.command.UnlikedCommand;

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

    public void getUserByEmail(String email, ICallback callback) {
        mRequestExecutor.execute(new GetUserByEmailCommand(email), callback);
    }

    public void getNumAccepted(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new GetNumAcceptedCommand(challenge), callback);
    }

    public void getNumCompleted(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new GetNumCompletedCommand(challenge), callback);
    }

    public void setBookmarked(UserEntity user, ChallengeEntity challenge, boolean isBookmarked, ICallback callback) {
        if (isBookmarked)
            mRequestExecutor.execute(new AddBookmarkedCommand(user, challenge), callback);
        else
            mRequestExecutor.execute(new RemoveBookmarkedCommand(user, challenge), callback);
    }

    public void setLiked(UserEntity user, ChallengeEntity challenge, boolean isLiked, ICallback callback) {
        if (isLiked)
            mRequestExecutor.execute(new LikedCommand(user, challenge), callback);
        else
            mRequestExecutor.execute(new UnlikedCommand(user, challenge), callback);
    }

    public void getCategories(ICallback callback) {
        mRequestExecutor.execute(new GetCategories(), callback);
    }
}
