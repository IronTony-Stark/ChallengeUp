package com.example.challengeup.viewModel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.dto.ChallengeSearchDTO;
import com.example.challengeup.request.ICallback;
import com.example.challengeup.request.RequestExecutor;
import com.example.challengeup.request.command.AddBookmarkedCommand;
import com.example.challengeup.request.command.AddChallengeCommand;
import com.example.challengeup.request.command.AddChallengeToUndone;
import com.example.challengeup.request.command.BanCommand;
import com.example.challengeup.request.command.ChallengeSearchCommand;
import com.example.challengeup.request.command.CreateVideoConfirmation;
import com.example.challengeup.request.command.GetAllChallengesCommand;
import com.example.challengeup.request.command.GetCategories;
import com.example.challengeup.request.command.GetChallengeByIdCommand;
import com.example.challengeup.request.command.GetNumAcceptedCommand;
import com.example.challengeup.request.command.GetNumCompletedCommand;
import com.example.challengeup.request.command.LikedCommand;
import com.example.challengeup.request.command.RemoveBookmarkedCommand;
import com.example.challengeup.request.command.ReportCommand;
import com.example.challengeup.request.command.UnlikedCommand;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class ChallengesViewModel extends ViewModel {

    private final RequestExecutor mRequestExecutor;

    public ChallengesViewModel(RequestExecutor requestExecutor) {
        mRequestExecutor = requestExecutor;
    }

    public void getChallengeById(String uid, ICallback callback) {
        mRequestExecutor.execute(new GetChallengeByIdCommand(uid), callback);
    }

    public void getAllChallenges(ICallback callback) {
        mRequestExecutor.execute(new GetAllChallengesCommand(), callback);
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

    public void search(ChallengeSearchDTO challengeSearch, ICallback callback) {
        mRequestExecutor.execute(new ChallengeSearchCommand(challengeSearch), callback);
    }

    public void addChallengeToUndone(UserEntity user, ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new AddChallengeToUndone(user, challenge), callback);
    }

    public void createVideoConfirmation(UserEntity user, String challenge, ICallback callback) {
        mRequestExecutor.execute(new CreateVideoConfirmation(user, challenge), callback);
    }

    public void addChallenge(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new AddChallengeCommand(challenge), callback);
    }

    public void getCategories(ICallback callback) {
        mRequestExecutor.execute(new GetCategories(), callback);
    }

    public void inflateChipGroup(ChipGroup chipGroup, List<String> categories, Context context) {
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);

            Chip chip = new Chip(context);
            chip.setChipDrawable(ChipDrawable.createFromResource(
                    context, R.xml.item_chip_filter));
            chip.setText(category);

            chipGroup.addView(chip);
        }
    }

    public void banChallenge(ChallengeEntity challenge, boolean ban, ICallback callback) {
        mRequestExecutor.execute(new BanCommand(challenge, ban), callback);
    }

    public void reportChallenge(ChallengeEntity challenge, ICallback callback) {
        mRequestExecutor.execute(new ReportCommand(challenge), callback);
    }
}
