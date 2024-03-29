package com.example.challengeup.fragment;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.databinding.ConfirmationBinding;
import com.example.challengeup.dto.UserDTO;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengeChallengesViewModel;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.ChallengeChallengesFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;


public class ChallengeUnconfirmedFragment extends Fragment {

    private final ChallengeEntity challenge;
    //    private ConfirmationBinding mBinding;
    private ChallengeChallengesViewModel mViewModel;
    private List<VideoConfirmationEntity> mData = new ArrayList<>();
    private Adapter mAdapter;

    public ChallengeUnconfirmedFragment(ChallengeEntity challenge) {
        this.challenge = challenge;
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

        mAdapter = new Adapter(mData);
        recyclerView.setAdapter(mAdapter);

        ILoadable loadable = (ILoadable) requireActivity();
        loadable.startLoading();

        mViewModel.getAllUnconfirmedVideos(challenge, result2 -> {
            if (result2 instanceof Result.Success) {
                //noinspection unchecked
                mData = ((Result.Success<List<VideoConfirmationEntity>>) result2).data;

                mAdapter.setDataset(mData);
                mAdapter.notifyItemRangeInserted(0, mData.size());
            }
        });

        loadable.finishLoading();
    }

    class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<VideoConfirmationEntity> mDataset;
//        private ConfirmationBinding binding;

        public Adapter(@NonNull List<VideoConfirmationEntity> myDataset) {
            mDataset = myDataset;
        }

        @NotNull
        @Override
        public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ConfirmationBinding binding = DataBindingUtil.inflate(inflater, R.layout.confirmation, parent, false);
            return new MyViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NotNull Adapter.MyViewHolder holder, int position) {
            VideoConfirmationEntity videoConfirmationEntity = mDataset.get(position);

            Log.w("URL", videoConfirmationEntity.getUrl());
            File file = mViewModel.getSdcardFileForVideo(videoConfirmationEntity.getId());
            if (file.exists()) {
                holder.video.setVideoPath(file.getPath());
            } else {
                holder.buffering.setVisibility(VideoView.VISIBLE);
                mViewModel.loadVideoToCash(videoConfirmationEntity.getUrl(), file.getAbsolutePath(), result -> {
                    if (result instanceof Result.Success) {
                        holder.video.setVideoPath(file.getPath());
                    } else {
                        holder.buffering.setText(R.string.downloadingError);
                    }
                });
            }
            AtomicInteger mCurrentPosition = new AtomicInteger();
            holder.video.setOnClickListener(v -> {
                if (holder.video.isPlaying()) {
                    holder.video.pause();
                    holder.play.setVisibility(View.VISIBLE);
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    holder.play.startAnimation(fadeIn);
                    mCurrentPosition.set(holder.video.getCurrentPosition());
                } else {
                    Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                    holder.play.startAnimation(fadeOut);
                    holder.play.setVisibility(View.INVISIBLE);
                    holder.video.seekTo(mCurrentPosition.get());
                    holder.video.start();
                }
            });
            // Listener for onPrepared() event (runs after the media is prepared).
            holder.video.setOnPreparedListener(
                    mediaPlayer -> {
                        // Hide buffering message.
                        holder.buffering.setVisibility(VideoView.INVISIBLE);
                        holder.play.setVisibility(View.INVISIBLE);
                        // Restore saved position, if available.
                        if (mCurrentPosition.get() > 0) {
                            holder.video.seekTo(mCurrentPosition.get());
                        } else {
                            // Skipping to 1 shows the first frame of the video.
                            holder.video.seekTo(1);
                        }
                        // Start playing!
                        holder.video.start();
                        mediaPlayer.setLooping(true);
                    });

            // Listener for onCompletion() event (runs after media has finished playing).
            holder.video.setOnCompletionListener(
                    mediaPlayer -> {
                        holder.video.seekTo(0);
                    });

            MainActivityViewModel mMainActivityViewModel = new ViewModelProvider(requireActivity())
                    .get(MainActivityViewModel.class);
            AtomicReference<UserDTO> currentUser = new AtomicReference<>(mMainActivityViewModel.getUser().getValue());
            if (currentUser.get() != null) {
                if (!currentUser.get().getId().equals(videoConfirmationEntity.getUser_id())) {
                    boolean isConfirmed = false;
                    for (String userId : videoConfirmationEntity.getUsersWhoConfirmedOrDenied())
                        if (userId.equals(currentUser.get().getId())) {
                            isConfirmed = true;
                            break;
                        }
                    if (!isConfirmed) {
                        holder.confirmButton.setVisibility(View.VISIBLE);
                        holder.denyButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            mViewModel.getUserByID(videoConfirmationEntity.getUser_id(), result -> {
                UserEntity user = (UserEntity) ((Result.Success) result).data;
                String name;
                if (user != null) {
                    name = user.getNick();
                    holder.binding.setUserName("@" + user.getTag());
                    String photoUrl = user.getPhoto();
                    holder.binding.setUserAvatar(photoUrl != null ? photoUrl : MainActivityViewModel.DEFAULT_AVATAR_URL);
                } else {
                    name = "Anonym";
                    holder.binding.setUserName("Anonym");
                    holder.binding.setUserAvatar(MainActivityViewModel.DEFAULT_AVATAR_URL);
                }
                holder.avatar.setOnClickListener(v ->
                {
                    holder.avatar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
                    Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
                });
            });

            holder.confirmButton.setOnClickListener(v -> {
                holder.confirmButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
                setConfirmationButtonsInactive(holder);
                mViewModel.sendConfirmation(videoConfirmationEntity, currentUser.get().getId(), result1 -> {
                    if ((int) ((Result.Success) result1).data == 0) {
                        Toast.makeText(getContext(), R.string.challenge_is_completed, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), R.string.confirmed, Toast.LENGTH_SHORT).show();
                    }
                });
            });

            holder.denyButton.setOnClickListener(v -> {
                holder.denyButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
                setConfirmationButtonsInactive(holder);
                mViewModel.sendRejection(videoConfirmationEntity, currentUser.get().getId(), result1 -> {
                    if ((int) ((Result.Success) result1).data == 0) {
                        Toast.makeText(getContext(), R.string.totallyRejected, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), R.string.rejected, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        private void setConfirmationButtonsInactive(MyViewHolder holder) {
            holder.denyButton.setEnabled(false);
            holder.denyButton.setImageIcon(Icon.createWithResource(getContext(), R.drawable.undone_disabled));
            holder.confirmButton.setEnabled(false);
            holder.confirmButton.setImageIcon(Icon.createWithResource(getContext(), R.drawable.done_disabled));
        }

        private void setConfirmationButtonsActive(MyViewHolder holder) {
            holder.denyButton.setEnabled(true);
            holder.denyButton.setImageIcon(Icon.createWithResource(getContext(), R.drawable.done_outline));
            holder.confirmButton.setEnabled(true);
            holder.confirmButton.setImageIcon(Icon.createWithResource(getContext(), R.drawable.undone));
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void setDataset(List<VideoConfirmationEntity> newDataset) {
            mDataset = newDataset;
            notifyDataSetChanged();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            VideoView video;
            TextView buffering, username;
            ImageView avatar, play;
            ImageView denyButton, confirmButton;

            ConfirmationBinding binding;

            public MyViewHolder(ConfirmationBinding binding) {
                super(binding.getRoot());

                View itemView = binding.getRoot();
                this.binding = binding;

                video = itemView.findViewById(R.id.video);
                buffering = itemView.findViewById(R.id.buffering_textview);
                avatar = itemView.findViewById(R.id.avatar);
                username = itemView.findViewById(R.id.username);
                denyButton = itemView.findViewById(R.id.btnDeny);
                confirmButton = itemView.findViewById(R.id.btnConfirm);
                play = itemView.findViewById(R.id.imagePlay);
            }
        }
    }
}

