package com.kdl.naukriexpress.ui.daily_quiz.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.daily_quiz.DailyQuiz;
import com.kdl.naukriexpress.ui.test.ScoreCard;
import com.kdl.naukriexpress.ui.test.TestDetails;
import com.kdl.naukriexpress.appSDK.SpiTech;

/**
 * Created by Hp on 7/17/2016.
 */

public class QuizAdapter extends BaseAdapter {

    ArrayList<DataBin> list;
    Context context;
    DailyQuiz parent;
    private LayoutInflater inflater = null;

    public QuizAdapter(Context context, ArrayList<DataBin> list) {
        this.context = context;
        this.list = list;
        parent = (DailyQuiz) context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        final ViewHolder holder;
        DataBin data = list.get(position);
        if (view == null) {
            vi = inflater.inflate(R.layout.daily_quiz_row, null);
            holder = new QuizAdapter.ViewHolder();
            holder.name = vi.findViewById(R.id.name);
            holder.date = vi.findViewById(R.id.date);
            holder.details = vi.findViewById(R.id.details);
            holder.student_count = vi.findViewById(R.id.student_count);
            holder.btnStart = vi.findViewById(R.id.btnStart);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        String name = data.getName();
        if (data.getIsAttempted() > 0) {
            name += " (ATTEMPTED)";
            holder.name.setTextColor(Color.RED);
            holder.btnStart.setText("Result");
            holder.btnStart.setBackground(parent.getDrawable(R.drawable.button_green));
        } else {
            holder.name.setTextColor(Color.BLACK);
            holder.btnStart.setText("Start");
            holder.btnStart.setBackground(parent.getDrawable(R.drawable.button_orange));
        }
        holder.name.setText(name);
        String test_date = SpiTech.getInstance().getMyDate("dd/MMM/yyyy", data.getDate());
        holder.date.setText(test_date);
        String strDetails = data.getTotalMarks() + " Marks | " + data.getDuration() + " Min | " + data.getTotalQuestions() + " Questions";
        holder.details.setText(strDetails);
        holder.student_count.setText(data.getStudentCount() + " Students");
        holder.btnStart.setOnClickListener(v -> {
            if (data.getIsAttempted() > 0) {
                parent.session.setPackageId("0"); // Daily Quiz
                parent.session.setResultId( String.valueOf(data.getIsAttempted()));
                context.startActivity(new Intent(context, ScoreCard.class));
            }else{
                Intent intent = new Intent(context, TestDetails.class);
                intent.putExtra("test_id", data.getRowId());
                intent.putExtra("back_screen_name", "DailyQuiz");
                context.startActivity(intent);
            }
        });
        return vi;
    }

    public static class ViewHolder {
        public TextView name, date, details, student_count;
        public Button btnStart;
    }

}
