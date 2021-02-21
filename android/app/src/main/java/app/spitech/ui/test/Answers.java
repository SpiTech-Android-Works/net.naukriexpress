package app.spitech.ui.test;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import app.spitech.R;
import app.spitech.appSDK.AppConfig;
import app.spitech.appSDK.BaseActivity;
import app.spitech.ui.auth.Login;

public class Answers extends BaseActivity {

    String result_id = "0", test_id = "";
    WebView webView;
    private Button btnRestart;
    private TextView test_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.answers);
        initMain();
    }

    void initMain() {
        load(Answers.this, "SpitechAnswers", getString(R.string.test_answer));
       //aHideMenuList.add(R.id.action_notification);
        webView =findViewById(R.id.webview);
        screen=findViewById(R.id.screen);
        test_name= findViewById(R.id.test_name);

        if (session.checkLogin() && getIntent().hasExtra("result_id")) {
            result_id = getIntent().getExtras().getString("result_id");
            test_id = getIntent().getExtras().getString("test_id");
            test_name.setText("Answer of Test No : " + test_id);
            String url = AppConfig.server + "webapi/get_answers/" + result_id+"/hindi";
            debug("answer_url",url);
            webView.loadUrl(url);
        } else {
            Intent intent = new Intent(context, Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(context,ScoreCard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}