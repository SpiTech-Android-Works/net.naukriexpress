package app.spitech.ui.batch;

import android.os.Bundle;

import app.spitech.R;
import app.spitech.appSDK.BaseActivity;

public class BatchDetails extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.batch_details);
    }
}