package com.example.challengeup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.databinding.FragmentStatsBinding;
import com.example.challengeup.dto.StatsDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.MainActivityViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StatsFragment extends Fragment {

    private MainActivityViewModel mMainActivityViewModel;
    private MutableLiveData<StatsDTO> mStats = new MutableLiveData<>();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentStatsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stats,
                container, false);
        binding.setLifecycleOwner(this);
        binding.setStats(mStats);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMainActivityViewModel = new ViewModelProvider(requireActivity())
                .get(MainActivityViewModel.class);

        String uid = StatsFragmentArgs.fromBundle(requireArguments()).getUid();
        mMainActivityViewModel.getUserById(uid, (getUserResult) -> {
            if (getUserResult instanceof Result.Success) {
                //noinspection unchecked
                UserEntity user = ((Result.Success<UserEntity>) getUserResult).data;

                StatsDTO stats = new StatsDTO();

                mMainActivityViewModel.getRank(user, (getRankResult) -> {
                    if (getRankResult instanceof Result.Success) {
                        //noinspection unchecked
                        Integer rank = ((Result.Success<Integer>) getRankResult).data;
                        StatsDTO prevStats = mStats.getValue();
                        if (prevStats != null) {
                            prevStats.setRank(String.valueOf(rank + 1));
                            mStats.setValue(prevStats);
                        }
                    }
                });

                stats.setTotalRpEarned(String.valueOf(user.getTotalRp()));

                mMainActivityViewModel.getCreatedChallenges(user, (getCreatedChallengesResult) -> {
                    if (getCreatedChallengesResult instanceof Result.Success) {
                        //noinspection unchecked
                        List<ChallengeEntity> challengesCreated =
                                ((Result.Success<List<ChallengeEntity>>) getCreatedChallengesResult).data;
                        StatsDTO prevStats = mStats.getValue();
                        if (prevStats != null) {
                            prevStats.setChallengesCreated(String.valueOf(challengesCreated.size()));
                            mStats.setValue(prevStats);
                        }
                    }
                });

                stats.setChallengesCompleted(String.valueOf(user.getDone().size()));

                mMainActivityViewModel.getSubscribers(user, (getSubscribersResult) -> {
                    if (getSubscribersResult instanceof Result.Success) {
                        //noinspection unchecked
                        List<UserEntity> followers =
                                ((Result.Success<List<UserEntity>>) getSubscribersResult).data;
                        StatsDTO prevStats = mStats.getValue();
                        if (prevStats != null) {
                            prevStats.setFollowers(String.valueOf(followers.size()));
                            mStats.setValue(prevStats);
                        }
                    }
                });

                stats.setFollowing(String.valueOf(user.getSubscriptions().size()));
                stats.setAchievements(String.valueOf(user.getTrophies().size()));
                stats.setChallengesInProgress(String.valueOf(user.getUndone().size()));
                mStats.setValue(stats);
            }
        });
    }
}