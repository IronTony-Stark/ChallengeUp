package com.example.challengeup.fragment;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengePlayersViewModel;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.ChallengePlayersFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChallengePlayersFragment extends Fragment {

    private ChallengePlayersViewModel mViewModel;
    private List<UserEntity> mData = new ArrayList<>();
    private Adapter mAdapter;

    private String challengeId;

    public ChallengePlayersFragment(String challengeId) {
        this.challengeId = challengeId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenge_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new ChallengePlayersFactory(
                appContainer.mRequestExecutor
        )).get(ChallengePlayersViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.challenge_players_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        mAdapter = new Adapter(mData);
        recyclerView.setAdapter(mAdapter);

        mViewModel.getChallengeById(challengeId, result -> {
            if (result instanceof Result.Success) {

                ChallengeEntity challenge = ((Result.Success<ChallengeEntity>) result).data;

                mViewModel.peopleWhoAccepted(challenge, result2 -> {
                    if (result2 instanceof Result.Success) {
                        //noinspection unchecked
                        mData = ((Result.Success<List<UserEntity>>) result2).data;

                        Collections.sort(mData, (u1, u2) ->
                                Integer.compare(u1.getTotalRp(), u2.getTotalRp()));

                        mAdapter.setDataset(mData);
                        mAdapter.notifyItemRangeInserted(0, mData.size());
                    }
                });

            }
            });



    }

    static class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<UserEntity> mDataset;

        public static class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView avatar;
            TextView rank, name, rp;

            public MyViewHolder(View itemView) {
                super(itemView);

                avatar = itemView.findViewById(R.id.avatar);
                rank = itemView.findViewById(R.id.rank);
                name = itemView.findViewById(R.id.name);
                rp = itemView.findViewById(R.id.rp);
            }
        }

        public Adapter(@NonNull List<UserEntity> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_top_players, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NotNull Adapter.MyViewHolder holder, int position) {
            UserEntity user = mDataset.get(position);

//            holder.binding.setUserAvatar(photoUrl != null ? photoUrl : MainActivityViewModel.DEFAULT_AVATAR_URL);

            holder.rank.setText(String.valueOf(position + 1));
            holder.name.setText(user.getNick());
            holder.rp.setText(String.valueOf(user.getTotalRp()));

            holder.itemView.setOnClickListener(view -> {
                //todo navigation
//                TopPlayersFragmentDirections.ActionTopPlayersToProfile action =
//                        TopPlayersFragmentDirections.actionTopPlayersToProfile(user.getId());
//                Navigation.findNavController(view).navigate(action);
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void setDataset(List<UserEntity> newDataset) {
            mDataset = newDataset;
            notifyDataSetChanged();
        }
    }
}
