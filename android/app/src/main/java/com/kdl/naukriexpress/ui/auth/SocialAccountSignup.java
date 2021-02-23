package com.kdl.naukriexpress.ui.auth;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;
import com.kdl.naukriexpress.appSDK.StaticMethods;
import com.kdl.naukriexpress.appSDK.Validation;
import com.kdl.naukriexpress.ui.home.Home;

public class SocialAccountSignup extends BaseActivity {

    private Button btnSubmit;
    private EditText name, email,state;
    private TextView state_id;
    private String emailId="",customer_id="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.social_login);
        init();
    }

    void init() {
        checkLogin=false;
        load(SocialAccountSignup.this, "Register", "Register");
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        btnSubmit = findViewById(R.id.btnSubmit);
        state = findViewById(R.id.state);
        state_id = findViewById(R.id.state_id);

        readyOnlyEditText(state);
        state.setOnClickListener(v -> stateDialog(SocialAccountSignup.this,"Select State",state,state_id));

        if(getIntent().hasExtra("customer_id")){ // already registered but profile is not complete
            customer_id=getIntent().getExtras().getString("customer_id");
            state_id.setText(getIntent().getExtras().getString("state_id"));
            name.setText(getIntent().getExtras().getString("name"));
            email.setText(getIntent().getExtras().getString("email"));
        }else if(getIntent().hasExtra("email")){
            emailId=getIntent().getExtras().getString("email");
            email.setText(emailId);
            name.setText(getIntent().getExtras().getString("name"));
        }
        btnSubmit.setOnClickListener(view -> validate());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.checkLogin()) {
            startActivity(new Intent(context, Home.class));
        }
    }

    void validate() {
        if (Validation.isNotEmpty(name.getText().toString())) {
            if (Validation.isNotEmpty(state.getText().toString())) {
                registerAndSaveToken();
            } else {
                showAlert("Please select state");
            }
        } else {
            showAlert("Name is required");
        }
    }

    //------------REGISTER USER'S FCM TOKEN BEGIN-------------------
    public void registerAndSaveToken() {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.fcmTopic);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token=task.getResult().getToken();
                session.setToken(token);
                submit(token);
            }
        });
    }

    void submit(String token) {
        showProgress(context, "Creating Account...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "social_register",
                response -> {
                    closeProgress();
                    Log.e(tag, response);
                    try {
                        JSONObject object = new JSONObject(response);
                        String message = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            session.setEmail(data.getString("email"));
                            session.setName(data.getString("name"));
                            session.setMobile(data.getString("mobile"));
                            session.setUserId(data.getString("customer_id"));
                            session.setPhoto(data.getString("photo"));
                            session.setCode(data.getString("code"));
                            session.setLogin(true);
                            startActivity(new Intent(context, Home.class));
                            finish();
                        } else {
                            showAlert(message);
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    Log.e(tag, error.toString());
                    closeProgress();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Log.e("FCM_TOKEN",token);
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("name", name.getText().toString());
                params.put("email", email.getText().toString());
                params.put("state_id", state_id.getText().toString());
                params.put("fcm_token", token);
                params.put("device_name", StaticMethods.getDeviceName());
                params.put("device_id", getDeviceId(context));
                params.put("ip_address", StaticMethods.getIpAddress(getApplicationContext()));
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
