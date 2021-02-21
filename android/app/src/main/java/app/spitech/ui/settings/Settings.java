package app.spitech.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.ComingSoon;
import app.spitech.appSDK.BaseActivity;
import app.spitech.appSDK.SpiTech;
import app.spitech.appSDK.Validation;
import app.spitech.ui.bookmarks.Bookmarks;

public class Settings extends BaseActivity {

    private LinearLayout boxBookmarks, boxWallet, boxStudentZone, boxFeedback;
    private TextView name, customer_id, username;
    private ImageView image,btnEdit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.settings);

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    void init() {
        load(Settings.this,"Settings","My Account");
        Toolbar toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        TextView toolbar_title=findViewById(R.id.toolbar_title);
        toolbar_title.setText("My Account");


        boxBookmarks = findViewById(R.id.boxBookmarks);
        boxStudentZone = findViewById(R.id.boxStudentZone);
        boxWallet = findViewById(R.id.boxWallet);
        boxFeedback = findViewById(R.id.boxFeedback);
        btnEdit= findViewById(R.id.btnEdit);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        customer_id = findViewById(R.id.customer_id);
        customer_id.setText("Student Id - " + session.getCode());
        name.setText(session.getName());
        if(Validation.isNotEmpty(session.getMobile())){
            username.setText(session.getMobile());
        }else{
            username.setText(session.getEmail());
        }
        if (Validation.isNotEmpty(session.getPhoto())) {
            String url = AppConfig.mediaCustomer + session.getPhoto();
            SpiTech.getInstance().loadImage(context,url,image);
        }
        btnEdit.setOnClickListener(v -> startActivity(new Intent(context, ProfileEdit.class)));
        boxBookmarks.setOnClickListener(v -> startActivity(new Intent(context, Bookmarks.class)));
        boxStudentZone.setOnClickListener(v -> startActivity(new Intent(context, ComingSoon.class)));
        boxWallet.setOnClickListener(v -> startActivity(new Intent(context, ComingSoon.class)));
        boxFeedback.setOnClickListener(v -> startActivity(new Intent(context, ComingSoon.class)));
    }
}