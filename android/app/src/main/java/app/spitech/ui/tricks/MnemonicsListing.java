package app.spitech.ui.tricks;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import app.spitech.R;
import app.spitech.appSDK.AppMethods;
import app.spitech.appSDK.BaseActivity;

public class MnemonicsListing extends BaseActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.mnemonics_listing);
        init();
    }

    void init() {
        String subject_name="Mnemonics";
        load(MnemonicsListing.this, "MnemonicsListing", subject_name);
        if(session.getSubject()!=null){
           setTitle(session.getSubject());
        }

       //aHideMenuList.add(R.id.action_notification);
        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(context, R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);

        AppMethods.getInstance().mnemonicsList(context,recyclerView,session.getSubjectId());
    }



}
