package com.kdl.naukriexpress.ui.news;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
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

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.adapter.CommonJNSASListingAdapter;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.BaseFragment;
import com.kdl.naukriexpress.models.DataBin;


public class FrgNewsListings extends BaseFragment {

    private static NewsDetails parent;
    RecyclerView recyclerView;
    CommonJNSASListingAdapter adapter;
    ArrayList<DataBin> list;
    AppSession session;

    public FrgNewsListings() {

    }

    public static FrgNewsListings newInstance(Context param1, String param2,NewsDetails parentActivity) {
        mContext=param1;
        parent=parentActivity;
        FrgNewsListings fragment = new FrgNewsListings();
        Bundle args = new Bundle();
        args.putString("news_id", param2);
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
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        session = new AppSession(getContext());
        init();
        return rootView;
    }

    void init() {
        tag = "spsoniFrgSimilarJobs";
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getContext(), R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);
        loadData();
    }

    void loadData() {
        list = new ArrayList<DataBin>();
        adapter = new CommonJNSASListingAdapter(getActivity(), list, "news");
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "news_list",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("news_id"));
                                data.setTitle(value.getString("title"));
                                data.setDate(value.getString("publish_date"));
                                data.setImage(AppConfig.mediaBlog+value.getString("image"));
                                data.setTime(value.getString("time"));
                                data.setIsBookmarked(value.getInt("is_bookmarked"));
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
