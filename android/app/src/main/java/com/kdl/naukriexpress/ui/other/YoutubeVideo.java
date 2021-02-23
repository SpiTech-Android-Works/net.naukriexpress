package com.kdl.naukriexpress.ui.other;

import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.models.VideoModel;
import com.kdl.naukriexpress.ui.video.adapter.VideoAdapter;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class YoutubeVideo extends BaseActivity {

    VideoAdapter adapterVideo;
    ArrayList<VideoModel> videoList;
    String tag = "YoutubeVideo";
    private RecyclerView recyclerViewVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.youtube_video);

        recyclerViewVideo = findViewById(R.id.recyclerViewVideos);
        recyclerViewVideo.setHasFixedSize(true);
        recyclerViewVideo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadVideo();
    }

    void loadVideo() {

        videoList = new ArrayList<VideoModel>();
        adapterVideo = new VideoAdapter(this, videoList);
        recyclerViewVideo.setAdapter(adapterVideo);

        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("api_key", AppConfig.api_key);
        httpClient.post(AppConfig.moduleApi + "get_videos", params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.e(tag, result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                        VideoModel data = null;
                        for (int row = 0; row < jsonArray.length(); row++) {
                            JSONObject value = jsonArray.getJSONObject(row);
                            data = new VideoModel();
                            data.setRowId(value.getInt("video_id"));
                            data.setTitle(value.getString("title"));
                            data.setPublishDate(value.getString("publish_date"));
                            data.setUrl(value.getString("video_url"));
                            videoList.add(data);
                        }
                        adapterVideo.notifyDataSetChanged();
                    }
                } catch (JSONException ex) {
                    Log.e(tag, ex.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(tag, error.toString());
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });

    }

}
