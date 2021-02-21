package app.spitech.ui.common;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;

import java.io.File;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;

public class VideoViewPlayer extends BaseActivity {

    Button btnSetPlayback;
    VideoView playerView;
    MediaPlayer mp;
    MediaController mc;
    String downloadURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view_player);
        init();
    }

    void init() {
        checkInternet = false;
        load(VideoViewPlayer.this, "VideoPlayer", "Video Player");

        playerView = findViewById(R.id.playerView);
        btnSetPlayback = (Button) findViewById(R.id.btnSetPlayback);
        btnSetPlayback.setOnClickListener(v -> {
            openDialog();
        });
        if (session.getKeyCurrentVideoUrl() != null) {
            downloadURL = AppConfig.appFolderPath + session.getKeyCurrentVideoUrl() + ".mp4";
            showLog("downloadURL", downloadURL);
            File file = new File(downloadURL);
            if (file.exists()) {
                initializePlayer(1.0F);
            } else {
                YTDownload(session.getKeyCurrentVideoUrl());
            }
        }
    }

    private void initializePlayer(Float speed) {
        mc = new MediaController(this);
        Uri vidFile = Uri.parse(downloadURL);
        playerView.setVideoURI(vidFile);
        mc.setAnchorView(playerView);
        playerView.setMediaController(mc);
        playerView.setOnPreparedListener(mp -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                PlaybackParams myPlayBackParams= new PlaybackParams();
                myPlayBackParams.setSpeed(speed);
                mp.setPlaybackParams(myPlayBackParams);
            }
        });
        playerView.start();
    }


    public void YTDownload(String videoId) {
        @SuppressLint("StaticFieldLeak") YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                try {
                    if (ytFiles != null) {
                        downloadURL = ytFiles.get(18).getUrl();    // tag=22 or 18
                        initializePlayer(1.0F);
                    }
                } catch (Exception ex) {
                    showLog("getVideoUrl", ex.toString());
                }
            }
        };
        String VideoURLDownload = "https://www.youtube.com/watch?v=" + videoId;
        youTubeUriExtractor.execute(VideoURLDownload);
    }

    void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Speed");
        String[] animals = {"1x", "1.2x", "1.5x", "2x"};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    btnSetPlayback.setText("1x");
                    initializePlayer(1F);
                    break;
                case 1:
                    btnSetPlayback.setText("1.2x");
                    initializePlayer(1.2F);
                    break;
                case 2:
                    btnSetPlayback.setText("1.5x");
                    initializePlayer(1.5F);
                    break;
                case 3:
                    btnSetPlayback.setText("2x");
                    initializePlayer(2F);
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        if (!isFinishing()) {
            dialog.show();
        }
    }

}