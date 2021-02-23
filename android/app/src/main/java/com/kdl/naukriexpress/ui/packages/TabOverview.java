package com.kdl.naukriexpress.ui.packages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.AppSession;
import com.kdl.naukriexpress.appSDK.BaseFragment;
import com.kdl.naukriexpress.appSDK.SpiTech;
import com.kdl.naukriexpress.appSDK.Validation;
import com.kdl.naukriexpress.ui.cart.PackageCart;

public class TabOverview extends BaseFragment {

    ProductDetails parent;
    WebView details;
    TextView title,product_flag,pdf_count,test_count,audio_count,video_count;
    ImageView image;
    Button btnBuy,btnShowAll,btnTutorTalk;
    LinearLayout testCount,pdfCount,audioCount,videoCount;
    static String package_id,price="0",imageUrl="";
    public TabOverview() {

    }

    public static TabOverview newInstance(String packageId,String image) {
        package_id=packageId;
        imageUrl=image;
        return new TabOverview();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_overview, container, false);
        init(rootView);
        return rootView;
    }

    void init( View rootView) {
        tag = "TabOverview";
        mContext=getContext();
        session= new AppSession(getContext());
        parent = (ProductDetails) getActivity();

        progressBar=rootView.findViewById(R.id.progressBar);
        title=rootView.findViewById(R.id.title);
        details=rootView.findViewById(R.id.details);
        image=rootView.findViewById(R.id.image);
        product_flag=rootView.findViewById(R.id.product_flag);
        video_count=rootView.findViewById(R.id.video_count);
        audio_count=rootView.findViewById(R.id.audio_count);
        pdf_count=rootView.findViewById(R.id.pdf_count);
        test_count=rootView.findViewById(R.id.test_count);

        btnBuy=rootView.findViewById(R.id.btnBuy);
        testCount=rootView.findViewById(R.id.testCount);
        pdfCount=rootView.findViewById(R.id.pdfCount);
        audioCount=rootView.findViewById(R.id.audioCount);
        videoCount=rootView.findViewById(R.id.videoCount);
        btnShowAll=rootView.findViewById(R.id.btnShowAll);
        btnTutorTalk=rootView.findViewById(R.id.btnTutorTalk);

        if(Validation.isNotEmpty(imageUrl)){
            SpiTech.getInstance().loadImage(mContext,imageUrl,image);
        }

        btnBuy.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PackageCart.class);
            intent.putExtra("package_id", package_id);
            intent.putExtra("product_name", title.getText().toString());
            intent.putExtra("price", price);
            intent.putExtra("image", imageUrl);
            startActivity(intent);
        });
        btnShowAll.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ProductDetails.class);
            intent.putExtra("package_id", package_id);
            intent.putExtra("image", imageUrl);
            intent.putExtra("package_name", title.getText().toString());
            intent.putExtra("active_tab", 1);
            startActivity(intent);
        });
        btnTutorTalk.setOnClickListener(v -> {
            String msg="Hello "+AppConfig.appName+", I am "+session.getName();
            parent.contactWhatsapp(AppConfig.appWhatsAppContact,msg);
        });
        loadDetails();
    }

    void loadDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.productApi + "product_details",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        Log.e(tag,response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject( object.getString("data"));

                            if(data.getString("is_purchased").equals("0")){
                               btnBuy.setVisibility(View.VISIBLE);
                            }else{
                                btnBuy.setVisibility(View.GONE);
                            }

                            title.setText(data.getString("name"));
                            details.loadData(data.getString("description"),"text/html","UTF-8");



                            price=data.getString("price");
                            btnBuy.setText("Buy Now For Rs."+Double.parseDouble(price)+"/-");

                            if(Validation.isNotEmpty(data.getString("product_flag"))){
                                product_flag.setVisibility(View.VISIBLE);
                            }else{
                                product_flag.setVisibility(View.GONE);
                            }
                            product_flag.setText(data.getString("product_flag"));

                            if(!data.getString("pdf_count").equalsIgnoreCase("0")){
                                pdfCount.setVisibility(View.VISIBLE);
                                pdf_count.setText(data.getString("pdf_count")+" PDF");
                            }
                            if(!data.getString("audio_count").equalsIgnoreCase("0")){
                                audioCount.setVisibility(View.VISIBLE);
                                audio_count.setText(data.getString("audio_count")+" Audio");
                            }
                            if(!data.getString("video_count").equalsIgnoreCase("0")){
                                videoCount.setVisibility(View.VISIBLE);
                                video_count.setText(data.getString("video_count")+" Video");
                            }
                            if(!data.getString("test_count").equalsIgnoreCase("0")){
                                testCount.setVisibility(View.VISIBLE);
                                test_count.setText(data.getString("test_count")+" Test");
                            }


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
                params.put("package_id", package_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

}
