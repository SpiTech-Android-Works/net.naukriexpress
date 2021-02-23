package com.kdl.naukriexpress.ui.tricks;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class MnemonicDetails extends BaseActivity {

    private TextView title;
    private WebView description;
    private String mnemonic_id="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.mnemonics_details);
        init();
    }

    void init() {
        load(MnemonicDetails.this, "SpitechMnemonicDetails", "Mnemonic Details");
       //aHideMenuList.add(R.id.action_notification);
        screen=findViewById(R.id.screen);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        if(getIntent().hasExtra("mnemonic_id")){
            mnemonic_id=getIntent().getExtras().getString("mnemonic_id");
        }
        loadData();
    }

    void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "get_mnemonics_details",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("data");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(msg);
                            title.setText(setStringValue(data.getString("title")));
                            description.loadDataWithBaseURL(null,data.getString("description"),"text/html","UTF-8",null);
                        }else{
                            showAlert(msg);
                        }
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();

                params.put("api_key", AppConfig.api_key);
                params.put("mnemonic_id", mnemonic_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
