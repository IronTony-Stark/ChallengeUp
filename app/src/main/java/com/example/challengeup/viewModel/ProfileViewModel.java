package com.example.challengeup.viewModel;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.challengeup.dto.UserDTO;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<UserDTO> mUser = new MutableLiveData<>();
    private final MutableLiveData<Bitmap> mUserAvatar = new MutableLiveData<>();

    public void setUser(UserDTO user) {
        mUser.setValue(user);
    }

    public void setUserAvatar(Bitmap avatar) {
        mUserAvatar.setValue(avatar);
    }

    public LiveData<UserDTO> getUser() {
        return mUser;
    }

    public LiveData<Bitmap> getAvatar() {
        return mUserAvatar;
    }
}
