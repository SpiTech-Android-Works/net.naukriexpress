package app.spitech.ui.chat.group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.HelperMethods;
import app.spitech.appSDK.Validation;
import app.spitech.models.DataBin;
import app.spitech.ui.chat.group.adapter.GroupMemberAdapter;

public class GroupDetails extends HelperMethods {

    String group_id = "0",is_admin="0",owner_id="0";
    ImageView group_image,owner_photo;
    TextView user_count,group_name,btnAddMember,owner_name,owner_photo_name,description;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.group_details);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    void init() {
        context = GroupDetails.this;
        session = new AppSession(context);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        group_image = findViewById(R.id.group_image);
        user_count=findViewById(R.id.user_count);
        group_name=findViewById(R.id.group_name);
        owner_name = findViewById(R.id.owner_name);
        owner_photo = findViewById(R.id.owner_photo);
        description = findViewById(R.id.description);
        owner_photo_name = findViewById(R.id.owner_photo_name);
        btnAddMember=findViewById(R.id.btnAddMember);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent().hasExtra("group_id")) {
            Bundle bundle = getIntent().getExtras();
            group_id = bundle.getString("group_id");
            loadDetails();
        }

        btnAddMember.setOnClickListener(view -> {
            if(is_admin.equalsIgnoreCase("1")){
                Intent intent=new Intent(context, AddGroupMember.class);
                intent.putExtra("group_id",group_id);
                startActivity(intent);
            }
        });
    }



    void loadDetails(){
        ArrayList<DataBin> list = new ArrayList<>();

        showProgress(context, "Loading Details...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.messageApi + "group_details",
                response -> {
                    closeProgress();
                    try {
                        Log.e("group_details", response);
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));

                            is_admin=data.getString("is_admin");
                            GroupMemberAdapter adapter = new GroupMemberAdapter(context, list,is_admin,this);
                            recyclerView.setAdapter(adapter);


                            owner_id=data.getString("owner_id");
                            user_count.setText(data.getString("member_count")+" members");
                            group_name.setText(data.getString("name"));
                            description.setText(data.getString("description"));
                            owner_name.setText(data.getString("owner_name"));
                            if(Validation.isNotEmpty(data.getString("owner_photo"))){
                                String url= AppConfig.mediaCustomer+data.getString("owner_photo");
                                SpiTech.getInstance().loadImage(context,url,owner_photo);
                            }else{
                                owner_photo_name.setText(SpiTech.getInstance().getNamedPhoto(owner_name.getText().toString()));
                            }
                            if(is_admin.equalsIgnoreCase("1")){
                                btnAddMember.setText("+ Add Group Members");
                            }else{
                                btnAddMember.setText("Group Members");
                            }
                            if(Validation.isNotEmpty(data.getString("image"))){
                                String url= AppConfig.mediaGroup+data.getString("image");
                                SpiTech.getInstance().loadImage(context,url,group_image);
                            }

                            JSONArray aMember = new JSONArray(data.getString("aMember"));
                            DataBin data1 = null;
                            for (int row = 0; row < aMember.length(); row++) {
                                JSONObject value = aMember.getJSONObject(row);
                                data1 = new DataBin();
                                data1.setRowId(value.getString("id"));
                                data1.setUserId(value.getString("customer_id"));
                                data1.setName(value.getString("name"));
                                data1.setImage(value.getString("photo"));
                                data1.setIsAdmin(value.getString("is_admin"));
                                list.add(data1);
                            }
                            adapter.notifyDataSetChanged();
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
                params.put("group_id",group_id);
                params.put("user_id",session.getUserId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAdapterItemClick(String [] args) {
        if(args[0].equalsIgnoreCase("update_group_admin")){
            updateGroupAdmin(args[1],args[2]);
        }else if(args[0].equalsIgnoreCase("remove_member")){
            removeFromGroup(args[1]);
        }
    }

    //-------Update Group Admin--------------
    public void updateGroupAdmin(String id, String status) {
        String tag="update_group_admin";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.messageApi + "update_group_admin",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg=object.getString("message");
                        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                        init();
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    Log.e(tag, error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("id", id);
                params.put("status", status);
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

    //-------Delete Member from group--------------
    public void removeFromGroup(String id) {
        String tag="update_group_admin";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.messageApi + "leave_group",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg=object.getString("message");
                        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                        init();
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    Log.e(tag, error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("id", id);
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
}