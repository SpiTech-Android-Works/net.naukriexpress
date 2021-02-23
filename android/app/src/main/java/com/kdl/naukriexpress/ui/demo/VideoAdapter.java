package com.kdl.naukriexpress.ui.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.models.DataBin;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    VideoContent parent;

    public VideoAdapter(Context context1, ArrayList<DataBin> list1, VideoContent parent1) {
        this.context = context1;
        this.list = list1;
        this.parent=parent1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_video_row, parent, false);
        return new VideoAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        String fileName=data.getFile();
        String encryptedVideoPath= AppConfig.appFolderName+ File.separator+fileName;
        File file=new File(encryptedVideoPath);
        if(file.exists()){
            holder.btnPlay.setImageResource(R.drawable.ic_play);
        }else{
            holder.btnPlay.setImageResource(R.drawable.ic_download);
        }
        holder.name.setText(data.getName());
        holder.btnPlay.setOnClickListener(v -> {
            parent.session.setKeyCurrentVideoUrl(data.getUrl());
            parent.session.setKeyCurrentVideoId(data.getRowId());
            parent.startDownload();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image,btnPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }

}