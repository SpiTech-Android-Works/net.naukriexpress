package com.kdl.naukriexpress.ui.bookmarks;

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

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.models.DataBin;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.BaseFragment;
import com.kdl.naukriexpress.ui.bookmarks.adapter.BookmarkListingAdapter;


public class FrgBookmarkContainer extends BaseFragment {

    RecyclerView recyclerView;
    BookmarkListingAdapter adapter;
    ArrayList<DataBin> list;
    String type = "",imageDirectory="";
    AppSession session;

    public FrgBookmarkContainer() {

    }

    public static FrgBookmarkContainer newInstance(String param1) {
        FrgBookmarkContainer fragment = new FrgBookmarkContainer();
        Bundle args = new Bundle();
        args.putString("type",param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            type = getArguments().getString("type");
        }
        if(type.equalsIgnoreCase("jobs")){
            imageDirectory= AppConfig.mediaJob;
        }else{
            imageDirectory= AppConfig.mediaBlog;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_bookmark_listing, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        init();
        return rootView;
    }

    void init() {
        tag = "FrgBookmarkContainer";
        //  parent=(Bookmarks)getContext();
        session = new AppSession(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        loadData();
    }

    void loadData() {
        list = new ArrayList();
        list.clear();
        adapter = new BookmarkListingAdapter(getActivity(), list, type);
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.moduleApi + "bookmark_list",
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
                                data.setRowId(value.getString("primary_key"));
                                data.setIsBookmarked(value.getInt("is_bookmarked"));
                                data.setTitle(value.getString("title"));
                                data.setDate(value.getString("publish_date"));
                                data.setImage(imageDirectory+value.getString("image"));
                                data.setTime(value.getString("time"));
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
                params.put("type", type);
                params.put("customer_id", session.getUserId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
