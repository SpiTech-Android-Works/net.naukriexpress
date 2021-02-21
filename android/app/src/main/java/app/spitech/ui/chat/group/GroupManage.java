package app.spitech.ui.chat.group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;
import app.spitech.ui.chat.ChatHome;

public class GroupManage extends BaseActivity {

    String group_id = "0", imageByteString = "";
    EditText name, description;
    ImageView image, btnImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.group_manage);
        init();
    }

    void init() {
        context = GroupManage.this;
        session = new AppSession(context);
        mAuth=FirebaseAuth.getInstance();
        mDB=FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        TextView toolbar_title = findViewById(R.id.toolbar_title);

        name = findViewById(R.id.name);
        description = findViewById(R.id.description);
        image = findViewById(R.id.image);
        btnImage = findViewById(R.id.btnImage);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        toolbar_title.setText("Create Group");
        if (getIntent().hasExtra("group_id")) {
            toolbar_title.setText("Edit Group");

            Bundle bundle = getIntent().getExtras();
            group_id = bundle.getString("group_id");
            name.setText(bundle.getString("name"));
            description.setText(bundle.getString("description"));
            if (Validation.isNotEmpty(bundle.getString("image"))) {
                String url = AppConfig.mediaGroup + bundle.getString("image");
                SpiTech.getInstance().loadImage(context, url, image);
            }
        }

        btnSubmit.setOnClickListener(view -> {
            validation();
        });
        btnImage.setOnClickListener(view -> {
            showFileChooser();
        });

    }


    //--------------File Browse Begin--------------
    private int PICK_IMAGE_REQUEST = 1;

    private void showFileChooser() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("aspectX", 1);
        pickImageIntent.putExtra("aspectY", 1);
        pickImageIntent.putExtra("scale", true);
        pickImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
                Bitmap lastBitmap = null;
                lastBitmap = bitmap;
                imageByteString = getStringImage(lastBitmap);
                Log.e("spsoni", imageByteString);
                //submitData(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }
    //--------------File Browse End--------------

    void validation() {
        if (Validation.isNotEmpty(name.getText().toString())) {
            if (Validation.isNotEmpty(description.getText().toString())) {
                submit();
            } else {
                description.requestFocus();
                showAlert("Group Description is Required");
            }
        } else {
            name.requestFocus();
            showAlert("Group Name is Required");
        }
    }

    public void submit() {
        showProgress(context, "Saving...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.messageApi + "group_save",
                response -> {
                    closeProgress();
                    Log.e("chat_group_save", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            String createdGroupId=object.getString("data");
                            if (group_id.equalsIgnoreCase("0")) {
                                createGroup(createdGroupId);
                                showToast("Group Created Successfully");
                            } else {
                                showToast("Group Updated Successfully");
                            }
                            onBackPressed();
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    closeProgress();
                    Log.e(tag, error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("group_id", group_id);
                params.put("admin_user_id", session.getUserId());
                params.put("name", name.getText().toString());
                params.put("description", description.getText().toString());
                if (image != null) {
                    params.put("image", imageByteString);
                }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, ChatHome.class);
        intent.putExtra("active_tab", 2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //------firebase--------------
    void createGroup(String group_id) {
        mDB.child("groups").child("group_"+group_id).setValue("")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Group Created Successfully");
                    }
                });
    }
}