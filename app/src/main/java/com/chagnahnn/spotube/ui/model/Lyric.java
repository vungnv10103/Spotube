package com.chagnahnn.spotube.ui.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

public class Lyric implements Parcelable {
    private String content;
    private String timestamp;


    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Lyric that = (Lyric) obj;
        return TextUtils.equals(content, that.content);
    }

    protected Lyric(@NonNull Parcel in) {
        content = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Lyric> CREATOR = new Creator<>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public Lyric createFromParcel(Parcel in) {
            return new Lyric(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public Lyric[] newArray(int size) {
            return new Lyric[size];
        }
    };


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
