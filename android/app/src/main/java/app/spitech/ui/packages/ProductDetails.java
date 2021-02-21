package app.spitech.ui.packages;

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

public class ProductDetails extends BaseActivity {

    TabLayout tabs;
    private String package_id,image="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stopScreenshot();
        setContentView(R.layout.product_details);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    void init() {
        //---------Basic Begin------------
        load(ProductDetails.this,"ProductDetails","Package Details");
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText("Package Details");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //---------Basic End------------

        if (getIntent().hasExtra("package_id")) {
            toolbar_title.setText( getIntent().getExtras().getString("package_name"));
            package_id = getIntent().getExtras().getString("package_id");
            image = getIntent().getExtras().getString("image");
        }
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
        private final int[] TAB_TITLES = new int[]{R.string.tab_overview,R.string.tab_content};
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
                    fragment = TabOverview.newInstance(package_id,  image);
                    break;
                case 1:
                    fragment = TabContents.newInstance(package_id);
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
