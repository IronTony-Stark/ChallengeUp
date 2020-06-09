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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.R;
import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengesViewModel;
import com.example.challengeup.viewModel.factory.ChallengesFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Challenges extends Fragment {

    private ChallengesViewModel mViewModel;
    private List<Challenge> mArrayList = new ArrayList<>();
    private Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_challenges, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new ChallengesFactory(
                appContainer.mRequestExecutor
        )).get(ChallengesViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.challenges_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        mAdapter = new Adapter(mArrayList);
        recyclerView.setAdapter(mAdapter);

        mViewModel.getAllChallenges(result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                mArrayList = ((Result.Success<List<Challenge>>) result).data;
                mAdapter.setDataset(mArrayList);
                mAdapter.notifyItemRangeInserted(0, mArrayList.size());
            }
        });
    }

    class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<Challenge> mDataset;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView thumbnail, avatar;
            TextView name, description, dataAccepted, dataCompleted, dataLiked;

            public MyViewHolder(View itemView) {
                super(itemView);

                thumbnail = itemView.findViewById(R.id.thumbnail);
                avatar = itemView.findViewById(R.id.avatar);
                name = itemView.findViewById(R.id.name);
                description = itemView.findViewById(R.id.description);
                dataAccepted = itemView.findViewById(R.id.dataAccepted);
                dataCompleted = itemView.findViewById(R.id.dataCompleted);
                dataLiked = itemView.findViewById(R.id.dataLiked);
            }
        }

        public Adapter(@NonNull List<Challenge> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_challenge, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {
            Challenge challenge = mDataset.get(position);

            // holder.thumbnail.setImageBitmap(challenge.// todo image

            holder.name.setText(challenge.getName());
            holder.description.setText(challenge.getTask());
            holder.dataLiked.setText(String.valueOf(challenge.getLikes()));

            holder.itemView.setOnClickListener(view -> {
                // TODO navigate
            });

            mViewModel.getUserById(challenge.getCreator_id(), result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    Bitmap avatar = ((Result.Success<User>) result).data.getPhoto();
                    if (avatar != null)
                        holder.avatar.setImageBitmap(avatar);
                }
            });

            mViewModel.getNumAccepted(challenge, result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    Long num = ((Result.Success<Long>) result).data;
                    holder.dataAccepted.setText(String.format("%s", num.toString()));
                }
            });

            mViewModel.getNumCompleted(challenge, result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    Long num = ((Result.Success<Long>) result).data;
                    holder.dataCompleted.setText(String.format("%s", num.toString()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void setDataset(List<Challenge> newDataset) {
            mDataset = newDataset;
            notifyDataSetChanged();
        }
    }
}
