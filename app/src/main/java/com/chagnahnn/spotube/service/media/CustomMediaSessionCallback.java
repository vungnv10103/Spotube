package com.chagnahnn.spotube.service.media;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.CommandButton;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;
import androidx.media3.session.SessionCommand;
import androidx.media3.session.SessionCommands;
import androidx.media3.session.SessionResult;

import com.chagnahnn.spotube.R;
import com.chagnahnn.spotube.ui.MainActivity;
import com.chagnahnn.spotube.util.LogUtils;
import com.chagnahnn.spotube.util.RandomUtils;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomMediaSessionCallback implements MediaSession.Callback {
    public static final String SAVE_TO_FAVORITES = "10001";
    public static final String REMOVE_FROM_FAVORITES = "10002";
    public static final String PLAY_NEXT = "10004";
    public static final String PLAY_PREVIOUS = "10005";
    public static final String SAVE_TO_LIBRARY = "10006";
    public static final String REMOVE_FROM_LIBRARY = "10007";

    private final MediaSessionService mediaSessionService;

    public CustomMediaSessionCallback(MediaSessionService mediaSessionService) {
        this.mediaSessionService = mediaSessionService;
    }

    @NonNull
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public MediaSession.ConnectionResult onConnect(
            @NonNull MediaSession session, @NonNull MediaSession.ControllerInfo controller) {
        SessionCommand customCommandSave = new SessionCommand(SAVE_TO_FAVORITES, new Bundle());
        SessionCommand customCommandRemove = new SessionCommand(REMOVE_FROM_FAVORITES, new Bundle());
        SessionCommand customCommandPrevious = new SessionCommand(PLAY_PREVIOUS, new Bundle());
        SessionCommand customCommandNext = new SessionCommand(PLAY_NEXT, new Bundle());
        SessionCommand customCommandSaveLibrary = new SessionCommand(SAVE_TO_LIBRARY, new Bundle());
        SessionCommand customCommandRemoveLibrary = new SessionCommand(REMOVE_FROM_LIBRARY, new Bundle());
        SessionCommands sessionCommands =
                MediaSession.ConnectionResult.DEFAULT_SESSION_COMMANDS
                        .buildUpon()
                        .add(customCommandSave)
                        .add(customCommandRemove)
                        .add(customCommandPrevious)
                        .add(customCommandNext)
                        .add(customCommandSaveLibrary)
                        .add(customCommandRemoveLibrary)
                        .build();
        if (session.isMediaNotificationController(controller)) {
            Player.Commands playerCommands =
                    MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS
                            .buildUpon()
//                            .remove(Player.COMMAND_SEEK_TO_PREVIOUS)
//                            .remove(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
//                            .remove(Player.COMMAND_SEEK_TO_NEXT)
//                            .remove(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                            .build();
            // Custom layout and available commands to configure the legacy/framework session.
            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session)
//                    .setCustomLayout(customLayoutCommandButtons)
                    .setAvailablePlayerCommands(playerCommands)
                    .setAvailableSessionCommands(sessionCommands)
                    .build();
        } else if (session.isAutoCompanionController(controller)) {
            // Available commands to accept incoming custom commands from Auto.
            return new MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                    .setAvailableSessionCommands(sessionCommands)
                    .build();
        }
        // Default commands without default custom layout for all other controllers.
        return new MediaSession.ConnectionResult.AcceptedResultBuilder(session).build();
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onPostConnect(@NonNull MediaSession session, @NonNull MediaSession.ControllerInfo controller) {
        session.setCustomLayout(controller, getCustomLayoutCommandButtons());
        Intent intent = new Intent(mediaSessionService, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mediaSessionService, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        session.setSessionActivity(pendingIntent);
        MediaSession.Callback.super.onPostConnect(session, controller);
    }

    @SuppressWarnings({"unused"})
    @NonNull
    private List<CommandButton> getCustomLayoutCommandButtons() {
        List<CommandButton> customLayoutCommandButtons = new ArrayList<>();
        boolean flag = RandomUtils.getRandomBoolean();
        customLayoutCommandButtons.add(new CommandButton.Builder()
                .setDisplayName(flag ? "Remove to favorites" : "Save to favorites")
                .setIconResId(flag ? R.drawable.ic_favorite_fill_24dp : R.drawable.ic_favorite_24dp)
                .setSessionCommand(flag ? new SessionCommand(REMOVE_FROM_FAVORITES, new Bundle()) : new SessionCommand(SAVE_TO_FAVORITES, new Bundle()))
                .build());
        customLayoutCommandButtons.add(new CommandButton.Builder()
                .setDisplayName(flag ? "Remove to library" : "Save to library")
                .setIconResId(flag ? R.drawable.ic_add_to_library_fill_24dp : R.drawable.ic_add_to_library_24dp)
                .setSessionCommand(flag ? new SessionCommand(REMOVE_FROM_LIBRARY, new Bundle()) : new SessionCommand(SAVE_TO_LIBRARY, new Bundle()))
                .build());
        return customLayoutCommandButtons;
    }

    @SuppressWarnings({"unused"})
    private final List<CommandButton> customLayoutCommandButtons = Arrays.asList(
            new CommandButton.Builder()
                    .setDisplayName("Previous")
                    .setIconResId(R.drawable.ic_previous_24dp)
                    .setSessionCommand(new SessionCommand(PLAY_PREVIOUS, new Bundle()))
                    .build(),
            new CommandButton.Builder()
                    .setDisplayName("Next")
                    .setIconResId(R.drawable.ic_next_24dp)
                    .setSessionCommand(new SessionCommand(PLAY_NEXT, new Bundle()))
                    .build()
    );

    @NonNull
    @OptIn(markerClass = UnstableApi.class)
    @Override
    public ListenableFuture<MediaSession.MediaItemsWithStartPosition> onPlaybackResumption(
            @NonNull MediaSession mediaSession, @NonNull MediaSession.ControllerInfo controller) {
        SettableFuture<MediaSession.MediaItemsWithStartPosition> settableFuture = SettableFuture.create();
        settableFuture.addListener(() -> {
            // Your app is responsible for storing the playlist and the start position to use here
            MediaSession.MediaItemsWithStartPosition resumptionPlaylist = restorePlaylist();
            settableFuture.set(resumptionPlaylist);
        }, MoreExecutors.directExecutor());
        return settableFuture;
    }

    @Nullable
    @Contract(pure = true)
    @OptIn(markerClass = UnstableApi.class)
    private MediaSession.MediaItemsWithStartPosition restorePlaylist() {
        return null;
    }

    @NonNull
    @Override
    public ListenableFuture<SessionResult> onCustomCommand(
            @NonNull MediaSession session,
            @NonNull MediaSession.ControllerInfo controller,
            @NonNull SessionCommand customCommand, @NonNull Bundle args) {
        switch (customCommand.customAction) {
            case SAVE_TO_FAVORITES:
                saveToFavorites(session.getPlayer().getCurrentMediaItem());
                return Futures.immediateFuture(
                        new SessionResult(SessionResult.RESULT_SUCCESS)
                );
            case REMOVE_FROM_FAVORITES:
                removeFromFavorites(session.getPlayer().getCurrentMediaItem());
                return Futures.immediateFuture(
                        new SessionResult(SessionResult.RESULT_SUCCESS)
                );
            case PLAY_NEXT:
                nextSong(session);
                return Futures.immediateFuture(
                        new SessionResult(SessionResult.RESULT_SUCCESS)
                );
            case PLAY_PREVIOUS:
                previousSong(session);
                return Futures.immediateFuture(
                        new SessionResult(SessionResult.RESULT_SUCCESS)
                );
            case SAVE_TO_LIBRARY:
                saveLibrary(session);
                return Futures.immediateFuture(
                        new SessionResult(SessionResult.RESULT_SUCCESS)
                );
            case REMOVE_FROM_LIBRARY:
                removeLibrary(session);
                return Futures.immediateFuture(
                        new SessionResult(SessionResult.RESULT_SUCCESS)
                );
        }
        return MediaSession.Callback.super.onCustomCommand(session, controller, customCommand, args);
    }

    private void saveToFavorites(MediaItem currentMediaItem) {
        if (currentMediaItem == null) return;
        LogUtils.logD("saveToFavorites: " + currentMediaItem.mediaId);
    }

    private void removeFromFavorites(MediaItem currentMediaItem) {
        if (currentMediaItem == null) return;
        LogUtils.logD("removeFromFavorites: " + currentMediaItem.mediaId);
    }

    private void previousSong(MediaSession session) {
        if (session == null) return;
        MediaItem currentMediaItem = session.getPlayer().getCurrentMediaItem();
        if (currentMediaItem == null) return;
        LogUtils.logD("previousSong: " + currentMediaItem.mediaId);
        session.getPlayer().seekToPrevious();
    }

    private void nextSong(MediaSession session) {
        if (session == null) return;
        MediaItem currentMediaItem = session.getPlayer().getCurrentMediaItem();
        if (currentMediaItem == null) return;
        LogUtils.logD("nextSong: " + currentMediaItem.mediaId);
        session.getPlayer().seekToNext();
    }

    private void saveLibrary(MediaSession session) {
        if (session == null) return;
        MediaItem currentMediaItem = session.getPlayer().getCurrentMediaItem();
        if (currentMediaItem == null) return;
        LogUtils.logD("saveLibrary: " + currentMediaItem.mediaId);
    }

    private void removeLibrary(MediaSession session) {
        if (session == null) return;
        MediaItem currentMediaItem = session.getPlayer().getCurrentMediaItem();
        if (currentMediaItem == null) return;
        LogUtils.logD("removeLibrary: " + currentMediaItem.mediaId);
    }
}