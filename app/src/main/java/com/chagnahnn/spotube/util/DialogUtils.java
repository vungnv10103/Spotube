package com.chagnahnn.spotube.util;

import android.content.Context;

import com.chagnahnn.spotube.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DialogUtils {
    public static void startDlgOK(Context context, Object message) {
        if (message != null) {
            new MaterialAlertDialogBuilder(context)
                    .setIcon(R.drawable.app_icon)
                    .setTitle(context.getString(R.string.notification))
                    .setMessage(message.toString())
                    .setPositiveButton(android.R.string.ok, ((dialog, which) -> dialog.dismiss()))
                    .setCancelable(false)
                    .show();
        }
    }
}
