package com.kdl.naukriexpress.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;
import com.kdl.naukriexpress.appSDK.Validation;

public class AccountUpdateOTP extends BaseActivity {

    private String username = "";
    private Button btnSubmit;
    private TextView btnResend, otp_mobile, otp_timer, lbl_username;
    private String mobile = "";
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private LinearLayout layout3, resendLayout;
    private ImageView btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.otp_for_account_update);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(AccountUpdateOTP.this, "AccountUpdateOTP", "Account Verification");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Profile Edit");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //---------Basic End------------

        mAuth = FirebaseAuth.getInstance();

        btnChange = findViewById(R.id.btnChange);
        btnSubmit = findViewById(R.id.btnSubmit);
        lbl_username = findViewById(R.id.lbl_username);
        otp_mobile = findViewById(R.id.otp_mobile);
        otp_timer = findViewById(R.id.otp_timer);
        btnResend = findViewById(R.id.btnResend);
        layout3 = findViewById(R.id.layout3);
        resendLayout = findViewById(R.id.resendLayout);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);

        if (getIntent().hasExtra("username")) {
            username = getIntent().getExtras().getString("username");
            Log.e("Username:",username);
            lbl_username.setText(username);
            startTimer();
            if (Validation.isValidMobile(username)) {
                sendMobileOTP(username);
            } else {
                sendEmailOTP(username);
            }
        }

        btnSubmit.setOnClickListener(view -> {
            String OTP = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
            if (Validation.isValidMobile(username)) {
                verifyMobileOTP(mVerificationId, OTP);
            } else {
                emailOTPVerification(OTP);
            }
        });
        btnResend.setOnClickListener(view -> {
            startTimer();
            layout3.setVisibility(View.VISIBLE);
            resendLayout.setVisibility(View.GONE);
            if (Validation.isValidMobile(username)) {
                resendMobileTOP(username, mResendToken);
            } else {
                sendEmailOTP(username);
            }
        });

        btnChange.setOnClickListener(view -> {
            onBackPressed();
        });

        otpReader();
    }


    //-------------FIREBASE OTP VERIFICATION START---------------
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String TAG = "Firebase";

    void sendMobileOTP(String phoneNumber) {
       String phone = "+91" + phoneNumber;

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.e(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, "Invalid phone number."+phone);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.e(TAG, "Quota exceeded."+phone);
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.e(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this, mCallbacks);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    private void verifyMobileOTP(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendMobileTOP(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        phoneNumber = "+91" + phoneNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks, token);
    }

    void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "signInWithCredential:success");
                        updateAccount();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }


    /*------------------------------------ CountDown Timer Begin---------------------------------------*/
    private long mTimeLeftInMillis = (long) 00.00;

    private void startTimer() {
        mTimeLeftInMillis = 60000*2; //
        new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                layout3.setVisibility(View.GONE);
                resendLayout.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void updateCountDownText() {
        try {
            int minutes = (int) ((mTimeLeftInMillis / 1000) / 60);
            int seconds = (int) ((mTimeLeftInMillis / 1000) % 60);
            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            otp_timer.setText(timeLeftFormatted);
        } catch (Exception ex) {
            Log.e("TimerIssue", ex.toString());
        }
    }

    /*------------------------------------ CountDown Timer End---------------------------------------*/

    void otpReader() {
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 1) {
                    otp2.setFocusable(true);
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 1) {
                    otp3.setFocusable(true);
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 1) {
                    otp4.setFocusable(true);
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 1) {
                    otp5.setFocusable(true);
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 1) {
                    otp6.setFocusable(true);
                    otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    //-------------FIREBASE OTP VERIFICATION STOP---------------

    void sendEmailOTP(String username) {
        tag="send_email_otp";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "send_email_otp",
                response -> {
                    Log.e(tag, response);
                    try {
                        JSONObject object = new JSONObject(response);
                        showToast(object.getString("message"));
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> {
                    Log.e(tag, error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("email", username);
                params.put("customer_id", session.getUserId());
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

    void emailOTPVerification(String OTP) {
        tag="verify_email_otp";
        showProgress(context,"Verifying OTP");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "verify_email_otp",
                response -> {
                    closeProgress();
                    Log.e(tag, response);
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            updateAccount();
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
                params.put("customer_id", session.getUserId());
                params.put("otp", OTP);
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

    void updateAccount() {
        tag="update_account";
        showProgress(context, "Updating...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "update_account",
                response -> {
                    closeProgress();
                    Log.e(tag, response);
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg=object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                             showToast(msg);
                             startActivity(new Intent(context,ProfileEdit.class));
                             finish();
                        }else{
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
                if(Validation.isValidEmail(username)){
                    params.put("email", username);
                }else{
                    params.put("mobile", username);
                }
                params.put("customer_id", session.getUserId());
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
