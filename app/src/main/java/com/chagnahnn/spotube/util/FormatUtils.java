package com.chagnahnn.spotube.util;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.Player;

import com.chagnahnn.spotube.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;

@SuppressWarnings({"unused"})
public class FormatUtils {
    public static int px2Dp(@NonNull Context context, float px) {
        return Math.round(px / context.getResources().getDisplayMetrics().density);
    }

    public static int dp2Px(@NonNull Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    public static String compareTime(Context context, String beginTime, boolean isShortFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date createdDate = dateFormat.parse(beginTime);
            if (createdDate == null) {
                return beginTime;
            }
            Date nowDate = new Date();
            long timeBetween = Math.abs(createdDate.getTime() - nowDate.getTime());
            long year = timeBetween / (12L * 30 * 24 * 60 * 60 * 1000);
            long month = timeBetween / (30L * 24 * 60 * 60 * 1000);
            long day = timeBetween / (24 * 60 * 60 * 1000);
            long hour = timeBetween / (60 * 60 * 1000);
            long minutes = timeBetween / (60 * 1000);
            long second = timeBetween / 1000;
            if (year >= 1) {
                if (isShortFormat) {
                    return year + " " + context.getString(R.string.des_short_year_time);
                }
                return year + " " + context.getString(R.string.des_long_year_time);
            } else if (month >= 1) {
                if (isShortFormat) {
                    return month + " " + context.getString(R.string.des_short_month_time);
                }
                return month + " " + context.getString(R.string.des_long_month_time);
            } else if (day >= 1) {
                if (isShortFormat) {
                    return day + " " + context.getString(R.string.des_short_day_time);
                }
                return day + " " + context.getString(R.string.des_long_day_time);
            } else if (hour >= 1) {
                if (isShortFormat) {
                    return hour + " " + context.getString(R.string.des_short_hour_time);
                }
                return hour + " " + context.getString(R.string.des_long_hour_time);
            } else if (minutes >= 1) {
                if (isShortFormat) {
                    return minutes + " " + context.getString(R.string.des_short_minute_time);
                }
                return minutes + " " + context.getString(R.string.des_long_minute_time);
            } else {
                if (isShortFormat) {
                    return second + " " + context.getString(R.string.des_short_second_time);
                }
                return second + " " + context.getString(R.string.des_long_second_time);
            }
        } catch (ParseException e) {
            LogUtils.logE("compareTime: " + e.getMessage(), e);
            return beginTime;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDate(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = inputFormat.parse(timestamp);
            if (date == null) return timestamp;
            return outputFormat.format(date);
        } catch (ParseException e) {
            LogUtils.logE(e.getMessage(), e);
            return timestamp;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatTime(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");

        try {
            Date date = inputFormat.parse(timestamp);
            if (date == null) return timestamp;
            return outputFormat.format(date);
        } catch (ParseException e) {
            LogUtils.logE(e.getMessage(), e);
            return timestamp;
        }
    }

    @NonNull
    public static String formatCount(Context context, long count) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String result;
        if (count >= 1_000_000_000) {
            double billions = count / 1_000_000_000.0;
            result = context.getString(R.string.views_billion, decimalFormat.format(billions));
        } else if (count >= 1_000_000) {
            double millions = count / 1_000_000.0;
            result = context.getString(R.string.views_million, decimalFormat.format(millions));
        } else if (count >= 1_000) {
            double thousands = count / 1_000.0;
            result = context.getString(R.string.views_thousand, decimalFormat.format(thousands));
        } else {
            result = context.getString(R.string.views, count);
        }
        return result;
    }

    @NonNull
    public static String formatViews(Context context, long views) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String result;
        if (views >= 1_000_000_000) {
            double billions = views / 1_000_000_000.0;
            result = context.getString(R.string.views_billion, decimalFormat.format(billions));
        } else if (views >= 1_000_000) {
            double millions = views / 1_000_000.0;
            result = context.getString(R.string.views_million, decimalFormat.format(millions));
        } else if (views >= 1_000) {
            double thousands = views / 1_000.0;
            result = context.getString(R.string.views_thousand, decimalFormat.format(thousands));
        } else {
            result = context.getString(R.string.views, views);
        }
        result += " " + context.getString(R.string.views_desc);
        return result;
    }

    @NonNull
    public static String stringForTime(long timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = (int) (timeMs / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    public static String getTimeString(int seconds) {
        if (seconds <= 0) return "00:00";
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    @Nullable
    public static String getStateName(int i) {
        switch (i) {
            case Player.STATE_IDLE:
                return "STATE_IDLE";
            case Player.STATE_BUFFERING:
                return "STATE_BUFFERING";
            case Player.STATE_READY:
                return "STATE_READY";
            case Player.STATE_ENDED:
                return "STATE_ENDED";
            default:
                return null;
        }
    }

    public static int getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
}