package com.kdl.naukriexpress.ui.chat.student.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.chat.student.Chat;


public class FirebaseChatAdapter extends RecyclerView.Adapter<FirebaseChatAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    Chat parent;

    // RecyclerView recyclerView;
    public FirebaseChatAdapter(Context context1, ArrayList<DataBin> list1, Chat parent) {
        this.context = context1;
        this.list = list1;
        this.parent=parent;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_row, parent, false);
        return new FirebaseChatAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        if(data.getFromUserId().equalsIgnoreCase(parent.session.getUserId())){
            holder.from_layout.setVisibility(View.VISIBLE);
            holder.to_layout.setVisibility(View.GONE);
            holder.from_message.setText(data.getMessage());
            holder.from_time.setText(data.getDate());
        }else{
            holder.from_layout.setVisibility(View.GONE);
            holder.to_layout.setVisibility(View.VISIBLE);
            holder.to_message.setText(data.getMessage());
            holder.to_time.setText(data.getDate());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView from_time, to_time,from_message,to_message;
        public RelativeLayout from_layout,to_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            from_layout = itemView.findViewById(R.id.from_layout);
            to_layout = itemView.findViewById(R.id.to_layout);
            from_time = itemView.findViewById(R.id.from_time);
            to_time = itemView.findViewById(R.id.to_time);
            from_message = itemView.findViewById(R.id.from_message);
            to_message = itemView.findViewById(R.id.to_message);
        }
    }

}