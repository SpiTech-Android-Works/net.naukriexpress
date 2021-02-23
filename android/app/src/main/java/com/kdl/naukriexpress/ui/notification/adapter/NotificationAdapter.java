package com.kdl.naukriexpress.ui.notification.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.ui.notification.NotificationDetails;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;

    public NotificationAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, parent, false);
        return new NotificationAdapter.ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title,date,btnView;
        public CardView linearLayoutView;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            btnView= itemView.findViewById(R.id.btnView);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.title.setText(data.getTitle());
        String date="Publish Date : "+SpiTech.getInstance().getMyDate("dd/MMM/YYYY",data.getDate());
        holder.date.setText(date);
        holder.btnView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NotificationDetails.class);
            intent.putExtra("notification_id", data.getRowId());
            intent.putExtra("title", data.getTitle());
            intent.putExtra("date", date);
            intent.putExtra("description", data.getDescription());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}