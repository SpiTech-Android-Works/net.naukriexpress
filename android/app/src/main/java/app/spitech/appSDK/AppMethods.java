package app.spitech.appSDK;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.DefaultSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.adapter.SpinnerStateAdapter;
import app.spitech.models.BookModel;
import app.spitech.models.DataBin;
import app.spitech.models.SpinnerStateModel;
import app.spitech.ui.auth.AnotherDevice;
import app.spitech.ui.batch.adapter.BatchAdapter;
import app.spitech.ui.home.adapter.TodayJoinedAdapter;
import app.spitech.ui.home.adapter.ToppersAdapter;
import app.spitech.ui.notification.adapter.NotificationAdapter;
import app.spitech.ui.other.Maintenance;
import app.spitech.ui.packages.ProductDetails;
import app.spitech.ui.packages.adapter.PackageAdapter;
import app.spitech.ui.publication.adapter.PublicationAdapter;
import app.spitech.ui.subject.adapter.SubjectAdapter;
import app.spitech.ui.toppers.adapter.ToppersTestAdapter;
import app.spitech.ui.tricks.adapter.MnemonicListingAdapter;

public class AppMethods extends BaseActivity {

    private static volatile AppMethods appMethods;
    private AppSession session;

    private AppMethods() {
        //St1ep1 - Prevent form the reflection api.
        if (appMethods != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static AppMethods getInstance() {
        if (appMethods == null) {
            //St1ep2 - Prevent  singleton from thread.
            synchronized (SpiTech.class) {
                if (appMethods == null) {
                    appMethods = new AppMethods();
                }
            }
        }
        return appMethods;
    }

    //St1ep3 - Prevent  singleton from serialize and deserialize operation.
    protected AppMethods readResolve() {
        return getInstance();
    }

    //St1ep4 - Prevent  singleton from cloning.
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    /*------------------------methods-----------------*/

    public void loadState(Context context, Spinner spinner){
        ArrayList<SpinnerStateModel> itemList = new ArrayList();
        SpinnerStateAdapter adapter = new SpinnerStateAdapter(context, android.R.layout.simple_spinner_dropdown_item, itemList);
        spinner.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "get_states",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            SpinnerStateModel data;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data=new SpinnerStateModel();
                                data.setStateId(value.getString("state_id"));
                                data.setState(value.getString("state"));
                                itemList.add(data);
                            }
                            adapter.notifyDataSetChanged();
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
                params.put("country_id","88");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }



    public void mnemonicsList(Context context,RecyclerView recyclerView,String subject_id) {
        ArrayList<DataBin> list = new ArrayList<>();
        MnemonicListingAdapter adapter = new MnemonicListingAdapter(context, list);
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "get_mnemonics",
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
                                data.setRowId(value.getString("mnemonic_id"));
                                data.setTitle(value.getString("title"));
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
                params.put("subject_id", subject_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }










    public void subjectList(Context context, GridView gridView, String purpose) {
        String tag="subjects";
        ArrayList<DataBin> list = new ArrayList<>();
        SubjectAdapter adapter = new SubjectAdapter(context, list);
        gridView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.testApi + "subjects",
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
                                data.setRowId(value.getString("subject_id"));
                                data.setTitle(value.getString("subject"));
                                data.setImage(value.getString("image"));
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
                params.put("purpose", purpose);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void checkAppVersion(Context context, LinearLayout appUpdatesContainer) {
      String app_version=SpiTech.getInstance().getAppVersion(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "check_app_version",
                response -> {
                    try {
                        Log.e("real_app_version",app_version);
                        Log.e("check_app_version",response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equals("1")) {
                            if(app_version.equals(object.getString("data"))){
                                appUpdatesContainer.setVisibility(View.GONE);
                            }else{
                                appUpdatesContainer.setVisibility(View.VISIBLE);
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

    public void checkAppStatus(Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "check_app_status",
                response -> {
                    try {
                        Log.e("check_app_status",response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equals("1")) {

                            if(object.getString("data").equals("1")){
                                Log.e("check_app_status",object.getString("data"));
                                startActivity(new Intent(context, Maintenance.class));
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

    public void loadBatch(Context context, RecyclerView recyclerView,LinearLayout containerLayout) {
        ArrayList<DataBin> list = new ArrayList<>();
        BatchAdapter adapter = new BatchAdapter(context, list);
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.batchApi + "batch_list",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            containerLayout.setVisibility(View.VISIBLE);
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("batch_id"));
                                data.setName(value.getString("name"));
                                data.setTiming(value.getString("timing"));
                                data.setIsPurchased(value.getString("joining_id"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            containerLayout.setVisibility(View.GONE);
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


    public void deviceLogout(Context context, String customer_id,AppSession session) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "device_logout",
                response -> {
                    try {
                        Log.e("deviceLogout","device_logout");
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            session.logoutUser();
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
                params.put("customer_id", customer_id);
                params.put("device_id", getDeviceId(context));
                params.put("ip_address", StaticMethods.getIpAddress(context));
                params.put("device_name", StaticMethods.getDeviceName());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void checkDeviceLogin(Context context,String customer_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "device_login_check",
                response -> {
                    try {
                        Log.e("device_login_check",response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("-1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            Log.e("device_login_check","if section");
                            Intent intent=new Intent(context, AnotherDevice.class);
                            intent.putExtra("device_name",data.getString("device_name"));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }else{
                            Log.e("device_login_check","Same Device");
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
                params.put("customer_id", customer_id);
                params.put("device_name", StaticMethods.getDeviceName());
                params.put("device_id", getDeviceId(context));
                params.put("ip_address", StaticMethods.getIpAddress(context));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }



    //-----------Books------------
    public void loadBooks(Context context, RecyclerView recyclerView,LinearLayout publicationContainer) {
        ArrayList<BookModel> list = new ArrayList<BookModel>();
        PublicationAdapter adapter = new PublicationAdapter(context, list);
        recyclerView.setAdapter(adapter);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.productApi + "product_list",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            publicationContainer.setVisibility(View.VISIBLE);
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            BookModel data = null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data = new BookModel();
                                data.setRowId(value.getString("package_id"));
                                data.setName(value.getString("name"));
                                data.setImage(value.getString("image"));
                                data.setRate(value.getString("rate"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            publicationContainer.setVisibility(View.GONE);
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
                params.put("type","59");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }



    public void notificationStatusUpdate(Context context, String notification_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "notification_status",
                response -> {
                    try {
                        Log.e("notification_list",response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {

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
                params.put("notification_id", notification_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void notificationList(Context context, RecyclerView recyclerView) {
        ArrayList<DataBin> list1 = new ArrayList<>();
        NotificationAdapter adapter1 = new NotificationAdapter(context, list1);
        recyclerView.setAdapter(adapter1);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "notification_list",
                response -> {
                    try {
                        Log.e("notification_list",response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("notification_id"));
                                data.setTitle(value.getString("title"));
                                data.setDescription(value.getString("description"));
                                data.setDate(value.getString("publish_date"));
                                list1.add(data);
                            }
                            adapter1.notifyDataSetChanged();
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

    public void packageList(Context context, RecyclerView recyclerView, LinearLayout emptyView,String category_id) {
        ArrayList<DataBin> list1 = new ArrayList<>();
        PackageAdapter adapter1 = new PackageAdapter(context, list1);
        recyclerView.setAdapter(adapter1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "get_package",
                response -> {
                    Log.e("packageList", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            showData(emptyView,recyclerView,1);
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data = null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data = new DataBin();
                                data.setRowId(value.getString("package_id"));
                                data.setName(value.getString("name"));
                                data.setStudentCount(value.getString("student_count"));
                                data.setRate(value.getString("rate"));
                                data.setImage(value.getString("image"));
                                data.setTestCount(value.getString("test_count"));
                                list1.add(data);
                            }
                            adapter1.notifyDataSetChanged();
                        }else{
                            showData(emptyView,recyclerView,0);
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
                params.put("category_id", category_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    //--------------- package ------------------------------------
    public void productSlider(Context context, SliderLayout mDemoSlider,String type,String customer_id,LinearLayout packageContainer) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.productApi + "product_list",
                response -> {
                    try {
                        Log.e("product_list",response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            packageContainer.setVisibility(View.VISIBLE);
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                String package_id = value.getString("package_id");
                                String imageUrl= AppConfig.mediaProduct+value.getString("image");
                                String package_name=value.getString("name");
                                DefaultSliderView textView = new DefaultSliderView(context);
                                textView.image(imageUrl);
                                mDemoSlider.addSlider(textView);
                                textView.setOnSliderClickListener(slider -> {
                                    Intent intent = new Intent(context, ProductDetails.class);
                                    intent.putExtra("package_id", package_id);
                                    intent.putExtra("image", imageUrl);
                                    intent.putExtra("package_name",package_name);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                });
                            }
                            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Tablet);
                            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
                            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                            mDemoSlider.setDuration(4000);
                        }else{
                            packageContainer.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_key", AppConfig.api_key);
                params.put("type", type);
                params.put("customer_id", customer_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void loadToppers(Context context, RecyclerView recyclerView, View containerView,int limit) {
        ArrayList<DataBin> list= new ArrayList<>();
        ToppersAdapter adapter=new ToppersAdapter(context,list);
        recyclerView.setAdapter(adapter);
        String tag="toppers";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi+ "toppers",
                response -> {
                    try {
                        Log.e(tag,response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            containerView.setVisibility(View.VISIBLE);
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data=null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data=new DataBin();
                                String photo= AppConfig.mediaCustomer+value.getString("photo");
                                data.setRowId(value.getString("customer_id"));
                                data.setImage(photo);
                                data.setName(value.getString("name"));
                                data.setRank(value.getString("rank"));
                                data.setOnlineStatus("1");
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            containerView.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {
                        Log.e(tag,ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("limit", String.valueOf(limit));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void loadTestToppers(Context context, RecyclerView recyclerView,String test_id) {
        ArrayList<DataBin> list= new ArrayList<>();
        ToppersTestAdapter adapter=new ToppersTestAdapter(context,list);
        recyclerView.setAdapter(adapter);
        String tag="test_toppers";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi+ "test_toppers",
                response -> {
                    try {
                        Log.e(tag,response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data=null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data=new DataBin();
                                String photo= AppConfig.mediaCustomer+value.getString("photo");
                                data.setRowId(value.getString("customer_id"));
                                data.setImage(photo);
                                data.setName(value.getString("name"));
                                data.setRank(value.getString("rank"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Log.e(tag,ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("test_id", test_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void loadRecentlyJoined(Context context, RecyclerView recyclerView,String customer_id, View containerView) {
        ArrayList<DataBin> list= new ArrayList<>();
        TodayJoinedAdapter adapter=new TodayJoinedAdapter(context,list);
        recyclerView.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.userApi+ "recently_joined",
                response -> {
                    try {
                        Log.e("recently_joined",response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            containerView.setVisibility(View.VISIBLE);
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            DataBin data=null;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data=new DataBin();
                                String photo= AppConfig.mediaCustomer+value.getString("photo");
                                data.setRowId(value.getString("customer_id"));
                                data.setImage(photo);
                                data.setName(value.getString("name"));
                                list.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            containerView.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {
                        Log.e(tag,ex.toString());
                    }
                },
                error -> {
                    Log.e(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("customer_id",customer_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


}
