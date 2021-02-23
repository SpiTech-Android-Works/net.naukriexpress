package com.kdl.naukriexpress.ui.toppers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.models.DataBin;


/**
 * Created by Hp on 7/17/2016.
 */

public class TodaysAllToperAdapter extends RecyclerView.Adapter<TodaysAllToperAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;

    // RecyclerView recyclerView;
    public TodaysAllToperAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todays_all_topers_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(data.getName());
        holder.rank.setText(data.getRank());
        if(data.getOnlineStatus().equalsIgnoreCase("1")){
            holder.online_status.setTextColor(context.getResources().getColor(R.color.green));
            holder.online_status.setText("Online");
        }else{
            holder.online_status.setTextColor(context.getResources().getColor(R.color.red));
            holder.online_status.setText("Offline");
        }
        SpiTech.getInstance().loadRoundedImage(context,data.getImage(), holder.photo,R.drawable.ic_user);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,rank,online_status;
        public ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            online_status = itemView.findViewById(R.id.online_status);
            rank =itemView.findViewById(R.id.rank);
            photo =itemView.findViewById(R.id.photo);
        }
    }

}