package com.kdl.naukriexpress.ui.toppers;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppMethods;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class Toppers extends BaseActivity {

    private RecyclerView recyclerViewToppers;
    private String test_id="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.activity_toppers);
        init();
    }

    void init(){
        load(Toppers.this,"Toppers","Toppers");
        if(getIntent().hasExtra("test_id")){
            test_id=getIntent().getExtras().getString("test_id");
        }
        recyclerViewToppers=findViewById(R.id.recyclerViewToppers);
        recyclerViewToppers.setHasFixedSize(true);
        recyclerViewToppers.setLayoutManager(new LinearLayoutManager(context));
        AppMethods.getInstance().loadTestToppers(context,recyclerViewToppers,test_id);
    }
}
