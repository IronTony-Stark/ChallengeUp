package com.example.challengeup.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.IBlockingLoadable;
import com.example.challengeup.ILoadable;
import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengeViewModel;
import com.example.challengeup.viewModel.factory.ChallengeFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static android.app.Activity.RESULT_OK;

public class ChallengeFragment extends Fragment {

    private static final int GET_VIDEO_REQUEST = 1;
    View view;
    private ChallengeViewModel mViewModel;
    private HashTagHelper mTextHashTagHelper;
    private StorageReference mStorage;
    private String userIDToConfirm;
    private String challengeIDToConfirm;

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

        String challengeId = ChallengeFragmentArgs
                .fromBundle(requireArguments()).getChallengeId();

        mStorage = FirebaseStorage.getInstance().getReference();

        ImageView imageChallenge = view.findViewById(R.id.imageChallenge);
        ImageView avatar = view.findViewById(R.id.avatar);

        TextView name = view.findViewById(R.id.dataCompleted);
        TextView nameUser = view.findViewById(R.id.nameUser);
        TextView description = view.findViewById(R.id.description);
        TextView task = view.findViewById(R.id.task);
        ViewGroup tags = view.findViewById(R.id.tags);
        TextView hashtags = view.findViewById(R.id.hashTags);
        TextView rewardRP = view.findViewById(R.id.rewardRP);//100 RP

        AtomicReference<ChallengeEntity> challenge = new AtomicReference<>();

        //todo accepted/completed/liked

        IBlockingLoadable loadable = (IBlockingLoadable) requireActivity();
        loadable.startBlockingLoading(5000);

        mViewModel.getChallengeById(challengeId, result -> {
            if (result instanceof Result.Success) {
                challenge.set(((Result.Success<ChallengeEntity>) result).data);
                //todo imageChallenge

                mViewModel.getUserById(challenge.get().getCreator_id(), result2 -> {
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

                name.setText(challenge.get().getName());
                description.setText(challenge.get().getTask());
                task.setText(challenge.get().getTask());

                mTextHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(getContext(), R.color.colorPrimary),
                        hashTag -> {
                            Toast.makeText(getContext(), hashTag, Toast.LENGTH_SHORT).show();
                        }, '_');
                mTextHashTagHelper.handle(task);

                for (String tag : challenge.get().getCategories()) {
                    Chip chip = new Chip(getContext());
                    chip.setText(tag);
//            chip.chipIcon = ContextCompat.getDrawable(requireContext(), baseline_person_black_18)
                    chip.setCloseIconVisible(false);
                    chip.setClickable(true);
                    chip.setCheckable(false);
                    tags.addView(chip);
                }

                List<String> tagsList = challenge.get().getTags();
                StringBuilder hashtagsBuilder = new StringBuilder();

                for (int i = 0; i < tagsList.size(); i++)
                    hashtagsBuilder.append("#").append(tagsList.get(0));
                hashtags.setText(hashtagsBuilder.toString());

                rewardRP.setText(challenge.get().getRewardRp() + " RP");

                Button accept = view.findViewById(R.id.btnAccept);
                Button uploadConfirm = view.findViewById(R.id.btnLoadVideo);
                AtomicReference<UserEntity> user = new AtomicReference<>();
                mViewModel.getUserByEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail(), u -> {
                    if (u instanceof Result.Success) {
                        //noinspection unchecked
                        user.set(((Result.Success<UserEntity>) u).data);
                        if (user.get() != null) {
                            boolean isAccepted = false;
                            for (String acceptedId : user.get().getUndone()) {
                                if (acceptedId.equals(challengeId))
                                    isAccepted = true;
                            }
                            if (isAccepted)
                                uploadConfirm.setVisibility(View.VISIBLE);
                            else accept.setVisibility(View.VISIBLE);
                            for (String waitingConfirmId : user.get().getWaitingConfirmation()) {
                                if (waitingConfirmId.equals(challengeId))
                                    uploadConfirm.setEnabled(false);
                            }
                        }
                    }
                });

                accept.setOnClickListener(l -> {
                    mViewModel.addChallengeToUndone(user.get(), challenge.get(), r -> {
                    });
                    accept.setVisibility(View.INVISIBLE);
                    uploadConfirm.setVisibility(View.VISIBLE);
                });

                uploadConfirm.setOnClickListener(v -> {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("video/*");
                    userIDToConfirm = user.get().getId();
                    challengeIDToConfirm = challengeId;
                    if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                        startActivityForResult(intent, GET_VIDEO_REQUEST);
                });

            }

            ViewPagerAdapter adapter = new ViewPagerAdapter(this, challenge.get());
            ViewPager2 viewPager = (ViewPager2) view.findViewById(R.id.viewPager);
            viewPager.setAdapter(adapter);

            TabLayout tabLayout = view.findViewById(R.id.tab_layout);
            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        switch (position) {
                            case 0:
                                tab.setText("Users");
                                break;
                            case 1:
                                tab.setText("Challenges Unconfirmed");
                                break;
                            case 2:
                                tab.setText("Challenges Confirmed");
                                break;
                        }
                    }
            ).attach();

            loadable.finishBlockingLoading();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GET_VIDEO_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            Uri photoUri = data.getData();
            Intent intent = getActivity().getIntent();
            mViewModel.createVideoConfirmation(userIDToConfirm, challengeIDToConfirm, result -> {
                if (result instanceof Result.Success) {
                    String fileId = ((Result.Success<String>) result).data;
                    view.findViewById(R.id.btnLoadVideo).setEnabled(false);
                    uploadVideo(photoUri, challengeIDToConfirm, fileId);
                } else
                    Toast.makeText(getContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void uploadVideo(Uri videoUri, String challengeId, String fileId) {
        if (videoUri != null) {
            StorageReference file = mStorage.child("challenge_confirmation_videos/" + challengeId + "/" + fileId + "_video");
            file.putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> Toast.makeText(getContext(),
                            "Uploaded Successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(),
                            "Upload Failed", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    class ViewPagerAdapter extends FragmentStateAdapter {

        private ChallengeEntity challenge;

        public ViewPagerAdapter(Fragment fragment, ChallengeEntity challenge) {
            super(fragment);
            this.challenge = challenge;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new ChallengePlayersFragment(challenge.getId());
                    break;
                case 1:
                    fragment = new ChallengeUnconfirmedFragment(challenge);
                    break;
                case 2:
                    fragment = new ChallengeConfirmedFragment(challenge.getId());
                    break;
            }

            //noinspection ConstantConditions
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
