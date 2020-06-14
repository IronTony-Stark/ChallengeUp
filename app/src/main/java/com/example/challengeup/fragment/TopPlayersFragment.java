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
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.databinding.ItemTopPlayersBinding;
import com.example.challengeup.dto.TopPlayerDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.TopPlayersViewModel;
import com.example.challengeup.viewModel.factory.TopPlayersFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopPlayersFragment extends Fragment {

    private List<UserEntity> mArrayList = new ArrayList<>();
    private Adapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_players, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        TopPlayersViewModel viewModel = new ViewModelProvider(this, new TopPlayersFactory(
                appContainer.mRequestExecutor
        )).get(TopPlayersViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.top_players_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        mAdapter = new Adapter(mArrayList);
        recyclerView.setAdapter(mAdapter);

        ILoadable loadable = (ILoadable) requireActivity();
        loadable.startLoading();

        viewModel.getAllUsers(result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                mArrayList = ((Result.Success<List<UserEntity>>) result).data;

                // TODO sometimes values are null
                Collections.sort(mArrayList, (u1, u2) -> {
                    if (u1 == null && u2 == null)
                        return 0;
                    else if (u1 == null)
                        return 1;
                    else if (u2 == null)
                        return -1;
                    else return -Integer.compare(u1.getTotalRp(), u2.getTotalRp());
                });

                mAdapter.setDataset(mArrayList);
                mAdapter.notifyItemRangeInserted(0, mArrayList.size());
                loadable.finishLoading();
            }
        });
    }

    static class Adapter extends RecyclerView.Adapter<Adapter.PlayerViewHolder> {

        private List<UserEntity> mDataset;

        public Adapter(@NonNull List<UserEntity> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public PlayerViewHolder onCreateViewHolder(@NotNull ViewGroup parent,
                                                   int viewType) {
            ItemTopPlayersBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_top_players, parent, false);

            return new PlayerViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NotNull PlayerViewHolder holder, int position) {
            UserEntity user = mDataset.get(position);

            String rank = String.valueOf(position + 1);
            String userPhoto = user.getPhoto() != null ?
                    user.getPhoto() : MainActivityViewModel.DEFAULT_AVATAR_URL;
            String name = user.getNick();
            String rp = String.valueOf(user.getTotalRp());

            TopPlayerDTO topPlayer = new TopPlayerDTO(rank, userPhoto, name, rp);
            holder.bind(topPlayer);

            holder.itemView.setOnClickListener(view -> {
                TopPlayersFragmentDirections.ActionTopPlayersToProfile action =
                        TopPlayersFragmentDirections.actionTopPlayersToProfile();
                action.setUid(user.getId());
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

        static class PlayerViewHolder extends RecyclerView.ViewHolder {

            private final ItemTopPlayersBinding mBinding;

            public PlayerViewHolder(ItemTopPlayersBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void bind(TopPlayerDTO topPlayerDTO) {
                mBinding.setPlayer(topPlayerDTO);
                mBinding.executePendingBindings();
            }
        }
    }
}
