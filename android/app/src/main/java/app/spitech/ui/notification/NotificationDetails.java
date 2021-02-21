package app.spitech.ui.notification;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import app.spitech.R;
import app.spitech.appSDK.AppMethods;
import app.spitech.appSDK.BaseActivity;

public class NotificationDetails extends BaseActivity {

    Button btnJoin;
    TextView date,title;
    WebView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.notification_details);
        init();
    }

    void init() {
        load(NotificationDetails.this, "NotificationDetails", "Notification Details");
       //aHideMenuList.add(R.id.action_notification);
        title=findViewById(R.id.title);
        date=findViewById(R.id.date);
        description=findViewById(R.id.description);
        loadDetails();
    }

    void  loadDetails(){
        if(getIntent().hasExtra("notification_id")){
            Bundle bundle=getIntent().getExtras();
            title.setText(bundle.getString("title"));
            date.setText(bundle.getString("date"));
            description.loadData(bundle.getString("description"),"text/html","UTF-8");
            AppMethods.getInstance().notificationStatusUpdate(context,bundle.getString("notification_id"));
        }

    }



}
