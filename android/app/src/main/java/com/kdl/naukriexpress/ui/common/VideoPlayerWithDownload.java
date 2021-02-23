package com.kdl.naukriexpress.ui.common;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class VideoPlayerWithDownload extends BaseActivity {

    Button btnSetPlayback;
    String videoUrl;
    PlayerView playerView;
    SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_demo);
        init();
    }

    void init() {
        checkInternet=false;
        load(VideoPlayerWithDownload.this, "VideoPlayer", "Video Player");

        playerView = findViewById(R.id.playerView);
        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        playerView.setPlayer(player);
        btnSetPlayback = findViewById(R.id.btnSetPlayback);
        btnSetPlayback.setOnClickListener(v -> {
            openDialog();
        });
        if (session.getKeyCurrentVideoUrl() != null) {
            fileName=session.getKeyCurrentVideoUrl()+".mp4";
            if (checkFilePermissions()) {
                createFile();
            } else {
                requestFilePermissions();
            }
        }
    }

    void getVideoUrl(String videoId) {
        String youtubeLink = "http://youtube.com/watch?v=" + videoId;
        new YouTubeUriExtractor(this) {
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {
                if (ytFiles != null) {
                    int itag = 22;
                    videoUrl = ytFiles.get(itag).getUrl();
                    if (Util.SDK_INT >= 24) {
                        new downloadVideo().execute(videoUrl);
                    }
                }
            }
        }.execute(youtubeLink);
    }

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

    //-------download--------
    String fileName = "";
    void createFile() {
        try {
            String filepath = AppConfig.appFolderName+"/"+fileName;
            File file=new File(filepath);
            if(!file.exists()){
                file.createNewFile();
                getVideoUrl(session.getKeyCurrentVideoUrl());
            }else{
               initializePlayer(filepath);
            }
        } catch (Exception ex) {
            showLog("createFile", ex.toString());
        }
    }

    private final class downloadVideo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(context,"Downloading Video....");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String file_url = params[0];
                showLog("DownloadUrl",file_url);
                String documentFile = AppConfig.appFolderName+"/"+fileName;
                byte[] buff = new byte[1024 * 4];
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(file_url).build();
                Response response = client.newCall(request).execute();
                InputStream inputStream = response.body().byteStream();
                OutputStream output = new FileOutputStream(documentFile, false);
                while (true) {
                    int readed = inputStream.read(buff);
                    if (readed == -1) {
                        break;
                    }
                    output.write(buff, 0, readed);
                }
                output.flush();
                output.close();
            } catch (Exception ex) {
                Log.e("PDFLOAD", ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String downloadedFilePath) {
            closeProgress();
            initializePlayer( AppConfig.appFolderName+"/"+fileName);
        }
    }

}