package app.spitech.ui.other;

import android.os.Bundle;

import app.spitech.R;
import app.spitech.appSDK.BaseActivity;

public class Invite extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.activity_invite);
    }
}
