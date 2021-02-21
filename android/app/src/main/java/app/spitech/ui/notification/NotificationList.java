package app.spitech.ui.notification;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.spitech.R;
import app.spitech.appSDK.AppMethods;
import app.spitech.appSDK.BaseActivity;

public class NotificationList extends BaseActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.notifications);
        init();
    }

    void init() {
        load(NotificationList.this, "Notifications", "Notifications");
       //aHideMenuList.add(R.id.action_notification);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        AppMethods.getInstance().notificationList(context,recyclerView);
    }
}
