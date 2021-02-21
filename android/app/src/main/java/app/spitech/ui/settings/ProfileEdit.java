package app.spitech.ui.settings;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;

public class ProfileEdit extends BaseActivity {

    private Button btnSubmit;
    private EditText name, email, city, state;
    private TextView mobile, customer_id;
    private CircleImageView imageView;
    private int PICK_IMAGE_REQUEST = 1;
    private String image;
    private TextView state_id;
    private ImageView btnEmailEdit, btnMobileEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.profile_edit);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(ProfileEdit.this, "ProfileEdit", "Profile Edit");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Profile Edit");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        //---------Basic End------------

        name = findViewById(R.id.name);
        btnEmailEdit = findViewById(R.id.btnEmailEdit);
        btnMobileEdit = findViewById(R.id.btnMobileEdit);
        state_id = findViewById(R.id.state_id);
        mobile = findViewById(R.id.mobile);
        customer_id = findViewById(R.id.customer_id);
        email = findViewById(R.id.email);
        city = findViewById(R.id.city);
        btnSubmit = findViewById(R.id.btnSubmit);
        state = findViewById(R.id.state);
        imageView = findViewById(R.id.photo);
        btnSubmit.setOnClickListener(view -> validate());
        imageView.setOnClickListener(v -> showFileChooser());

        readyOnlyEditText(state);
        state.setOnClickListener(v -> stateDialog(ProfileEdit.this, "Select State", state, state_id));
        btnEmailEdit.setOnClickListener(v -> {
            Intent intent=new Intent(context, EmailUpdate.class);
            intent.putExtra("username",email.getText().toString());
            startActivity(intent);
        });
        btnMobileEdit.setOnClickListener(v -> {
            Intent intent=new Intent(context, MobileUpdate.class);
            intent.putExtra("username",mobile.getText().toString());
            startActivity(intent);
        });
        loadProfile();
    }

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
                imageView.setImageBitmap(bitmap);
                Bitmap lastBitmap = null;
                lastBitmap = bitmap;
                image = getStringImage(lastBitmap);
                Log.e("spsoni", image);
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

    void validate() {
        if (Validation.isValidName(name.getText().toString())) {
            if (Validation.isNotEmpty(city.getText().toString())) {
                if (Validation.isNotEmpty(state.getText().toString())) {
                    submitData();
                } else {
                    showAlert("State is Required");
                }
            } else {
                showAlert("City is Required");
            }
        } else {
            showAlert("Please enter valid name");
        }
    }

    void submitData() {
        String tag="profile_update";
        showProgress(context, "Saving...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "profile_update",
                response -> {
                    closeProgress();
                    try {
                        Log.e(tag, response);
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            session.setPhoto(data.getString("photo"));
                            showToast("Profile details saved successfully");
                            Intent intent = new Intent(context, Settings.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
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
                params.put("name", name.getText().toString());
                params.put("city", city.getText().toString());
                params.put("state_id", state_id.getText().toString());
                params.put("customer_id", session.getUserId());
                if (image != null) {
                    params.put("image", image);
                }
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    void loadProfile() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "get_profile",
                response -> {
                    try {
                        progressBar.setVisibility(View.GONE);
                        /*Log.e(tag, response);*/
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            customer_id.setText("Student Id : " + setStringValue(data.getString("code")));
                            name.setText(setStringValue(data.getString("name")));
                            mobile.setText(setStringValue(data.getString("mobile")));
                            email.setText(setStringValue(data.getString("email")));
                            city.setText(setStringValue(data.getString("city")));
                            state.setText(setStringValue(data.getString("state")));
                            state_id.setText(setStringValue(data.getString("state_id")));
                            String photo = setStringValue(data.getString("photo"));
                            if (!photo.equalsIgnoreCase("")) {
                                String url = AppConfig.mediaCustomer + photo;
                                SpiTech.getInstance().loadImage(context, url, imageView);
                            }
                        } else {
                            showAlert(msg);
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("customer_id", session.getUserId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}
