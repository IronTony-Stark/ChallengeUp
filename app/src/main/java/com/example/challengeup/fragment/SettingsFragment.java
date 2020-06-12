package com.example.challengeup.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.challengeup.R;
import com.example.challengeup.utils.LoginUtils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment {

    private StorageReference mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStorage = FirebaseStorage.getInstance().getReference();

        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            LoginUtils activity = new LoginUtils(getActivity());
            activity.signOut();
            activity.createSignInIntent();
        });

        Button loadImageBtn = view.findViewById(R.id.btnLoadImage);
        loadImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivityForResult(intent, GET_IMAGE_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GET_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            Uri photoUri = data.getData();
            Toast.makeText(getContext(), photoUri.toString(), Toast.LENGTH_SHORT).show();
            uploadImage(photoUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            StorageReference file = mStorage.child("testImageLoadThisMustBeUserOrChallengeId");
            file.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> Toast.makeText(getContext(),
                            "Uploaded Successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(),
                            "Upload Failed", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private static final int GET_IMAGE_REQUEST = 1;
}