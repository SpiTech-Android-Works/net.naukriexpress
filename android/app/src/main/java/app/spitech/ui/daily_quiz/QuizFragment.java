package app.spitech.ui.daily_quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.models.DataBin;
import app.spitech.ui.daily_quiz.adapter.QuizAdapter;
import app.spitech.ui.test.TestDetails;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.BaseFragment;

/**
 * Created by in.gdc4gpatnew.spitech on 12/22/17.
 */

public class QuizFragment extends BaseFragment {

    String tag = getClass().getSimpleName();
    private QuizAdapter testAdapter;
    private ArrayList<DataBin> testList;
    private ListView listView;
    private TextView emptyElement;
    private ProgressBar progressBar;

    public QuizFragment() {

    }

    public QuizFragment newInstance(String monthName, String year) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        int monthNumber = 0;
        try {
            Date date = new SimpleDateFormat("MMM").parse(monthName);//put your month name here
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            monthNumber = cal.get(Calendar.MONTH) + 1;
        } catch (Exception e) {

        }
        args.putString("month", String.valueOf(monthNumber));
        args.putString("year", year);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_daily_quiz, container, false);
        listView = rootView.findViewById(R.id.listView);
        emptyElement = rootView.findViewById(R.id.emptyElement);
        progressBar = rootView.findViewById(R.id.progressBar);
        listView.setEmptyView(emptyElement);

        loadData(getArguments().getString("month"), getArguments().getString("year"));
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getActivity(), TestDetails.class);
            intent.putExtra("test_id", testList.get(i).getRowId());
            startActivity(intent);
        });
        session=new AppSession(getContext());
        return rootView;
    }

    private void loadData(final String month, final String year) {
        testList = new ArrayList<>();
        testAdapter = new QuizAdapter(getActivity(), testList);
        listView.setAdapter(testAdapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "get_daily_quiz",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("get_daily_quiz", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin obj = null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                obj = new DataBin();
                                obj.setRowId(value.getString("test_id"));
                                obj.setName(value.getString("name"));
                                obj.setDate(value.getString("publish_date"));
                                obj.setStudentCount(value.getString("student_count"));
                                obj.setDuration(value.getString("duration"));
                                obj.setTotalQuestions(value.getString("total_questions"));
                                obj.setTotalMarks(value.getString("total_marks"));
                                obj.setIsAttempted(value.getInt("is_attempted"));
                                testList.add(obj);
                            }
                            testAdapter.notifyDataSetChanged();
                        } else {
                            emptyElement.setText("No record found.");
                        }

                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error ->{
                    progressBar.setVisibility(View.GONE);
                    Log.e(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("customer_id", session.getUserId());
                params.put("month", month);
                params.put("year", year);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}
