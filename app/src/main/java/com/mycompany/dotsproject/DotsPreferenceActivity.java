package com.mycompany.dotsproject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.TextView;

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

        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);

        Preference button = findPreference(getString(R.string.resetHighScores));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                emptyRecords();
                return true;
            }
        });

        Intent m_intent = getIntent();
        if(m_intent.getBooleanExtra("ingame", false)) {
            PreferenceCategory prefCat = new PreferenceCategory(this);
            prefCat.setLayoutResource(R.layout.warning_message);
            screen.addPreference(prefCat);
            setPreferenceScreen(screen);
            addPreferencesFromResource(R.xml.preferences);
        }
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
