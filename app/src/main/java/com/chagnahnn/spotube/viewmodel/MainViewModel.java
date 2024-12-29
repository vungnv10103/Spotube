package com.chagnahnn.spotube.viewmodel;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.Util;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.chagnahnn.spotube.service.media.PlayBackService;
import com.chagnahnn.spotube.util.DialogUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainViewModel extends AndroidViewModel {
    private final Handler handler;
    private Runnable runnable;
    private final MutableLiveData<Long[]> mCurrentPosition;
    private final MutableLiveData<ListenableFuture<MediaController>> mControllerFuture;
    private final MutableLiveData<MediaController> mMediaController;

    private final MutableLiveData<List<MediaItem>> mMediaItemList;
    private final MutableLiveData<String> mTitle;
    private final MutableLiveData<String> mArtist;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mControllerFuture = new MutableLiveData<>();
        mMediaController = new MutableLiveData<>();
        mMediaItemList = new MutableLiveData<>();
        mTitle = new MutableLiveData<>();
        mArtist = new MutableLiveData<>();

        handler = new Handler();
        mCurrentPosition = new MutableLiveData<>();
    }

    public LiveData<Long[]> getCurrentPosition() {
        return mCurrentPosition;
    }

    private void initTimeBar() {
        runnable = new Runnable() {
            @Override
            public void run() {
                MediaController mediaController = mMediaController.getValue();
                if (mediaController == null) return;
                boolean shouldShowPlayButton = Util.shouldShowPlayButton(mediaController);
                if (!shouldShowPlayButton) {
                    Long[] longs = new Long[3];
                    long position = mediaController.getCurrentPosition();
                    long duration = mediaController.getDuration();
                    long bufferedPosition = mediaController.getBufferedPosition();
                    longs[0] = position;
                    longs[1] = duration;
                    longs[2] = bufferedPosition;
                    mCurrentPosition.setValue(longs);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    public void initMediaController(Context context, Player.Listener playerListener) {
        if (mMediaController.getValue() == null) {
            SessionToken sessionToken = new SessionToken(context,
                    new ComponentName(context, PlayBackService.class));
            ListenableFuture<MediaController> controllerFuture = new MediaController.Builder(context, sessionToken).buildAsync();
            controllerFuture.addListener(() -> {
                try {
                    MediaController controller = controllerFuture.get();
                    controller.addListener(playerListener);
                    mMediaController.setValue(controller);
                    initTimeBar();
                } catch (ExecutionException | InterruptedException e) {
                    DialogUtils.startDlgOK(context, "InitMediaController: " + e.getMessage());
                }
            }, MoreExecutors.directExecutor());
            mControllerFuture.setValue(controllerFuture);
        }
    }

    public void setArtist(CharSequence artist) {
        if (artist != null) {
            mArtist.setValue(artist.toString());
        } else {
            mArtist.setValue("Unknown");
        }
    }

    public void setTitle(CharSequence title) {
        if (title != null)
            mTitle.setValue(title.toString());
    }

    public LiveData<String> getArtist() {
        return mArtist;
    }

    public LiveData<String> getTitle() {
        return mTitle;
    }

    public LiveData<ListenableFuture<MediaController>> getControllerFuture() {
        return mControllerFuture;
    }

    public LiveData<MediaController> getMediaController() {
        return mMediaController;
    }

    public LiveData<List<MediaItem>> getMediaItemList() {
        return mMediaItemList;
    }

    private void releaseMediaController() {

    }

    @Override
    protected void onCleared() {
        super.onCleared();

        releaseMediaController();
        handler.removeCallbacks(runnable);
    }
}
