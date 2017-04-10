package com.jad.pholoc.util;

import com.jad.pholoc.fragments.ListLocFragment;
import com.jad.pholoc.fragments.MapLocFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * FragmentPageAdapter for LocationsActivity
 *
 * @author Jorge Alvarado
 */
public class LocationsFragmentPagerAdapter extends FragmentPagerAdapter {

    // Number of tabs
    private final int PAGE_COUNT = 2;
    // Fragments
    private ListLocFragment fragment1;
    private MapLocFragment fragment2;

    /**
     * Constructor
     *
     * @param fm Fragmentmanager
     */
    public LocationsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int arg0) {
        switch (arg0) {
            case 0:
                // Tab 1: list Locations Fragment
                fragment1 = new ListLocFragment();
                return fragment1;
            case 1:
                // Tab 2: Map Locations Fragment
                fragment2 = new MapLocFragment();
                return fragment2;
        }
        return null;
    }

    /**
     * Get Page Count
     */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
