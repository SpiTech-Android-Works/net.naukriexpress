package app.spitech.ui.cart;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;

public class PayUMoneyHandler extends BaseActivity {

    String PAYU_MONEY_HASH = "";
    String PAYU_MONEY_MERCHANT_KEY = "";
    String PAYU_MONEY_MERCHANT_ID = "";
    String PAYU_MONEY_SURL = "";
    String PAYU_MONEY_FURL = "";

    String amount, txnId, phone, productName, firstName, email, orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.cart_payu_money);
        init();
    }

    void init() {
        if(getIntent().hasExtra("orderId")){

            amount=getIntent().getExtras().getString("amount");
            phone=getIntent().getExtras().getString("phone");
            productName=getIntent().getExtras().getString("productName");
            firstName=getIntent().getExtras().getString("firstName");
            email=getIntent().getExtras().getString("email");
            orderId=getIntent().getExtras().getString("orderId");
            txnId=String.valueOf(Math.random());

            setServerSidePaymoneyHash();

        }else{
            showAlert("PayUMoney Fields are missing");
        }
    }

    void setServerSidePaymoneyHash() {
        String tag="payumoney_hash";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.orderApi + "payumoney_hash",
                response -> {
                    closeProgress();
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            String hash_string = data.getString("hash_string");
                            PAYU_MONEY_HASH = data.getString("hash");
                            PAYU_MONEY_MERCHANT_KEY = data.getString("merchant_key");
                            PAYU_MONEY_MERCHANT_ID = data.getString("merchant_id");
                            PAYU_MONEY_SURL = data.getString("surl");
                            PAYU_MONEY_FURL = data.getString("furl");
                            payUInit(amount, txnId, phone, productName, firstName, email, orderId);
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
                params.put("txnid", txnId);
                params.put("amount", amount);
                params.put("productinfo", productName);
                params.put("firstname", firstName);
                params.put("email", email);
                params.put("udf1", orderId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    void payUInit(String amount, String txnId, String phone, String productName, String firstName, String email, String orderId) {

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(amount)                          // Payment amount
                .setTxnId(txnId)                           // Transaction ID
                .setPhone(phone)                           // User Phone number
                .setProductName(productName)               // Product Name or description
                .setFirstName(firstName)                   // User First name
                .setEmail(email)                           // User Email ID
                .setsUrl(PAYU_MONEY_SURL)       // Success URL (surl)
                .setfUrl(PAYU_MONEY_FURL)        //Failure URL (furl)
                .setUdf1(orderId)
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(false)                              // Integration environment - true (Debug)/ false(Production)
                .setKey(PAYU_MONEY_MERCHANT_KEY)                // Merchant key
                .setMerchantId(PAYU_MONEY_MERCHANT_ID);         // Merchant ID
        try {
            //declare paymentParam object
            PayUmoneySdkInitializer.PaymentParam paymentParam = builder.build();
            //set the hash
            paymentParam.setMerchantHash(PAYU_MONEY_HASH);
            // Invoke the following function to open the checkout page.
            PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, this, R.style.AppTheme_default, false);
        } catch (Exception ex) {
             Log.e("payUInit",ex.toString());
        }
    }
}
