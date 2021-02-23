package com.kdl.naukriexpress.ui.downloads.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.downloads.Download;


public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context mContex;

    public ExamAdapter(Context context1, ArrayList<DataBin> list1) {
        this.mContex = context1;
        this.list = list1;
        inflater = (LayoutInflater) mContex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_row, parent, false);
        return new ExamAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);
        holder.name.setText(data.getTitle());
        holder.count.setText("File Count :"+data.getCount());
        holder.btnNext.setOnClickListener(v -> {
            Intent intent=new Intent(mContex, Download.class);
            intent.putExtra("exam_id",data.getRowId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContex.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, count;
        public ImageView btnNext;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            count = itemView.findViewById(R.id.count);
            btnNext = itemView.findViewById(R.id.btnNext);
        }
    }
}