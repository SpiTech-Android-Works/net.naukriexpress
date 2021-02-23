package com.kdl.naukriexpress.ui.other;

import android.os.Bundle;
import android.widget.Button;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppConfig;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class Maintenance extends BaseActivity {

    Button btnContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.maintenance);
        init();
    }

    void init(){
        btnContact=findViewById(R.id.btnContact);
        btnContact.setOnClickListener(v -> {
            contactWhatsapp(AppConfig.appWhatsAppContact,getResources().getString(R.string.whatsapp_default_msg_app_maintenance));
        });
    }


}