package app.spitech.ui.chat.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.BaseFragment;
import app.spitech.models.DataBin;
import app.spitech.ui.chat.ChatHome;
import app.spitech.ui.chat.group.adapter.GroupListAdapter;

public class GroupListFragment extends BaseFragment {

    ChatHome parent;
    private RecyclerView recyclerView;
    GroupListAdapter adapter1;
    public GroupListFragment() {

    }
    public static GroupListFragment newInstance() {
        GroupListFragment fragment = new GroupListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.group_list, container, false);
        session=new AppSession(getContext());
        setHasOptionsMenu(true);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getContext(), R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);

        FloatingActionButton btnAdd=rootView.findViewById(R.id.btnAdd);
        if(AppConfig.adminMobile.equalsIgnoreCase(session.getMobile())){
            btnAdd.setVisibility(View.VISIBLE);
        }
        btnAdd.setOnClickListener(view -> {
            startActivity(new Intent(getContext(),GroupManage.class));
        });
        init();
        return rootView;
    }

    void init() {
        tag = "GroupList";
        parent = (ChatHome) getActivity();
        groupList(getContext(), recyclerView, session.getUserId());
    }

    public void groupList(Context context, RecyclerView recyclerView, String customer_id) {
        ArrayList<DataBin> list1 = new ArrayList<>();
        adapter1 = new GroupListAdapter(context, list1);
        recyclerView.setAdapter(adapter1);

        parent.showProgress(getContext(), "Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.messageApi + "group_list",
                response -> {
                    parent.closeProgress();
                    Log.e("group_list", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("group_id"));
                                data.setName(value.getString("name"));
                                data.setImage(value.getString("image"));
                                data.setDescription(value.getString("description"));
                                data.setCount(value.getString("member_count"));
                                list1.add(data);
                            }
                            adapter1.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    parent.closeProgress();
                    Log.e(tag, error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("customer_id", customer_id);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
