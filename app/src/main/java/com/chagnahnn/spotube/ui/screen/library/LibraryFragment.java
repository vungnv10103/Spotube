package com.chagnahnn.spotube.ui.screen.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chagnahnn.spotube.databinding.FragmentLibraryBinding;

public class LibraryFragment extends Fragment {
    private FragmentLibraryBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}