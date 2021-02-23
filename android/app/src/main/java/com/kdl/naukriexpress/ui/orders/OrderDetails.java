package com.kdl.naukriexpress.ui.orders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;
import com.kdl.naukriexpress.appSDK.ConvertTo;
import com.kdl.naukriexpress.appSDK.HelperMethods;

public class OrderDetails extends BaseActivity {

    TextView order_id,order_date,amount,package_name;
    WebView package_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);
        init();
    }

    void init() {
        load(OrderDetails.this,"OrderDetails","Order Details");
        progressBar=findViewById(R.id.progressBar);
        toolbar=findViewById(R.id.toolbar);
        toolbar_title=findViewById(R.id.toolbar_title);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        toolbar_title.setText("Order Details");
        order_id=findViewById(R.id.order_id);
        order_date=findViewById(R.id.order_date);
        amount=findViewById(R.id.amount);
        package_name=findViewById(R.id.package_name);
        package_details=findViewById(R.id.package_details);
        if (getIntent().hasExtra("order_id")) {
            loadData(getIntent().getExtras().getString("order_id"));
        }
    }

    void loadData(String orderId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.orderApi + "order_details",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            order_id.setText(data.getString("order_id"));
                            String datetime= ConvertTo.customDate(data.getString("order_date_time"),"yyyy-MM-dd HH:mm:ss","dd/MMM/yyyy hh:mm:ss a");
                            order_date.setText(datetime);
                            amount.setText(HelperMethods.getRs(Double.parseDouble(data.getString("net_amount"))));
                            package_name.setText(data.getString("package_name"));
                            package_details.loadData(data.getString("package_details"),"text/html","UTF-8");
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
                params.put("order_id", orderId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}