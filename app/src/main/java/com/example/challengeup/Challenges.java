package com.example.challengeup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.result.ICallback;
import com.example.challengeup.result.Result;
import com.example.challengeup.viewModel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Challenges extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Challenge> mArrayList = new ArrayList<>();
   // private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    View view;


    MainActivityViewModel mViewModel = new MainActivityViewModel();

    public Challenges() {

    }


    private void getAllChallangesCallback() {
//        ICallback getAllChallengesCallback = result -> {
//            if (result instanceof Result.Success) {
//                //mRecyclerView = view.findViewById(R.id.challenges_list);
//
//                mArrayList = ((Result.Success<ArrayList<Challenge>>) result).data;
//
//                Toast.makeText(
//                        view.getContext(),
//                        mArrayList.size(),
//                        Toast.LENGTH_LONG)
//                        .show();
//
////                mAdapter = new MyAdapter(mArrayList);
////                mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
////
////                mRecyclerView.setItemAnimator( new DefaultItemAnimator());
////                mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
////
////                mRecyclerView.setAdapter(mAdapter);
//
//
//
//            } else {
//                Toast.makeText(
//                        view.getContext(),
//                        "Can't get all challenges from db",
//                        Toast.LENGTH_LONG)
//                        .show();
//            }
//        };
//
//        getAllChallenges(getAllChallengesCallback);
//       // mViewModel.getUserById(user.getUid(), getUserCallback);

        mViewModel.getAllChallenges(result -> {
            if (result instanceof Result.Success) {
                /*List<Challenge> challenges*/ mArrayList = ((Result.Success<List<Challenge>>) result).data;
                Toast.makeText(
                        view.getContext(),
                        "Size: " + mArrayList.size(),
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(
                        view.getContext(),
                        "Can't get challenges",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*View */view = inflater.inflate(R.layout.fragment_challenges, container, false);

        mRecyclerView = view.findViewById(R.id.challenges_list);

        getAllChallangesCallback();

        mAdapter = new MyAdapter(mArrayList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        mRecyclerView.setItemAnimator( new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

//        InitTask initTask=new InitTask();
//        initTask.execute();
//        try {
//            String result = getChallengesTask.execute().get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //mArrayList = Challenge.getAllChallenges();

//        Log.v("Array",new Integer(mArrayList.size()).toString());

//        mAdapter = new MyAdapter(mArrayList);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));


////        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        mRecyclerView.setItemAnimator( new DefaultItemAnimator());
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
//        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Challenge> mDataset = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, avatar;
        ImageView iconSave;
        TextView name, description, dataAccepted, dataCompleted, dataLiked;
        public MyViewHolder(View itemView) {
            super(itemView);
            Log.v("ViewHolder","in View Holder");
            thumbnail = itemView.findViewById(R.id.thumbnail);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            dataAccepted = itemView.findViewById(R.id.dataAccepted);
            dataCompleted = itemView.findViewById(R.id.dataCompleted);
            dataLiked = itemView.findViewById(R.id.dataLiked);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<Challenge> myDataset) {
        if(myDataset!=null)
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        Log.v("CreateViewHolder", "in onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_challenges,parent,false);

        return new MyViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.v("BindViewHolder", "in onBindViewHolder");
        Challenge challenge = mDataset.get(position);
       // holder.thumbnail.setImageBitmap(challenge.//todo image
        holder.avatar.setImageBitmap(User.getUserById(challenge.getCreator_id()).getPhoto());
        holder.name.setText(challenge.getName());
        holder.description.setText(challenge.getTask());//todo show only part of description
        //holder.dataAccepted.setText(challenge.//todo accepted
        //todo completed
       holder.dataLiked.setText(challenge.getLikes());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}