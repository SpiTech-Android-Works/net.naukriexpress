package com.kdl.naukriexpress.ui.subject;

import android.os.Bundle;
import android.widget.GridView;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.AppMethods;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class SubjectListing extends BaseActivity {

    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.subject);
        init();
    }

    void init() {
        load(SubjectListing.this, "SubjectListing", "Select Subject");
       //aHideMenuList.add(R.id.action_notification);

        gridView = findViewById(R.id.gridView);
        AppMethods.getInstance().subjectList(context,gridView,session.getPurpose());
    }

}
