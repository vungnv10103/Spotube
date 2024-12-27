package com.chagnahnn.spotube.util;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.R;

@SuppressWarnings({"unused"})
public class ThemeUtils {

    @NonNull
    public static GradientDrawable getDrawableTabIndicationNone(Context context) {
        return getDrawableFromAttr(context, R.attr.colorControlNormal);
    }

    @NonNull
    private static GradientDrawable getDrawableFromAttr(@NonNull Context context, int attr) {
        GradientDrawable indicatorDrawable = new GradientDrawable();
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        indicatorDrawable.setColor(ContextCompat.getColor(context, typedValue.resourceId));
        indicatorDrawable.setSize(0, 0);
        return indicatorDrawable;
    }

    public static int getColor(Context context, int color) {
        return ContextCompat.getColor(context, color);
    }

    public static int getColorSecondary(Context context) {
        return getColorFromAttr(context, R.attr.colorSecondary);
    }

    public static int getColorTertiary(Context context) {
        return getColorFromAttr(context, R.attr.colorTertiary);
    }

    public static int getColorPrimary(Context context) {
        return getColorFromAttr(context, R.attr.colorPrimary);
    }

    public static int getColorSurfaceContainer(Context context) {
        return getColorFromAttr(context, R.attr.colorSurfaceContainer);
    }

    public static int getColorSurface(Context context) {
        return getColorFromAttr(context, R.attr.colorSurface);
    }

    public static int getColorSurfaceVariant(Context context) {
        return getColorFromAttr(context, R.attr.colorSurfaceVariant);
    }


    @ColorInt
    private static int getColorFromAttr(@NonNull Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}
