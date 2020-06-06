package com.example.challengeup;

import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private boolean isUserAuthenticated = false;

    public boolean isUserAuthenticated() {
        return isUserAuthenticated;
    }

    public void setUserAuthenticated() {
        isUserAuthenticated = true;
    }
}