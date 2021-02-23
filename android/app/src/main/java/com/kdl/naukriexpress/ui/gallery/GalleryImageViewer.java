package com.kdl.naukriexpress.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.ui.gallery.adapter.GalleryImageViewerAdapter;

public class GalleryImageViewer extends BaseActivity {

    ViewPager viewPager;
    private ImageView img_next,img_back;
    int current_position=0;
    String gallery_id="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_image_viewer);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(GalleryImageViewer.this, "GalleryImageViewer","Gallery Image View");
        toolbar=findViewById(R.id.toolbar);
        toolbar_title=findViewById(R.id.toolbar_title);
        toolbar_title.setText("Gallery Images");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar=findViewById(R.id.progressBar);
        emptyView=findViewById(R.id.emptyView);
        emptyTextView=findViewById(R.id.emptyTextView);
        emptyTextView.setText("There is no images in this gallery");
        //---------Basic End------------

        viewPager=findViewById(R.id.viewPager);
        img_back=findViewById(R.id.img_back);
        img_next=findViewById(R.id.img_next);
        screen=findViewById(R.id.screen);

        if(getIntent().hasExtra("gallery_id")){
            gallery_id=getIntent().getExtras().getString("gallery_id");
        }
        if(getIntent().hasExtra("current_position")){
            current_position=getIntent().getExtras().getInt("current_position");
        }

        loadData();
        new Handler().postDelayed(() -> viewPager.setCurrentItem(current_position), 300);

        img_next.setOnClickListener(view -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1, true);
            setButton();
        });
        img_back.setOnClickListener(view -> {
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1, true);
            setButton();
        });
        viewPager.setPageTransformer(true, (page, position) -> {
            setButton();
        });
    }

    void setButton(){
        if(viewPager.getChildCount()==1){
            img_next.setVisibility(View.GONE);
            img_back.setVisibility(View.GONE);
        }else{
            if(viewPager.getCurrentItem()==viewPager.getChildCount()-1){
                img_next.setVisibility(View.GONE);
            }else{
                img_next.setVisibility(View.VISIBLE);
            }
            if(viewPager.getCurrentItem()==0){
                img_back.setVisibility(View.GONE);
            }else{
                img_back.setVisibility(View.VISIBLE);
            }
        }
    }

    public void loadData() {
        ArrayList<DataBin> list1 = new ArrayList<DataBin>();
        GalleryImageViewerAdapter adapter1 = new GalleryImageViewerAdapter(context, list1);
        viewPager.setAdapter(adapter1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "gallery_images",
                response -> {
                   // Log.e("gallery_images", response);
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            emptyView.setVisibility(View.GONE);
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("image_id"));
                                data.setTitle(value.getString("title"));
                                data.setImage(value.getString("image"));
                                list1.add(data);
                            }
                            adapter1.notifyDataSetChanged();
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
                params.put("gallery_id",gallery_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(context, GalleryImage.class);
        intent.putExtra("gallery_id",gallery_id);
        startActivity(intent);
        finish();
    }
}