package com.chagnahnn.spotube.data.respository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.chagnahnn.spotube.data.local.dao.WordDao;
import com.chagnahnn.spotube.data.local.db.WordRoomDatabase;
import com.chagnahnn.spotube.data.local.entities.Word;

import java.util.List;

public class WordRepository {
    private final WordDao mWordDao;
    private final LiveData<List<Word>> mAllWords;

    public WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = mWordDao.getAlphabetizedWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return mAllWords;
    }

    public void insert(Word word) {
        WordRoomDatabase.databaseWriteExecutor.execute(() -> mWordDao.insert(word));
    }
}