package com.chagnahnn.spotube.service.media;

import androidx.media3.common.ForwardingPlayer;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaSession;

import com.chagnahnn.spotube.util.LogUtils;

@UnstableApi
public class CallerAwareForwardingPlayer extends ForwardingPlayer {
    private MediaSession session;

    public CallerAwareForwardingPlayer(Player player) {
        super(player);
    }

    public void setSession(MediaSession session) {
        this.session = session;
    }

    @Override
    public void play() {
        super.play();
    }

    @Override
    public void seekToNext() {
        if (session != null && session.getControllerForCurrentRequest() != null)
            LogUtils.logD("seekToNext called from package: "
                    + session.getControllerForCurrentRequest().getPackageName());
        super.seekToNext();
    }

    @Override
    public long getContentBufferedPosition() {
        return super.getContentBufferedPosition();
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        super.setPlayWhenReady(playWhenReady);
    }
}