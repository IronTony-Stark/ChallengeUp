package com.example.challengeup;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challengeup.backend.Challenge;
import com.example.challengeup.backend.User;
import com.example.challengeup.result.Result;
import com.example.challengeup.viewModel.ChallengesViewModel;

import java.util.ArrayList;
import java.util.List;

public class Challenges extends Fragment {

    private RecyclerView mRecyclerView;
    private List<Challenge> mArrayList = new ArrayList<>();
   // private LinearLayoutManager mLayoutManager;
    private MyAdapter/*RecyclerView.Adapter*/ mAdapter;

    View view;


    private ChallengesViewModel mViewModel;

    private NavController mNavController;


    public Challenges() {

    }


    private void getAllChallangesCallback() {

        mViewModel.getAllChallenges(result -> {
            if (result instanceof Result.Success) {
                /*List<Challenge> challenges*/mArrayList = ((Result.Success<List<Challenge>>) result).data;
                Toast.makeText(
                        Challenges.this.getActivity(),
                        "Size: " + mArrayList.size(),
                        Toast.LENGTH_LONG)
                        .show();//todo remove toast

                mAdapter.setmDataset(mArrayList);
                mAdapter.notifyItemRangeInserted(0,mArrayList.size());

            } else {
                Toast.makeText(
                        Challenges.this.getActivity(),
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

        //mNavController = Navigation.findNavController(view);//todo navigation


        mRecyclerView = view.findViewById(R.id.challenges_list);
        mViewModel = new ViewModelProvider(this).get(ChallengesViewModel.class);

        getAllChallangesCallback();

        mAdapter = new MyAdapter(mArrayList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        mRecyclerView.setItemAnimator( new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

        return view;
    }




    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<Challenge> mDataset = new ArrayList<>();

        public class MyViewHolder extends RecyclerView.ViewHolder{
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
                    .inflate(R.layout.item_challenge/*fragment_challenges*/,parent,false);

            return new MyViewHolder(itemView);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Log.v("BindViewHolder", "in onBindViewHolder");


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // handle your click here.
                    //mNavController.navigate(R.id.challenge);
                    //todo send challenge id
                } });



            Challenge challenge = mDataset.get(position);
            // holder.thumbnail.setImageBitmap(challenge.//todo image

            mViewModel.getUserById(challenge.getCreator_id(), result -> {
                if (result instanceof Result.Success) {
                    Bitmap avatar = ((Result.Success<User>) result).data.getPhoto();
                    if(avatar!=null)
                        holder.avatar.setImageBitmap(avatar);
                    else{
                        Toast.makeText(Challenges.this.getActivity(), "Avatar is null", Toast.LENGTH_LONG).show();//todo remove toast
                    }
                } else {
                    Toast.makeText(
                            Challenges.this.getActivity(),
                            "Can't get user",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });


            holder.name.setText(challenge.getName());
            holder.description.setText(challenge.getTask());//todo show only part of description
//            holder.dataAccepted.setText(new Long(challenge.numberOfPeopleWhoAccepted()).toString());
//            holder.dataCompleted.setText(new Long(challenge.numberOfPeopleWhoComplete()).toString());

            mViewModel.numberOfPeopleWhoAccepted(challenge, result -> {
                if (result instanceof Result.Success) {
                   holder.dataAccepted.setText(((Result.Success<Long>) result).data.toString());

                } else {
                    Toast.makeText(
                            Challenges.this.getActivity(),
                            "Can't get number of people accepted",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

            mViewModel.numberOfPeopleWhoComplete(challenge, result -> {
                if (result instanceof Result.Success) {
                    holder.dataCompleted.setText(((Result.Success<Long>) result).data.toString());

                } else {
                    Toast.makeText(
                            Challenges.this.getActivity(),
                            "Can't get number of people complete",
                            Toast.LENGTH_LONG)
                            .show();
                }
            });

            holder.dataLiked.setText(new Integer(challenge.getLikes()).toString());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void setmDataset(List<Challenge> arrayList){
            mDataset = arrayList;
        }


    }

}
