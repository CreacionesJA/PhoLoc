package com.jad.pholoc.fragments;

import android.os.Bundle;

import com.jad.pholoc.R;

/**
 * Fragment of Settings Activity
 *
 * @author Jorge Alvarado
 */
public class SettingsFragment extends PreferenceFragment {

    public final String TAG = this.getClass().getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
