package com.chagnahnn.spotube.ui.tablayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.chagnahnn.spotube.core.SpotubeFragment;
import com.chagnahnn.spotube.databinding.FragmentMediaRelatedBinding;

public class RelatedTabFragment extends SpotubeFragment {
    private FragmentMediaRelatedBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMediaRelatedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
