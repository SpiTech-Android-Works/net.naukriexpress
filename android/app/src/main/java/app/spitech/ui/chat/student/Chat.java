package app.spitech.ui.chat.student;

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
import app.spitech.ui.chat.student.adapter.FirebaseChatAdapter;

public class Chat extends BaseActivity {

    ArrayList<DataBin> listMessage;
    FirebaseChatAdapter adapter;

    String from_user_id = "", to_user_id = "";
    private RecyclerView recyclerView;
    EditText editMessage;
    ImageView to_user_image, from_user_image;
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        init();
    }

    void init() {
        context = Chat.this;
        session = new AppSession(context);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        TextView toolbar_title = findViewById(R.id.toolbar_title);

       //aHideMenuList.add(R.id.action_notification);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDB = FirebaseDatabase.getInstance().getReference();

        to_user_image = findViewById(R.id.to_user_image);
        from_user_image = findViewById(R.id.from_user_image);

        if (getIntent().hasExtra("name")) {
            Bundle bundle=getIntent().getExtras();
            toolbar_title.setText(ConvertTo.toTitleCase(bundle.getString("name")));
            to_user_id =bundle.getString("to_user_id");
            if (Validation.isNotEmpty(bundle.getString("to_user_image"))) {
                String url= AppConfig.mediaCustomer+bundle.getString("to_user_image");
                SpiTech.getInstance().loadImage(context,url, to_user_image);
            } else {
                TextView to_photo_name = findViewById(R.id.to_photo_name);
                to_photo_name.setText(SpiTech.getInstance().getNamedPhoto(bundle.getString("name")));
            }

            from_user_id = session.getUserId();
            if (Validation.isNotEmpty(session.getPhoto())) {
                SpiTech.getInstance().loadImage(context, session.getPhoto(), from_user_image);
            } else {
                TextView from_photo_name = findViewById(R.id.from_photo_name);
                from_photo_name.setText(SpiTech.getInstance().getNamedPhoto(session.getName()));
            }

        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        listMessage = new ArrayList();
        adapter = new FirebaseChatAdapter(context, listMessage, this);
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

    public void saveMessage(String from_user_id, String to_user_id, String message) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.messageApi + "send_message",
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
                params.put("from_user_name", session.getName());
                params.put("from_user_id", from_user_id);
                params.put("to_user_id", to_user_id);
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
        String senderIdRef = "Messages/" + from_user_id + "/" + to_user_id;
        String receiverIdRef = "Messages/" + to_user_id + "/" + from_user_id;
        DatabaseReference userMessageKeyRef = mDB.child("messages").child(from_user_id).child(to_user_id).push();
        String messagePushId = userMessageKeyRef.getKey();

        Calendar dateCalender = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        String date = dateFormat.format(dateCalender.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String time = timeFormat.format(dateCalender.getTime());

        HashMap messageTextBody = new HashMap();
        messageTextBody.put("type", "text");
        messageTextBody.put("message", editMessage.getText().toString());
        messageTextBody.put("datetime", date + "," + time);
        messageTextBody.put("from_user_id", from_user_id);
        messageTextBody.put("to_user_id", to_user_id);

        HashMap messageBodyDetails = new HashMap<String, Map<String, String>>();
        messageBodyDetails.put(senderIdRef + "/" + messagePushId, messageTextBody);
        messageBodyDetails.put(receiverIdRef + "/" + messagePushId, messageTextBody);
        mDB.updateChildren(messageBodyDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                saveMessage(from_user_id, to_user_id, editMessage.getText().toString());
                editMessage.setText("");
                Log.e(tag, "Send successfully");
            } else {
                Log.e(tag, task.getException().toString());
            }
        });
    }


    void firebaseLoadMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        DataBin dataBin;
        while (iterator.hasNext()) {
            dataBin = new DataBin();
            dataBin.setDate(((DataSnapshot) iterator.next()).getValue().toString());
            dataBin.setFromUserId(((DataSnapshot) iterator.next()).getValue().toString());
            dataBin.setMessage(((DataSnapshot) iterator.next()).getValue().toString());
            dataBin.setToUserId(((DataSnapshot) iterator.next()).getValue().toString());
            dataBin.setType(((DataSnapshot) iterator.next()).getValue().toString());
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
        mDB.child("Messages").child(from_user_id).child(to_user_id).addChildEventListener(new ChildEventListener() {
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
    }
}
