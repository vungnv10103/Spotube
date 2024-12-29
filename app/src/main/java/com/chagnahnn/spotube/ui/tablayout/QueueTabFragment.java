package com.chagnahnn.spotube.ui.tablayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chagnahnn.spotube.core.SpotubeFragment;
import com.chagnahnn.spotube.databinding.FragmentMediaQueueBinding;

public class QueueTabFragment extends SpotubeFragment {
    public static QueueTabFragment instance;
    private FragmentMediaQueueBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        instance = this;
        binding = FragmentMediaQueueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void updateColor() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
