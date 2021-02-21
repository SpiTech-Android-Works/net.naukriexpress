package app.spitech.ui.help;

import android.os.Bundle;

import app.spitech.R;
import app.spitech.appSDK.BaseActivity;

public class Help extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.help);
    }
}