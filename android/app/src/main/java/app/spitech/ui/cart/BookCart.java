package app.spitech.ui.cart;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.adapter.AddressAdapter;
import app.spitech.adapter.SpinnerStateAdapter;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;
import app.spitech.models.DataBin;
import app.spitech.models.SpinnerStateModel;

public class BookCart extends BaseActivity{


    private EditText house_no, name, mobile, email, alternate_contact, city, street_name, zip, landmark;
    private Spinner state;
    String package_id;
    AddressAdapter adapter;
    ArrayList<DataBin> list;
    RecyclerView recyclerView;
    ImageView image;
    private TextView btnSelectAddress, product_name, net_total;
    private Button btnCheckout;
    String price="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.book_cart);
        init();
    }

    void init() {
        load(BookCart.this, "BookCart", "GDC PAYMENT SYSTEM");
       //aHideMenuList.add(R.id.action_notification);

        product_name = findViewById(R.id.product_name);
        image = findViewById(R.id.image);
        net_total = findViewById(R.id.net_total);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnSelectAddress = findViewById(R.id.btnSelectAddress);

        name = findViewById(R.id.name);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        alternate_contact = findViewById(R.id.alternate_contact);
        house_no = findViewById(R.id.house_no);
        zip = findViewById(R.id.zip);
        city = findViewById(R.id.city);
        street_name = findViewById(R.id.street_name);
        landmark = findViewById(R.id.landmark);
        state = findViewById(R.id.state);

        btnCheckout.setOnClickListener(view -> {
            validate();
        });
        btnSelectAddress.setOnClickListener(view -> {
            openDialog();
        });
        if (getIntent().hasExtra("package_id")) {
            Bundle bundle=getIntent().getExtras();
            package_id = bundle.getString("package_id");
            price = bundle.getString("price");
            product_name.setText(bundle.getString("product_name"));
            SpiTech.getInstance().loadImage(context,bundle.getString("image"), image);
            net_total.setText(getRs(Double.parseDouble(price)));
        }
        name.setText(session.getName());
        mobile.setText(session.getMobile());
        email.setText(session.getEmail());
        loadState(context,state);
    }

    void validate() {
        if (Validation.isValidName(name.getText().toString())) {
            if (Validation.isValidMobile(mobile.getText().toString())) {
                if (Validation.isValidEmail(email.getText().toString())) {
                    if (Validation.isNotEmpty(city.getText().toString())) {
                        if (Validation.isNotEmpty(zip.getText().toString())) {
                            if (Validation.isNotEmpty(house_no.getText().toString())) {
                                order(package_id,session.getUserId());
                            } else {
                                house_no.setError("House/Flat/Building No is required");
                                house_no.requestFocus();
                            }
                        } else {
                            zip.setError("Zip is Required");
                            zip.requestFocus();
                        }
                    } else {
                        city.setError("City is Required");
                        city.requestFocus();
                    }
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
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("data");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            String order_id=object.getString("data");
                            paymentGatewayInitialize(price,session.getMobile(),product_name.getText().toString(),session.getName(),session.getEmail(),order_id);
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
                params.put("house_no", house_no.getText().toString());
                params.put("name", name.getText().toString());
                params.put("mobile", mobile.getText().toString());
                params.put("alternate_contact", alternate_contact.getText().toString());
                params.put("email", email.getText().toString());
                params.put("zip", zip.getText().toString());
                params.put("state", state.getSelectedItem().toString());
                params.put("landmark", landmark.getText().toString());
                params.put("city", city.getText().toString());
                params.put("street_name", street_name.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    AlertDialog alertDialog;
    void openDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.prompt_address, null);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
        loadAddress();

    }

    void loadAddress() {
        Log.e("SelftId", session.getUserId());
        list = new ArrayList<>();
        adapter = new AddressAdapter(context, list, this);
        recyclerView.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.orderApi + "customer_address",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        String msg = object.getString("message");
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jsonArray = new JSONArray(object.getString("data"));
                            DataBin data1 = null;
                            for (int row = 0; row < jsonArray.length(); row++) {
                                JSONObject value = jsonArray.getJSONObject(row);
                                data1 = new DataBin();
                                data1.setRowId(value.getString("address_id"));
                                data1.setName(value.getString("name"));
                                data1.setHouseNo(value.getString("house_no"));
                                data1.setStreet(value.getString("street_name"));
                                data1.setCity(value.getString("city"));
                                data1.setZip(value.getString("zip"));
                                data1.setState(value.getString("state"));
                                data1.setLandmark(value.getString("landmark"));
                                list.add(data1);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
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

    @Override
    public void onAdapterItemClick(String[] args) {
        alertDialog.dismiss();
        int position = Integer.parseInt(args[0]);
        DataBin data = list.get(position);
        name.setText(data.getName());
        house_no.setText(data.getHouseNo());
        city.setText(data.getCity());
        street_name.setText(data.getStreet());
        zip.setText(data.getZip());
        landmark.setText(data.getLandmark());
        state.setSelection(getSelectedState(data.getState()));
    }

    //--------------State Spinner Start-------------
    int getSelectedState(String id){
        int position = -1;
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getState().equalsIgnoreCase(id)) {
                position = i;
            }
        }
        return position;
    }

    ArrayList<SpinnerStateModel> itemList;
    public void loadState(Context context, Spinner spinner){
        itemList = new ArrayList();
        SpinnerStateAdapter adapter = new SpinnerStateAdapter(context, android.R.layout.simple_spinner_dropdown_item, itemList);
        spinner.setAdapter(adapter);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.sharedApi + "get_states",
                response -> {
                    Log.e("get_states", response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONArray jArray = new JSONArray(object.getString("data"));
                            SpinnerStateModel data;
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data=new SpinnerStateModel();
                                data.setStateId(value.getString("state_id"));
                                data.setState(value.getString("state"));
                                itemList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Log.e(tag, ex.toString());
                    }
                },
                error -> Log.e(tag, error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                params.put("api_key", AppConfig.api_key);
                params.put("country_id","88");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    //--------------State Spinner Stop-------------


    //*************************PAYUMONEY PAYMENT START********************************
    String PAYU_MONEY_HASH = "";
    String PAYU_MONEY_MERCHANT_KEY = "";
    String PAYU_MONEY_MERCHANT_ID = "";
    String PAYU_MONEY_SURL = "";
    String PAYU_MONEY_FURL = "";
    void paymentGatewayInitialize(String amount, String phone, String productName, String firstName, String email, String orderId) {
        String txnId=String.valueOf(Math.random());
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
