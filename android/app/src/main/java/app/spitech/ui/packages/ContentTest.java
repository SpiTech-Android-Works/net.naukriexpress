package app.spitech.ui.packages;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.models.DataBin;
import app.spitech.ui.packages.adapter.TestContentAdapter;

public class ContentTest extends BaseActivity {

    RecyclerView recyclerView;
    static String folder_id, package_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.package_content);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(ContentTest.this, "ContentTest", "Package Test List");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Package Test List");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBarWithText);
        emptyView = findViewById(R.id.emptyView);
        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setText("There is no test");
        //---------Basic End------------
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (getIntent().hasExtra("package_id")) {
            package_id = getIntent().getExtras().getString("package_id");
            folder_id = getIntent().getExtras().getString("folder_id");
            toolbar_title.setText(getIntent().getExtras().getString("package_name"));
            loadDetails();
        }

    }

    void loadDetails() {
        tag="folder_pdf_list";
        ArrayList list = new ArrayList<DataBin>();
        TestContentAdapter adapter = new TestContentAdapter(context, list);
        recyclerView.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.productApi + "folder_test_list",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        Log.e(tag, response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));

                            JSONArray jsonArray = new JSONArray(data.getString("aContent"));
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                DataBin data1 = new DataBin();
                                data1.setRowId(value.getString("test_id"));
                                data1.setTitle(value.getString("title"));
                                data1.setTotalMarks(value.getString("total_marks"));
                                data1.setDuration(value.getString("duration"));
                                data1.setTotalQuestions(value.getString("total_questions"));
                                list.add(data1);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("folder_id", folder_id);
                params.put("package_id", package_id);
                params.put("customer_id", session.getUserId());
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

}
