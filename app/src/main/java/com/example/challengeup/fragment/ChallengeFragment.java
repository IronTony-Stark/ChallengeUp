package com.example.challengeup.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.IBlockingLoadable;
import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.databinding.FragmentChallengeBinding;
import com.example.challengeup.dto.ChallengeDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengesViewModel;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.ChallengesFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ChallengeFragment extends Fragment {

    private FragmentChallengeBinding mBinding;

    private ChallengesViewModel mViewModel;
    private MainActivityViewModel mMainActivityViewModel;

    private StorageReference mStorage;
    private IBlockingLoadable loadable;

    private ChallengeEntity mChallenge;
    private UserEntity mUser;

    private Button mBtnLoadVideo;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenge,
                container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new ChallengesFactory(
                appContainer.mRequestExecutor
        )).get(ChallengesViewModel.class);
        mMainActivityViewModel = new ViewModelProvider(requireActivity())
                .get(MainActivityViewModel.class);

        mStorage = FirebaseStorage.getInstance().getReference("challenge_confirmation_videos");

        mBtnLoadVideo = mBinding.btnLoadVideo;

        loadable = (IBlockingLoadable) requireActivity();
        loadable.startBlockingLoading(5000);

        String challengeId = ChallengeFragmentArgs
                .fromBundle(requireArguments()).getChallengeId();

        mViewModel.getChallengeById(challengeId, getChallengeResult -> {
            if (getChallengeResult instanceof Result.Success) {
                //noinspection unchecked
                mChallenge = ((Result.Success<ChallengeEntity>) getChallengeResult).data;

                setupChallengeData(mChallenge);

                mMainActivityViewModel.getUserById(mChallenge.getCreator_id(), getCreatorResult -> {
                    if (getCreatorResult instanceof Result.Success) {
                        //noinspection unchecked
                        UserEntity user = ((Result.Success<UserEntity>) getCreatorResult).data;
                        if (user != null)
                            setupUserData(user);
                    }
                });

                setupHashtags();

                setupChips();

                setupButtons();
            }

            setupTabs(mChallenge);

            loadable.finishBlockingLoading();
        });
    }

    private void setupUserData(UserEntity user) {
        mBinding.setUserName(user.getNick());
        mBinding.setUserAvatar(user.getPhoto());
    }

    private void setupChallengeData(ChallengeEntity challenge) {
        ChallengeDTO challengeDTO = ChallengeDTO
                .builder()
                .name(challenge.getName())
                .description(challenge.getTask())
                .liked(String.valueOf(challenge.getLikes()))
                .rp(String.valueOf(challenge.getRewardRp()))
                .build();

        mBinding.setChallenge(challengeDTO);
        mBinding.setChallengeThumbnail(challenge.getPhoto());

        mViewModel.getNumAccepted(challenge, result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                Long num = ((Result.Success<Long>) result).data;
                challengeDTO.setAccepted(String.valueOf(num));
                mBinding.setChallenge(challengeDTO);
            }
        });

        mViewModel.getNumCompleted(challenge, result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                Long num = ((Result.Success<Long>) result).data;
                challengeDTO.setCompleted(String.valueOf(num));
                mBinding.setChallenge(challengeDTO);
            }
        });
    }

    private void setupHashtags() {
        HashTagHelper textHashTagHelper = HashTagHelper.Creator.create(
                ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                hashTag -> Toast.makeText(getContext(), hashTag, Toast.LENGTH_SHORT
                ).show(), '_');
        textHashTagHelper.handle(mBinding.description);
    }

    private void setupChips() {
        ViewGroup categories = mBinding.categories;
        for (String category : mChallenge.getCategories()) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setClickable(true);
            chip.setCheckable(false);
            categories.addView(chip);
        }
    }

    private void setupButtons() {
        Button btnAccept = mBinding.btnAccept;
        Button btnLoadVideo = mBinding.btnLoadVideo;

        mMainActivityViewModel.getUserById(Objects.requireNonNull(
                mMainActivityViewModel.getUser().getValue()).getId(), getUserResult -> {
            if (getUserResult instanceof Result.Success) {
                //noinspection unchecked
                mUser = ((Result.Success<UserEntity>) getUserResult).data;

                if (mUser != null) {
                    boolean isAccepted = false;
                    for (String acceptedId : mUser.getUndone()) {
                        if (acceptedId.equals(mChallenge.getId())) {
                            isAccepted = true;
                            break;
                        }
                    }

                    if (isAccepted)
                        btnLoadVideo.setVisibility(View.VISIBLE);
                    else
                        btnAccept.setVisibility(View.VISIBLE);

                    for (String waitingConfirmationId : mUser.getWaitingConfirmation())
                        if (waitingConfirmationId.equals(mChallenge.getId()))
                            btnLoadVideo.setEnabled(false);
                }
            }
        });

        btnAccept.setOnClickListener(v -> {
            mViewModel.addChallengeToUndone(mUser, mChallenge, ignored -> {
            });
            btnAccept.setVisibility(View.INVISIBLE);
            btnLoadVideo.setVisibility(View.VISIBLE);
        });

        btnLoadVideo.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivityForResult(intent, GET_VIDEO_REQUEST);
        });
    }

    private void setupTabs(ChallengeEntity challengeEntity) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, challengeEntity);
        ViewPager2 viewPager = mBinding.viewPager;
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = mBinding.tabLayout;
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GET_VIDEO_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            Uri photoUri = data.getData();
            mViewModel.createVideoConfirmation(mUser, mChallenge.getId(), result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    String fileId = ((Result.Success<String>) result).data;

                    mBtnLoadVideo.setEnabled(false);
                    loadable.startBlockingLoading(20000);
                    uploadVideo(photoUri, mChallenge.getId(), fileId);
                } else
                    Toast.makeText(getContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // TODO image and video upload are the same. Extract to method
    private void uploadVideo(Uri videoUri, String challengeId, String fileId) {
        if (videoUri != null) {
            StorageReference file = mStorage.child(challengeId + "/" + fileId + "_video");
            file.putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(),
                                "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        loadable.finishBlockingLoading();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(),
                                "Upload Failed", Toast.LENGTH_SHORT).show();
                        VideoConfirmationEntity.deleteVideo(fileId);
                        loadable.finishBlockingLoading();
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    class ViewPagerAdapter extends FragmentStateAdapter {

        private final ChallengeEntity mChallenge;

        public ViewPagerAdapter(Fragment fragment, ChallengeEntity challenge) {
            super(fragment);
            this.mChallenge = challenge;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = null;

            switch (position) {
                case 0:
                    if (mFragmentsCache[0] == null)
                        mFragmentsCache[0] = new ChallengePlayersFragment(mChallenge.getId());
                    fragment = mFragmentsCache[0];
                    break;
                case 1:
                    if (mFragmentsCache[1] == null)
                        mFragmentsCache[1] = new ChallengeUnconfirmedFragment(mChallenge);
                    fragment = mFragmentsCache[1];
                    break;
                case 2:
                    if (mFragmentsCache[2] == null)
                        mFragmentsCache[2] = new ChallengeConfirmedFragment(mChallenge.getId());
                    fragment = mFragmentsCache[2];
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

    private Fragment[] mFragmentsCache = new Fragment[3];
    private static final int GET_VIDEO_REQUEST = 1;
}
