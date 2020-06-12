package com.example.challengeup.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengeViewModel;
import com.example.challengeup.viewModel.factory.ChallengeFactory;
import com.google.android.material.chip.Chip;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.List;

public class ChallengeFragment extends Fragment {

    View view;

    private ChallengeViewModel mViewModel;

    private HashTagHelper mTextHashTagHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_challenge, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ConstraintLayout progressBarHolder = view.findViewById(R.id.progress_overlay);
//        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
//        inAnimation.setDuration(200);
//        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
//        outAnimation.setDuration(200);
//
//        progressBarHolder.setAnimation(inAnimation);
//        progressBarHolder.setVisibility(View.VISIBLE);

//        ProgressWheel wheel = view.findViewById(R.id.progress_wheel);
//        wheel.setBarColor(Color.BLUE);
//        wheel.spin();

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new ChallengeFactory(
                appContainer.mRequestExecutor
        )).get(ChallengeViewModel.class);

        String challengeId = ChallengeFragmentArgs
                .fromBundle(requireArguments()).getChallengeId();

        ImageView imageChallenge = view.findViewById(R.id.imageChallenge);
        ImageView avatar = view.findViewById(R.id.avatar);

        TextView name = view.findViewById(R.id.dataCompleted);
        TextView nameUser = view.findViewById(R.id.nameUser);
        TextView description = view.findViewById(R.id.description);
        TextView task = view.findViewById(R.id.task);
        ViewGroup tags = view.findViewById(R.id.tags);
        TextView hashtags = view.findViewById(R.id.hashTags);
        TextView revardRP = view.findViewById(R.id.rewardRP);//100 RP

        //todo accepted/completed/liked


        mViewModel.getChallengeById(challengeId, result -> {
            if (result instanceof Result.Success) {
                ChallengeEntity challenge = ((Result.Success<ChallengeEntity>) result).data;
                //todo imageChallenge

                mViewModel.getUserById(challenge.getCreator_id(), result2 -> {
                    if (result2 instanceof Result.Success) {
                        //noinspection unchecked
                        UserEntity user = ((Result.Success<UserEntity>) result2).data;
                        if (user != null) {
//                            Bitmap avatarBitmap = user.getPhoto();
//                            if (avatarBitmap != null)
//                                avatar.setImageBitmap(avatarBitmap);

                            nameUser.setText(user.getNick());
                        }
                    }
                });

                name.setText(challenge.getName());
                description.setText(challenge.getTask());
                task.setText(challenge.getTask());

                mTextHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(getContext(), R.color.colorPrimary),
                        hashTag -> {
                            Toast.makeText(getContext(), hashTag, Toast.LENGTH_SHORT).show();
                        }, '_');
                mTextHashTagHelper.handle(task);

                for (String tag : challenge.getCategories()) {
                    Chip chip = new Chip(getContext());
                    chip.setText(tag);
//            chip.chipIcon = ContextCompat.getDrawable(requireContext(), baseline_person_black_18)
                    chip.setCloseIconVisible(false);
                    chip.setClickable(true);
                    chip.setCheckable(false);
                    tags.addView(chip);
                }

                List<String> tagsList = challenge.getTags();
                StringBuilder hashtagsBuilder = new StringBuilder();

                for (int i = 0; i < tagsList.size(); i++)
                    hashtagsBuilder.append("#").append(tagsList.get(0));
                hashtags.setText(hashtagsBuilder.toString());

                revardRP.setText(challenge.getRewardRp() + " RP");

//                progressBarHolder.setAnimation(outAnimation);
//                progressBarHolder.setVisibility(View.GONE);
//                wheel.stopSpinning();
            }
        });
    }
}
