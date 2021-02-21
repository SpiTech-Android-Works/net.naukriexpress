package app.spitech.ui.other;

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

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;

public class CMS extends BaseActivity {

    WebView webView;
    String page_name="mobile_about";
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.cms);
        init();
    }

    void init() {
        load(CMS.this, "CMS", "Contact Us");
       //aHideMenuList.add(R.id.action_notification);
        webView = findViewById(R.id.webview);
        progressBar= findViewById(R.id.progressBar);
        if(getIntent().hasExtra("page_name")){
            page_name=getIntent().getExtras().getString("page_name");
        }
        loadData();
    }


    void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "cms",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        Log.e(tag, response);
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            setTitle(data.getString("page_title"));
                            webView.loadData( data.getString("page_content"), "text/html", "UTF-8");
                        } else {
                            showAlert(msg);
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
                params.put("page_name",page_name);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
