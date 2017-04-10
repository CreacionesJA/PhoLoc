package com.jad.pholoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jad.pholoc.util.LocationsFragmentPagerAdapter;

/**
 * List of locations and map
 *
 * @author Jorge Alvarado
 */
public class LocationsActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();

    private ViewPager mPager;
    public static FragmentManager fragmentManager;
    private ActionBar mActionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        // Put the back arrow on the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        // Get the Action Bar
        mActionbar = getSupportActionBar();

        // Navigation mode with tabs
        mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); // Deprecated
        mPager = (ViewPager) findViewById(R.id.pager);
        fragmentManager = getSupportFragmentManager();
        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mActionbar.setSelectedNavigationItem(position); // Deprecated
            }
        };
        mPager.setOnPageChangeListener(pageChangeListener); // Deprecated
        LocationsFragmentPagerAdapter fragmentPagerAdapter = new LocationsFragmentPagerAdapter(
                fragmentManager);

        mPager.setAdapter(fragmentPagerAdapter);
        mActionbar.setDisplayShowTitleEnabled(true);
        // Tab Listener
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabSelected(Tab tab, FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(Tab tab, FragmentTransaction ft) {
            }
        };

        // Tab 1: List Locations
        Tab tab1 = mActionbar.newTab().setText(R.string.tab_listloc)
                .setTabListener(tabListener);

        // Tab 2: Map Locations
        Tab tab2 = mActionbar.newTab().setText(R.string.tab_mapsloc)
                .setTabListener(tabListener);

        // Add the tabs
        mActionbar.addTab(tab1);
        mActionbar.addTab(tab2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.locations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.settings:
                i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.about:
                i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
