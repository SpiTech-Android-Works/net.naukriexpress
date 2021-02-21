package app.spitech.ui.toppers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import app.spitech.ui.toppers.adapter.TodaysAllToperAdapter;

public class TodaysAllToppers extends BaseActivity {

    private RecyclerView recyclerViewToppers;
    private String test_id="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.todays_all_toppers);
        init();
    }

    void init(){
        load(TodaysAllToppers.this,"Toppers","Toppers");
        progressBar=findViewById(R.id.progressBar);
        toolbar=findViewById(R.id.toolbar);
        toolbar_title=findViewById(R.id.toolbar_title);
        toolbar_title.setText("Today's All Topers");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerViewToppers=findViewById(R.id.recyclerViewToppers);
        recyclerViewToppers.setLayoutManager(new LinearLayoutManager(context));
        loadData();
    }

    public void loadData() {
        ArrayList<DataBin> list= new ArrayList<>();
        TodaysAllToperAdapter adapter=new TodaysAllToperAdapter(context,list);
        recyclerViewToppers.setAdapter(adapter);
        String tag="toppers";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi+ "toppers",
                response -> {
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data=null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data=new DataBin();
                                String photo= AppConfig.mediaCustomer+value.getString("photo");
                                data.setRowId(value.getString("customer_id"));
                                data.setImage(photo);
                                data.setName(value.getString("name"));
                                data.setRank(value.getString("rank"));
                                data.setOnlineStatus("1");
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            showAlert("No records found");
                        }
                    } catch (Exception ex) {
                        Log.e(tag,ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("limit","0");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
