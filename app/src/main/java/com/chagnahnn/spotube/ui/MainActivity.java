package com.chagnahnn.spotube.ui;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.chagnahnn.spotube.R;
import com.chagnahnn.spotube.core.SpotubeAppCompatActivity;
import com.chagnahnn.spotube.databinding.ActivityMainBinding;
import com.chagnahnn.spotube.service.firebase.Common;
import com.chagnahnn.spotube.ui.viewmodel.MainViewModel;
import com.chagnahnn.spotube.util.RandomUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends SpotubeAppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_shorts, R.id.navigation_explore,
                R.id.navigation_library)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        applyInsetsPadding(binding.getRoot(), 0);
        applyInsetsPadding(navView, 4);
        setRadiusPlayerView(32);

        Common.initFirebaseService();
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initView();
    }

    private void initView() {
        binding.tvTitleMediaExpand.setSelected(true);
        String[] mediaArr = {
                "https://firebasestorage.googleapis.com/v0/b/spotube-22-10-24.appspot.com/o/multimedia%2Fmusic%2FKh%C3%B4ng%20Y%C3%AAu%20Xin%20%C4%90%E1%BB%ABng%20N%C3%B3i%20-%20Prod%20Kirimi%20Remix.mp3?alt=media&token=380f8849-5637-4b2f-8c07-d84a6d94b1bd",
                "https://firebasestorage.googleapis.com/v0/b/spotube-22-10-24.appspot.com/o/multimedia%2Fmusic%2FFEVER.mp3?alt=media&token=c219410c-ea0b-44fa-b4f2-cd06746ea333",
                "https://firebasestorage.googleapis.com/v0/b/spotube-22-10-24.appspot.com/o/multimedia%2Fmusic%2FLying%20to%20myself.mp3?alt=media&token=4070ba25-fd1a-4a5a-9816-c73fc7593abd",
                "https://firebasestorage.googleapis.com/v0/b/spotube-22-10-24.appspot.com/o/multimedia%2Fmusic%2Fuocgianhaynoiyeutoi.mp3?alt=media&token=29f08ceb-ada9-42c3-8014-641cabc76ac6",
                "https://firebasestorage.googleapis.com/v0/b/spotube-22-10-24.appspot.com/o/multimedia%2Fmusic%2Fng%E1%BB%A7%20m%E1%BB%99t%20m%C3%ACnh%20(t%C3%ACnh%20r%E1%BA%A5t%20t%C3%ACnh).mp3?alt=media&token=7b297b68-3985-4723-abae-c1ec489215f0",
                "https://firebasestorage.googleapis.com/v0/b/spotube-22-10-24.appspot.com/o/multimedia%2Fmusic%2FMua%20Dong%20Ben%20Doi.mp3?alt=media&token=d757cafc-2031-48e4-8840-6e542252500f",
                "https://firebasestorage.googleapis.com/v0/b/spotube-22-10-24.appspot.com/o/multimedia%2Fmusic%2FIf%20You%20Said%20So%20(feat.%20Wxrdie%20%26%202Pillz).mp3?alt=media&token=cdbcdc22-1a49-458a-be05-5d0601d7a9e0",
                "https://firebasestorage.googleapis.com/v0/b/spotube-22-10-24.appspot.com/o/multimedia%2Fmusic%2FHIEUTHUHAI%20-%20Ai%20C%C5%A9ng%20Ph%E1%BA%A3i%20B%E1%BA%AFt%20%C4%90%E1%BA%A7u%20T%E1%BB%AB%20%C4%90%C3%A2u%20%C4%90%C3%B3%2FExit%20Sign.mp3?alt=media&token=198be641-73fa-4acc-baf1-29d7e21221b6",
        };
        int rnd = RandomUtils.getRandomInt(0, mediaArr.length - 1);
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(mediaArr[rnd]);
        player.setMediaItem(mediaItem);
        player.prepare();
//        player.play();
        binding.btnLike.setText("11,7 N");
//        binding.btnDislike.setText("99");
        binding.btnCmt.setText("1,7 N");

        binding.btnBackToCollapse.setOnClickListener(view -> {
            binding.container.transitionToState(R.id.collapse);
        });
    }

    private void setRadiusPlayerView(int radius) {
        setRadiusView(binding.playerView, radius);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
        binding = null;
        mainViewModel.clear();
    }
}