package com.example.challengeup.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.challengeup.ApplicationContainer;
import com.example.challengeup.Container;
import com.example.challengeup.R;
import com.example.challengeup.backend.ChallengeEntity;
import com.example.challengeup.backend.UserEntity;
import com.example.challengeup.request.Result;
import com.example.challengeup.viewModel.CreateChallengeViewModel;
import com.example.challengeup.viewModel.MainActivityViewModel;
import com.example.challengeup.viewModel.factory.CreateChallengeFactory;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.util.ArrayList;

public class CreateChallengeFragment extends Fragment {

    private View view;
    private CreateChallengeViewModel mViewModel;
    private MainActivityViewModel mainViewModel;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_create_challenge, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Container appContainer = ((ApplicationContainer) requireActivity().getApplication()).mContainer;
        mViewModel = new ViewModelProvider(this, new CreateChallengeFactory(
                appContainer.mRequestExecutor
        )).get(CreateChallengeViewModel.class);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        userId = mainViewModel.getUser().getValue().getId();

        View viewIncluded = view.findViewById(R.id.include);

        EditText inputName = view.findViewById(R.id.inputName);
        EditText inputDescription = view.findViewById(R.id.inputDescription);
        Button loadImageButton = view.findViewById(R.id.btnLoadImage);
        Button createButton = view.findViewById(R.id.btnCreate);

        ImageView thumbnail = viewIncluded.findViewById(R.id.thumbnail);
        ImageView avatar = viewIncluded.findViewById(R.id.avatar);
        TextView name = viewIncluded.findViewById(R.id.name);
        TextView description = viewIncluded.findViewById(R.id.description);

        TextView dataAccepted = viewIncluded.findViewById(R.id.dataAccepted);
        TextView dataCompleted = viewIncluded.findViewById(R.id.dataCompleted);
        TextView dataLiked = viewIncluded.findViewById(R.id.dataLiked);

        mViewModel.getUserById(userId, result -> {
            if(result instanceof Result.Success){
                UserEntity user = ((Result.Success<UserEntity>) result).data;
                //todo set avatar

            }

        });

        dataAccepted.setText("0");
        dataCompleted.setText("0");
        dataLiked.setText("0");

        HashTagHelper mHashTagHelper = HashTagHelper.Creator.create(ContextCompat.getColor(getContext(), R.color.colorPrimary), null, '_');
        mHashTagHelper.handle(description);

        HashTagHelper mHashTagHelperInput = HashTagHelper.Creator.create(ContextCompat.getColor(getContext(), R.color.colorPrimary), null, '_');
        mHashTagHelperInput.handle(inputDescription);

        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name.setText(inputName.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                description.setText(inputDescription.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        loadImageButton.setOnClickListener();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ChallengeEntity challenge = new ChallengeEntity(inputName.getText().toString(), inputDescription.getText().toString(), userId,
                                                                (ArrayList)mHashTagHelperInput.getAllHashTags(), new ArrayList<>());
                //todo add categories

                Toast.makeText(getContext(), "Made",Toast.LENGTH_LONG).show();

                //todo loader

                    mViewModel.addChallenge(challenge, result -> {

                        if (result instanceof Result.Success) {

                            Toast.makeText(getContext(), getResources().getString(R.string.challenge_created),Toast.LENGTH_LONG).show();
                            //todo navigate back

                        }
                        else{
                            Toast.makeText(getContext(),((Result.Error) result).exception.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                        }
                    });

            }
        });

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.addMenuItem).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
