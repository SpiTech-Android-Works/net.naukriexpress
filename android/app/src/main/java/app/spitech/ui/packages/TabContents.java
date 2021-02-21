package app.spitech.ui.packages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.AppSession;
import app.spitech.appSDK.BaseFragment;
import app.spitech.models.DataBin;
import app.spitech.ui.cart.PackageCart;
import app.spitech.ui.packages.adapter.FolderAdapter;

public class TabContents extends BaseFragment {

    RecyclerView recyclerView;
    ProductDetails parent;
    Button btnBuy;
    static String package_id;
    String price, package_name, package_image_url;

    public TabContents() {

    }

    public static TabContents newInstance(String packageId) {
        package_id = packageId;
        TabContents fragment = new TabContents();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_content, container, false);
        init(rootView);
        return rootView;
    }

    void init(View rootView) {
        tag = "TabContents";
        mContext = getContext();
        session = new AppSession(getContext());
        parent = (ProductDetails) getActivity();
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        loadDetails();
    }

    @Override
    public void buyCourse() {
        super.buyCourse();
        Intent intent = new Intent(mContext, PackageCart.class);
        intent.putExtra("package_id", package_id);
        intent.putExtra("product_name", package_name);
        intent.putExtra("price", price);
        intent.putExtra("image", package_image_url);
        startActivity(intent);
    }


    void loadDetails() {
        ArrayList list1 = new ArrayList<DataBin>();
        FolderAdapter adapter1 = new FolderAdapter(mContext, list1,this,package_name);
        recyclerView.setAdapter(adapter1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.productApi + "product_folders",
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        Log.e(tag, response);
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject data = new JSONObject(object.getString("data"));

                            String is_purchased = data.getString("is_purchased");
                            /*if (is_purchased.equals("1")) {
                                btnBuy.setVisibility(View.GONE);
                            } else {
                                btnBuy.setVisibility(View.VISIBLE);
                            }*/
                            price = data.getString("rate");
                            package_name = data.getString("name");
                            package_image_url = data.getString("image");
                           // btnBuy.setText("Buy Now For Rs." + Double.parseDouble(price) + "/-");

                            DataBin data1 = null;
                            JSONArray jsonArray = null;
                            jsonArray = new JSONArray(data.getString("aFolder"));
                            if (jsonArray.length() > 0) {
                                for (int row = 0; row < jsonArray.length(); row++) {
                                    JSONObject value = jsonArray.getJSONObject(row);
                                    data1 = new DataBin();
                                    data1.setRowId(value.getString("folder_id"));
                                    data1.setName(value.getString("folder_name"));
                                    data1.setCount(value.getString("item_count"));
                                    data1.setPackageId(value.getString("package_id"));
                                    data1.setIsPurchased(is_purchased);
                                    data1.setType(value.getString("type"));
                                    data1.setRate(price);
                                    list1.add(data1);
                                }
                                adapter1.notifyDataSetChanged();
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
                params.put("package_id", package_id);
                params.put("customer_id", session.getUserId());
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
