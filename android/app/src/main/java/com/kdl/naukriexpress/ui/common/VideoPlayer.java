package com.kdl.naukriexpress.ui.common;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class VideoPlayer extends BaseActivity {

    Button btnSetPlayback;
    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);
        init();
    }

    void init() {
        checkInternet = false;
        load(VideoPlayer.this, "VideoPlayer", "Video Player");

        playerView = (PlayerView)findViewById(R.id.playerView);
        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        playerView.setPlayer(player);
        btnSetPlayback = (Button) findViewById(R.id.btnSetPlayback);
        btnSetPlayback.setOnClickListener(v -> {
            openDialog();
        });
        if (session.getKeyCurrentVideoUrl() != null) {
              String downloadURL= getFilesDir()+"/"+session.getKeyCurrentVideoUrl()+ ".mp4";
//            showLog("downloadURL",downloadURL);
            File file=new File(getFilesDir(),session.getKeyCurrentVideoUrl()+ ".mp4");
            if(file.exists()){
                if (Util.SDK_INT >= 24) {
                    initializePlayer(downloadURL);
                }
            }else{
                YTDownload(session.getKeyCurrentVideoUrl());
            }
        }
    }

    public void YTDownload(String videoId) {
        @SuppressLint("StaticFieldLeak") YouTubeUriExtractor youTubeUriExtractor = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                try {
                    if (ytFiles != null) {
                        String downloadURL = ytFiles.get(18).getUrl();    // tag=22 or 18
                        if (Util.SDK_INT >= 24) {
                            initializePlayer(downloadURL);
                        }
                    }
                } catch (Exception ex) {
                    showLog("getVideoUrl", ex.toString());
                }
            }
        };
        String VideoURLDownload = "https://www.youtube.com/watch?v="+videoId;
        youTubeUriExtractor.execute(VideoURLDownload);
    }

   /* void getVideoUrl(String videoId) {
        String youtubeLink = "https://youtube.com/watch?v=" + videoId;
        showLog("VideoUrl", youtubeLink);
        YouTubeUriExtractor ytr = new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                try {
                    if (ytFiles != null) {
                        videoUrl = ytFiles.get(18).getUrl();
                        if (Util.SDK_INT >= 24) {
                            initializePlayer(videoUrl);
                        }
                    }
                } catch (Exception ex) {
                    showLog("getVideoUrl", ex.toString());
                }
            }
        };
        ytr.execute(youtubeLink);
    }*/

    private void initializePlayer(String videoUrl) {
        MediaSource mediaSource = buildMediaSource(videoUrl);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    void setSpeed(Float speed) {
        PlaybackParameters param = new PlaybackParameters(speed);
        player.setPlaybackParameters(param);
    }

    private MediaSource buildMediaSource(String uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "spitech");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(uri));
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Speed");
        String[] animals = {"1x", "1.2x", "1.5x", "2x"};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    btnSetPlayback.setText("1x");
                    setSpeed(1F);
                    break;
                case 1:
                    btnSetPlayback.setText("1.2x");
                    setSpeed(1.2F);
                    break;
                case 2:
                    btnSetPlayback.setText("1.5x");
                    setSpeed(1.5F);
                    break;
                case 3:
                    btnSetPlayback.setText("2x");
                    setSpeed(2F);
                    break;
            }
        });
        AlertDialog dialog = builder.create();
        if (!isFinishing()) {
            dialog.show();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }


}