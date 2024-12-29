package com.chagnahnn.spotube.ui.perform;

import androidx.annotation.NonNull;

import com.chagnahnn.spotube.ui.model.MultiMedia;

import java.util.List;

public interface MultiMediaCallback extends SpotubeCallback {
    void onClick(@NonNull List<MultiMedia> multiMediaList, @NonNull MultiMedia currentMultiMedia, int position);

    void onLongClick(@NonNull List<MultiMedia> multiMediaList, @NonNull MultiMedia currentMultiMedia, int position);

    void onMore(@NonNull List<MultiMedia> multiMediaList, @NonNull MultiMedia currentMultiMedia, int position);

    void onRowClearCompleted(boolean isPositionChanged, int fromPosition, int toPosition);
}
