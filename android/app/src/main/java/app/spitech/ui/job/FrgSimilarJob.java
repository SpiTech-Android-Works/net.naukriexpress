package app.spitech.ui.job;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import app.spitech.adapter.CommonJNSASListingAdapter;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.BaseFragment;
import app.spitech.models.DataBin;


public class FrgSimilarJob extends BaseFragment {

    private static JobDetails parent;
    RecyclerView recyclerView;
    CommonJNSASListingAdapter adapter;
    ArrayList<DataBin> list;
    AppSession session;

    public FrgSimilarJob() {

    }

    public static FrgSimilarJob newInstance(Context param1, String param2,JobDetails parentActivity) {
        mContext=param1;
        parent=parentActivity;
        FrgSimilarJob fragment = new FrgSimilarJob();
        Bundle args = new Bundle();
        args.putString("job_id", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        session = new AppSession(getContext());
        init();
        return rootView;
    }

    void init() {
        tag = "FrgJobListings";
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        loadData();
    }

    void loadData() {
        list = new ArrayList<>();
        adapter = new CommonJNSASListingAdapter(getActivity(), list, "jobs");
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "job_list",
                response -> {
                    Log.e("job_list", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("job_id"));
                                data.setTitle(value.getString("title"));
                                data.setDate(value.getString("publish_date"));
                                data.setTime(value.getString("time"));
                                data.setViewCounter(value.getString("view_counter"));
                                data.setImage(AppConfig.mediaBlog+value.getString("image"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


}
