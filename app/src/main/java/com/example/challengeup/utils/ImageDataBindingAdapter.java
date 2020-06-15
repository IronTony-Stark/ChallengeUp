package com.example.challengeup.utils;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class ImageDataBindingAdapter {

    @BindingAdapter("roundImageUrl")
    public static void roundImageUrl(ImageView imageView, String url) {
        Glide.with(imageView).load(url).circleCrop().into(imageView);
    }

    @BindingAdapter("centerCropImageUrl")
    public static void centerCropImageUrl(ImageView imageView, String url) {
        Glide.with(imageView).load(url).centerCrop().into(imageView);
    }
}
