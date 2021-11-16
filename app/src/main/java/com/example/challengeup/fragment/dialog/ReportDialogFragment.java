package com.example.challengeup.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.challengeup.R;

public class ReportDialogFragment extends DialogFragment {

    public interface ReportDialogListener {
        void onReportClick();
    }

    private final ReportDialogListener mListener;

    public ReportDialogFragment(ReportDialogListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.report_dialog_title)
                .setItems(R.array.reportDialog, (dialog, which) -> {
                    if (which == 0)
                        mListener.onReportClick();
                });
        return builder.create();
    }
}
