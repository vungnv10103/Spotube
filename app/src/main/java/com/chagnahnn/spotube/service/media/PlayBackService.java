package com.chagnahnn.spotube.service.media;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class PlayBackService extends MediaSessionService {
    private MediaSession mediaSession = null;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();

        AudioAttributes musicAudioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
//                .setAllowedCapturePolicy(C.ALLOW_CAPTURE_BY_NONE)
//                .setFlags(C.FLAG_AUDIBILITY_ENFORCED)
                .build();

        ExoPlayer player = new ExoPlayer.Builder(this)
                .setDeviceVolumeControlEnabled(true)
                .setAudioAttributes(musicAudioAttributes, true)
//                .setMediaSourceFactory(mediaSourceFactory)
                .build();

        CallerAwareForwardingPlayer callerAwareForwardingPlayer = new CallerAwareForwardingPlayer(player);
        mediaSession = new MediaSession.Builder(this, callerAwareForwardingPlayer)
                .setCallback(new CustomMediaSessionCallback(this))
                .setId(getClass().getSimpleName())
                .build();

        callerAwareForwardingPlayer.setSession(mediaSession);
    }


    // The user dismissed the app from the recent tasks
    @Override
    public void onTaskRemoved(@Nullable Intent rootIntent) {
        Player player = mediaSession.getPlayer();
        if (!player.getPlayWhenReady()
                || player.getMediaItemCount() == 0
                || player.getPlaybackState() == Player.STATE_ENDED) {
            // Stop the service if not playing, continue playing in the background otherwise.
            stopSelf();
        }
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaSession != null) {
            mediaSession.getPlayer().release();
            mediaSession.release();
            mediaSession = null;
        }
    }
}
