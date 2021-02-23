package com.kdl.naukriexpress.ui.news;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.kdl.naukriexpress.appSDK.BaseFragment;
import com.kdl.naukriexpress.appSDK.SpiTech;


public class FrgNewsDetails extends BaseFragment {


    String news_id = "";
    private static NewsDetails parent;
    private SliderLayout mDemoSlider;
    private WebView details;
    private TextView publish_date, title, view_counter;
    private LinearLayout packageContainer;

    public FrgNewsDetails() {

    }

    public static FrgNewsDetails newInstance(Context param1, String param2,NewsDetails parentActivity) {
        mContext=param1;
        parent=parentActivity;
        FrgNewsDetails fragment = new FrgNewsDetails();
        Bundle args = new Bundle();
        args.putString("news_id", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            news_id = getArguments().getString("news_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.news_details, container, false);
        packageContainer= rootView.findViewById(R.id.packageContainer);
        mDemoSlider = rootView.findViewById(R.id.liveTestSlider);
        details = rootView.findViewById(R.id.details);
        title = rootView.findViewById(R.id.title);
        view_counter =rootView.findViewById(R.id.view_counter);
        publish_date = rootView.findViewById(R.id.publish_date);
        init();
        return rootView;
    }

    void init() {
        tag = "FrgNewsDetails";
        parent = (NewsDetails) getActivity();
        loadData();
        AppMethods.getInstance().productSlider(getContext(), mDemoSlider,"57",parent.session.getUserId(),packageContainer);
    }

    void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "news_details",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("data");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(msg);
                            title.setText(parent.setStringValue(data.getString("title")));
                            view_counter.setText("  :  "+parent.setStringValue(data.getString("view_counter")));
                            publish_date.setText("  :  "+SpiTech.getInstance().getMyDate("d/MMM/Y", parent.setStringValue(data.getString("publish_date"))));

                            details.loadDataWithBaseURL(null, data.getString("description"), "text/html", "UTF-8", null);

                            String url = AppConfig.mediaBlog + data.getString("image");
                           SpiTech.getInstance().loadImage(getContext(),url,parent.image);
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
                params.put("news_id", news_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void onStop() {
        super.onStop();
    }

}
