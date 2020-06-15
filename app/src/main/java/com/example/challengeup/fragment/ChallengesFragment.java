package com.example.challengeup.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.example.challengeup.backend.OrderBy;
import com.example.challengeup.backend.OrderDirection;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.databinding.FragmentChallengesBinding;
import com.example.challengeup.databinding.ItemChallengeBinding;
import com.example.challengeup.dto.ChallengeDTO;
import com.example.challengeup.dto.ChallengeSearchDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengesViewModel;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.ChallengesFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChallengesFragment extends Fragment {

    private FragmentChallengesBinding mBinding;
    private ChallengesViewModel mViewModel;
    private Adapter mAdapter;
    private ILoadable mLoadable;
    private ScaleAnimation mScaleAnimation;
    private List<ChallengeEntity> mArrayList = new ArrayList<>();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_challenges,
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

        mScaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.7f,
                Animation.RELATIVE_TO_SELF, 0.7f);
        mScaleAnimation.setDuration(500);
        mScaleAnimation.setInterpolator(new BounceInterpolator());

        RecyclerView recyclerView = mBinding.challengesList;

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        mAdapter = new Adapter(mArrayList);
        recyclerView.setAdapter(mAdapter);

        mLoadable = (ILoadable) requireActivity();
        mLoadable.startLoading();

        mViewModel.getAllChallenges(result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                mArrayList = ((Result.Success<List<ChallengeEntity>>) result).data;
                mAdapter.setDataset(mArrayList);
            }
            mLoadable.finishLoading();
        });

        setupFilter();
    }

    private void setupFilter() {
        ChipGroup chips = mBinding.searchArea.chipFilter;

        mViewModel.getCategories(result -> {
            if (result instanceof Result.Success) {
                //noinspection unchecked
                List<String> categories = ((Result.Success<List<String>>) result).data;
                if (categories != null) {
                    for (int i = 0; i < categories.size(); i++) {
                        String category = categories.get(i);

                        Chip chip = new Chip(requireContext());
                        chip.setChipDrawable(ChipDrawable.createFromResource(
                                requireContext(), R.xml.item_chip_filter));
                        chip.setCheckable(true);
                        chip.setText(category);

                        chips.addView(chip);
                    }
                }
            }
        });

        EditText queryEdiText = mBinding.searchArea.editTextQuery;

        EditText likedEditText = mBinding.searchArea.editTextLiked;
        EditText acceptedEditText = mBinding.searchArea.editTextAccepted;
        EditText completedEditText = mBinding.searchArea.editTextCompleted;
        EditText rpEditText = mBinding.searchArea.editTextRP;

        Spinner orderBySpinner = mBinding.searchArea.spinnerOrderBy;
        orderBySpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                R.layout.item_spinner, OrderBy.values()));

        Spinner orderDirectionSpinner = mBinding.searchArea.spinnerDirection;
        orderDirectionSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                R.layout.item_spinner, OrderDirection.values()));

        mBinding.searchArea.iconSearch.setOnClickListener(v -> {
            mLoadable.startLoading();

            String query = queryEdiText.getText().toString();

            List<String> categories = chips.getCheckedChipIds()
                    .stream()
                    .map(id -> ((Chip) chips.getChildAt(id - 1)).getText().toString())
                    .collect(Collectors.toList());

            String likedText = likedEditText.getText().toString();
            String acceptedText = acceptedEditText.getText().toString();
            String completedText = completedEditText.getText().toString();
            String rpText = rpEditText.getText().toString();

            Integer liked = likedText.isEmpty() ? null : Integer.valueOf(likedText);
            Integer accepted = acceptedText.isEmpty() ? null : Integer.valueOf(acceptedText);
            Integer completed = completedText.isEmpty() ? null : Integer.valueOf(completedText);
            Integer rp = rpText.isEmpty() ? null : Integer.valueOf(rpText);

            OrderBy orderBy = OrderBy.valueOf(
                    orderBySpinner.getSelectedItem().toString());
            OrderDirection orderDirection = OrderDirection.valueOf(
                    orderDirectionSpinner.getSelectedItem().toString());

            ChallengeSearchDTO challengeSearchDTO = new ChallengeSearchDTO(
                    query, categories, liked, accepted, completed, rp, orderBy, orderDirection
            );

            mViewModel.search(challengeSearchDTO, result -> {
                mLoadable.finishLoading();
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    List<ChallengeEntity> challenges =
                            ((Result.Success<List<ChallengeEntity>>) result).data;
                    mAdapter.setDataset(challenges);
                }
            });
        });
    }

    class Adapter extends RecyclerView.Adapter<Adapter.ChallengeViewHolder> {

        private List<ChallengeEntity> mDataset;
        private TextView mDescription;
        private CompoundButton likedButton;
        private CompoundButton savedButton;

        public Adapter(@NonNull List<ChallengeEntity> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public ChallengeViewHolder onCreateViewHolder(@NotNull ViewGroup parent,
                                                      int viewType) {
            ItemChallengeBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_challenge, parent, false);

            mDescription = binding.description;
            likedButton = binding.buttonLiked;
            savedButton = binding.buttonSave;

            return new ChallengesFragment.Adapter.ChallengeViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NotNull ChallengeViewHolder holder, int position) {
            ChallengeEntity challenge = mDataset.get(position);

            ChallengeDTO challengeDTO = new ChallengeDTO();

            challengeDTO.setName(challenge.getName());
            challengeDTO.setDescription(challenge.getTask());
            challengeDTO.setLiked(String.valueOf(challenge.getLikes()));

            holder.bind(challengeDTO);
            holder.bindThumbnail(challenge.getPhoto());

            mViewModel.getUserById(challenge.getCreator_id(), result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    UserEntity user = ((Result.Success<UserEntity>) result).data;
                    if (user != null) {

                        if (user.getLiked().contains(challenge.getId()))
                            likedButton.setChecked(true);
                        if (user.getSaved().contains(challenge.getId()))
                            savedButton.setChecked(true);

                        if (user.getPhoto() != null)
                            holder.bindAvatar(user.getPhoto());
                        else
                            holder.bindAvatar(MainActivityViewModel.DEFAULT_AVATAR_URL);

                        likedButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                            compoundButton.startAnimation(mScaleAnimation);

                            String prevLiked = challengeDTO.getLiked();
                            if (isChecked)
                                challengeDTO.setLiked(String.valueOf(Integer.parseInt(prevLiked) + 1));
                            else
                                challengeDTO.setLiked(String.valueOf(Integer.parseInt(prevLiked) - 1));
                            holder.bind(challengeDTO);

                            mViewModel.setLiked(user, challenge, isChecked, ignored -> {
                            });
                        });

                        savedButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                            compoundButton.startAnimation(mScaleAnimation);

                            mViewModel.setBookmarked(user, challenge, isChecked, ignored -> {
                            });
                        });

                    }
                }
            });

            mViewModel.getNumAccepted(challenge, result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    Long num = ((Result.Success<Long>) result).data;
                    challengeDTO.setAccepted(String.valueOf(num));
                    holder.bind(challengeDTO);
                }
            });

            mViewModel.getNumCompleted(challenge, result -> {
                if (result instanceof Result.Success) {
                    //noinspection unchecked
                    Long num = ((Result.Success<Long>) result).data;
                    challengeDTO.setCompleted(String.valueOf(num));
                    holder.bind(challengeDTO);
                }
            });

            HashTagHelper mTextHashTagHelper = HashTagHelper.Creator
                    .create(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            hashTag -> Toast.makeText(getContext(), hashTag, Toast.LENGTH_SHORT)
                                    .show(), '_'
                    );
            mTextHashTagHelper.handle(mDescription);

            holder.itemView.setOnClickListener(view -> {
                ChallengesFragmentDirections.ActionChallengesToChallenge action =
                        ChallengesFragmentDirections.actionChallengesToChallenge(challenge.getId());
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

        public class ChallengeViewHolder extends RecyclerView.ViewHolder {

            private final ItemChallengeBinding mBinding;

            public ChallengeViewHolder(ItemChallengeBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void bind(ChallengeDTO challengeDTO) {
                mBinding.setChallenge(challengeDTO);
                mBinding.executePendingBindings();
            }

            public void bindThumbnail(String url) {
                mBinding.setThumbnail(url);
                mBinding.executePendingBindings();
            }

            public void bindAvatar(String url) {
                mBinding.setAvatar(url);
                mBinding.executePendingBindings();
            }
        }
    }
}
