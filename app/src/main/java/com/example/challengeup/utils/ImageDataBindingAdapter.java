package com.example.challengeup.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

public class ImageDataBindingAdapter {

    @BindingAdapter("imageBitmap")
    public static void setImageResource(ImageView imageView, Bitmap resource) {
        imageView.setImageBitmap(resource);
    }
}
