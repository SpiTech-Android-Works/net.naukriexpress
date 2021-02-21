package app.spitech.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.StaticMethods;
import app.spitech.ui.home.Home;

public class OTP extends BaseActivity {

    private Button btnSubmit;
    private TextView btnResend, otp_mobile, otp_timer;
    private String mobile = "";
    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private LinearLayout layout3, resendLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.otp);
        init();
    }

    void init() {
        checkLogin = false;
        load(OTP.this, "OTP", "OTP");
        mAuth = FirebaseAuth.getInstance();

        btnSubmit = findViewById(R.id.btnSubmit);
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

        if (getIntent().hasExtra("mobile")) {
            mobile = getIntent().getExtras().getString("mobile");
            otp_mobile.setText("OTP is sent to your mobile number : " + mobile);
            if(enableOTP){
                startTimer();
                startPhoneNumberVerification(mobile);
            }else{
                dbLogin();
            }
        }

        btnSubmit.setOnClickListener(view -> {
            String code = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
            verifyPhoneNumberWithCode(mVerificationId, code);
        });
        btnResend.setOnClickListener(view -> {
            startTimer();
            layout3.setVisibility(View.VISIBLE);
            resendLayout.setVisibility(View.GONE);
            resendVerificationCode(mobile, mResendToken);
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

    void startPhoneNumberVerification(String phoneNumber) {
        phoneNumber = "+91" + phoneNumber;
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.e(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.e(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, "Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    showAlert("Quota exceeded.");
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.e(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
                showToast("OTP sent to your mobile number");
            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
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

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        phoneNumber = "+91" + phoneNumber;

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "signInWithCredential:success");
                        dbLogin();
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
    void dbLogin() {
        showProgress(context, "Creating Account...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.authApi + "check_username",
                response -> {
                    closeProgress();
                    Log.e(tag, response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if (data.getString("signup_status").equalsIgnoreCase("1")) {
                                session.setEmail(data.getString("email"));
                                session.setName(data.getString("name"));
                                session.setMobile(data.getString("mobile"));
                                session.setUserId(data.getString("customer_id"));
                                session.setPhoto(data.getString("photo"));
                                session.setCode(data.getString("code"));
                                session.setLogin(true);
                                startActivity(new Intent(context, Home.class));
                                finish();
                            } else {
                                showToast("Please complete your profile first.");
                                Intent intent = new Intent(this, MobileNumberSignup.class);
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("email", data.getString("email"));
                                intent.putExtra("name", data.getString("name"));
                                intent.putExtra("customer_id", data.getString("customer_id"));
                                intent.putExtra("state_id", data.getString("state_id"));
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            Intent intent = new Intent(this, MobileNumberSignup.class);
                            intent.putExtra("mobile", mobile);
                            startActivity(intent);
                            finish();
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
                params.put("mobile", mobile);
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
