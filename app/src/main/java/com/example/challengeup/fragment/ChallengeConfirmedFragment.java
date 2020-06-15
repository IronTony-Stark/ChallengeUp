package com.example.challengeup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengeChallengesViewModel;
import com.example.challengeup.viewModel.factory.ChallengeChallengesFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ChallengeConfirmedFragment extends Fragment {

    private ChallengeChallengesViewModel mViewModel;
    private List<VideoConfirmationEntity> mArrayList = new ArrayList<>();
    private Adapter mAdapter;

    private String challengeId;

    public ChallengeConfirmedFragment(String challengeId) {
        this.challengeId = challengeId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenge_challenges, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new ChallengeChallengesFactory(
                appContainer.mRequestExecutor
        )).get(ChallengeChallengesViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.challenge_challenges_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        mAdapter = new Adapter(mArrayList);
        recyclerView.setAdapter(mAdapter);

        mViewModel.getChallengeById(challengeId, result -> {
            if (result instanceof Result.Success) {

                ChallengeEntity challenge = ((Result.Success<ChallengeEntity>) result).data;

                mViewModel.getAllConfirmedVideos(challenge, result2 -> {
                    if (result2 instanceof Result.Success) {
                        //noinspection unchecked
                        mArrayList = ((Result.Success<List<VideoConfirmationEntity>>) result2).data;

                        mAdapter.setDataset(mArrayList);
                        mAdapter.notifyItemRangeInserted(0, mArrayList.size());
                    }
                });

            }
        });



    }


    static class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<VideoConfirmationEntity> mDataset;

        public static class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView videoImage, avatar;
            Button denyButton, confirmButton;

            public MyViewHolder(View itemView) {
                super(itemView);

                videoImage = itemView.findViewById(R.id.video);
                avatar = itemView.findViewById(R.id.avatar);
                denyButton = itemView.findViewById(R.id.btnDeny);
                confirmButton = itemView.findViewById(R.id.btnConfirm);

            }
        }

        public Adapter(@NonNull List<VideoConfirmationEntity> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.confirmation, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NotNull Adapter.MyViewHolder holder, int position) {
            VideoConfirmationEntity videoConfirmationEntity = mDataset.get(position);

            //todo set images + video, add listeners to buttons

//            holder.denyButton.setVisibility(Button.VISIBLE);

            holder.denyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void setDataset(List<VideoConfirmationEntity> newDataset) {
            mDataset = newDataset;
            notifyDataSetChanged();
        }
    }


}
