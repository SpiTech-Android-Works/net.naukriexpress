package com.kdl.naukriexpress.ui.downloads;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.downloads.adapter.DownloadAdapter;

public class Download extends BaseActivity {

    private String exam_id="0";
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.downloads);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(Download.this, "Download", "Download List");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Downloads");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        emptyView=findViewById(R.id.emptyView);
        emptyTextView=findViewById(R.id.emptyTextView);
        emptyTextView.setText("There is no file to download");
        //---------Basic End------------
        if(getIntent().hasExtra("exam_id")){
            exam_id=getIntent().getExtras().getString("exam_id");
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        loadData();
    }

    //-----------Best Download Begin-----------------
    TextView progressBarText;
    void showHideProgress(boolean isShow){
        progressBarText= findViewById(R.id.progressBarText);
        RelativeLayout progressBarLayout = findViewById(R.id.progress_layout);
        if(isShow){
            progressBarLayout.setVisibility(View.VISIBLE);
        }else{
            progressBarLayout.setVisibility(View.GONE);
        }
    }
    View downloading;
    private static final int REQUEST = 112;
    String downloadURL,downloadTitle,fileName;
    public void checkPermision(String downloadURL,String downloadTitle,String filename, View downloading1) {
        this.downloadURL=downloadURL;
        this.downloadTitle=downloadTitle;
        this.fileName=filename;
        this.downloading=downloading1;
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST );
            } else {
                startDownload(this.downloadURL,this.fileName,this.downloading);
            }
        } else {
            startDownload(this.downloadURL, this.fileName,this.downloading);
        }
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload(this.downloadURL,this.fileName,this.downloading);
                } else {
                    Toast.makeText(getApplicationContext(), "The app was not allowed to write in your storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void startDownload(String downloadURL,String fileName, View downloading1) {
        downloading=downloading1;
        ((Button)downloading).setText("Downloading...");
        try {
            AsyncTaskDownload asyncTask=new AsyncTaskDownload();
            asyncTask.execute(fileName,downloadURL);
        } catch (Exception ex) {
            showLog("getVideoUrl", ex.toString());
        }
    }

    private class AsyncTaskDownload extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showHideProgress(true);
            progressBarText.setText("Downloading...");
        }
        @Override
        protected String doInBackground(String... strings) {
            String downloadedUrl="";
            try {
                fileName= strings[0];
                URL u = new URL(strings[1]);
                URLConnection conn = u.openConnection();
                int contentLength = conn.getContentLength();
                DataInputStream stream = new DataInputStream(u.openStream());
                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();
                File outputFile = new File(getFilesDir(), fileName);
                DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
                fos.write(buffer);
                fos.flush();
                fos.close();
            } catch(Exception e) {
                Log.e("DownloadException",e.toString());
            }
            return downloadedUrl;
        }
        @Override
        protected void onPostExecute(String downloadedUrl) {
            super.onPostExecute(downloadedUrl);
            showHideProgress(false);
            ((Button)downloading).setText("Open PDF");
            showToast("Video Downloaded Successfully");
        }
    }
    //-----------Best Download End-----------------

    public void loadData() {
        tag="download_list";
        ArrayList<DataBin> list = new ArrayList<>();
        DownloadAdapter adapter = new DownloadAdapter(context, list,this);
        recyclerView.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "download_list",
                response -> {
                    try {
                        showLog(tag, response);
                      showHideProgress(false);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("download_id"));
                                data.setTitle(value.getString("name"));
                                data.setUrl(value.getString("file_name"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException ex) {
                        showLog(tag, ex.toString());
                    }
                },
                error -> {
                    showHideProgress(false);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("exam_id",exam_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}