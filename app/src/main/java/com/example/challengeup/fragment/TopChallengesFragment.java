package com.example.challengeup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.ILoadable;
import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.databinding.ItemTopChallengesBinding;
import com.example.challengeup.dto.TopChallengeDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengesViewModel;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.ChallengesFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopChallengesFragment extends Fragment {

    private ChallengesViewModel mViewModel;
    private MainActivityViewModel mMainActivityViewModel;
    private List<ChallengeEntity> mArrayList = new ArrayList<>();
    private Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_challenges, container, false);
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

        RecyclerView recyclerView = view.findViewById(R.id.top_challenges_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        mAdapter = new Adapter(mArrayList);
        recyclerView.setAdapter(mAdapter);

        ILoadable loadable = (ILoadable) requireActivity();
        loadable.startLoading();

        mViewModel.getAllChallenges(result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                mArrayList = ((Result.Success<List<ChallengeEntity>>) result).data;

                Collections.sort(mArrayList, (c1, c2) ->
                        -Integer.compare(c1.getLikes(), c2.getLikes()));

                mAdapter.setDataset(mArrayList);
                mAdapter.notifyItemRangeInserted(0, mArrayList.size());
                loadable.finishLoading();
            }
        });
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ChallengeViewHolder> {

        private List<ChallengeEntity> mDataset;

        public Adapter(@NonNull List<ChallengeEntity> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public ChallengeViewHolder onCreateViewHolder(@NotNull ViewGroup parent,
                                                      int viewType) {
            ItemTopChallengesBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_top_challenges, parent, false);

            return new TopChallengesFragment.Adapter.ChallengeViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NotNull ChallengeViewHolder holder, int position) {
            ChallengeEntity challenge = mDataset.get(position);

            String rank = String.valueOf(position + 1);
            String name = challenge.getName();
            String liked = String.valueOf(challenge.getLikes());

            TopChallengeDTO topChallenge = new TopChallengeDTO();
            topChallenge.setRank(rank);
            topChallenge.setName(name);
            topChallenge.setLiked(liked);

            holder.bind(topChallenge);

            mMainActivityViewModel.getUserById(challenge.getCreator_id(), result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    UserEntity user = ((Result.Success<UserEntity>) result).data;
                    if (user != null) {
                        String userPhoto = user.getPhoto() != null ?
                                user.getPhoto() : MainActivityViewModel.DEFAULT_AVATAR_URL;
                        topChallenge.setAvatar(userPhoto);
                        holder.bind(topChallenge);
                    }
                }
            });

            mViewModel.getNumAccepted(challenge, result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    Long num = ((Result.Success<Long>) result).data;
                    topChallenge.setAccepted(String.valueOf(num));
                    holder.bind(topChallenge);
                }
            });

            mViewModel.getNumCompleted(challenge, result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    Long num = ((Result.Success<Long>) result).data;
                    topChallenge.setCompleted(String.valueOf(num));
                    holder.bind(topChallenge);
                }
            });

            holder.itemView.setOnClickListener(view -> {
                TopChallengesFragmentDirections.ActionTopChallengesToChallenge action =
                        TopChallengesFragmentDirections.actionTopChallengesToChallenge(challenge.getId());
                Navigation.findNavController(view).navigate(action);
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void setDataset(List<ChallengeEntity> newDataset) {
            mDataset = newDataset;
            notifyDataSetChanged();
        }

        class ChallengeViewHolder extends RecyclerView.ViewHolder {

            private final ItemTopChallengesBinding mBinding;

            public ChallengeViewHolder(ItemTopChallengesBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
                // TODO avatar click listener
            }

            public void bind(TopChallengeDTO topChallengeDTO) {
                mBinding.setChallenge(topChallengeDTO);
                mBinding.executePendingBindings();
            }
        }
    }
}
