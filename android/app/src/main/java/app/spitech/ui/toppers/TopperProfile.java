package app.spitech.ui.toppers;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import app.spitech.models.DataBin;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.SpiTech;
import app.spitech.ui.toppers.adapter.ToppersTestAdapter;

public class TopperProfile extends BaseActivity {

    private TextView name,total_test,todays_rank;
    private RecyclerView recyclerView;
    String customer_id="0";
    private ImageView photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.topper_profile);
        init();
    }

    void init() {
        load(TopperProfile.this, "SpitechTopperProfile", "Todays Topper");
        screen=findViewById(R.id.screen);
        name=findViewById(R.id.name);
        total_test=findViewById(R.id.total_test);
        todays_rank=findViewById(R.id.todays_rank);
        photo=findViewById(R.id.photo);

        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        SpiTech.getInstance().recyclerViewHorizontalSeperator(context,recyclerView);



        if(getIntent().hasExtra("customer_id")){
            customer_id=getIntent().getExtras().getString("customer_id");
        }
        loadDetails(customer_id);
    }


    public void loadDetails(String customer_id) {
        ArrayList<DataBin> list1 = new ArrayList<DataBin>();
        ToppersTestAdapter adapter1 = new ToppersTestAdapter(context, list1);
        recyclerView.setAdapter(adapter1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.userApi + "toppers_profile",
                response -> {
                    /*Log.e(tag, response);*/
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equalsIgnoreCase("1")) {
                            JSONObject object1 = new JSONObject(object.getString("data"));
                            name.setText(object1.getString("name"));

                            String url= AppConfig.mediaCustomer+object1.getString("photo");
                            SpiTech.getInstance().loadRoundedImage(context,url,photo,R.drawable.ic_user);

                            JSONArray jArray = new JSONArray(object1.getString("aTest"));
                            DataBin data = null;
                            double obtained_marks=0.0,total_marks=0.0;

                            total_test.setText("Total Test :"+String.valueOf(jArray.length()));
                            for (int row = 0; row < jArray.length(); row++) {
                                JSONObject value = jArray.getJSONObject(row);
                                data = new DataBin();
                                obtained_marks+=value.getDouble("obtained_marks");
                                total_marks+=value.getDouble("total_marks");
                                String score=value.getDouble("obtained_marks")+"/"+value.getDouble("total_marks");

                                data.setScore(score);
                                String test_name="("+(row+1)+")"+value.getString("test_name");
                                data.setName(test_name);
                                list1.add(data);
                            }
                            todays_rank.setText("Today\'s Score : "+String.format("%.2f", obtained_marks)+"/"+String.format("%.2f", total_marks));
                            adapter1.notifyDataSetChanged();
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

                params.put("customer_id",customer_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}

