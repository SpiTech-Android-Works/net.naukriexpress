package com.kdl.naukriexpress.ui.packages;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class CustomYoutube extends BaseActivity {

    private YouTubePlayerFragment youTubePlayerFragment;

    private YouTubePlayer mPlayer;
    private View video_control;
    private TextView mPlayTimeTextView;
    private Handler mHandler = null;
    private SeekBar mSeekBar;
    private ImageView play_video,btn_resize;
    AppSession session;
    String videoId = "i5DVUUmsn10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(enableScreenCapture==false) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_youtube);
        init();
    }

    void  init(){
        load(CustomYoutube.this,"CustomYoutube","Video");
        session=new AppSession(CustomYoutube.this);
        video_control = findViewById(R.id.video_control);
        play_video= findViewById(R.id.btnPlay);
        btn_resize= findViewById(R.id.btn_resize);

        youTubePlayerFragment =(YouTubePlayerFragment)getFragmentManager().findFragmentById(R.id.youtube_player_fragment);

          youTubePlayerFragment.initialize(context.getResources().getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,boolean wasRestored) {
                mPlayer = player;
                displayCurrentTime();
                if (!wasRestored) {
                    mPlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                    mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION | YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
                    mPlayer.cueVideo(videoId); //session.getKeyCurrentVideoUrl()

                }

                //mSeekBar.setMax(mPlayer.getDurationMillis());
                video_control.setVisibility(View.VISIBLE);
                mPlayer.setPlayerStateChangeListener(mPlayerStateChangeListener);
                mPlayer.setPlaybackEventListener(mPlaybackEventListener);

            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
              showLog("Youtube","Youtube Player View initialization failed");
            }
        });


        play_video.setOnClickListener(v -> {
            if (null != mPlayer){
                if(mPlayer.isPlaying()){
                    play_video.setImageResource(R.drawable.ic_videoplay);
                    mPlayer.pause();
                }else{
                    play_video.setImageResource(R.drawable.ic_pause);

                    mPlayer.play();
                }
            }
        });
        mPlayTimeTextView = findViewById(R.id.play_time);
        mSeekBar = findViewById(R.id.video_seekbar);
        mSeekBar.setOnSeekBarChangeListener(mVideoSeekBarChangeListener);
        mHandler = new Handler();
        btn_resize.setOnClickListener(v -> {
            if(getRequestedOrientation()== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

    }

    YouTubePlayer.PlaybackEventListener mPlaybackEventListener = new YouTubePlayer.PlaybackEventListener() {
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

    YouTubePlayer.PlayerStateChangeListener mPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
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

}