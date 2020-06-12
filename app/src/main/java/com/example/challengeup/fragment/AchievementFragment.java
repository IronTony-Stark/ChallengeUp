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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.R;
import com.example.challengeup.backend.TrophyEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.AchievementViewModel;
import com.example.challengeup.viewModel.factory.AchievementFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AchievementFragment extends Fragment {

    View view;

    private AchievementViewModel mViewModel;
    private List<UserEntity> mArrayList = new ArrayList<>();
    private Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_achievement, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new AchievementFactory(
                appContainer.mRequestExecutor
        )).get(AchievementViewModel.class);

        String trophyId = "";//todo set trophy id       //ChallengeArgs.fromBundle(getArguments()).getChallengeId();


        ImageView icon = view.findViewById(R.id.icon);
        TextView name = view.findViewById(R.id.dataCompleted);
        TextView description = view.findViewById(R.id.description);

        RecyclerView recyclerView = view.findViewById(R.id.players_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), GridLayoutManager.VERTICAL));

        mAdapter = new Adapter(mArrayList);
        recyclerView.setAdapter(mAdapter);

        mViewModel.getTrophyById(trophyId, result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                TrophyEntity trophy = ((Result.Success<TrophyEntity>) result).data;

                mViewModel.getUsersWithThisTrophy(trophy, result2 -> {
                    if (result2 instanceof Result.Success) {
                        //noinspection unchecked
                        mArrayList = ((Result.Success<List<UserEntity>>) result2).data;
                        mAdapter.setDataset(mArrayList);
                        mAdapter.notifyItemRangeInserted(0, mArrayList.size());

                    }
                });

            }
        });
    }


    class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<UserEntity> mDataset;

        public Adapter(@NonNull List<UserEntity> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_top_players, parent, false);

            return new Adapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NotNull Adapter.MyViewHolder holder, int position) {
            UserEntity user = mDataset.get(position);

//            Bitmap avatar = user.getPhoto();
//            if (avatar != null)
//                holder.avatar.setImageBitmap(avatar);

            holder.rank.setText(String.valueOf(position + 1));
            holder.name.setText(user.getNick());
            holder.rp.setText(String.valueOf(user.getTotalRp()));

            holder.itemView.setOnClickListener(view -> {
                TopPlayersFragmentDirections.ActionTopPlayersToProfile action =
                        TopPlayersFragmentDirections.actionTopPlayersToProfile(user.getId());
                Navigation.findNavController(view).navigate(action);
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


        class MyViewHolder extends RecyclerView.ViewHolder {

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
    }

}