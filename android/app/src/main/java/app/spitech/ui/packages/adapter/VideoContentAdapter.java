package app.spitech.ui.packages.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.util.Util;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.io.File;
import java.util.ArrayList;

import app.spitech.R;
import app.spitech.models.DataBin;
import app.spitech.ui.common.VideoPlayer;
import app.spitech.ui.common.VideoViewPlayer;
import app.spitech.ui.packages.ContentVideo;


public class VideoContentAdapter extends RecyclerView.Adapter<VideoContentAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    ContentVideo parent;

    public VideoContentAdapter(Context context1, ArrayList<DataBin> list1, ContentVideo parent1) {
        this.context = context1;
        this.list = list1;
        this.parent=parent1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_youtube_video_row, parent, false);
        return new VideoContentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                Log.e("YoutubeVideo", errorReason.toString());
            }
            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };

        holder.thumbnail.initialize(context.getResources().getString(R.string.youtube_api_key), new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(data.getUrl());
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
            }
            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                Log.e("YoutubeVideo1", youTubeInitializationResult.toString());
            }
        });

        File file=new File (context.getFilesDir(),data.getUrl()+ ".mp4");
        if(file.exists()){
            holder.btnDownload.setVisibility(View.GONE);
        }else{
            holder.btnDownload.setVisibility(View.VISIBLE);
        }
        holder.name.setText(data.getName());
        holder.btnDownload.setOnClickListener(v -> {

            parent.checkPermision(data.getUrl(),holder.btnDownload);
        });
        holder.btnPlay.setOnClickListener(v -> {
            playVideo(data);
        });
    }

    void playVideo(DataBin data){
        parent.session.setKeyCurrentVideoUrl(data.getUrl());
        parent.session.setKeyCurrentVideoId(data.getRowId());
        if (Util.SDK_INT >= 28) {
            Intent intent=new Intent(context, VideoPlayer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else{
            Intent intent=new Intent(context, VideoViewPlayer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
        public ImageView playButton;
        public Button btnDownload,btnPlay;
        YouTubeThumbnailView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            playButton = itemView.findViewById(R.id.btnYoutube_player);
            thumbnail =itemView.findViewById(R.id.thumbnail);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            btnDownload = itemView.findViewById(R.id.btnDownload);
            btnPlay = itemView.findViewById(R.id.btnPlay);
        }
    }



}