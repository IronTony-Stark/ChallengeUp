package com.example.challengeup.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.challengeup.backend.User;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.SavedChallengesViewModel;
import com.example.challengeup.viewModel.factory.SavedChallengesFactory;

import com.example.challengeup.backend.Challenge;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SavedChallenges extends Fragment {

    String id;
    User user;
    private SavedChallengesViewModel mViewModel;
    private List<Challenge> mArrayList = new ArrayList<>();
    private Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_challenges, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new SavedChallengesFactory(
                appContainer.mRequestExecutor
        )).get(SavedChallengesViewModel.class);

        //todo get current user id
        id = "a";
        mViewModel.getUserById(id, result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                user = ((Result.Success<User>) result).data;


                RecyclerView recyclerView = view.findViewById(R.id.saved_challenges_list);

                recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

                mAdapter = new Adapter(mArrayList);
                recyclerView.setAdapter(mAdapter);

                mViewModel.getSavedChallenges(user,result2 -> {
                    if (result2 instanceof Result.Success) {
                        //noinspection unchecked
                        mArrayList = ((Result.Success<List<Challenge>>) result2).data;

                        mAdapter.setDataset(mArrayList);
                        mAdapter.notifyItemRangeInserted(0, mArrayList.size());

                    }

                });

            }

        });



    }


    class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<com.example.challengeup.backend.Challenge> mDataset;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView avatar;
            TextView rank, name, dataAccepted, dataCompleted, dataLiked;

            public MyViewHolder(View itemView) {
                super(itemView);

                avatar = itemView.findViewById(R.id.avatar);
                rank = itemView.findViewById(R.id.rank);
                name = itemView.findViewById(R.id.name);
                dataAccepted = itemView.findViewById(R.id.dataAccepted);
                dataCompleted = itemView.findViewById(R.id.dataCompleted);
                dataLiked = itemView.findViewById(R.id.dataLiked);
            }
        }

        public Adapter(@NonNull List<com.example.challengeup.backend.Challenge> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_top_challenges, parent, false);

            return new Adapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NotNull Adapter.MyViewHolder holder, int position) {
            com.example.challengeup.backend.Challenge challenge = mDataset.get(position);

            holder.rank.setText(String.valueOf(position + 1));
            holder.name.setText(challenge.getName());
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
                    holder.dataAccepted.setText(String.format("%s", num));
                }
            });

            mViewModel.getNumCompleted(challenge, result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    Long num = ((Result.Success<Long>) result).data;
                    holder.dataCompleted.setText(String.format("%s", num));
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
