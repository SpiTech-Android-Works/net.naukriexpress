package com.kdl.naukriexpress.ui.batch;

import android.os.Bundle;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class BatchDetails extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.batch_details);
    }
}