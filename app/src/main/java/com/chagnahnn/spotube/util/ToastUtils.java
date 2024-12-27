package com.chagnahnn.spotube.util;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class ToastUtils {
    public static void show(Context context, @NonNull Object message) {
        showShort(context, message);
    }

    public static void showShort(Context context, @NonNull Object message) {
        Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, @NonNull Object message) {
        Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show();
    }
}
