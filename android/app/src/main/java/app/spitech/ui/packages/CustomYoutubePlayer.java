package app.spitech.ui.packages;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


import app.spitech.R;
import app.spitech.appSDK.AppSession;

public class CustomYoutubePlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    YouTubePlayer mPlayer;
    YouTubePlayerView youtube_player_view;
    AppSession session;

    private View video_control;
    private TextView mPlayTimeTextView;
    private Handler mHandler = null;
    private SeekBar mSeekBar;
    private ImageView btnPlay,btnResize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.customer_youtube_player);
        init();
    }

    void init() {
        session = new AppSession(CustomYoutubePlayer.this);
        youtube_player_view = findViewById(R.id.youtube_player_view);
        video_control = findViewById(R.id.video_control);
        btnPlay= findViewById(R.id.btnPlay);
        btnResize= findViewById(R.id.btnResize);
        mPlayTimeTextView = findViewById(R.id.play_time);
        mSeekBar = findViewById(R.id.video_seekbar);
        mSeekBar.setOnSeekBarChangeListener(mVideoSeekBarChangeListener);
        mHandler = new Handler();

        youtube_player_view.initialize(getApplicationContext().getResources().getString(R.string.youtube_api_key), this);

        btnPlay.setOnClickListener(v -> {
            if (null != mPlayer){
                if(mPlayer.isPlaying()){
                    btnPlay.setImageResource(R.drawable.ic_videoplay);
                    mPlayer.pause();
                }else{
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    mPlayer.play();
                }
            }
        });
        btnResize.setOnClickListener(v -> {
            if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                mPlayer.setFullscreen(true);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else{
                mPlayer.setFullscreen(false);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        mPlayer=youTubePlayer;
        /** add listeners to YouTubePlayer instance **/
        mPlayer.setPlayerStateChangeListener(playerStateChangeListener);
        mPlayer.setPlaybackEventListener(playbackEventListener);
        /** Start buffering **/
        if (!wasRestored) {
            mPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
            mPlayer.cueVideo(session.getKeyCurrentVideoUrl());
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    SeekBar.OnSeekBarChangeListener mVideoSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            long lengthPlayed = (mPlayer.getDurationMillis() * progress) / 100;
            mPlayer.seekToMillis((int) lengthPlayed);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void displayCurrentTime() {
        if (null == mPlayer) return;

        String formattedTime = formatTime(mPlayer.getDurationMillis() - mPlayer.getCurrentTimeMillis());
        mPlayTimeTextView.setText(formattedTime);
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        return (hours == 0 ? "--:" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.release();
        }
        super.onDestroy();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            displayCurrentTime();
            mHandler.postDelayed(this, 100);
        }
    };



    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }
        @Override
        public void onPaused() {
            mHandler.removeCallbacks(runnable);
        }

        @Override
        public void onPlaying() {

            // mPlayer.
            mHandler.postDelayed(runnable, 100);
            displayCurrentTime();
        }

        @Override
        public void onSeekTo(int arg0) {
            mHandler.postDelayed(runnable, 100);
        }

        @Override
        public void onStopped() {
            mHandler.removeCallbacks(runnable);
        }

    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
            displayCurrentTime();
        }
    };

}