package com.chagnahnn.spotube.ui.screen.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.chagnahnn.spotube.MainActivity;
import com.chagnahnn.spotube.R;
import com.chagnahnn.spotube.core.SpotubeFragment;
import com.chagnahnn.spotube.databinding.FragmentHomeBinding;
import com.chagnahnn.spotube.ui.adapter.MultiMediaAdapter;
import com.chagnahnn.spotube.ui.model.MultiMedia;
import com.chagnahnn.spotube.ui.perform.MultiMediaCallback;
import com.chagnahnn.spotube.util.FileUtil;
import com.chagnahnn.spotube.viewmodel.HomeViewModel;

import java.util.List;

public class HomeFragment extends SpotubeFragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    private MainActivity mainActivity;

    private MultiMediaAdapter multiMediaAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) requireActivity();
        initView();
        return binding.getRoot();
    }

    private void initView() {
        disableOverscroll(binding.rcvMultiMedia);
        multiMediaAdapter = new MultiMediaAdapter(requireContext(), R.layout.item_media_small_collapse, new MultiMediaCallback() {
            @Override
            public void onClick(@NonNull List<MultiMedia> multiMediaList, @NonNull MultiMedia currentMultiMedia, int position) {
                mainActivity.createMediaItemList(multiMediaList, currentMultiMedia);
            }

            @Override
            public void onLongClick(@NonNull List<MultiMedia> multiMediaList, @NonNull MultiMedia currentMultiMedia, int position) {

            }

            @Override
            public void onMore(@NonNull List<MultiMedia> multiMediaList, @NonNull MultiMedia currentMultiMedia, int position) {

            }

            @Override
            public void onRowClearCompleted(boolean isPositionChanged, int fromPosition, int toPosition) {

            }
        });

        binding.rcvMultiMedia.setAdapter(multiMediaAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<MultiMedia> multiMedias = FileUtil.parseMultiMediaList(requireContext(), R.raw.dump_data);
        multiMediaAdapter.setItemList(multiMedias);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        homeViewModel.clear();
        binding = null;
    }
}