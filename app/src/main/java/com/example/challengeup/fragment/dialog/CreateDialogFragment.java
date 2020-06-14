package com.example.challengeup.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.challengeup.R;

import org.jetbrains.annotations.NotNull;

public class CreateDialogFragment extends DialogFragment {

    public interface CreateDialogListener {
        void onChallengeClick();

        void onPostClick();
    }

    private CreateDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.create_dialog_title)
                .setItems(R.array.createDialog, (dialog, which) -> {
                    if (which == 0) mListener.onChallengeClick();
                    else mListener.onPostClick();
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CreateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement NoticeDialogListener");
        }
    }
}
