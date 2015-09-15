package com.mycompany.dotsproject;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by ragnaradolf on 13/09/15.
 */
public class DotsPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference button = findPreference(getString(R.string.resetHighScores));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                emptyRecords();
                return true;
            }
        });
    }

    private void emptyRecords() {
        try {
            FileOutputStream fos = openFileOutput("records.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(new ArrayList<Record>());
        } catch(IOException ex) {
            // TODO: Handler exception
        }
    }
}
