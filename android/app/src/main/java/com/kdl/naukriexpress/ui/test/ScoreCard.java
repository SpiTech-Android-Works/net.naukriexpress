package com.kdl.naukriexpress.ui.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.ui.home.Home;
import com.kdl.naukriexpress.ui.toppers.Toppers;

public class ScoreCard extends BaseActivity {

    String result_id = "0",test_id="0",package_id="0";
    private TextView name,test_name, test_rank, no_of_ques, attemped, correct_questions, skipped, incorrect_questions, total_marks, test_category, obtained_marks, start_time, end_time, duration, date;
    private ImageView photo;
    private Button btnAnswer;
    private Button btnToppers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.score_card);
        init();
    }

    void init() {
        load(ScoreCard.this, "SpitechScoreCard", "My Score Card");
       //aHideMenuList.add(R.id.action_notification);
        screen=findViewById(R.id.screen);
        name =findViewById(R.id.name);
        photo =findViewById(R.id.photo);
        test_name =findViewById(R.id.test_name);
        test_rank =findViewById(R.id.test_rank);
        no_of_ques =findViewById(R.id.no_of_ques);
        attemped =findViewById(R.id.attemped);
        correct_questions =findViewById(R.id.correct_questions);
        incorrect_questions =findViewById(R.id.incorrect_questions);
        total_marks =findViewById(R.id.total_marks);
        obtained_marks =findViewById(R.id.obtained_marks);
        test_category =findViewById(R.id.test_category);
        start_time =findViewById(R.id.start_time);
        end_time =findViewById(R.id.end_time);
        duration =findViewById(R.id.duration);
        date =findViewById(R.id.date);
        btnAnswer =findViewById(R.id.btnAnswer);
        btnToppers =findViewById(R.id.btnToppers);
        if (session.getResultId()!=null) {
            result_id = session.getResultId();
        }
        if (session.getPackageId()!=null) {
            package_id = session.getPackageId();
        }
        loadData(result_id);
        btnAnswer.setOnClickListener(view -> {
            Intent intent=new Intent(context, Answers.class);
            intent.putExtra("test_id",test_id);
            intent.putExtra("result_id",result_id);
            startActivity(intent);
        });
        btnToppers.setOnClickListener(view -> {
            Intent intent=new Intent(context, Toppers.class);
            intent.putExtra("test_id",test_id);
            startActivity(intent);
        });

    }

    void barChart(int top, int you, int avg, int max_marks) {
        BarChart barChart = (BarChart) findViewById(R.id.barchart);
        barChart.animateY(5000);

        YAxis yl = barChart.getAxisLeft();
        yl.setAxisMaxValue(max_marks);
        yl.setAxisMinValue(0);

        YAxis yr = barChart.getAxisRight();
        yr.setAxisMaxValue(max_marks);
        yr.setAxisMinValue(0);


        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Topper");
        labels.add("Avg");
        labels.add("You");

        // create BarEntry for Bar Group 1
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry((float) top, 0));
        entries.add(new BarEntry((float) avg, 1));
        entries.add(new BarEntry((float) you, 2));


        BarDataSet bardataset = new BarDataSet(entries, "Cells");
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of labels into chart
    }

    void pieChart(int correct, int incorrect, int not_attempt) {

        PieChart pieChart = (PieChart) findViewById(R.id.chart);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("QG");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(25f);
        pieChart.setHoleRadius(25f);
        pieChart.animateXY(1400, 1400);
        pieChart.setDescription(""); //pieChart.setContentDescription("Score Card Analysis");

        ArrayList<String> labels = new ArrayList<String>();

        ArrayList<Integer> colors = new ArrayList<Integer>();
        ArrayList<Entry> entries = new ArrayList<Entry>();
        if (not_attempt > 0) {
            labels.add("Not Attempted");
            entries.add(new Entry((float) not_attempt, 0));
            colors.add(ColorTemplate.rgb("#3C8DBC"));
        }
        if (incorrect > 0) {
            labels.add("InCorrect");
            entries.add(new Entry((float) incorrect, 1));
            colors.add(ColorTemplate.rgb("#fe0000"));
        }
        if (correct > 0) {
            labels.add("Correct");
            entries.add(new Entry((float) correct, 2));
            colors.add(ColorTemplate.rgb("#709800"));
        }

        PieDataSet dataset = new PieDataSet(entries, "Cells");
        dataset.setColors(colors);

        PieData data = new PieData(labels, dataset);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        data.setDataSet(dataset);
        pieChart.setData(data);

    }

    void loadData(final String result_id) {
        showProgress(context, "Loading...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.testApi + "score_card",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            JSONObject mainData = new JSONObject(jsonObject.getString("data"));

                            String url= AppConfig.mediaCustomer+mainData.getString("photo");
                            SpiTech.getInstance().loadRoundedImage(context,url,photo,R.drawable.ic_user);

                            test_id=mainData.getString("test_id");
                            test_name.setText(mainData.getString("test_name"));
                            test_category.setText(mainData.getString("test_category"));
                            no_of_ques.setText(mainData.getString("total_question"));
                            total_marks.setText(mainData.getString("total_marks"));
                            duration.setText(mainData.getString("duration"));
                            date.setText(SpiTech.getInstance().getMyDate("dd-MMM-yyyy", mainData.getString("date")));
                            start_time.setText(mainData.getString("start_time"));
                            end_time.setText(mainData.getString("end_time"));
                            test_rank.setText("RANK : "+mainData.getString("rank"));
                            obtained_marks.setText(mainData.getString("obtained_marks"));
                            attemped.setText(mainData.getString("attempt_questions"));
                            correct_questions.setText(mainData.getString("correct"));
                            incorrect_questions.setText(mainData.getString("incorrect"));
                            name.setText(mainData.getString("name"));

                            pieChart(mainData.getInt("correct"), mainData.getInt("incorrect"), mainData.getInt("not_attempt"));
                            barChart(mainData.getInt("top_marks"), mainData.getInt("your_marks"), mainData.getInt("avg_marks"), mainData.getInt("total_marks"));

                        }
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                    closeProgress();
                },
                error -> {
                    closeProgress();
                    Log.e(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);

                params.put("customer_id", session.getUserId());
                params.put("result_id", result_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, Home.class));
        finish();
    }
}

