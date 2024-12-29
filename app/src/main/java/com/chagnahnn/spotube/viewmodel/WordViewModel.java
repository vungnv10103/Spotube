package com.chagnahnn.spotube.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.chagnahnn.spotube.data.local.entities.Word;
import com.chagnahnn.spotube.data.respository.WordRepository;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private final WordRepository mRepository;

    private final LiveData<List<Word>> mAllWords;

    public WordViewModel(Application application) {
        super(application);
        mRepository = new WordRepository(application);
        mAllWords = mRepository.getAllWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public void insert(Word word) {
        mRepository.insert(word);
    }
}
