package com.example.night_out;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class DiscoverActivity extends AppCompatActivity {
    private BottomNavigationView Nav;
    private TabLayout Tabs;
    private ViewPager ViewPager;
    private TabItem LocationsTab, PeopleTab;
    private PagerAdapter pagerAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        Tabs = findViewById(R.id.discover_tab_layout);
        ViewPager = findViewById(R.id.discover_view_pager);
        LocationsTab = findViewById(R.id.discover_locations_tab);
        PeopleTab = findViewById(R.id.discover_people_tab);
        pagerAdapter = new PageAdapter(getSupportFragmentManager(), Tabs.getTabCount());
        ViewPager.setAdapter(pagerAdapter);

        Tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ViewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0){
                    pagerAdapter.notifyDataSetChanged();

                }else if (tab.getPosition() == 1){
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(Tabs));

        Nav = findViewById(R.id.discover_bottom_nav);
        Nav.setSelectedItemId(R.id.nav_discover);

        Nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });
    }

    private void UserMenuSelector(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_discover:
                //startActivity(new Intent(getApplicationContext(), DiscoverActivity.class));
                break;
            case R.id.nav_messages:
                startActivity(new Intent(getApplicationContext(), MessagesActivity.class));
                overridePendingTransition(0,0);
                break;
            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0,0);
                break;

        }
    }
}