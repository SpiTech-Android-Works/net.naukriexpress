package com.kdl.naukriexpress.ui.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.BaseFragment;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.orders.adapter.OrderAdapter;

public class TabPackageOrder extends BaseFragment {

    RecyclerView recyclerView;
    public TabPackageOrder() {

    }
    public static TabPackageOrder newInstance() {
        TabPackageOrder fragment = new TabPackageOrder();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_test_package, container, false);
        init(rootView);
        return rootView;
    }

    void init(View rootView) {
        tag = "TabPackageOrder";
        mContext=getContext();
        session = new AppSession(getContext());
        progressBar= rootView.findViewById(R.id.progressBar);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        loadData();
    }

    void loadData() {
        ArrayList list = new ArrayList<DataBin>();
        OrderAdapter adapter = new OrderAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.orderApi + "order_list",
                response -> {
                     progressBar.setVisibility(View.GONE);
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String message = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(object.getString("data"));
                            DataBin data1 = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data1 = new DataBin();
                                data1.setRowId(value.getString("order_id"));
                                data1.setDate(value.getString("order_date_time"));
                                data1.setRate(value.getString("net_amount"));
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
                params.put("customer_id", session.getUserId());
                params.put("type", "57");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(stringRequest);
    }

}
