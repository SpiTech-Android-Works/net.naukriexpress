package app.spitech.ui.job;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import app.spitech.adapter.CommonJNSASListingAdapter;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.models.DataBin;

public class JobListing extends BaseActivity {

    RecyclerView recyclerView;
    CommonJNSASListingAdapter adapter;
    ArrayList<DataBin> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.job_listing);
        init();
    }

    void init() {
        load(JobListing.this, "JobListing", "Jobs");
       //aHideMenuList.add(R.id.action_notification);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(context, R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);
        loadData();
    }


    void loadData() {
        list = new ArrayList<>();
        adapter = new CommonJNSASListingAdapter(context, list, "jobs");
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "job_list",
                response -> {
                    Log.e("job_list", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("job_id"));
                                data.setTitle(value.getString("title"));
                                data.setDate(value.getString("publish_date"));
                                data.setPost(value.getString("posts"));
                                data.setViewCounter(value.getString("view_counter"));
                                String image= AppConfig.mediaBlog+value.getString("image");
                                data.setImage(image);
                                data.setTime(value.getString("time"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("customer_id", session.getUserId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
