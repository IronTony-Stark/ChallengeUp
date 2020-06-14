package com.example.challengeup.fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class ChallengeUnconfirmedFragment extends Fragment {

    private ChallengeChallengesViewModel mViewModel;
    private List<VideoConfirmationEntity> mArrayList = new ArrayList<>();
    private Adapter mAdapter;

    private String challengeId;

    public ChallengeUnconfirmedFragment(String challengeId) {
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

                mViewModel.getAllUnconfirmedVideos(challenge, result2 -> {
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

    // DownloadTask for downloding video from URL
    class VideoDownloader extends AsyncTask<String, Void, Void> {

        private String TAG = "VideoDownloader";

        private boolean isExternalStorageReadOnly() {
            String extStorageState = Environment.getExternalStorageState();
            return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
        }

        private boolean isExternalStorageAvailable() {
            String extStorageState = Environment.getExternalStorageState();
            return Environment.MEDIA_MOUNTED.equals(extStorageState);
        }

        private boolean isExternalStorageWritable() {
            String state = Environment.getExternalStorageState();
            return Environment.MEDIA_MOUNTED.equals(state);
        }

        private File getSdcardDir(String fileName) {
            if (!isExternalStorageWritable()) {
                throw new RuntimeException("Unable to access external storage");
            }

            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                Toast.makeText(getContext(), "Cannot write", Toast.LENGTH_SHORT).show();
                throw new RuntimeException("Unable to access external storage");
            } else {
                File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ChallengeUp/Videos/");
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }
                return new File(appDir, fileName);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        void downloadFile(String downloadURL, String fileName) {
//            try {

//                //close the output stream when complete //
//                fileOutput.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            File myExternalFile = getSdcardDir(fileName);
            Log.w("Main", myExternalFile.getAbsolutePath());
            try {
//                FileOutputStream fos = new FileOutputStream(myExternalFile);
//                URL url = new URL(downloadURL);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setDoOutput(true);
//                //connect
//                urlConnection.connect();
//                //Stream used for reading the data from the internet
//                InputStream inputStream = urlConnection.getInputStream();
//                //this is the total size of the file which we are downloading
////                int totalSize = urlConnection.getContentLength();
//
//                //create a buffer...
//                byte[] buffer = new byte[1024];
//                int bufferLength = 0;
////                int downloadedSize = 0;

//                while ((bufferLength = inputStream.read(buffer)) > 0) {
//                    fos.write(buffer, 0, bufferLength);
////                    downloadedSize += bufferLength;
//                }
                Log.w("Main", downloadURL);
                InputStream in = new URL(downloadURL).openStream();
                Files.copy(in, Paths.get(myExternalFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
//                fos.write("Sample text".getBytes());
//                fos.close();
                Log.w("Main", "Saved");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(String... urls) {
            if (urls.length >= 2) {
                downloadFile(urls[0], urls[1]);
            } else {
                Toast.makeText(getContext(), "Download Failed.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_SHORT).show();
        }
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
            holder.video.setVideoURI(Uri.parse(videoConfirmationEntity.getUrl()));

//            holder.video
//            holder.video.setOnPreparedListener(mp -> mp.setLooping(true));
//            holder.video.start();
            AtomicInteger mCurrentPosition = new AtomicInteger();
            holder.video.setOnClickListener(v -> {
                if (holder.video.isPlaying()) {
                    holder.video.pause();
                    mCurrentPosition.set(holder.video.getCurrentPosition());
                } else {
//                    holder.video.requestFocus();
                    holder.video.seekTo(mCurrentPosition.get());
                    holder.video.start();
                }
            });
            // Listener for onPrepared() event (runs after the media is prepared).
            holder.video.setOnPreparedListener(
                    mediaPlayer -> {
//                        mediaPlayer.get
                        // Hide buffering message.
                        holder.buffering.setVisibility(VideoView.INVISIBLE);

                        // Restore saved position, if available.
                        if (mCurrentPosition.get() > 0) {
                            holder.video.seekTo(mCurrentPosition.get());
                        } else {
                            // Skipping to 1 shows the first frame of the video.
                            holder.video.seekTo(1);
                        }

                        // Start playing!
//                        holder.video.start();
//                        mediaPlayer.setLooping(true);
                    });

            // click the video to save it
//            holder.video.setOnClickListener(v -> {
//
//                boolean success = false;
//
//                // make the directory
//                File vidDir = new File(android.os.Environment.getExternalStoragePublicDirectory
//                        (Environment.DIRECTORY_MOVIES) + File.separator + "Saved iCute Videos");
//                vidDir.mkdirs();
//                // create unique identifier
//                Random generator = new Random();
//                int n = 100;
//                n = generator.nextInt(n);
//                // create file name
//                String videoName = "Video_" + n + ".mp4";
//                File fileVideo = new File(vidDir.getAbsolutePath(), videoName);
//
//                try {
//                    fileVideo.createNewFile();
//                    success = true;
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                if (success) {
//                    Toast.makeText(getContext(), "Video saved!",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getContext(),
//                            "Error during video saving", Toast.LENGTH_LONG).show();
//                }
//            });

            // Listener for onCompletion() event (runs after media has finished
            // playing).
            holder.video.setOnCompletionListener(
                    mediaPlayer -> {
                        Toast.makeText(getContext(),
                                "Playback completed",
                                Toast.LENGTH_SHORT).show();
                        // Return the video position to the start.
                        holder.video.seekTo(0);
                    });
//            mViewModel.getUserByID(videoConfirmationEntity.getUser_id(), result -> {
//                if (result instanceof Result.Success) {
//                    UserEntity user = (UserEntity) ((Result.Success) result).data;
//                    if (user != null) {
//                        Glide.with(getContext())
//                                .load(videoConfirmationEntity.getUser_id())
//                                .into(holder.avatar);
//                    }
//                }
//            });
//            holder.denyButton.setVisibility(Button.VISIBLE);

            holder.denyButton.setOnClickListener(v -> Toast.makeText(getContext(), "Denied impl", Toast.LENGTH_SHORT).show());

            holder.confirmButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Confirm impl", Toast.LENGTH_SHORT).show();
                VideoDownloader downloader = new VideoDownloader();
                downloader.execute(videoConfirmationEntity.getUrl(), videoConfirmationEntity.getId());
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
            ImageView avatar;
            Button denyButton, confirmButton;

            //todo video + name, other text?

            public MyViewHolder(View itemView) {
                super(itemView);

                video = itemView.findViewById(R.id.video);
                buffering = itemView.findViewById(R.id.buffering_textview);
                avatar = itemView.findViewById(R.id.avatar);
                denyButton = itemView.findViewById(R.id.btnDeny);
                confirmButton = itemView.findViewById(R.id.btnConfirm);

            }
        }
    }
}

