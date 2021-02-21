package app.spitech.ui.cart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.CustomUI;
import app.spitech.appSDK.StaticMethods;
import app.spitech.appSDK.Validation;
import app.spitech.ui.store.Store;

public class CustomPayment extends BaseActivity {

    LinearLayout  bank_account_details_layout;
    TextView account_upi, bank_account_details, note, txtPackageName, txtAmount,btnPayNow;
    String amount = "100";
    String order_id = "0";
    String product_name = "Course Package";
    String customer_name = "Student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_payment);
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(CustomPayment.this, "CustomPayment", "CustomPayment");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(AppConfig.appName);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        progressBar = findViewById(R.id.progressBar);
        //---------Basic End------------
        account_upi = findViewById(R.id.account_upi);
        btnPayNow = findViewById(R.id.btnPayNow);
        bank_account_details_layout = findViewById(R.id.bank_account_details_layout);

        bank_account_details_layout.setVisibility(View.GONE);

        bank_account_details = findViewById(R.id.bank_account_details);
        note = findViewById(R.id.note);
        txtPackageName = findViewById(R.id.txtPackageName);
        txtAmount = findViewById(R.id.txtAmount);

        if (getIntent().hasExtra("amount")) {
            order_id= getIntent().getStringExtra("order_id");
            amount = getIntent().getStringExtra("amount");
            product_name = getIntent().getStringExtra("product_name");
            customer_name = session.getName();
            txtPackageName.setText(product_name);
            txtAmount.setText(CustomUI.getRs(Double.parseDouble(amount)));
            loadData();
            btnPayNow.setOnClickListener(v -> payUsingUpi(amount, account_upi.getText().toString(), customer_name, product_name));
        }
    }

    //------------------UPI PAYMENT BEGIN-----------------
    final int UPI_PAYMENT = 9001;
    void payUsingUpi(String amount, String upiId, String name, String note) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("mc", "")
                .appendQueryParameter("tr", order_id)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        Intent chooser = Intent.createChooser(upiPayIntent, "Select UPI Payment Method");
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(this, "No UPI app found, please install one to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case UPI_PAYMENT:
                if (data != null) {
                    String upiResponse = data.getStringExtra("response");
                    upiResponseHandler(upiResponse);
                } else {
                    upiResponseHandler("nothing");
                }
                break;
        }
    }

    private void upiResponseHandler(String upiResponse) {
        String tag="upiResponseHandler";
        if (StaticMethods.isNetworkAvailable(context)) {
            String str = upiResponse;
            Log.e("UpiResponse", str);
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }
            }
            if (status.equals("success")) {
                upiPaymentSuccess(approvalRefNo);
                Log.e(tag, "Transaction successful. ApprovalRefNo" + approvalRefNo);
            } else {
                Log.e(tag, "Payment failed/cancelled" );
            }
        } else {
            Toast.makeText(context, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    //---------------UPI PAYMENT END---------------

    private void loadData() {
        String tag = "academy_payment_details";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.orderApi + "academy_bank_details",
                response -> {
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if (Validation.isNotEmpty(data.getString("account_upi"))) {
                                account_upi.setVisibility(View.VISIBLE);
                            }
                            if (Validation.isNotEmpty(data.getString("bank_account_details"))) {
                                bank_account_details_layout.setVisibility(View.VISIBLE);
                            }
                            note.setText(data.getString("payment_note"));
                            account_upi.setText(data.getString("account_upi"));
                            bank_account_details.setText(data.getString("bank_account_details"));
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException ex) {
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private void upiPaymentSuccess(String orderId) {
        String tag = "upi_payment_success";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.orderApi + "upi_payment_success",
                response -> {
                    try {
                        progressBar.setVisibility(View.GONE);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            showToast("Payment done successfully");
                            Intent intent=new Intent(context, Store.class);
                            startActivity(intent);
                            finish();
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException ex) {
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
                params.put("order_id", orderId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}