package com.kdl.naukriexpress.ui.other;

import android.os.Bundle;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class Invite extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.activity_invite);
    }
}
