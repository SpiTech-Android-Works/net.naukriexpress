package app.spitech.ui.job;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import app.spitech.R;
import app.spitech.appSDK.Interfaces.CommonInterface;
import app.spitech.appSDK.BaseActivity;

public class JobDetails extends BaseActivity implements CommonInterface {

    public String job_id = "";
    public ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.job);
        init();
    }

    void init() {
        load(JobDetails.this, "JobDetails", "Job Description");
       //aHideMenuList.add(R.id.action_notification);
        screen=findViewById(R.id.screen);

        if (getIntent().hasExtra("job_id")) {
            job_id = getIntent().getExtras().getString("job_id");
        }
        JobPagerAdapter adapter = new JobPagerAdapter(context, getSupportFragmentManager(), job_id);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        image =findViewById(R.id.image);

    }

    public class JobPagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.job_details, R.string.similar_jobs};
        private final Context mContext;
        private String job_id = "";

        public JobPagerAdapter(Context context, FragmentManager fm, String param1) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            mContext = context;
            job_id = param1;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = FrgJobDetails.newInstance(mContext,job_id,JobDetails.this);
                    break;
                case 1:
                    fragment = FrgSimilarJob.newInstance(mContext,job_id,JobDetails.this);
                    break;
            }
            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getResources().getString(TAB_TITLES[position]);
        }

        @Override
        public int getCount() {
            return TAB_TITLES.length;
        }
    }

}