package com.kdl.naukriexpress.ui.bookmarks;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.BaseActivity;

public class Bookmarks extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.bookmarks);
        init();

    }

    void init() {
        load(Bookmarks.this, "Bookmarks", "My Bookmarks");
        BookmarkPagerAdapter adapter = new BookmarkPagerAdapter(context, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    public class BookmarkPagerAdapter extends FragmentPagerAdapter {

        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.news, R.string.job};
        private final Context mContext;

        public BookmarkPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
           String type="";
            switch (position) {
                case 0:
                    type="news";
                    break;
                case 1:
                    type="jobs";
                    break;
            }
            return FrgBookmarkContainer.newInstance(type);
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