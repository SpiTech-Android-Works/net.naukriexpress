package app.spitech.ui.packages.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.spitech.R;
import app.spitech.appSDK.AppSession;
import app.spitech.models.DataBin;


public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    AppSession session;

    public AudioAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        this.session=new AppSession(context1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_audio_row, parent, false);
        return new AudioAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(data.getName());
        if(data.getType().equalsIgnoreCase("pdf")){
            holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_pdf));
        }else if(data.getType().equalsIgnoreCase("test")){
            holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_test));
        }else if(data.getType().equalsIgnoreCase("audio")){
            holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_audio));
        }else if(data.getType().equalsIgnoreCase("video")){
            holder.image.setImageDrawable(context.getDrawable(R.drawable.ic_video));
        }
        holder.itemView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }

}