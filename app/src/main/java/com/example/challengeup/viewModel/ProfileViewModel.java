package com.example.challengeup.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.challengeup.dto.UserDTO;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<UserDTO> mUser = new MutableLiveData<>();
    private final MutableLiveData<String> mUserAvatar = new MutableLiveData<>();

    public void setUser(UserDTO user) {
        mUser.setValue(user);
    }

    public void setUserAvatar(String avatar) {
        mUserAvatar.setValue(avatar);
    }

    public LiveData<UserDTO> getUser() {
        return mUser;
    }

    public LiveData<String> getAvatar() {
        return mUserAvatar;
    }
}
