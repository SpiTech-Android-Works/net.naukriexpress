package app.spitech.ui.branch;

import android.os.Bundle;
import android.util.Log;

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
import app.spitech.ui.branch.adapter.BranchAdapter;

public class BranchListing extends BaseActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.branch);
        init();
    }

    void init() {
        load(BranchListing.this, "BranchListing", "Our Branches");
       //aHideMenuList.add(R.id.action_notification);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        loadData();
    }

    public void loadData() {
        ArrayList<DataBin> list = new ArrayList<>();
        BranchAdapter adapter = new BranchAdapter(context, list);
        recyclerView.setAdapter(adapter);
        showProgress(context,"Loading...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "branch_list",
                response -> {
                    Log.e("branchList", response);
                    closeProgress();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("agent_id"));
                                data.setTitle(value.getString("branch_name"));
                                data.setDescription(value.getString("address"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    closeProgress();
                    Log.e(tag, error.toString());
             }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
