package com.kdl.naukriexpress.ui.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

import com.kdl.naukriexpress.R;
import com.kdl.naukriexpress.appSDK.HelperMethods;
import com.kdl.naukriexpress.ui.chat.group.GroupListFragment;
import com.kdl.naukriexpress.ui.chat.student.ChatStudentList;

public class ChatHome extends HelperMethods {

    TabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    void init() {
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        TextView toolbar_title=findViewById(R.id.toolbar_title);
        toolbar_title.setText(getResources().getString(R.string.company_name));

        ChatPagerAdapter sectionsPagerAdapter = new ChatPagerAdapter(this, getSupportFragmentManager());
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

    public class ChatPagerAdapter extends FragmentPagerAdapter {
        @StringRes
        private final int[] TAB_TITLES = new int[]{R.string.chat_students,R.string.chat_group};
        private final Context mContext;

        public ChatPagerAdapter(Context context, FragmentManager fm) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = ChatStudentList.newInstance();
                    break;
                case 1:
                    fragment = GroupListFragment.newInstance();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        final MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView)menuItem.getActionView();
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
            {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item)
                {
                    tabs.setVisibility(View.VISIBLE);
                    return true;
                }
                @Override
                public boolean onMenuItemActionExpand(MenuItem item)
                {
                    tabs.setVisibility(View.GONE);
                    return true;
                }
            });
        } else {
            searchView.setOnSearchClickListener(view -> {
                tabs.setVisibility(View.GONE);
            });
            searchView.setOnCloseListener(() -> {
                tabs.setVisibility(View.VISIBLE);
                return true;
            });
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
