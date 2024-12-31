package com.chagnahnn.spotube.ui.screen.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chagnahnn.spotube.data.local.entities.Word;
import com.chagnahnn.spotube.databinding.FragmentExploreBinding;
import com.chagnahnn.spotube.ui.adapter.WordDiff;
import com.chagnahnn.spotube.ui.adapter.WordListAdapter;
import com.chagnahnn.spotube.util.RandomUtils;
import com.chagnahnn.spotube.viewmodel.WordViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExploreFragment extends Fragment {
    private FragmentExploreBinding binding;
    private WordViewModel mWordViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        mWordViewModel = new ViewModelProvider(requireActivity()).get(WordViewModel.class);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        RecyclerView recyclerView = binding.recyclerview;
        final WordListAdapter adapter = new WordListAdapter(new WordDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mWordViewModel.getAllWords().observe(requireActivity(), adapter::submitList);

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(view -> {
            Word word = new Word(RandomUtils.getRandomString(4));
            mWordViewModel.insert(word);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
