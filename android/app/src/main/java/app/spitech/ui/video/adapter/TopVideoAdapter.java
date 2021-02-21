package app.spitech.ui.video.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.models.VideoModel;


/**
 * Created by Hp on 7/17/2016.
 */

public class TopVideoAdapter extends RecyclerView.Adapter<TopVideoAdapter.VideoInfoHolder> {

    LayoutInflater inflater;
    ArrayList<VideoModel> list;
    Context context;

    public TopVideoAdapter(Context context, ArrayList<VideoModel> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public TopVideoAdapter.VideoInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_video_row, parent, false);
        return new TopVideoAdapter.VideoInfoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoInfoHolder holder, final int position) {
        final VideoModel data = list.get(position);

        final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
            @Override
            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                Log.e("YoutubeVideo", errorReason.toString());
            }

            @Override
            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                youTubeThumbnailView.setVisibility(View.VISIBLE);
                holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
            }
        };

        holder.youTubeThumbnailView.initialize(context.getResources().getString(R.string.youtube_api_key), new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                youTubeThumbnailLoader.setVideo(data.getUrl());
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                //Toast.makeText(context,youTubeInitializationResult.toString(), Toast.LENGTH_LONG).show();
                Log.e("YoutubeVideo1", youTubeInitializationResult.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VideoInfoHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relativeLayoutOverYouTubeThumbnailView;
        public ImageView playButton;
        YouTubeThumbnailView youTubeThumbnailView;

        public VideoInfoHolder(View itemView) {
            super(itemView);
            playButton = itemView.findViewById(R.id.btnYoutube_player);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, context.getResources().getString(R.string.youtube_api_key), list.get(getLayoutPosition()).getUrl());
                    context.startActivity(intent);
                }
            });
            relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
            youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
        }

    }
}


