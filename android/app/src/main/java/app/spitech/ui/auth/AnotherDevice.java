package app.spitech.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import app.spitech.R;
import app.spitech.appSDK.AppMethods;
import app.spitech.appSDK.BaseActivity;

public class AnotherDevice extends BaseActivity {

    TextView device_name;
    Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.another_device);
        init();
    }

    void init(){
        load(AnotherDevice.this,"AnotherDevice","Another Device Login Detected");
        device_name=findViewById(R.id.device_name);
        btnLogout=findViewById(R.id.btnLogout);
        if(getIntent().hasExtra("device_name")){
            device_name.setText(getIntent().getExtras().getString("device_name"));
        }
        btnLogout.setOnClickListener(v -> AppMethods.getInstance().deviceLogout(getApplicationContext(),session.getUserId(),session));
    }



}