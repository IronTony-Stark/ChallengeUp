package com.example.challengeup.utils;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

public class ImageDataBindingAdapter {

    @BindingAdapter("imageUrl")
    public static void setImageResource(ImageView imageView, String url) {
        Glide.with(imageView).load(url).circleCrop().into(imageView);
    }
}
