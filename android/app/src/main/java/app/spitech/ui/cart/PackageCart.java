package app.spitech.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;

public class PackageCart extends BaseActivity{

    private EditText name, mobile, email,district,block;
    String package_id;
    ImageView image;
    private TextView product_name, net_total;
    private Button btnCheckout;
    String price="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.package_cart);
        init();
    }

    void init() {
        load(PackageCart.this, "PackageCart", AppConfig.appName);
       //aHideMenuList.add(R.id.action_notification);

        product_name = findViewById(R.id.product_name);
        image = findViewById(R.id.image);
        net_total = findViewById(R.id.net_total);
        btnCheckout = findViewById(R.id.btnCheckout);

        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        district = findViewById(R.id.district);
        block = findViewById(R.id.block);


        if (getIntent().hasExtra("package_id")) {
            Bundle bundle=getIntent().getExtras();
            package_id = bundle.getString("package_id");
            price = bundle.getString("price");
            product_name.setText(bundle.getString("product_name"));
            SpiTech.getInstance().loadImage(context,bundle.getString("image"), image);
            net_total.setText(getRs(Double.parseDouble(price)));
        }
        name.setText(setStringValue(session.getName()));
        mobile.setText(setStringValue(session.getMobile()));
        email.setText(setStringValue(session.getEmail()));
        btnCheckout.setOnClickListener(view -> {
            validate();
        });
    }

    void validate() {
        if (Validation.isValidName(name.getText().toString())) {
            if (Validation.isValidMobile(mobile.getText().toString())) {
                if (Validation.isValidEmail(email.getText().toString())) {
                    order(package_id,session.getUserId());
                } else {
                    email.setError("Invalid Email Id");
                    email.requestFocus();
                }
            } else {
                mobile.setError("Invalid Mobile Number");
                mobile.requestFocus();
            }
        } else {
            name.setError("Invalid Name");
            name.requestFocus();
        }
    }

    private void order(String package_id,String customer_id) {

        String tag="order";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.orderApi + "order",
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("data");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data= new JSONObject(object.getString("data"));
                            String order_id=data.getString("order_id");
                            if(data.getString("payment_system").equalsIgnoreCase("custom")){
                                Intent intent=new Intent(context,CustomPayment.class);
                                intent.putExtra("order_id",order_id);
                                intent.putExtra("amount",price);
                                intent.putExtra("product_name",product_name.getText().toString());
                                startActivity(intent);
                                finish();
                            }else{
                                paymentGatewayInitialize(price,mobile.getText().toString(),product_name.getText().toString(),name.getText().toString(),email.getText().toString(),order_id);
                            }
                        }else{
                            showAlert(msg);
                        }
                    } catch (JSONException ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("package_id", package_id);
                params.put("customer_id", customer_id);
                params.put("name", name.getText().toString());
                params.put("mobile",mobile.getText().toString());
                params.put("email", email.getText().toString());
                params.put("district", district.getText().toString());
                params.put("block", block.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    //*************************PAYUMONEY PAYMENT START********************************
    String PAYU_MONEY_HASH = "";
    String PAYU_MONEY_MERCHANT_KEY = "";
    String PAYU_MONEY_MERCHANT_ID = "";
    String PAYU_MONEY_SURL = "";
    String PAYU_MONEY_FURL = "";
    void paymentGatewayInitialize(String amount, String phone, String productName, String firstName, String email, String orderId) {
        String txnId=orderId;
        String tag="payumoney_hash";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.orderApi + "payumoney_hash",
                response -> {
                    closeProgress();
                    Log.e(tag, response);
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));
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
