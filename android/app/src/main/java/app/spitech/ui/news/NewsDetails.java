package app.spitech.ui.news;

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

import app.spitech.appSDK.Interfaces.CommonInterface;
import app.spitech.R;
import app.spitech.appSDK.BaseActivity;

public class NewsDetails extends BaseActivity implements CommonInterface {

    public String news_id = "";
    public ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.news);
        init();
    }

    void init() {
        load(NewsDetails.this, "NewsDetails", "News Description");
       //aHideMenuList.add(R.id.action_notification);
        screen=findViewById(R.id.screen);
        if (getIntent().hasExtra("news_id")) {
            news_id = getIntent().getExtras().getString("news_id");
        }
        NewsPagerAdapter adapter = new NewsPagerAdapter(context, getSupportFragmentManager(), news_id);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        image = findViewById(R.id.image);

    }

    class NewsPagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.news_details, R.string.similar_news};
        private final Context mContext;
        private String news_id = "";

        public NewsPagerAdapter(Context context, FragmentManager fm, String param1) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            mContext = context;
            news_id = param1;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = FrgNewsDetails.newInstance(mContext, news_id,NewsDetails.this);
                    break;
                case 1:
                    fragment = FrgNewsListings.newInstance(mContext, null,NewsDetails.this);
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