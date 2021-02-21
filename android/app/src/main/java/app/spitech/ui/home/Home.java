package app.spitech.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppMethods;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;
import app.spitech.ui.auth.Login;
import app.spitech.ui.bookmarks.Bookmarks;
import app.spitech.ui.branch.BranchListing;
import app.spitech.ui.chat.ChatHome;
import app.spitech.ui.notification.NotificationList;
import app.spitech.ui.orders.Orders;
import app.spitech.ui.other.CMS;
import app.spitech.ui.settings.Settings;
import app.spitech.ui.store.Store;

public class Home extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    Fragment fragment = null;
    Toolbar toolbar;
    TextView academy_name;
    RelativeLayout notificationLayout;
    TextView notification_counter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    void init() {
        context=Home.this;
        session=new AppSession(context);
        load(context,"Home","Home");
        if(!Validation.isNotEmpty(session.getUserId())){
          startActivity(new Intent(context,Login.class));
        }

        /*********Checking App Status*************/
        AppMethods.getInstance().checkAppStatus(context);


        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        academy_name = findViewById(R.id.academy_name);
        notificationLayout = findViewById(R.id.notificationLayout);
        notification_counter = findViewById(R.id.notification_counter);

        //-----------navigation drawer-----------
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //-----header---------------
        View headerview = navigationView.getHeaderView(0);
        TextView name = headerview.findViewById(R.id.name);
        TextView email = headerview.findViewById(R.id.email);

        if (session.checkLogin()) {
            name.setText(session.getName());
            email.setText("Student Id - " + session.getCode());
            CircleImageView image = headerview.findViewById(R.id.photo);
            if (Validation.isNotEmpty(session.getPhoto())) {
                String url = AppConfig.mediaCustomer + session.getPhoto();
                SpiTech.getInstance().loadImage(context, url, image);
            }
        }
        headerview.setOnClickListener(v -> {
            closeDrawer();
            if (session.checkLogin()) {
                startActivity(new Intent(context, Settings.class));
            } else {
                startActivity(new Intent(context, Login.class));
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.getMenu().findItem(R.id.navigation_chat).setVisible(AppConfig.enableChat);
        bottomNavigationView.setItemIconTintList(null);

        loadFragment("FrgHome");
        registerAndSaveToken();

        homeCounters();
        notificationLayout.setOnClickListener(v -> {
           startActivity(new Intent(context, NotificationList.class));
        });
    }

    //------------REGISTER USER'S FCM TOKEN BEGIN-------------------
    public void registerAndSaveToken() {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.fcmTopic);
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token=task.getResult().getToken();
                session.setToken(token);
                saveToken(token);
            }
        });
    }

    private void saveToken(String token) {
        String user_id = session.getUserId();
        String user_type = "user";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "save_user_token",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {

                        } else {
                            showAlert(msg);
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    Log.e(tag, error.toString());
                    closeProgress();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("user_id", user_id);
                params.put("user_type", user_type);
                params.put("token", token);
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
    //------------REGISTER USER'S FCM TOKEN END-------------------


    void loadFragment(String fragment_name) {
        switch (fragment_name) {
            case "FrgHome":
                fragment = FrgHome.newInstance();
                break;
        }
        session.setActiveFragment(fragment_name);
        switchFragment(fragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                loadFragment("FrgHome");
                break;
            case R.id.navigation_chat:
                startActivity(new Intent(context, ChatHome.class));
                break;
            case R.id.navigation_store:
                startActivity(new Intent(context, Store.class));
                break;
            case R.id.navigation_account:
                startActivity(new Intent(context, Settings.class));
                break;
        }
        return true;
    };

    void closeDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.e("SelectedMenu", String.valueOf(R.id.nav_home));
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            loadFragment("FrgHome");
        } else if (id == R.id.nav_logout) {
            AppMethods.getInstance().deviceLogout(getApplicationContext(),session.getUserId(),session);
        } else if (id == R.id.nav_bookmark) {
            startActivity(new Intent(context, Bookmarks.class));
        } else if (id == R.id.nav_order) {
            startActivity(new Intent(context, Orders.class));
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(context, CMS.class);
            intent.putExtra("page_name", "mobile_about");
            startActivity(intent);
        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(context, CMS.class);
            intent.putExtra("page_name", "mobile_privacy");
            startActivity(intent);
        } else if (id == R.id.nav_terms) {
            Intent intent = new Intent(context, CMS.class);
            intent.putExtra("page_name", "mobile_term_condition");
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            String msg = getResources().getString(R.string.app_sharing_message);
            whatsappShare(context, msg);
        }else if (id == R.id.nav_branch) {
            startActivity(new Intent(context, BranchListing.class));
        }else if (id == R.id.nav_powered) {
           openUrl("http://spitech.in");
        }
        closeDrawer();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()>1){
            super.onBackPressed();
        }else{
            backToClose();
        }
    }

    //-------Home Counters--------------
    public void homeCounters() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "home_counters",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if(data.getString("notification_counter")!="0"){
                                notificationLayout.setVisibility(View.VISIBLE);
                                notification_counter.setText(data.getString("notification_counter"));
                            }else{
                                notificationLayout.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
