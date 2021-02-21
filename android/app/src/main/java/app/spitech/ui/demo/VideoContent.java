package app.spitech.ui.demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.potyvideo.library.AndExoPlayerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.models.DataBin;

public class VideoContent extends BaseActivity {

    RecyclerView recyclerView;
    static String folder_id, package_id;
    LinearLayout videoList;
    AndExoPlayerView videoView;
    ImageView btnPlay;

    String fileName = "";
    String videoTitle = "";
    String encryptedVideoPath = "";

  ProgressBar videoProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.content_video_list);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(VideoContent.this, "ContentVideo", "Package Video");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Package Video");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setText("There is no video");
        //---------Basic End------------
        videoList = findViewById(R.id.videoList);
        videoProgressBar = findViewById(R.id.videoProgressBar);
        videoView = findViewById(R.id.videoView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (getIntent().hasExtra("package_id")) {
            package_id = getIntent().getExtras().getString("package_id");
            folder_id = getIntent().getExtras().getString("folder_id");
            loadDetails();
        }


        /*BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    DownloadManager.Query req_query = new DownloadManager.Query();
                    req_query.setFilterById(enqueue_id);
                    Cursor c = dm.query(req_query);
                    if (c.moveToFirst()) {
                        int indexColumn = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(indexColumn)) {
                            videoPlay();
                        }
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));*/
        startDownload();
    }

    public void startDownload() {
        videoProgressBar.setVisibility(View.VISIBLE);
        if (!hasPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
        } else {
            File file=new File(AppConfig.appFolderName);
            if(!file.exists()){
                file.mkdir();
            }
            fileName = "video_"+folder_id+session.getKeyCurrentVideoId()+package_id+".mp4";
            encryptedVideoPath = AppConfig.appFolderName + File.separator + fileName;
            file = new File(encryptedVideoPath);
            if (file.exists()) {
                videoProgressBar.setVisibility(View.GONE);
                videoView.setSource(encryptedVideoPath);
            }else{
                /*Log.e("Downloading", session.getKeyCurrentVideoUrl());
                FileDownloader dl = new FileDownloader(AppConfig.appFolderName, fileName, session.getKeyCurrentVideoUrl());
                dl.setDownloaderCallback(this);
                dl.start();*/
                new DownloadTask().execute(session.getKeyCurrentVideoUrl());
            }
        }
    }

    //---------------Download Start----------------
    class DownloadTask extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onPreExecute() {
           videoProgressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Long doInBackground(String... params) {
            long totalSize = 0;
            try {
                //--------step1: download file and save it--------------
                String path=AppConfig.appFolderName;
                File file=new File(path);
                if(!file.exists()){
                    file.mkdirs();
                }
                URL url = new URL(params[0]);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                int count;
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(path+"/"+fileName);
                byte data[] = new byte[4096];
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {

            }
            return totalSize;
        }
        protected void onProgressUpdate(Integer... progress) {
            videoProgressBar.setVisibility(View.VISIBLE);
        }
        protected void onPostExecute(Long result) {
            videoProgressBar.setVisibility(View.GONE);
           loadDetails();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
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

        if (requestCode == 111) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload();
            }
        }
    }
//---------------Download Stop----------------

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoList.setVisibility(View.GONE);
        } else {
            videoList.setVisibility(View.VISIBLE);
        }
        super.onConfigurationChanged(newConfig);
    }

    void loadDetails() {
        ArrayList list = new ArrayList<DataBin>();
        VideoAdapter adapter = new VideoAdapter(context, list, this);
        recyclerView.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.productApi + "folder_video_list",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        Log.e(tag, response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));

                            JSONArray jsonArray = new JSONArray(data.getString("aContent"));
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                DataBin data1 = new DataBin();

                                data1.setRowId(value.getString("video_id"));
                                String file_name="video_"+folder_id+value.getString("video_id")+package_id+".mp4";
                                data1.setFile(file_name);
                                data1.setName(value.getString("title"));
                                String url = value.getString("file");
                                if (value.getString("type").equalsIgnoreCase("1")) {
                                    url = value.getString("file");
                                } else {
                                    url = value.getString("youtube_video_id");
                                }
                                data1.setUrl(url);
                                data1.setType(value.getString("type"));
                                list.add(data1);
                                if (row == 0) {
                                    session.setKeyCurrentVideoId(value.getString("video_id"));
                                    session.setKeyCurrentVideoUrl(url);
                                }
                            }
                            adapter.notifyDataSetChanged();
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
