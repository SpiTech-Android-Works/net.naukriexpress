package com.kdl.naukriexpress.ui.publication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.glide.slider.library.SliderLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.AppMethods;
import com.kdl.naukriexpress.appSDK.BaseActivity;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.appSDK.Validation;
import com.kdl.naukriexpress.appSDK.services.FileDownloadService;
import com.kdl.naukriexpress.ui.cart.BookCart;

public class Publication extends BaseActivity {


    public static final String PROGRESS_UPDATE = "progress_update";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private SliderLayout mDemoSlider;
    private RecyclerView recyclerBooks;
    private TextView title;
    private WebView details;
    private Button btnBuy, btnDownloadSample;
    private LinearLayout packageContainer,publicationContainer;
    private ImageView image;
    private String attachment_file = "", package_id = "", imageUrl, price, product_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.activity_publication);
        init();
    }

    void init() {
        load(Publication.this, "Publication", "Our Publication");
       //aHideMenuList.add(R.id.action_notification);
        title = findViewById(R.id.title);
        image = findViewById(R.id.image);
        details = findViewById(R.id.details);
        btnBuy = findViewById(R.id.btnBuy);
        btnDownloadSample = findViewById(R.id.btnDownloadSample);

        packageContainer= findViewById(R.id.packageContainer);
        mDemoSlider = findViewById(R.id.liveTestSlider);

        publicationContainer= findViewById(R.id.publicationContainer);
        recyclerBooks = findViewById(R.id.recyclerBooks);
        recyclerBooks.setHasFixedSize(true);
        recyclerBooks.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));


        registerReceiver();
        btnDownloadSample.setOnClickListener(v -> {
            if (checkPermission()) {
                startDownload();
            } else {
                requestPermission();
            }
        });
        btnBuy.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookCart.class);
            intent.putExtra("package_id", package_id);
            intent.putExtra("product_name", product_name);
            intent.putExtra("price", price);
            intent.putExtra("image", imageUrl);
            startActivity(intent);

        });
        AppMethods.getInstance().productSlider(context, mDemoSlider, "0","57",packageContainer);
        if(AppConfig.enablePublication){
            AppMethods.getInstance().loadBooks(context, recyclerBooks,publicationContainer);
        }


        if (getIntent().hasExtra("package_id")) {
            package_id = getIntent().getExtras().getString("package_id");
            loadData();
            btnBuy.setVisibility(View.VISIBLE);
        }else{
            btnBuy.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.productApi + "product_details",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("data");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));

                            price = data.getString("price");
                            btnBuy.setText("Buy in (Rs."+price+")");
                            imageUrl = AppConfig.mediaProduct + data.getString("web_image");
                            product_name = data.getString("name");

                            title.setText(setStringValue(data.getString("name")));
                            details.loadDataWithBaseURL(null, data.getString("description"), "text/html", "UTF-8", null);

                            attachment_file = setStringValue(data.getString("attachment"));
                            if (Validation.isNotEmpty(attachment_file)) {
                                btnDownloadSample.setVisibility(View.VISIBLE);
                            } else {
                                btnDownloadSample.setVisibility(View.GONE);
                            }
                            SpiTech.getInstance().loadImage(context, imageUrl, image);
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
                params.put("package_id", package_id);
                params.put("customer_id", session.getUserId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //----------download start-----------
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PROGRESS_UPDATE)) {
                boolean downloadComplete = intent.getBooleanExtra("downloadComplete", false);
                String file_name = intent.getStringExtra("file_name");
                if (downloadComplete) {
                    Log.e("file_name2", file_name);
                    Toast.makeText(context, "File download completed", Toast.LENGTH_SHORT).show();
                    openFile(context, file_name);
                }
            }
        }
    };


    void startDownload() {
        Log.e("file_name", attachment_file);
        Intent intent = new Intent(context, FileDownloadService.class);
        intent.putExtra("msg", "Downloading file");
        intent.putExtra("file_name", attachment_file);
        intent.putExtra("media_folder_url", AppConfig.mediaProduct);
        startService(intent);
    }

    private void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PROGRESS_UPDATE);
        bManager.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    Toast.makeText(context.getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    //----------download stop-----------------

}
