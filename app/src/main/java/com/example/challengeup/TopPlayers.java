package com.example.challengeup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.challengeup.backend.User;
import com.example.challengeup.result.Result;
import com.example.challengeup.viewModel.TopPlayersViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TopPlayers extends Fragment {

    private RecyclerView mRecyclerView;
    private List<User> mArrayList = new ArrayList<>();
    private MyAdapter mAdapter;

    View view;

    private TopPlayersViewModel mViewModel;//todo???? create topchallengesviewmodel

    public TopPlayers(){}

    private void getAllUsersCallback() {

        mViewModel.getAllUsers(result -> {
            if (result instanceof Result.Success) {
                mArrayList = ((Result.Success<List<User>>) result).data;
                Toast.makeText(
                        TopPlayers.this.getActivity(),
                        "Size: " + mArrayList.size(),
                        Toast.LENGTH_LONG)
                        .show();//todo remove toast

                //sorting arraylist
                Collections.sort(mArrayList, new Comparator<User>() {
                    @Override
                    public int compare(User u1, User u2) {
                        if (u1.getTotalRp()==u2.getTotalRp())
                            return 0;
                        if (u1.getTotalRp()>u2.getTotalRp())
                            return 1;

                        return -1;
                    }
                });

                mAdapter.setmDataset(mArrayList);
                mAdapter.notifyItemRangeInserted(0,mArrayList.size());

            } else {
                Toast.makeText(
                        TopPlayers.this.getActivity(),
                        "Can't get players",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_top_players, container, false);
        mRecyclerView = view.findViewById(R.id.top_players_list);

        mViewModel = new ViewModelProvider(this).get(TopPlayersViewModel.class);

        getAllUsersCallback();

        mAdapter = new MyAdapter(mArrayList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        mRecyclerView.setItemAnimator( new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);


        return view;
    }



    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private List<com.example.challengeup.backend.User> mDataset = new ArrayList<>();

        public class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView avatar;
            TextView rank, name, rp;
            public MyViewHolder(View itemView) {
                super(itemView);
                Log.v("ViewHolder","in View Holder");
                avatar = itemView.findViewById(R.id.avatar);
                rank = itemView.findViewById(R.id.rank);
                name = itemView.findViewById(R.id.name);
                rp = itemView.findViewById(R.id.rp);
            }

        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(List<com.example.challengeup.backend.User> myDataset) {
            if(myDataset!=null)
                mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            Log.v("CreateViewHolder", "in onCreateViewHolder");
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_top_players,parent,false);

            return new MyAdapter.MyViewHolder(itemView);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            Log.v("BindViewHolder", "in onBindViewHolder");


            User user = mDataset.get(position);

            Bitmap avatar = user.getPhoto();
            if(avatar!=null)
                holder.avatar.setImageBitmap(avatar);
            else{
                Toast.makeText(TopPlayers.this.getActivity(), "Avatar is null", Toast.LENGTH_LONG).show();//todo remove toast
            }


            holder.rank.setText(new Integer(position+1).toString());
            holder.name.setText(user.getNick());
            holder.rp.setText(new Integer(user.getTotalRp()).toString());

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void setmDataset(List<User> arrayList){
            mDataset = arrayList;
        }


    }
}


/*




}

 */