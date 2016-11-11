package com.example.android.shoppingapp3.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.example.android.shoppingapp3.R;

/**
 * Created by hernandez on 2/28/2016.
 */
public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.error_ok_button_text)).setMessage("There was an error. Please try again.").setPositiveButton("Ok", null);

        AlertDialog dialog = builder.create();
        return dialog;

    }
}
