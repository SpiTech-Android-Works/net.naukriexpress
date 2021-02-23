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


public class ToppersTestAdapter extends RecyclerView.Adapter<ToppersTestAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;

    // RecyclerView recyclerView;
    public ToppersTestAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.topers_test_row, parent, false);
        return new ToppersTestAdapter.ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,rank;
        public ImageView photo;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rank = itemView.findViewById(R.id.rank);
            photo= itemView.findViewById(R.id.photo);
        }
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(data.getName());
        holder.rank.setText("Rank : "+data.getRank());
        SpiTech.getInstance().loadImage(context,data.getImage(), holder.photo);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}