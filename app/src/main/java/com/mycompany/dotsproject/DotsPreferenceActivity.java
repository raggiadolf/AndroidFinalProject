package com.mycompany.dotsproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Handles our preferences.
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

        Intent m_intent = getIntent();
        if(m_intent.getBooleanExtra("ingame", false)) {
            Preference warningMessage = new Preference(this);
            warningMessage.setKey("warningMessage");
            warningMessage.setSelectable(false);
            warningMessage.setPersistent(false);
            warningMessage.setSummary(getString(R.string.warning_message));
            ((PreferenceScreen)findPreference("prefScreen")).addPreference(warningMessage);
        }
    }

    /**
     * Empties the highscore on local storage.
     * We simply store an empty array, which overwrites the records in local storage.
     */
    private void emptyRecords() {
        try {
            FileOutputStream fossix = openFileOutput("recordssix.ser", Context.MODE_PRIVATE);
            FileOutputStream foseight = openFileOutput("recordseight.ser", Context.MODE_PRIVATE);

            ObjectOutputStream oossix = new ObjectOutputStream(fossix);
            oossix.writeObject(new ArrayList<Record>());

            ObjectOutputStream ooseight = new ObjectOutputStream(foseight);
            ooseight.writeObject(new ArrayList<Record>());
        } catch(IOException ex) {
            // This should be logged instead of printed.
            ex.printStackTrace();
            ex.getMessage();
        }
    }
}
