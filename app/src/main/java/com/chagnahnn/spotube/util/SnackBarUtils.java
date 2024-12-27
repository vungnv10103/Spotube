package com.chagnahnn.spotube.util;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

@SuppressWarnings("unused")
public class SnackBarUtils {
    public static void show(View view, View anchorView, Object message) {
        showShort(view, anchorView, message);
    }

    public static void show(View view, Object message) {
        showShort(view, null, message);
    }

    public static void showLong(View view, Object message) {
        if (message != null) {
            Snackbar.make(view, message.toString(), Snackbar.LENGTH_LONG).show();
        }
    }

    public static void showShort(View view, View anchorView, Object message) {
        if (message != null) {
            Snackbar snackbar = Snackbar.make(view, message.toString(), Snackbar.LENGTH_SHORT);
            if (anchorView != null) {
                snackbar.setAnchorView(anchorView);
            }
            snackbar.show();
        }
    }


    public static void showOK(View view, Object message) {
        if (message != null) {
            Snackbar.make(view, message.toString(), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Click", v -> System.out.println("Clicked: " + message))
                    .show();
        }
    }

    public static void showOkWithAction(View view, Object message, View.OnClickListener action) {
        if (message != null) {
            Snackbar.make(view, message.toString(), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", action)
                    .show();
        }
    }
}
