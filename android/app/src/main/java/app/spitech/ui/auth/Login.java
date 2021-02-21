package app.spitech.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppMethods;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.StaticMethods;
import app.spitech.appSDK.Validation;
import app.spitech.ui.home.Home;

public class Login extends BaseActivity {

    private Button btnSubmit;
    private EditText mobile;
    private ImageView btnGoogle;
    private TextView app_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
    }

    void init() {
        checkLogin = false;
        load(Login.this, "Login", "Login");
        progressBar=findViewById(R.id.progressBar);
        app_version=findViewById(R.id.app_version);
        app_version.setText("App Version : "+SpiTech.getInstance().getAppVersion(context));

        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        if(getIntent().hasExtra("logout")){
             firebaseSignout();
        }else{
            if (session != null && session.checkLogin()) {
                startActivity(new Intent(context, Home.class));
            }
        }
        //----google login----------
        if (AppConfig.enableGmailLogin) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }

        /*********Checking App Status*************/
        AppMethods.getInstance().checkAppStatus(context);

        mobile = findViewById(R.id.mobile);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnGoogle = findViewById(R.id.btnGoogle);
        if (AppConfig.enableGmailLogin) {
            btnGoogle.setVisibility(View.VISIBLE);
        }
        btnSubmit.setOnClickListener(view -> {
            if (Validation.isValidMobile(mobile.getText().toString())) {
                Intent intent = new Intent(context, OTP.class);
                intent.putExtra("mobile", mobile.getText().toString());
                startActivity(intent);
            } else {
                mobile.setError("Please enter valid mobile number");
                mobile.requestFocus();
            }
        });

        btnGoogle.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getIntent().hasExtra("logout")){
            firebaseSignout();

        }else{
            if (mAuth != null) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser!=null && currentUser.getEmail()!=""){
                    updateUI(currentUser);
                }
            }
        }
    }

    private void firebaseSignout(){
        if(mAuth!=null){
            Log.e("logout","logout");
            mAuth.signOut();
        }
        if(mGoogleSignInClient!=null){
            mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
                    task -> Log.e("revokeAccess","Firebase Logout Done"));
        }

    }

    //-----------Google Begin---------------
    private static final int GOOGLE_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e(tag, "GoogleLogin:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.e(tag,  "GoogleLogin:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    //-----------Google End---------------



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e(tag, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (Exception ex) {
                Log.e(tag, "Google sign in failed : "+ex.toString());
                updateUI(null);
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String uid = user.getUid();
            String email = user.getEmail();
            Log.e(tag, "Uid:" + uid);
            Log.e(tag, "Name:" + name);
            Log.e(tag, "Email:" + email);

            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "social_login",
                    response -> {
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equalsIgnoreCase("1")) {
                                JSONObject data = new JSONObject(object.getString("data"));
                                if(data.getString("signup_status").equalsIgnoreCase("1")){
                                    session.setEmail(data.getString("email"));
                                    session.setName(data.getString("name"));
                                    session.setMobile(data.getString("mobile"));
                                    session.setUserId(data.getString("customer_id"));
                                    session.setPhoto(data.getString("photo"));
                                    session.setCode(data.getString("code"));
                                    session.setLogin(true);
                                    startActivity(new Intent(context, Home.class));
                                    finish();
                                }else{
                                    Intent intent=new Intent(context, SocialAccountSignup.class);
                                    intent.putExtra("email",email);
                                    intent.putExtra("name",name);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Intent intent=new Intent(context, SocialAccountSignup.class);
                                intent.putExtra("email",email);
                                intent.putExtra("name",name);
                                startActivity(intent);
                                finish();
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
                    params.put("email", email);
                    params.put("device_name", StaticMethods.getDeviceName());
                    params.put("device_id", getDeviceId(context));
                    params.put("ip_address", StaticMethods.getIpAddress(getApplicationContext()));
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
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
