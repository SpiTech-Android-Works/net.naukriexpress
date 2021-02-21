package app.spitech.ui.chat.group;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.BaseActivity;
import app.spitech.models.DataBin;
import app.spitech.ui.chat.group.adapter.AddMemberAdapter;

public class AddGroupMember extends BaseActivity {

    private RecyclerView recyclerView;
    private String group_id;
    AddMemberAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.group_member_add);
        init();
    }

    void init() {
        context = AddGroupMember.this;
        session = new AppSession(context);
        if (getIntent().hasExtra("group_id")) {
            group_id = getIntent().getExtras().getString("group_id");
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        TextView toolbar_title = findViewById(R.id.toolbar_title);
        ExtendedFloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        toolbar_title.setText("Add Members");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        studentList();
        btnAdd.setOnClickListener(view -> {
            addUsers();
        });
    }


    void addUsers() {
        showProgress(context, "Adding...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.messageApi + "group_user_add",
                response -> {
                    Log.e("group_user_add", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("message");
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            onBackPressed();
                        } else {
                            showAlert(msg);
                        }
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                    closeProgress();
                },
                error -> {
                    Log.e(tag, error.toString());
                    closeProgress();
                }) {
            @Override
            protected Map<String, String> getParams() {
                String aUserList = new Gson().toJson(adapter1.getSelectedItem());
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("group_id", group_id);
                params.put("aUser", aUserList);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void studentList() {
        ArrayList<DataBin> list1 = new ArrayList<>();
        adapter1 = new AddMemberAdapter(context, list1);
        recyclerView.setAdapter(adapter1);
        showProgress(context, "Loading...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.messageApi + "group_users_to_add",
                response -> {
                    closeProgress();
                    Log.e("user_list", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("customer_id"));
                                data.setName(value.getString("name"));
                                data.setImage(value.getString("photo"));
                                list1.add(data);
                            }
                            adapter1.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    closeProgress();
                    Log.e(tag, error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("group_id", group_id);
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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_search){
            SearchView searchView= (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter1.filter(query);
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String query) {
                    adapter1.filter(query);
                    return false;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

}