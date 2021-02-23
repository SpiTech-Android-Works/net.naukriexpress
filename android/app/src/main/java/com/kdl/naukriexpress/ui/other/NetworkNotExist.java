package com.kdl.naukriexpress.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.BaseActivity;
import com.kdl.naukriexpress.appSDK.StaticMethods;
import com.kdl.naukriexpress.ui.auth.Login;

public class NetworkNotExist extends BaseActivity {

    Button btnTry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.network_not_exist);
        init();
    }
    void init(){
        btnTry=findViewById(R.id.btnTry);
        btnTry.setOnClickListener(v -> {
            if(StaticMethods.isNetworkAvailable(NetworkNotExist.this)){
                startActivity(new Intent(NetworkNotExist.this, Login.class));
            }
        });
    }

}