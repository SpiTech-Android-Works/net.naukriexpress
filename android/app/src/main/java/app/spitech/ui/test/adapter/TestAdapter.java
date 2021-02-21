package app.spitech.ui.test.adapter;

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

import app.spitech.R;
import app.spitech.models.DataBin;
import app.spitech.appSDK.CustomUI;
import app.spitech.ui.test.TestDetails;


public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<DataBin> list;
    Context context;
    CustomUI customUI;

    public TestAdapter(Context context1, ArrayList<DataBin> list1) {
        this.context = context1;
        this.list = list1;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_row, parent, false);
        return new TestAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final DataBin data = list.get(position);

        holder.name.setText(data.getName());
        holder.student_count.setText(data.getStudentCount() + " student");
        String str_details = data.getTotalMarks() + " Marks | " + data.getDuration() + " mins | " + data.getTotalQuestions() + " Questions";
        holder.details.setText(str_details);
        holder.linearLayoutView.setOnClickListener(v -> {
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
        public TextView name, student_count, details;
        public LinearLayout linearLayoutView;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            student_count = itemView.findViewById(R.id.student_count);
            details = itemView.findViewById(R.id.details);
            image = itemView.findViewById(R.id.image);
            linearLayoutView = itemView.findViewById(R.id.linearLayoutView);
        }
    }

}