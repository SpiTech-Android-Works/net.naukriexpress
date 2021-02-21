package app.spitech.ui.other;

import android.os.Bundle;
import android.widget.Button;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;

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