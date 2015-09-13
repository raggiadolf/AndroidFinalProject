package com.mycompany.dotsproject;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by ragnaradolf on 13/09/15.
 */
public class DotsPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
