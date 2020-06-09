package com.example.challengeup.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.R;
import com.example.challengeup.backend.User;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengeViewModel;
import com.example.challengeup.viewModel.factory.ChallengeFactory;

import java.util.List;


public class Challenge extends Fragment {

    View view;

    private ChallengeViewModel mViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_challenge, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new ChallengeFactory(
                appContainer.mRequestExecutor
        )).get(ChallengeViewModel.class);

        String challengeId = ChallengeArgs.fromBundle(getArguments()).getChallengeId();


        ImageView imageChallenge = view.findViewById(R.id.imageChallenge);
        ImageView avatar = view.findViewById(R.id.avatar);

        TextView name = view.findViewById(R.id.dataCompleted);
        TextView nameUser = view.findViewById(R.id.nameUser);
        TextView description = view.findViewById(R.id.description);
        TextView task = view.findViewById(R.id.task);
        TextView hashtags = view.findViewById(R.id.hashTags);
        TextView revardRP = view.findViewById(R.id.rewardRP);//100 RP

        //todo accepted/completed/liked



        mViewModel.getChallengeById(challengeId, result -> {
            if (result instanceof Result.Success) {
                com.example.challengeup.backend.Challenge challenge = ((Result.Success<com.example.challengeup.backend.Challenge>) result).data;
                //todo imageChallenge

                mViewModel.getUserById(challenge.getCreator_id(), result2 -> {
                    if (result2 instanceof Result.Success) {
                        //noinspection unchecked
                        User user = ((Result.Success<User>) result2).data;
                        if (user != null) {
                            Bitmap avatarBitmap = user.getPhoto();
                            if (avatarBitmap != null)
                                avatar.setImageBitmap(avatarBitmap);

                            nameUser.setText(user.getNick());
                        }
                    }
                });

                name.setText(challenge.getName());
                description.setText(challenge.getTask());
                task.setText(challenge.getTask());


                List<String> tagsList = challenge.getTags();
                if(tagsList.size()>0){
                    String tags = "#"+String.join(" #", tagsList);
                    hashtags.setText(tags);
                }

                revardRP.setText(challenge.getRewardRp()+" RP");

            }
        });
    }
}
