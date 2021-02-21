package app.spitech.ui.job;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
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

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppMethods;
import app.spitech.appSDK.BaseFragment;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;


public class FrgJobDetails extends BaseFragment {

    String job_id = "", job_url = "";
    private static JobDetails parent;
    private SliderLayout mDemoSlider;
    private WebView eligibility, job_details, contact_details;
    private TextView publish_date, posts, title;
    private Button btnApply;
    private LinearLayout packageContainer;

    public FrgJobDetails() {

    }

    public static FrgJobDetails newInstance(Context param1, String param2,JobDetails parentActivity) {
        mContext=param1;
        parent=parentActivity;
        FrgJobDetails fragment = new FrgJobDetails();
        Bundle args = new Bundle();
        args.putString("job_id", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            job_id = getArguments().getString("job_id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.job_details, container, false);
        packageContainer=rootView.findViewById(R.id.packageContainer);
        mDemoSlider = rootView.findViewById(R.id.liveTestSlider);
        eligibility = rootView.findViewById(R.id.eligibility);
        job_details = rootView.findViewById(R.id.job_details);
        contact_details =rootView.findViewById(R.id.contact_details);
        title = rootView.findViewById(R.id.title);
        publish_date = rootView.findViewById(R.id.publish_date);
        posts = rootView.findViewById(R.id.posts);
        btnApply = rootView.findViewById(R.id.btnApply);

        init();
        return rootView;
    }

    void init() {
        tag = "SpitechFrgJobDetails";
        btnApply.setOnClickListener(v -> parent.openUrl(job_url));
        loadData();
        AppMethods.getInstance().productSlider(getContext(),mDemoSlider,"57",parent.session.getUserId(),packageContainer);
    }

    void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "job_details",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("data");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(msg);
                            job_url = data.getString("job_url");
                            title.setText(parent.setStringValue(data.getString("title")));
                            posts.setText("  :  "+parent.setStringValue(data.getString("posts")));
                            publish_date.setText("  :  "+SpiTech.getInstance().getMyDate("d/MMM/Y", parent.setStringValue(data.getString("publish_date"))));

                            job_details.loadDataWithBaseURL(null, data.getString("description"), "text/html", "UTF-8", null);
                            contact_details.loadDataWithBaseURL(null, data.getString("contact_details"), "text/html", "UTF-8", null);
                            eligibility.loadDataWithBaseURL(null, data.getString("eligibility"), "text/html", "UTF-8", null);

                            if(Validation.isNotEmpty(data.getString("image"))){
                                String url = AppConfig.mediaBlog + data.getString("image");
                                SpiTech.getInstance().loadImage(getContext(),url,parent.image);
                            }
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
                params.put("job_id", job_id);
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
