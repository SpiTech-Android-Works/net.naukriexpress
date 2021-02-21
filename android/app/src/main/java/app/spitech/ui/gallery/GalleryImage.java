package app.spitech.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.models.DataBin;
import app.spitech.ui.gallery.adapter.GalleryImageAdapter;

public class GalleryImage extends BaseActivity {

    GridView gridView;
    String gallery_id = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_images);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(GalleryImage.this, "Gallery", "Gallery Images");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Gallery Images");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        emptyView=findViewById(R.id.emptyView);
        emptyTextView=findViewById(R.id.emptyTextView);
        emptyTextView.setText("There is no images in this gallery");
        //---------Basic End------------

        gridView = findViewById(R.id.gridView);
        if (getIntent().hasExtra("gallery_id")) {
            gallery_id = getIntent().getExtras().getString("gallery_id");
        }
        loadData();
    }

    public void loadData() {
        ArrayList<DataBin> list = new ArrayList<>();
        GalleryImageAdapter adapter = new GalleryImageAdapter(context, list);
        gridView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "gallery_images",
                response -> {
                    //Log.e("gallery_images", response);
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("image_id"));
                                data.setGalleryId(value.getString("gallery_id"));
                                data.setTitle(value.getString("title"));
                                data.setImage(value.getString("image"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (JSONException ex) {
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
                params.put("gallery_id", gallery_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
