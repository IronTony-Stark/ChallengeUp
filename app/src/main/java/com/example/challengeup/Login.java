package com.example.challengeup;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Login extends Fragment {

    private NavController mNavController;
    private LoginViewModel mLoginViewModel;

    public Login() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        mLoginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        view.findViewById(R.id.btnLogin).setOnClickListener(v -> {
            mLoginViewModel.setUserAuthenticated();
            mNavController.navigate(LoginDirections.actionLoginToNewsFeed());
        });
    }
}