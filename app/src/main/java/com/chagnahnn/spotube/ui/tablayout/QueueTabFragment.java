package com.chagnahnn.spotube.ui.tablayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chagnahnn.spotube.MainActivity;
import com.chagnahnn.spotube.core.SpotubeFragment;
import com.chagnahnn.spotube.databinding.FragmentMediaQueueBinding;

public class QueueTabFragment extends SpotubeFragment {
    public static QueueTabFragment instance;
    private FragmentMediaQueueBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        instance = this;
        mainActivity = (MainActivity) requireActivity();
        binding = FragmentMediaQueueBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnDemo.setOnClickListener(view1 -> {
        });
    }

    public void updateColor() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
