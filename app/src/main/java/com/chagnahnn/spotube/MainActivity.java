package com.chagnahnn.spotube;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.PlayerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.chagnahnn.spotube.core.MyMediaItem;
import com.chagnahnn.spotube.core.SpotubeAppCompatActivity;
import com.chagnahnn.spotube.databinding.ActivityMainBinding;
import com.chagnahnn.spotube.ui.adapter.CommentAdapter;
import com.chagnahnn.spotube.ui.adapter.TabLayoutAdapter;
import com.chagnahnn.spotube.ui.model.Comment;
import com.chagnahnn.spotube.ui.model.Lyric;
import com.chagnahnn.spotube.ui.screen.home.HomeFragment;
import com.chagnahnn.spotube.ui.tablayout.LyricTabFragment;
import com.chagnahnn.spotube.ui.tablayout.QueueTabFragment;
import com.chagnahnn.spotube.util.DepthPageTransformer;
import com.chagnahnn.spotube.util.FormatUtils;
import com.chagnahnn.spotube.util.NetworkUtils;
import com.chagnahnn.spotube.util.ThemeUtils;
import com.chagnahnn.spotube.viewmodel.MainViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SpotubeAppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private MediaItem currentMediaItem;
    private boolean mediaItemChanged = false;
    private boolean firstLoad = true;

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


    private CommentAdapter commentAdapter;

    private void initClick(MediaController mediaController) {
//        setupButtonBlurListeners();
        binding.btnBackToCollapse.setOnClickListener(view -> transitionToCollapse());
        binding.btnPlaybackSpeed.setOnClickListener(view -> {

        });
        binding.btnMoreMediaExpand.setOnClickListener(view -> {

        });
        binding.btnPlayPauseCollapse.setOnClickListener(view -> handlePLayPause());
        binding.btnPlayPauseExpand.setOnClickListener(view -> handlePLayPause());
        binding.btnLike.setOnClickListener(view -> {
        });
        binding.btnDislike.setOnClickListener(view -> {
        });
        binding.btnShuffle.setOnClickListener(v -> handleShuffle());
        binding.btnPrevious.setOnClickListener(v -> handlePrevious());
        binding.btnNext.setOnClickListener(v -> handleNext());
        binding.btnRepeat.setOnClickListener(v -> handleRepeat());

//        initTouchPlayerView();
        binding.btnFullScreen.setOnClickListener(view -> transitionToFullscreen());


        String textTemplate = getString(R.string.term_cmt);
        String wordToStyle = getString(R.string.term_title);
        String fullText = String.format(textTemplate, wordToStyle);

        commentAdapter = new CommentAdapter(this, getSpannableString(fullText, wordToStyle));
        disableOverscroll(binding.rcvComment);
        binding.rcvComment.setAdapter(commentAdapter);

        binding.btnBackToMainCmt.setOnClickListener(view -> hideReplyCmt());
        binding.btnCloseCmt.setOnClickListener(view -> transitionToExpand());
        binding.inputCmtContainer.edtCmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (charSequence.length() > 0) {
                    binding.inputCmtContainer.btnSendCmt.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.inputCmtContainer.edtCmt.getLayoutParams();
                    layoutParams.setMarginEnd(0);
                    binding.inputCmtContainer.edtCmt.setLayoutParams(layoutParams);
                } else {
                    binding.inputCmtContainer.btnSendCmt.setVisibility(View.GONE);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.inputCmtContainer.edtCmt.getLayoutParams();
                    layoutParams.setMarginEnd(FormatUtils.dp2Px(MainActivity.this, 12));
                    binding.inputCmtContainer.edtCmt.setLayoutParams(layoutParams);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputReplyCmtContainer.edtCmt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (charSequence.length() > 0) {
                    binding.inputReplyCmtContainer.btnSendCmt.setVisibility(View.VISIBLE);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.inputReplyCmtContainer.edtCmt.getLayoutParams();
                    layoutParams.setMarginEnd(0);
                    binding.inputReplyCmtContainer.edtCmt.setLayoutParams(layoutParams);
                } else {
                    binding.inputReplyCmtContainer.btnSendCmt.setVisibility(View.GONE);
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.inputReplyCmtContainer.edtCmt.getLayoutParams();
                    layoutParams.setMarginEnd(FormatUtils.dp2Px(MainActivity.this, 12));
                    binding.inputReplyCmtContainer.edtCmt.setLayoutParams(layoutParams);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCmtContainer.btnSendCmt.setOnClickListener(view -> showToast(binding.inputCmtContainer.edtCmt.getText().toString().trim()));
        binding.inputReplyCmtContainer.btnSendCmt.setOnClickListener(view -> showToast("Replay:: " + binding.inputReplyCmtContainer.edtCmt.getText().toString().trim()));
    }

    public void showReplyCmt(boolean isFocusEditText) {
        binding.btnBackToMainCmt.setVisibility(View.VISIBLE);
        binding.tvHeaderCmt.setText(getString(R.string.reply_title));
        animateLayout(binding.mainCommentContainer, binding.replyCommentContainer, "left", 350);
        if (isFocusEditText) {
            binding.inputReplyCmtContainer.edtCmt.requestFocus();
            binding.inputReplyCmtContainer.edtCmt.post(() -> {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(binding.inputReplyCmtContainer.edtCmt, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }
    }

    private void hideReplyCmt() {
        binding.btnBackToMainCmt.setVisibility(View.GONE);
        binding.tvHeaderCmt.setText(getString(R.string.comment));
        animateLayout(binding.replyCommentContainer, binding.mainCommentContainer, "right", 350);
        closeKeyboard();
    }

    private void closeKeyboard() {
        if (isKeyboardVisible(binding.getRoot())) {
            closeKeyBoard(this);
        }
    }


    /**
     * Animates the transition between two layouts with a sliding effect.
     *
     * @param layoutToHide   The layout currently visible that will be hidden.
     * @param layoutToShow   The layout currently hidden that will be shown.
     * @param slideDirection The direction of the slide animation. Can be "left" or "right".
     * @param duration       The duration of the animation in milliseconds.
     */
    @SuppressWarnings("SameParameterValue")
    private void animateLayout(@NonNull View layoutToHide, View layoutToShow,
                               @NonNull String slideDirection, long duration) {
        int width = layoutToHide.getWidth();

        // Determine animation direction
        float hideStartX = 0;
        float hideEndX = slideDirection.equals("left") ? -width : width;

        float showStartX = slideDirection.equals("left") ? width : -width;
        float showEndX = 0;

        // Slide out animation for the layout to hide
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(layoutToHide, "translationX", hideStartX, hideEndX);
        slideOut.setDuration(duration);

        // Slide in animation for the layout to show
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(layoutToShow, "translationX", showStartX, showEndX);
        slideIn.setDuration(duration);

        // AnimatorSet to play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(slideOut, slideIn);

        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {
                layoutToShow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                layoutToHide.setVisibility(View.GONE);
            }
        });

        animatorSet.start();
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

                binding.timeBarFullscreen.setPosition(longs[0]);
                binding.timeBarFullscreen.setDuration(longs[1]);
                binding.timeBarFullscreen.setBufferedPosition(longs[2]);
                binding.tvTimeStartFullscreen.setText(FormatUtils.getTimeString(Math.round((float) longs[0] / 1000)));
                binding.tvTimeEndFullscreen.setText(FormatUtils.getTimeString(Math.round((float) longs[1] / 1000)));
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
//        handlerBlurView = new Handler();
        initNavController();
        initMotionLayout();
        initTabOptionMusic();
        binding.timeBarCollapse.setScrubberColor(Color.TRANSPARENT);
        binding.timeBarCollapse.addListener(timeBar);
        binding.timeBarExpand.addListener(timeBar);
        binding.timeBarFullscreen.addListener(timeBar);

        binding.tvTitleMediaCollapse.setSelected(true);
        binding.tvTitleMediaExpand.setSelected(true);
        initClick(mediaController);
    }

    private void updatePlayPauseButton() {
        boolean shouldShowPlayButton = shouldShowPlayButton();
        binding.btnPlayPauseCollapse.setIconResource(shouldShowPlayButton ? R.drawable.ic_play_24dp : R.drawable.ic_pause_24dp);
        binding.btnPlayPauseExpand.setIconResource(shouldShowPlayButton ? R.drawable.ic_play_24dp : R.drawable.ic_pause_24dp);
        binding.btnPlayPauseBlur.setIconResource(shouldShowPlayButton ? R.drawable.ic_play_24dp : R.drawable.ic_pause_24dp);
    }

    private void setColorScheme(int colorDefault, int color) {
        if (colorDefault != color) {
            ConstraintSet constraintSet = binding.container.getConstraintSet(R.id.expand);
            startAnimationColor(constraintSet, colorDefault, color);
//            startColor(constraintSet, color);
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
        binding.btnShuffleFullscreen.setIconResource(shuffleModeEnabled ?
                R.drawable.ic_shuffle_fill_24dp : R.drawable.ic_shuffle_24dp);
    }

    private void updateRepeatModeButton(int repeatMode) {
        switch (repeatMode) {
            case Player.REPEAT_MODE_OFF:
                binding.btnRepeat.setIconResource(R.drawable.ic_repeat_24dp);
                binding.btnRepeatFullscreen.setIconResource(R.drawable.ic_repeat_24dp);
                break;
            case Player.REPEAT_MODE_ONE:
                binding.btnRepeat.setIconResource(R.drawable.ic_repeat_1_24dp);
                binding.btnRepeatFullscreen.setIconResource(R.drawable.ic_repeat_1_24dp);
                break;
            case Player.REPEAT_MODE_ALL:
                binding.btnRepeat.setIconResource(R.drawable.ic_repeat_all_24dp);
                binding.btnRepeatFullscreen.setIconResource(R.drawable.ic_repeat_all_24dp);
                break;
        }
    }

    private void set(MediaItem currentMediaItem) {
        mediaItemChanged = true;
        this.currentMediaItem = currentMediaItem;
    }

    @NonNull
    private List<Comment> filterComment(String mediaID) {
        List<Comment> commentList = new ArrayList<>();
        int size = HomeFragment.mCommentList.size();
        for (int i = 0; i < size; i++) {
            Comment comment = HomeFragment.mCommentList.get(i);
            if (comment.getMultiMediaId().equals(mediaID)) {
                commentList.add(comment);
            }
        }
        return commentList;
    }

    private void handleToggleLikeDislike(MediaItem currentMediaItem, @NonNull Bundle musicBundle) {
        long likes, dislikes;
        boolean enableShowDislike;
        likes = musicBundle.getLong("likesCount");
        dislikes = musicBundle.getLong("dislikesCount");
        enableShowDislike = musicBundle.getBoolean("enableShowDislike");
        binding.btnLike.setText(FormatUtils.formatCount(this, likes));
        if (enableShowDislike) {
            binding.btnDislike.setText(FormatUtils.formatCount(this, dislikes));
            int px = FormatUtils.dp2Px(this, 8);
            binding.btnDislike.setPadding(px, 0, px, 0);
        } else {
            binding.btnDislike.setText("");
            int px = FormatUtils.dp2Px(this, 0);
            binding.btnDislike.setPadding(px, 0, px, 0);
        }
        boolean[] likeOrDislike = MyMediaItem.isLikeOrDislike(currentMediaItem);
        binding.btnLike.setChecked(likeOrDislike[0]);
        if (likeOrDislike[0]) {
            binding.btnDislike.setChecked(false);
        } else {
            binding.btnDislike.setChecked(likeOrDislike[1]);
        }
    }

    private void clearEdtCmt() {
        binding.inputCmtContainer.edtCmt.setText("");
        binding.inputReplyCmtContainer.edtCmt.setText("");
    }

    private void handleMediaItemTransition(MediaItem currentMediaItem) {
        clearEdtCmt();
        if (isExpandTopComment()) {
            transitionToExpand();
        }
        if (currentMediaItem == null) return;
        set(currentMediaItem);
        Bundle musicBundle = currentMediaItem.mediaMetadata.extras;
        if (musicBundle == null) return;
        handleToggleLikeDislike(currentMediaItem, musicBundle);
        boolean enableShowComment = musicBundle.getBoolean("enableShowComment");
        if (enableShowComment) {
            List<Comment> commentList = filterComment(currentMediaItem.mediaId);
            commentAdapter.setItemList(commentList);
            if (commentList.isEmpty()) {
                binding.btnCmt.setIconPadding(0);
                binding.btnCmt.setText("");
            } else {
                binding.btnCmt.setIconPadding(FormatUtils.dp2Px(this, 6));
                binding.btnCmt.setText(String.valueOf(commentList.size()));
            }
            ColorStateList colorPrimary = ColorStateList.valueOf(ThemeUtils.getColorPrimary(this));
            binding.btnCmt.setIconTint(colorPrimary);
            binding.btnCmt.setTextColor(colorPrimary);
            binding.btnCmt.setOnClickListener(view -> transitionToExpandTopComment());
        } else {
            ColorStateList colorSurfaceVariant = ColorStateList.valueOf(ThemeUtils.getColorSurfaceVariant(this));
            binding.btnCmt.setIconTint(colorSurfaceVariant);
            binding.btnCmt.setTextColor(colorSurfaceVariant);
            binding.btnCmt.setOnClickListener(view -> showSnackBar(getString(R.string.comment_disable)));
        }

        updateTabItem();
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
                binding.container.setInteractionEnabled(true);

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
                    binding.container.setInteractionEnabled(false);
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

    private void updateTabItem() {
        TabLayout.Tab tabQueue = binding.tabOptionMusic.getTabAt(0);
        TabLayout.Tab tabLyric = binding.tabOptionMusic.getTabAt(1);
        TabLayout.Tab tabRelate = binding.tabOptionMusic.getTabAt(2);
        if (tabQueue != null && tabLyric != null && tabRelate != null) {
            tabConfigurationStrategy.onConfigureTab(tabQueue, 0);
            tabConfigurationStrategy.onConfigureTab(tabLyric, 1);
            tabConfigurationStrategy.onConfigureTab(tabRelate, 2);
        }
    }

    private final TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy = (tab, position) -> {
        MediaItem currentMediaItem = getCurrentMediaItem();
        ArrayList<Lyric> lyrics = MyMediaItem.getLyric(currentMediaItem);
        switch (position) {
            case 0:
                tab.setText(getString(R.string.music_next).toUpperCase());
                break;
            case 1:
                tab.setText(getString(R.string.music_lyrics).toUpperCase());
                if (lyrics == null || lyrics.isEmpty()) {
                    tab.view.setEnabled(false);
                    tab.view.setAlpha(0.5f);
                } else {
                    tab.view.setEnabled(true);
                    tab.view.setAlpha(1f);
                }
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
                closeKeyboard();
                if (startId == R.id.expand && endId == R.id.expand_top) {
                    if (binding.viewPagerOptionMusic.getVisibility() != View.VISIBLE) {
                        binding.viewPagerOptionMusic.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                if (startId == R.id.collapse && endId == R.id.expand) {
                    handleRadiusPlayerViewCollapse2Expand(progress);
                } else if (startId == R.id.expand && endId == R.id.expand_top) {
                    handleRadiusPlayerViewExpand2ExpandTop(progress);
                    binding.viewPagerOptionMusic.setAlpha(progress);
                    setMarginTopByFactor(binding.tabOptionMusic, progress);
                } else if (startId == R.id.expand && endId == R.id.expand_top_comment) {
                    handleRadiusPlayerViewExpand2ExpandTop(progress);
                    binding.commentContainer.setAlpha(progress);
                }
            }

            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
//                hiddenBlurItem();
                if (currentId == R.id.expand) {
                    setRadiusPlayerView(32);
                    binding.tabOptionMusic.setSelectedTabIndicator(null);
                    if (binding.viewPagerOptionMusic.getVisibility() != View.GONE) {
                        binding.viewPagerOptionMusic.setVisibility(View.GONE);
                    }
                    if (binding.viewPagerOptionMusic.getAlpha() != 0f) {
                        binding.viewPagerOptionMusic.setAlpha(0f);
                    }
                } else if (currentId == R.id.collapse) {
                    setRadiusPlayerView(16);
                    binding.tabOptionMusic.setSelectedTabIndicator(null);
                } else if (currentId == R.id.expand_top) {
                    setRadiusPlayerView(16);
                    binding.tabOptionMusic.setSelectedTabIndicator(getDrawableDefaultTabLayout());
                    if (binding.viewPagerOptionMusic.getVisibility() != View.VISIBLE) {
                        binding.viewPagerOptionMusic.setVisibility(View.VISIBLE);
                    }
                    if (binding.viewPagerOptionMusic.getAlpha() != 1f) {
                        binding.viewPagerOptionMusic.setAlpha(1f);
                    }
                }

                if (currentId == R.id.fullscreen) {
                    setRadiusPlayerView(0);
                    hideSystemUI();
                    binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    binding.playerView.setArtworkDisplayMode(PlayerView.ARTWORK_DISPLAY_MODE_FILL);
//                    binding.playerView.setUseController(true);
                    binding.btnFullScreen.setIcon(getDrawable(MainActivity.this, R.drawable.ic_fullscreen_exit_24dp));
                } else {
                    showSystemUI();
                    binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    binding.playerView.setArtworkDisplayMode(PlayerView.ARTWORK_DISPLAY_MODE_FIT);
//                    binding.playerView.setUseController(false);
                    binding.btnFullScreen.setIcon(getDrawable(MainActivity.this, R.drawable.ic_fullscreen_on_24dp));
                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                logD(triggerId + ":" + positive + ":" + progress);
                System.out.println(motionLayout.toString());
                showSnackBar(triggerId + ":" + positive + ":" + progress + "\n" + motionLayout);
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

    private boolean isFullscreen() {
        return binding.container.getCurrentState() == R.id.fullscreen;
    }


    public void transitionToExpand(int... delay) {
        if (delay != null && delay.length > 0) {
            binding.container.transitionToState(R.id.expand, delay[0]);
        } else {
            binding.container.transitionToState(R.id.expand);
        }
        closeKeyboard();
    }

    public void transitionToExpandTop(int... delay) {
        if (delay != null && delay.length > 0) {
            binding.container.transitionToState(R.id.expand_top, delay[0]);
        } else {
            binding.container.transitionToState(R.id.expand_top);
        }
        closeKeyboard();
    }

    public void transitionToCollapse(int... delay) {
        if (delay != null && delay.length > 0) {
            binding.container.transitionToState(R.id.collapse, delay[0]);
        } else {
            binding.container.transitionToState(R.id.collapse);
        }
        closeKeyboard();
    }

    public void transitionToFullscreen(int... delay) {
        if (delay != null && delay.length > 0) {
            binding.container.transitionToState(R.id.fullscreen, delay[0]);
        } else {
            binding.container.transitionToState(R.id.fullscreen);
        }
        closeKeyboard();
    }

    private void transitionToExpandTopComment(int... delay) {
        if (delay != null && delay.length > 0) {
            binding.container.transitionToState(R.id.expand_top_comment, delay[0]);
        } else {
            binding.container.transitionToState(R.id.expand_top_comment);
        }
        closeKeyboard();
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
        disableOverscroll(binding.rcvComment);
        disableOverscroll(binding.rcvReplyComment);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

//        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        ConstraintSet constraintSet = binding.container.getConstraintSet(R.id.expand);
        if (screenHeight <= 1800) {
            constraintSet.setMargin(R.id.blur_view, ConstraintSet.TOP, 0);
            constraintSet.setMargin(R.id.player_view, ConstraintSet.TOP, 0);
            constraintSet.setMargin(R.id.tv_title_media_expand, ConstraintSet.TOP, 0);
            constraintSet.setMargin(R.id.quick_action_container, ConstraintSet.TOP, 0);
            constraintSet.setMargin(R.id.time_bar_expand, ConstraintSet.TOP, 0);
            setMarginTopByFactor(binding.tabOptionMusic, 1f);
        } else {
            constraintSet.setMargin(R.id.blur_view, ConstraintSet.TOP, FormatUtils.dp2Px(this, 34));
            constraintSet.setMargin(R.id.player_view, ConstraintSet.TOP, FormatUtils.dp2Px(this, 34));
            constraintSet.setMargin(R.id.tv_title_media_expand, ConstraintSet.TOP, FormatUtils.dp2Px(this, 28));
            constraintSet.setMargin(R.id.quick_action_container, ConstraintSet.TOP, FormatUtils.dp2Px(this, 12));
            constraintSet.setMargin(R.id.time_bar_expand, ConstraintSet.TOP, FormatUtils.dp2Px(this, 16));
            setMarginTopByFactor(binding.tabOptionMusic, 0f);
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