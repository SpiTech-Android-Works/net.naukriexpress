package com.kdl.naukriexpress.ui.daily_quiz;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class DailyQuiz extends BaseActivity {

    public ImageView image;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private List<String> tabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.daily_quiz);
        init();
    }

    void init() {
        load(DailyQuiz.this, "DailyQuiz", "Daily Quiz");
       //aHideMenuList.add(R.id.action_notification);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        image = findViewById(R.id.image);
        loadData();
    }

    private void loadData() {
        tabList = new ArrayList<>();
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        showProgress(context, "Loading Quiz...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "get_month_name",
                response -> {
                    String result = new String(response);
                    Log.e(tag, result);
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                tabList.add(value.getString("year"));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                    closeProgress();
                },
                error -> {
                    closeProgress();
                    Log.e(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
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

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String[] monthYear = tabList.get(position).split("-");
            return new QuizFragment().newInstance(monthYear[0], monthYear[1]);
        }

        @Override
        public int getCount() {
            return tabList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] monthYear = tabList.get(position).split("-");
            return monthYear[0].substring(0, 3) + "-" + monthYear[1];
        }
    }
}
