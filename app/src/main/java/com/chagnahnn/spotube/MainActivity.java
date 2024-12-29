package com.chagnahnn.spotube;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.session.MediaController;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.chagnahnn.spotube.core.SpotubeAppCompatActivity;
import com.chagnahnn.spotube.databinding.ActivityMainBinding;
import com.chagnahnn.spotube.ui.adapter.TabLayoutAdapter;
import com.chagnahnn.spotube.ui.tablayout.LyricTabFragment;
import com.chagnahnn.spotube.ui.tablayout.QueueTabFragment;
import com.chagnahnn.spotube.util.DepthPageTransformer;
import com.chagnahnn.spotube.util.FormatUtils;
import com.chagnahnn.spotube.util.NetworkUtils;
import com.chagnahnn.spotube.util.ThemeUtils;
import com.chagnahnn.spotube.viewmodel.MainViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends SpotubeAppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private MediaItem currentMediaItem;
    private boolean mediaItemChanged = false;
    private boolean firstLoad = true;

    private Handler handlerBlurView;
    private Runnable hideBlurViewRunnable;
    private boolean isBlurViewVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        configureUI();
        initMediaController();
        loadData();
    }

    private void toggleBlurView() {
        if (isBlurViewVisible) {
            if (hideBlurViewRunnable != null) {
                handlerBlurView.removeCallbacks(hideBlurViewRunnable);
            }
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(500);
            binding.blurView.startAnimation(fadeOut);
            binding.blurView.setAlpha(0f);
            binding.btnFullScreen.setVisibility(View.GONE);
            isBlurViewVisible = false;
        } else {
            binding.blurView.setAlpha(1f);
            binding.btnFullScreen.setVisibility(View.VISIBLE);
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(500);
            binding.blurView.startAnimation(fadeIn);
            hideBlurViewRunnable = () -> {
                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(500);
                binding.blurView.startAnimation(fadeOut);
                binding.blurView.setAlpha(0f);
                binding.btnFullScreen.setVisibility(View.GONE);
                isBlurViewVisible = false;
            };
            handlerBlurView.postDelayed(hideBlurViewRunnable, 3000);
            isBlurViewVisible = true;
        }
    }

    private void initClick() {
        binding.btnBackToCollapse.setOnClickListener(view -> transitionToCollapse());
        binding.btnPlaybackSpeed.setOnClickListener(view -> toggleBlurView());
        binding.btnMoreMediaExpand.setOnClickListener(view -> {

        });
        binding.btnPlayPauseCollapse.setOnClickListener(view -> handlePLayPause());
        binding.btnPlayPauseExpand.setOnClickListener(view -> handlePLayPause());
        binding.btnLike.setOnClickListener(view -> {
            View divider = new View(this);
            divider.setBackgroundColor(Color.BLACK);
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    2,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            divider.setLayoutParams(dividerParams);

            binding.getRoot().addView(divider, 1);
        });
//        binding.btnDislike.setOnClickListener(view -> logViewDimensions(binding.btnDislike));

        binding.btnFullScreen.setOnClickListener(view -> transitionToFullscreen());
    }

    private void initMediaController() {
        mainViewModel.initMediaController(this, playerListener);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void loadData() {
        mainViewModel.getMediaItemList().observe(this, mediaItems -> {

        });
        mainViewModel.getControllerFuture().observe(this, this::setControllerFuture);
        mainViewModel.getMediaController().observe(this, this::setupMediaController);
        mainViewModel.getTitle().observe(this, title -> {
            binding.tvTitleMediaCollapse.setText(title);
            binding.tvTitleMediaExpand.setText(title);
        });
        mainViewModel.getArtist().observe(this, artist -> {
            binding.tvArtistMediaCollapse.setText(artist);
            binding.tvArtistMediaExpand.setText(artist);
        });
        mainViewModel.getCurrentPosition().observe(this, longs -> {
            if (longs != null && longs.length == 3) {
                binding.timeBarExpand.setPosition(longs[0]);
                binding.timeBarExpand.setDuration(longs[1]);
                binding.timeBarExpand.setBufferedPosition(longs[2]);
                binding.tvTimeStart.setText(FormatUtils.getTimeString(Math.round((float) longs[0] / 1000)));
                binding.tvTimeEnd.setText(FormatUtils.getTimeString(Math.round((float) longs[1] / 1000)));
                binding.timeBarCollapse.setPosition(longs[0]);
                binding.timeBarCollapse.setDuration(longs[1]);
                binding.timeBarCollapse.setBufferedPosition(longs[2]);
            }
        });
    }

    private void setupMediaController(MediaController mediaController) {
        setMedia3Controller(mediaController);
        binding.playerView.setPlayer(mediaController);
        binding.playerView.requestFocus();
        logD("PlayerView:: " + FormatUtils.getStateName(mediaController.getPlaybackState()));
        initView(mediaController);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initView(MediaController mediaController) {
        handlerBlurView = new Handler();
        initNavController();
        initMotionLayout();
        initTabOptionMusic();
        binding.timeBarCollapse.setScrubberColor(Color.TRANSPARENT);
        binding.timeBarCollapse.addListener(timeBar);
        binding.timeBarExpand.addListener(timeBar);

        binding.tvTitleMediaCollapse.setSelected(true);
        binding.tvTitleMediaExpand.setSelected(true);
        initClick();

        binding.btnLike.setText("11,7 N");
        binding.btnDislike.setText("999");
        binding.btnCmt.setText("125");
    }

    private void logViewDimensions(View view) {
        if (view != null) {
            view.post(() -> {
                int width = view.getWidth();
                int height = view.getHeight();
                Log.d("ViewDimensions", "Width: " + width + ", Height: " + height);
            });
        } else {
            Log.e("ViewDimensions", "View is null");
        }
    }

    private void updatePlayPauseButton() {
        boolean shouldShowPlayButton = shouldShowPlayButton();
        binding.btnPlayPauseCollapse.setIconResource(shouldShowPlayButton ? R.drawable.ic_play_24dp : R.drawable.ic_pause_24dp);
        binding.btnPlayPauseExpand.setIconResource(shouldShowPlayButton ? R.drawable.ic_play_24dp : R.drawable.ic_pause_24dp);
    }

    private void setColorScheme(int colorDefault, int color) {
        if (colorDefault != color) {
            ConstraintSet constraintSet = binding.container.getConstraintSet(R.id.expand);
            startAnimationColor(constraintSet, colorDefault, color);
            int colorOption = getBackgroundColorOption();
            binding.btnLike.setBackgroundColor(colorOption);
            binding.btnDislike.setBackgroundColor(colorOption);
            binding.btnCmt.setBackgroundColor(colorOption);
            binding.btnAddToPlaylist.setBackgroundColor(colorOption);
            binding.btnShareMedia.setBackgroundColor(colorOption);
            binding.btnPlayPauseExpand.setBackgroundColor(colorOption);
            ColorStateList colorPrimary = ColorStateList.valueOf(ThemeUtils.getColorPrimary(this));
            binding.btnPlayPauseExpand.setIconTint(colorPrimary);

            if (QueueTabFragment.instance != null) {
                QueueTabFragment.instance.updateColor();
            }
            if (LyricTabFragment.instance != null) {
                LyricTabFragment.instance.updateColor();
            }
        }
    }

    private void handleColor(@NonNull MediaMetadata mediaMetadata) {
        if (isNightMode()) {
            Bitmap artworkBitmap = getBitmapFromMediaMetaData(mediaMetadata);
            int color = getColorPalette(artworkBitmap);
            int colorDefault = ThemeUtils.getColorSurface(this);
            setColorScheme(colorDefault, color);
        }
    }

    private void handleTitle(MediaMetadata mediaMetadata) {
        if (mediaMetadata == null) return;
        mainViewModel.setTitle(mediaMetadata.title);
        mainViewModel.setArtist(mediaMetadata.artist);
        handleColor(mediaMetadata);
    }

    private void updateShuffleModeButton(boolean shuffleModeEnabled) {
        binding.btnShuffle.setIconResource(shuffleModeEnabled ?
                R.drawable.ic_shuffle_fill_24dp : R.drawable.ic_shuffle_24dp);
    }

    private void updateRepeatModeButton(int repeatMode) {
        switch (repeatMode) {
            case Player.REPEAT_MODE_OFF:
                binding.btnRepeat.setIconResource(R.drawable.ic_repeat_24dp);
                break;
            case Player.REPEAT_MODE_ONE:
                binding.btnRepeat.setIconResource(R.drawable.ic_repeat_1_24dp);
                break;
            case Player.REPEAT_MODE_ALL:
                binding.btnRepeat.setIconResource(R.drawable.ic_repeat_all_24dp);
                break;
        }
    }

    private void set(MediaItem currentMediaItem) {
        mediaItemChanged = true;
        this.currentMediaItem = currentMediaItem;
    }

    private void handleMediaItemTransition(MediaItem currentMediaItem) {
        if (currentMediaItem == null) return;
        set(currentMediaItem);


        setEnableMotionLayout();
    }

    final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
//            System.out.println(events);
            if (events.containsAny(
                    Player.EVENT_PLAY_WHEN_READY_CHANGED,
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED)) {
                updatePlayPauseButton();
                binding.btnNext.setEnabled(getMedia3Controller().isCommandAvailable(Player.COMMAND_SEEK_TO_NEXT));
            }
            if (events.contains(Player.EVENT_TRACKS_CHANGED)) {
                // If no video or image track: show shutter, hide image view.
                // Otherwise: do nothing to wait for first frame or image.
                logD("EVENT_TRACKS_CHANGED");
            }
            if (events.contains(Player.EVENT_RENDERED_FIRST_FRAME)) {
                logD("Hide shutter, hide image view.");
            }
        }

        @Override
        public void onSeekForwardIncrementChanged(long seekForwardIncrementMs) {
            Player.Listener.super.onSeekForwardIncrementChanged(seekForwardIncrementMs);
            showSnackBar(seekForwardIncrementMs);
        }

        @Override
        public void onSeekBackIncrementChanged(long seekBackIncrementMs) {
            Player.Listener.super.onSeekBackIncrementChanged(seekBackIncrementMs);
            showSnackBar(seekBackIncrementMs);
        }

        @Override
        public void onMediaMetadataChanged(@NonNull MediaMetadata mediaMetadata) {
            handleTitle(mediaMetadata);
        }

        @Override
        public void onMediaItemTransition(MediaItem mediaItem, @Player.MediaItemTransitionReason int reason) {
            handleMediaItemTransition(mediaItem);
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            if (playbackState == Player.STATE_READY) {
                firstLoad = false;
                mediaItemChanged = false;
                binding.tvTitleMediaCollapse.setSelected(true);
                binding.tvTitleMediaExpand.setSelected(true);

                if (currentMediaItem == null) return;
                MediaItem.LocalConfiguration localConfiguration = currentMediaItem.localConfiguration;
                if (localConfiguration == null) {
                    return;
                }
                RadioButton radioAudio = binding.toggleTypeDisplay.findViewById(R.id.radio_audio);
                RadioButton radioVideo = binding.toggleTypeDisplay.findViewById(R.id.radio_video);
                if (TextUtils.equals(localConfiguration.mimeType, MimeTypes.VIDEO_MP4)) {
                    if (!radioVideo.isChecked()) {
//                        binding.artwork.setAlpha(0f);
                        binding.toggleTypeDisplay.check(R.id.radio_video);
                    }
                } else {
                    if (!radioAudio.isChecked()) {
//                        binding.artwork.setAlpha(1f);
                        binding.toggleTypeDisplay.check(R.id.radio_audio);
                    }
                }
            } else if (playbackState == Player.STATE_BUFFERING) {
                if (firstLoad || mediaItemChanged) {
                    stopColorAnimator();
                    binding.tvTitleMediaCollapse.setSelected(false);
                    binding.tvTitleMediaExpand.setSelected(false);

//                    binding.artwork.setAlpha(0f);
                }
            }
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            updateShuffleModeButton(shuffleModeEnabled);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            updateRepeatModeButton(repeatMode);
        }

        @Override
        public void onIsLoadingChanged(boolean isLoading) {
            Player.Listener.super.onIsLoadingChanged(isLoading);
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Player.Listener.super.onIsPlayingChanged(isPlaying);
        }

        @Override
        public void onTimelineChanged(@NonNull Timeline timeline, @Player.TimelineChangeReason int reason) {
            Player.Listener.super.onTimelineChanged(timeline, reason);
            if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
                System.out.println("onTimelineChanged");
            }
        }

        @Override
        public void onSurfaceSizeChanged(int width, int height) {
            Player.Listener.super.onSurfaceSizeChanged(width, height);
//            System.out.println("(w:h):(" + width + ":" + height + ")");
        }

        @Override
        public void onDeviceVolumeChanged(int volume, boolean muted) {
            if (getVolumeControlEnabled()) {
                if (isExpand()) {
                    showToast(volume);
                } else {
                    showToastCenter(volume);
                }
            }
        }

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
//            Player.Listener.super.onPlayerError(error);
            Throwable cause = error.getCause();
            if (cause instanceof HttpDataSource.HttpDataSourceException) {
                // An HTTP error occurred.
                HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                // It's possible to find out more about the error both by casting and by querying
                // the cause.
                logW("An HTTP error occurred: " + httpError.getMessage());
                if (!NetworkUtils.isInternetAvailable(MainActivity.this)) {
                    return;
                }
                if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                    // Cast to InvalidResponseCodeException and retrieve the response code, message
                    // and headers.
                    showSnackBar("InvalidResponseCodeException: " + httpError);
                } else {
                    // Try calling httpError.getCause() to retrieve the underlying cause, although
                    // note that it may be null.
                    showSnackBar("Error null");
                }
            }
        }
    };

    private void handleRadiusPlayerViewCollapse2Expand(float progress) {
        if (progress <= 0.15) {
            setRadiusPlayerView(16);
        } else if (progress <= 0.25) {
            setRadiusPlayerView(18);
        } else if (progress <= 0.3) {
            setRadiusPlayerView(20);
        } else if (progress <= 0.4) {
            setRadiusPlayerView(22);
        } else if (progress <= 0.5) {
            setRadiusPlayerView(24);
        } else if (progress <= 0.75) {
            setRadiusPlayerView(28);
        } else if (progress <= 0.9) {
            setRadiusPlayerView(32);
        }
    }

    private void handleRadiusPlayerViewExpand2ExpandTop(float progress) {
        if (progress <= 0.15) {
            setRadiusPlayerView(32);
        } else if (progress <= 0.25) {
            binding.tabOptionMusic.setSelectedTabIndicator(null);
            setRadiusPlayerView(28);
        } else if (progress <= 0.3) {
            setRadiusPlayerView(24);
        } else if (progress <= 0.4) {
            setRadiusPlayerView(22);
        } else if (progress <= 0.5) {
            setRadiusPlayerView(20);
        } else if (progress <= 0.75) {
            binding.tabOptionMusic.setSelectedTabIndicator(getDrawableDefaultTabLayout());
            setRadiusPlayerView(18);
        } else if (progress <= 0.9) {
            setRadiusPlayerView(16);
        }
    }

    private final TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy = (tab, position) -> {
        switch (position) {
            case 0:
                tab.setText(getString(R.string.music_next).toUpperCase());
                break;
            case 1:
                tab.setText(getString(R.string.music_lyrics).toUpperCase());
                break;
            case 2:
                tab.setText(getString(R.string.music_related).toUpperCase());
                break;
        }
    };

    private void initTabOptionMusic() {
        if (getDrawableDefaultTabLayout() == null) {
            setDrawableDefaultTabLayout(binding.tabOptionMusic.getTabSelectedIndicator());
        }
        binding.tabOptionMusic.setSelectedTabIndicator(null);
        ViewPager2 viewPagerOptionMusic = binding.viewPagerOptionMusic;
        TabLayoutAdapter viewPagerAdapter = new TabLayoutAdapter(this, getTabFragmentList());
        viewPagerOptionMusic.setAdapter(viewPagerAdapter);
        viewPagerOptionMusic.setPageTransformer(new DepthPageTransformer());
        viewPagerOptionMusic.setUserInputEnabled(false);
        TabLayoutMediator tabLayoutMediator
                = new TabLayoutMediator(binding.tabOptionMusic, viewPagerOptionMusic, tabConfigurationStrategy);
        tabLayoutMediator.attach();

        binding.tabOptionMusic.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isExpandTop()) {
                    transitionToExpandTop();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * START MotionLayout
     */
    private void initMotionLayout() {
        binding.container.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                setPreviousTransition(startId);
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                if (startId == R.id.collapse && endId == R.id.expand) {
                    handleRadiusPlayerViewCollapse2Expand(progress);
                } else if (startId == R.id.expand && endId == R.id.expand_top) {
                    handleRadiusPlayerViewExpand2ExpandTop(progress);
                }
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                if (currentId == R.id.expand) {
                    setRadiusPlayerView(32);
                    binding.tabOptionMusic.setSelectedTabIndicator(null);
                } else if (currentId == R.id.collapse) {
                    setRadiusPlayerView(16);
                    binding.tabOptionMusic.setSelectedTabIndicator(null);
                } else if (currentId == R.id.expand_top) {
                    setRadiusPlayerView(16);
                    binding.tabOptionMusic.setSelectedTabIndicator(getDrawableDefaultTabLayout());
                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });
    }

    private void setRadiusPlayerView(int radius) {
        setRadiusView(binding.playerView, radius);
        setRadiusView(binding.artwork, radius);
        setRadiusView(binding.blurView, radius);
    }

    private boolean isCollapse() {
        return binding.container.getCurrentState() == R.id.collapse;
    }

    private boolean isExpand() {
        return binding.container.getCurrentState() == R.id.expand;
    }

    private boolean isExpandTop() {
        return binding.container.getCurrentState() == R.id.expand_top;
    }

    private boolean isExpandTopComment() {
        return binding.container.getCurrentState() == R.id.expand_top_comment;
    }

    public void transitionToExpandTop(int... delay) {
        if (delay != null && delay.length > 0) {
            binding.container.transitionToState(R.id.expand_top, delay[0]);
        } else {
            binding.container.transitionToState(R.id.expand_top);
        }
        if (isKeyboardVisible(binding.getRoot())) {
            closeKeyBoard(this);
        }
    }

    public void transitionToCollapse(int... delay) {
        if (delay != null && delay.length > 0) {
            binding.container.transitionToState(R.id.collapse, delay[0]);
        } else {
            binding.container.transitionToState(R.id.collapse);
        }
        if (isKeyboardVisible(binding.getRoot())) {
            closeKeyBoard(this);
        }
    }

    public void transitionToFullscreen(int... delay) {
        if (delay != null && delay.length > 0) {
            binding.container.transitionToState(R.id.fullscreen, delay[0]);
        } else {
            binding.container.transitionToState(R.id.fullscreen);
        }
        if (isKeyboardVisible(binding.getRoot())) {
            closeKeyBoard(this);
        }
    }

    public void showSnackBar(Object message) {
        if (isCollapse()) {
            showSnackBar(binding.getRoot(), binding.playerViewContainer, message);
        } else {
            showSnackBar(binding.getRoot(), message);
        }
    }

    private void initNavController() {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_shorts, R.id.navigation_explore,
                R.id.navigation_library)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void setEnableMotionLayout() {
        binding.container.setInteractionEnabled(currentMediaItem != null);
    }

    private void configureUI() {
        applyInsetsPadding(binding.getRoot(), 0);
        applyInsetsPadding(binding.navView, 4);
        disableOverscroll(binding.quickActionContainer);
        setRadiusPlayerView(16);
        setEnableMotionLayout();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

//        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        ConstraintSet constraintSet = binding.container.getConstraintSet(R.id.expand);
        if (screenHeight <= 1800) {
            constraintSet.setMargin(R.id.player_view, ConstraintSet.TOP, 0);
            constraintSet.setMargin(R.id.tv_title_media_expand, ConstraintSet.TOP, 0);
            constraintSet.setMargin(R.id.quick_action_container, ConstraintSet.TOP, 0);
            constraintSet.setMargin(R.id.time_bar_expand, ConstraintSet.TOP, 0);
        } else {
            constraintSet.setMargin(R.id.player_view, ConstraintSet.TOP, FormatUtils.dp2Px(this, 34));
            constraintSet.setMargin(R.id.tv_title_media_expand, ConstraintSet.TOP, FormatUtils.dp2Px(this, 28));
            constraintSet.setMargin(R.id.quick_action_container, ConstraintSet.TOP, FormatUtils.dp2Px(this, 12));
            constraintSet.setMargin(R.id.time_bar_expand, ConstraintSet.TOP, FormatUtils.dp2Px(this, 16));
        }

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVolumeControlEnabled(true);
            if (isExpand() || isExpandTop() || isExpandTopComment()) {
                handleFullScreen(true);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setVolumeControlEnabled(false);
            handleFullScreen(false);
        }
    }


    @OptIn(markerClass = UnstableApi.class)
    private void setFullscreenButtonState(boolean isFullscreen) {
        binding.playerView.setFullscreenButtonState(isFullscreen);
    }

    private void handleFullScreen(boolean isFullscreen) {
        if (isFullscreen) {
//            hideSystemUI();
            setFullscreenButtonState(true);
            transitionToFullscreen();
        } else {
            setFullscreenButtonState(false);
            binding.container.transitionToState(getPreviousTransition());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        binding = null;
    }
}