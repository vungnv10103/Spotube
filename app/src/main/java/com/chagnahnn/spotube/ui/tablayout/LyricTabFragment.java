package com.chagnahnn.spotube.ui.tablayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chagnahnn.spotube.core.SpotubeFragment;
import com.chagnahnn.spotube.databinding.FragmentMediaLyricBinding;

public class LyricTabFragment extends SpotubeFragment {
    public static LyricTabFragment instance;
    private FragmentMediaLyricBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        instance = this;
        binding = FragmentMediaLyricBinding.inflate(inflater, container, false);
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
