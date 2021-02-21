package app.spitech.ui.downloads;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.models.DataBin;
import app.spitech.ui.downloads.adapter.ExamAdapter;

public class Exam extends BaseActivity {

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.exam_list);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(Exam.this, "Exam", "Select Exam");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Downloads");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        emptyView=findViewById(R.id.emptyView);
        emptyTextView=findViewById(R.id.emptyTextView);
        emptyTextView.setText("No records found");
        //---------Basic End------------
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        loadData();
    }

    public void loadData() {
        tag="exam_list";
        ArrayList<DataBin> list = new ArrayList<>();
        ExamAdapter adapter = new ExamAdapter(context, list);
        recyclerView.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "exam_list",
                response -> {
                    try {
                        showLog(tag, response);
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("exam_id"));
                                data.setTitle(value.getString("name"));
                                data.setCount(value.getString("count"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException ex) {
                        showLog(tag, ex.toString());
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    showLog(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("purpose", "download");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}