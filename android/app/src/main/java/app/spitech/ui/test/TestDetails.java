package app.spitech.ui.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.ui.auth.Login;
import app.spitech.ui.daily_quiz.DailyQuiz;
import app.spitech.ui.other.MainActivity;
import app.spitech.appSDK.BaseActivity;

public class TestDetails extends BaseActivity {

    private String back_screen_name = "", test_id = "0",package_id="0";
    private TextView total_questions, total_marks, duration;
    private WebView description;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.test_details);
        init();
    }

    void init() {
        load(TestDetails.this, "SpitechTestDetails", "Test Details");
       //aHideMenuList.add(R.id.action_notification);
        total_questions = findViewById(R.id.total_questions);
        duration = findViewById(R.id.duration);
        total_marks = findViewById(R.id.total_marks);
        description = findViewById(R.id.description);
        btnStart = findViewById(R.id.btnStart);

        if (getIntent().hasExtra("back_screen_name")) {
            back_screen_name = getIntent().getExtras().getString("back_screen_name");
        }
        if (getIntent().hasExtra("test_id")) {
            test_id = getIntent().getExtras().getString("test_id");
        }
        if (session.getPackageId()!=null) {
            package_id = session.getPackageId();
        }

        loadTestDetails();
        btnStart.setOnClickListener(v -> {
            if (getIntent().hasExtra("test_id")) {
                if (session.checkLogin()) {
                    startTest();
                } else {
                    Intent intent = new Intent(context, Login.class);
                    intent.putExtra("goto", "TestDetails");
                    intent.putExtra("test_id", String.valueOf(test_id));
                    startActivity(intent);
                }
            }
        });
    }

    void startTest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.testApi + "start_test",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("data");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(msg);
                            Intent intent = new Intent(context, TestStart.class);
                            intent.putExtra("test_id", test_id);
                            intent.putExtra("duration", data.getInt("duration"));
                            intent.putExtra("result_id", data.getString("result_id"));
                            intent.putExtra("test_name", data.getString("test_name"));
                            startActivity(intent);
                            finish();
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
                params.put("customer_id", session.getUserId());
                params.put("test_id", test_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    void loadTestDetails() {
        showProgress(context, "Loading Details...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.testApi + "test_details",
                response -> {
                    closeProgress();
                    try {
                        /*Log.e(tag, response);*/
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("data");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(msg);
                            total_marks.setText(":  " + setStringValue(data.getString("total_marks")));
                            total_questions.setText(":  " + setStringValue(data.getString("total_questions")));
                            duration.setText(":  " + setStringValue(data.getString("duration")));
                            setTitle(data.getString("name"));
                            description.loadData(data.getString("description"), "text/html", "UTF-8");
                        } else {
                            showAlert(msg);
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
                Map<String, String> params = new HashMap();

                params.put("api_key", AppConfig.api_key);
                params.put("customer_id",session.getUserId());
                params.put("package_id",package_id);
                params.put("test_id", test_id);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Log.e(tag, back_screen_name);
                Intent intent = null;
                if (back_screen_name.equalsIgnoreCase("DailyQuiz")) {
                    intent = new Intent(context, DailyQuiz.class);
                } else {
                    intent = new Intent(context, MainActivity.class);
                }
                startActivity(intent);
            }
            default:
                return true;
        }
    }
}
