package app.spitech.ui.store;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

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
import app.spitech.ui.store.adapter.StoreAdapter;

public class Store extends BaseActivity {

    RecyclerView recyclerView;
    SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.store);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(Store.this, "Store", "All Courses &amp; Test Packages");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Store");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setText("There is no gallery");
        //---------Basic End------------
        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search Course/Package Name");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        loadData();


    }

    StoreAdapter adapter;
    public void loadData() {
        String tag = "product_list";
        ArrayList<DataBin> list = new ArrayList<>();
        adapter = new StoreAdapter(context, list);
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.productApi + "product_list",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("package_id"));
                                data.setName(value.getString("name"));
                                data.setImage(value.getString("image"));
                                data.setRate(value.getString("price"));
                                data.setOldRate(value.getString("old_price"));
                                data.setProductFlag(value.getString("product_flag"));
                                data.setShortDescription(value.getString("short_description"));
                                data.setIsPurchased(value.getString("is_purchased"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ex) {
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
                params.put("type", "57");
                params.put("customer_id", session.getUserId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}