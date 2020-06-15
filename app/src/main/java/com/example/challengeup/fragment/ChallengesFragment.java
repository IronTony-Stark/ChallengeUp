package com.example.challengeup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengesViewModel;
import com.example.challengeup.viewModel.factory.ChallengesFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChallengesFragment extends Fragment {

    private ChallengesViewModel mViewModel;
    private List<ChallengeEntity> mArrayList = new ArrayList<>();
    private Adapter mAdapter;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_challenges, container, false);
        return view;
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

        ILoadable loadable = (ILoadable) requireActivity();
        loadable.startLoading();

        mViewModel.getAllChallenges(result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                mArrayList = ((Result.Success<List<ChallengeEntity>>) result).data;
                mAdapter.setDataset(mArrayList);
                mAdapter.notifyItemRangeInserted(0, mArrayList.size());
            }
            loadable.finishLoading();
        });

    }

    class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<ChallengeEntity> mDataset;

        public Adapter(@NonNull List<ChallengeEntity> myDataset) {
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
            ChallengeEntity challenge = mDataset.get(position);

            // holder.thumbnail.setImageBitmap(challenge.// todo bookmarkChacked

            ViewGroup chips = view.findViewById(R.id.searchArea).findViewById(R.id.chipFilter);

            mViewModel.getCategories(result -> {
                if (result instanceof Result.Success) {
                    ArrayList<String> categories = (ArrayList<String>) ((Result.Success) result).data;
                    if (categories != null) {
                        for (int i = 0; i < categories.size(); i++) {
                            String category = categories.get(i);
                            Chip chip = new Chip(getContext());
                            chip.setChipDrawable(ChipDrawable.createFromResource(getContext(), R.xml.item_chip_filter));
                            chip.setText(category);
                            chip.setOnClickListener(v -> {
                                //TODO Implement filtering logic hire
                                Toast.makeText(getContext(), chip.getText(), Toast.LENGTH_LONG).show();
                            });
                            chips.addView(chip);
//                            chips.add
                        }
                    }
                }
            });

            holder.name.setText(challenge.getName());
            holder.description.setText(challenge.getTask());
            holder.dataLiked.setText(String.valueOf(challenge.getLikes()));

            HashTagHelper mTextHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(getContext(), R.color.colorPrimary),
                    hashTag -> {
                        Toast.makeText(getContext(), hashTag, Toast.LENGTH_SHORT).show();
                    }, '_');
            mTextHashTagHelper.handle(holder.description);

            holder.itemView.setOnClickListener(view -> {
                ChallengesFragmentDirections.ActionChallengesToChallenge action =
                        ChallengesFragmentDirections.actionChallengesToChallenge(challenge.getId());
                Navigation.findNavController(view).navigate(action);
            });

            ScaleAnimation scaleAnimation =
                    new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.7f,
                            Animation.RELATIVE_TO_SELF, 0.7f);
            scaleAnimation.setDuration(500);
            BounceInterpolator bounceInterpolator = new BounceInterpolator();
            scaleAnimation.setInterpolator(bounceInterpolator);

            CompoundButton likedButton =
                    holder.itemView.findViewById(R.id.button_liked);
            CompoundButton savedButton =
                    holder.itemView.findViewById(R.id.buttonSave);

            mViewModel.getUserByEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail(), userResult -> {
                if (userResult instanceof Result.Success) {
                    UserEntity user = ((Result.Success<UserEntity>) userResult).data;
                    if (user != null) {
                        try {
                            if (user.getLiked().contains(challenge.getId())) {
//                            likedButton.setChecked(true);
                                likedButton.setChecked(true);
                            }
                        } finally {
                            likedButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                                //animation
                                compoundButton.startAnimation(scaleAnimation);
                                if (isChecked) {
                                    holder.dataLiked.setText(String.valueOf(Integer.parseInt(holder.dataLiked.getText().toString()) + 1));
                                } else {
                                    holder.dataLiked.setText(String.valueOf(Integer.parseInt(holder.dataLiked.getText().toString()) - 1));
                                }
                                mViewModel.setLiked(user, challenge, isChecked, r -> {
                                });
                            });
                        }
                        try {
                            if (user.getSaved().contains(challenge.getId())) {
                                savedButton.setChecked(true);
                            }
                        } finally {
                            savedButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                                //animation
                                compoundButton.startAnimation(scaleAnimation);
                                mViewModel.setBookmarked(user, challenge, isChecked, r -> {
                                });
                            });
                        }
                    }
                }
            });

            mViewModel.getUserById(challenge.getCreator_id(), result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    UserEntity user = ((Result.Success<UserEntity>) result).data;
                    if (user != null) {

//                        Bitmap avatar = user.getPhoto();
//                        if (avatar != null)
//                            holder.avatar.setImageBitmap(avatar);
                    }
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

        public void setDataset(List<ChallengeEntity> newDataset) {
            mDataset = newDataset;
            notifyDataSetChanged();
        }

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
    }
}
