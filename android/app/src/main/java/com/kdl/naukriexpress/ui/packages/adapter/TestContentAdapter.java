package com.kdl.naukriexpress.ui.packages.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.CustomUI;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.test.TestDetails;


public class TestContentAdapter extends RecyclerView.Adapter<TestContentAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    CustomUI customUI;

    public TestContentAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public TestContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_test_row, parent, false);
        return new TestContentAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TestContentAdapter.ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        holder.name.setText(data.getTitle());
        String str_details = data.getTotalMarks() + " Marks | " + data.getDuration() + " mins | " + data.getTotalQuestions() + " Questions";
        holder.details.setText(str_details);
        holder.btnOpen.setOnClickListener(v -> {
            Intent intent = new Intent(context, TestDetails.class);
            intent.putExtra("test_id", data.getRowId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, student_count, details,btnOpen;
        public LinearLayout linearLayoutView;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            btnOpen= itemView.findViewById(R.id.btnOpen);
            name = itemView.findViewById(R.id.name);
            details = itemView.findViewById(R.id.details);
            image = itemView.findViewById(R.id.image);
        }
    }

}