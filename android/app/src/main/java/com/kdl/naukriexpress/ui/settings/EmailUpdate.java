package com.kdl.naukriexpress.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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
import com.kdl.naukriexpress.appSDK.Validation;

public class EmailUpdate extends BaseActivity {

    private Button btnSubmit;
    private EditText username;
    private TextView current_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_update);
        init();
    }
    void init() {
        //---------Basic Begin------------
        load(EmailUpdate.this, "EmailUpdate", "Email Id Update");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Email Id Update");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        //---------Basic End------------
        username = findViewById(R.id.username);
        btnSubmit = findViewById(R.id.btnSubmit);
        current_username= findViewById(R.id.current_username);


        if(getIntent().hasExtra("username")){
            current_username.setText(getIntent().getExtras().getString("username"));
        }else{
            current_username.setText("None");
        }

        btnSubmit.setOnClickListener(view -> {
            if(Validation.isValidEmail(username.getText().toString())){
                submit();
            }else{
                showAlert("Enter valid email id");
            }
        });
    }

    void submit() {
        tag="check_account_username";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "check_account_username",
                response -> {
                    Log.e(tag, response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            Intent intent=new Intent(context, AccountUpdateOTP.class);
                            intent.putExtra("username",username.getText().toString());
                            startActivity(intent);
                        }else{
                            showAlert(object.getString("message"));
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    Log.e(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("email", username.getText().toString());
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