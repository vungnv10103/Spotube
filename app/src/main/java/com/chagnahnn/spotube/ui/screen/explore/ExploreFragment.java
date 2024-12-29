package com.chagnahnn.spotube.ui.screen.explore;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chagnahnn.spotube.data.local.entities.Word;
import com.chagnahnn.spotube.databinding.FragmentExploreBinding;
import com.chagnahnn.spotube.ui.adapter.WordDiff;
import com.chagnahnn.spotube.ui.adapter.WordListAdapter;
import com.chagnahnn.spotube.ui.screen.library.LibraryFragment;
import com.chagnahnn.spotube.util.RandomUtils;
import com.chagnahnn.spotube.viewmodel.WordViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExploreFragment extends Fragment {
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
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

        // Update the cached copy of the words in the adapter.
        mWordViewModel.getAllWords().observe(requireActivity(), adapter::submitList);

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(view -> {
//            Intent intent = new Intent(requireActivity(), LibraryFragment.class);
//            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
            Word word = new Word(RandomUtils.getRandomString(4));
            mWordViewModel.insert(word);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Word word = new Word(data.getStringExtra(LibraryFragment.EXTRA_REPLY));
            mWordViewModel.insert(word);
        } else {
            Toast.makeText(
                    requireActivity(),
                    "R.string.empty_not_saved",
                    Toast.LENGTH_LONG).show();
        }
    }
}
