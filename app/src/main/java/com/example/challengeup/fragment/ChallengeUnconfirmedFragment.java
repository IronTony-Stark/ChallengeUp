package com.example.challengeup.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.example.challengeup.ILoadable;
import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.backend.VideoConfirmationEntity;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.ChallengeChallengesViewModel;
import com.example.challengeup.viewModel.factory.ChallengeChallengesFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class ChallengeUnconfirmedFragment extends Fragment {

    private ChallengeChallengesViewModel mViewModel;
    private List<VideoConfirmationEntity> mArrayList = new ArrayList<>();
    private Adapter mAdapter;

    private ChallengeEntity challenge;

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

        mAdapter = new Adapter(mArrayList);
        recyclerView.setAdapter(mAdapter);

        ILoadable loadable = (ILoadable) requireActivity();
        loadable.startLoading();

        mViewModel.getAllUnconfirmedVideos(challenge, result2 -> {
            if (result2 instanceof Result.Success) {
                //noinspection unchecked
                mArrayList = ((Result.Success<List<VideoConfirmationEntity>>) result2).data;

                mAdapter.setDataset(mArrayList);
                mAdapter.notifyItemRangeInserted(0, mArrayList.size());
            }
        });

        loadable.finishLoading();
    }

    class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

        private List<VideoConfirmationEntity> mDataset;

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

            //            todo set images + video, add listeners to buttons
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
            float fromScale = 1.35f;
            float toScale = 1f;
            float fromAlpha = 0f;
            float toAlpha = 1f;
            int length = 200;
            holder.video.setOnClickListener(v -> {
                if (holder.video.isPlaying()) {
                    holder.video.pause();
                    holder.play.setVisibility(View.VISIBLE);
//                    ScaleAnimation fade_in =
//                            new ScaleAnimation(fromScale, toScale, fromScale, toScale,
//                                    Animation.RELATIVE_TO_SELF, 0.5f,
//                                    Animation.RELATIVE_TO_SELF, 0.5f);
//                    fade_in.setDuration(length);
//                    fade_in.setFillAfter(true);
//                    AlphaAnimation alphaIn = new AlphaAnimation(fromAlpha, toAlpha);
//                    alphaIn.setDuration(length);
//                    holder.play.startAnimation(fade_in);
//                    holder.play.startAnimation(alphaIn);
                    Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    holder.play.startAnimation(fadeIn);
                    mCurrentPosition.set(holder.video.getCurrentPosition());
                } else {
//                    ScaleAnimation fade_out =
//                            new ScaleAnimation(toScale, fromScale, toScale, fromScale,
//                                    Animation.RELATIVE_TO_SELF, 0.5f,
//                                    Animation.RELATIVE_TO_SELF, 0.5f);
//                    fade_out.setDuration(length);
//                    fade_out.setFillAfter(true);
//                    AlphaAnimation alphaOut = new AlphaAnimation(toAlpha, fromAlpha);
//                    alphaOut.setDuration(length);
//                    holder.play.startAnimation(fade_out);
//                    holder.play.startAnimation(alphaOut);
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

            mViewModel.getUserByID(videoConfirmationEntity.getUser_id(), result -> {
                if (result instanceof Result.Success) {
                    UserEntity user = (UserEntity) ((Result.Success) result).data;
                    if (user != null) {
                        if (user.getId().equals(challenge.getCreator_id())) {
                            holder.denyButton.setEnabled(false);
                            holder.confirmButton.setEnabled(false);
                        }
                        // TODO set User avatar
//                        holder.avatar =
                    }
                }
            });

            holder.denyButton.setOnClickListener(v -> {
                // TODO Reject impl
                Toast.makeText(getContext(), "Denied impl", Toast.LENGTH_SHORT).show();
            });

            holder.confirmButton.setOnClickListener(v -> {
                // TODO Confirm impl
                Toast.makeText(getContext(), "Confirm impl", Toast.LENGTH_SHORT).show();
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

        public class MyViewHolder extends RecyclerView.ViewHolder {

            VideoView video;
            TextView buffering;
            ImageView avatar, play;
            Button denyButton, confirmButton;

            //todo video + name, other text?

            public MyViewHolder(View itemView) {
                super(itemView);

                video = itemView.findViewById(R.id.video);
                buffering = itemView.findViewById(R.id.buffering_textview);
                avatar = itemView.findViewById(R.id.avatar);
                denyButton = itemView.findViewById(R.id.btnDeny);
                confirmButton = itemView.findViewById(R.id.btnConfirm);
                play = itemView.findViewById(R.id.imagePlay);
            }
        }
    }
}

