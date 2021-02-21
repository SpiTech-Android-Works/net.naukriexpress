package app.spitech.ui.packages;

import android.app.Activity;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
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

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.models.DataBin;
import app.spitech.ui.packages.adapter.PDFContentAdapter;

public class ContentPDF extends BaseActivity {

    RecyclerView recyclerView;
    static String folder_id, package_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.package_content);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(ContentPDF.this, "ContentPDF", "Package PDF List");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Package PDF List");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setText("There is no pdf");
        //---------Basic End------------
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (getIntent().hasExtra("package_id")) {
            package_id = getIntent().getExtras().getString("package_id");
            folder_id = getIntent().getExtras().getString("folder_id");
            toolbar_title.setText(getIntent().getExtras().getString("package_name"));
            loadDetails();
        }
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
    private Context mContext=ContentPDF.this;
    private static final int REQUEST = 112;
    String downloadURL,downloadTitle,fileName;
    public void checkPermision(String downloadURL,String downloadTitle,String filename, View downloading1) {
        this.downloadURL=downloadURL;
        this.downloadTitle=downloadTitle;
        this.fileName=filename;
        this.downloading=downloading1;
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
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

    void loadDetails() {
        tag="folder_pdf_list";
        ArrayList list = new ArrayList<DataBin>();
        PDFContentAdapter adapter = new PDFContentAdapter(context, list,this);
        recyclerView.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.productApi + "folder_pdf_list",
                response -> {
                    showHideProgress(false);
                    try {
                        Log.e(tag, response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));

                            JSONArray jsonArray = new JSONArray(data.getString("aContent"));
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                DataBin data1 = new DataBin();
                                data1.setRowId(value.getString("pdf_id"));
                                data1.setTitle(value.getString("title"));
                                data1.setUrl(value.getString("file"));
                                data1.setIsDownloadable(value.getString("allow_download"));
                                list.add(data1);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    showHideProgress(false);
                    Log.e(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("folder_id", folder_id);
                params.put("package_id", package_id);
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
