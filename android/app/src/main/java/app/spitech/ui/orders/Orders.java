package app.spitech.ui.orders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import app.spitech.R;
import app.spitech.appSDK.BaseActivity;

public class Orders extends BaseActivity {

    TabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.orders);
        init();
    }

    void init() {
        load(Orders.this,"ProductDetails","My Orders");
        aHideMenuList.add(R.id.action_logout);

        MyPagerAdapter sectionsPagerAdapter = new MyPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        if(getIntent().hasExtra("active_tab")){
            viewPager.setCurrentItem(getIntent().getExtras().getInt("active_tab"));
        }else{
            viewPager.setCurrentItem(0);
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.tab_package,R.string.tab_book};
        private final Context mContext;

        public MyPagerAdapter(Context context, FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = TabPackageOrder.newInstance();
                    break;
                case 1:
                    fragment = TabBookOrder.newInstance();
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