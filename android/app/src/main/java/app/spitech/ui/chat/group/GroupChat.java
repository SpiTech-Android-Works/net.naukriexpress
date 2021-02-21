package app.spitech.ui.chat.group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.ConvertTo;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;
import app.spitech.models.DataBin;
import app.spitech.ui.chat.ChatHome;
import app.spitech.ui.chat.group.adapter.FirebaseGroupChatAdapter;

public class GroupChat extends BaseActivity {

    ArrayList<DataBin> listMessage;
    FirebaseGroupChatAdapter adapter;

    private RecyclerView recyclerView;
    EditText editMessage;
    ImageView group_image, from_user_image;
    String group_name = "",group_image_url="",from_user_id = "", group_id = "";
    private DatabaseReference groupNameRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat);
        init();
    }

    void init() {
        context = GroupChat.this;
        session = new AppSession(context);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        TextView groupname = findViewById(R.id.group_name);
        TextView member_count = findViewById(R.id.member_count);
       //aHideMenuList.add(R.id.action_notification);
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();


        group_image = findViewById(R.id.group_image);
        from_user_image = findViewById(R.id.from_user_image);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));


        if (getIntent().hasExtra("group_id")) {
            Bundle bundle=getIntent().getExtras();
            group_name=ConvertTo.toTitleCase(bundle.getString("group_name"));
            groupname.setText(group_name);
            member_count.setText(bundle.getString("member_count")+" members");
            group_id =bundle.getString("group_id");
            if (Validation.isNotEmpty(bundle.getString("group_image"))) {
                group_image_url= AppConfig.mediaGroup+bundle.getString("group_image");
                SpiTech.getInstance().loadImage(context,group_image_url, group_image);
            }

            from_user_id = session.getUserId();
            if (Validation.isNotEmpty(session.getPhoto())) {
                SpiTech.getInstance().loadImage(context, session.getPhoto(), from_user_image);
            } else {
                TextView from_photo_name = findViewById(R.id.from_photo_name);
                from_photo_name.setText(SpiTech.getInstance().getNamedPhoto(session.getName()));
            }
            groupNameRef=mDB.child("groups").child("group_"+group_id);
        }

        toolbar.setOnClickListener(view -> {
            Intent intent=new Intent(context,GroupDetails.class);
            intent.putExtra("group_id",group_id);
            startActivity(intent);
        });

        listMessage = new ArrayList();
        adapter = new FirebaseGroupChatAdapter(context, listMessage, this);
        recyclerView.setAdapter(adapter);

        ImageView btnSend = findViewById(R.id.btnSend);
        editMessage = findViewById(R.id.editMessage);
        btnSend.setOnClickListener(view -> {
            if (Validation.isNotEmpty(editMessage.getText().toString())) {
                sendMessage();
            } else {
                showAlert("Text Message is Required");
            }
        });
    }

    public void saveMessage(String from_user_id, String group_id, String message) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.messageApi + "send_group_message",
                response -> {
                    Log.e("send_message", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            editMessage.setText("");
                        }
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
                params.put("user_id", from_user_id);
                params.put("group_id", group_id);
                params.put("from_user_name", session.getName());
                params.put("message", message);
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

    //--------Firebase Begin----------------
    void sendMessage() {
        Calendar dateCalender = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        String date = dateFormat.format(dateCalender.getTime());
        SimpleDateFormat timeFormat =new SimpleDateFormat("hh:mm a");
        String time = timeFormat.format(dateCalender.getTime());

        String messageKey = groupNameRef.push().getKey();
        HashMap groupMessageKey = new HashMap();
        groupNameRef.updateChildren((Map<String, Object>)groupMessageKey);

        DatabaseReference groupMessageRef = groupNameRef.child(messageKey);
        HashMap messageInfoMap = new HashMap();
        messageInfoMap.put("user_id",session.getUserId());
        messageInfoMap.put("datetime", date + "," + time);
        messageInfoMap.put("message",editMessage.getText().toString());
        messageInfoMap.put("name",session.getName());
        groupMessageRef.updateChildren((Map<String, Object>)messageInfoMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                saveMessage(from_user_id, group_id, editMessage.getText().toString());
            }
        });
    }

    void firebaseLoadMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        DataBin dataBin;
        while (iterator.hasNext()) {
            dataBin = new DataBin();
            dataBin.setDate(((DataSnapshot) iterator.next()).getValue().toString());
            dataBin.setMessage(((DataSnapshot) iterator.next()).getValue().toString());
            dataBin.setName(((DataSnapshot) iterator.next()).getValue().toString());
            dataBin.setUserId(((DataSnapshot) iterator.next()).getValue().toString());
            listMessage.add(dataBin);
        }
        if (listMessage.size() > 0) {
            recyclerView.smoothScrollToPosition(listMessage.size() - 1);
        }
        adapter.notifyDataSetChanged();
    }

    //--------Firebase End----------------
    @Override
    protected void onStart() {
        super.onStart();
        try{
            groupNameRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        firebaseLoadMessage(dataSnapshot);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        firebaseLoadMessage(dataSnapshot);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        firebaseLoadMessage(dataSnapshot);
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        firebaseLoadMessage(dataSnapshot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(tag, databaseError.getMessage());
                }
            });
        }catch (Exception ex){

        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, ChatHome.class);
        intent.putExtra("active_tab", 2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
