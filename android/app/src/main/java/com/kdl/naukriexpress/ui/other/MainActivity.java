package com.kdl.naukriexpress.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.ui.auth.Login;
import com.kdl.naukriexpress.ui.home.Home;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class MainActivity extends BaseActivity {


    private SliderLayout mDemoSlider;
    private Button btnLogin, btnRegister, btnGoogle, btnFacebook;
    private int RC_SIGN_IN = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.main_activity);
        init();
    }

    void init() {
        load(MainActivity.this, "MainActivity", "Login");

        mDemoSlider = findViewById(R.id.slider);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(v -> startActivity(new Intent(context, Login.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(context, Login.class)));
        loadBanner();
    }

    //---------------Google Login Start --------------------
    @Override
    protected void onStart() {
        super.onStart();
        if (session.checkLogin()) {
            startActivity(new Intent(context, Home.class));
        }
    }




    //---------Google Login Stop--------------
    void socialLogin(String account_type, String email, String name, String photo) {
        showProgress(context, "Login...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "social_login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = new String(response);
                        Log.e(tag, result);
                        try {
                            JSONObject object = new JSONObject(result);
                            String msg = object.getString("data");
                            if (object.getString("status").equalsIgnoreCase("1")) {
                                JSONObject data = new JSONObject(msg);
                                session.setEmail(data.getString("email"));
                                session.setName(data.getString("name"));
                                session.setUserId(data.getString("customer_id"));
                                session.setLogin(true);
                                Intent intent = new Intent(context, Home.class);
                                startActivity(intent);
                            } else {
                                showAlert(msg);
                            }
                            closeProgress();
                        } catch (Exception ex) {
                            closeProgress();
                            Log.e(tag, ex.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(tag, error.toString());
                        closeProgress();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();

                params.put("api_key", AppConfig.api_key);
                params.put("email", email);
                params.put("name", name);
                params.put("account_type", account_type);
                params.put("photo", photo);
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

    //---------------loading banner ------------------------------------
    public void loadBanner() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "get_banner",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = new String(response);
                        Log.e(tag, result);
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.getString("status").equalsIgnoreCase("1")) {
                                JSONArray jArray = new JSONArray(object.getString("data"));
                                for (int row = 0; row < jArray.length(); row++) {
                                    JSONObject value = jArray.getJSONObject(row);
                                    //bannerImages.put(value.getString("caption").toString(), );
                                    TextSliderView textSliderView = new TextSliderView(context);
                                    textSliderView
                                            .description(value.getString("caption").toString())
                                            .image(AppConfig.mediaProduct + value.getString("image"));
                                    mDemoSlider.addSlider(textSliderView);


                                }
                                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                                mDemoSlider.setDuration(4000);
                            }
                        } catch (Exception ex) {
                            Log.e(tag, ex.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(tag, error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();

                params.put("api_key", AppConfig.api_key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backToClose();
    }
}
