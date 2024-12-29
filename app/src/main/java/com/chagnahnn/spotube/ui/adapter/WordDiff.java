package com.chagnahnn.spotube.ui.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.chagnahnn.spotube.data.local.entities.Word;

public class WordDiff extends DiffUtil.ItemCallback<Word> {

    @Override
    public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
        return oldItem == newItem;
    }

    @Override
    public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
        return oldItem.getWord().equals(newItem.getWord());
    }
}
