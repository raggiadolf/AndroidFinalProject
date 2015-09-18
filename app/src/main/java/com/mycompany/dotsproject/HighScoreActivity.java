package com.mycompany.dotsproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class HighScoreActivity extends AppCompatActivity {
    private ListView m_moveListView;
    private ListView m_timeListView;

    private ArrayList<Record> m_moveRecords = new ArrayList<>();
    private ArrayList<Record> m_timeRecords = new ArrayList<>();

    private RecordAdapter m_moveAdapter;
    private RecordAdapter m_timeAdapter;

    private String m_recordFileMoves;
    private String m_recordFileTimed;

    SharedPreferences m_sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        m_sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        m_recordFileMoves = m_sp.getBoolean("size", false) ? "recordseight.ser" : "recordssix.ser";
        m_recordFileTimed = m_sp.getBoolean("size", false) ? "recordseighttimed.ser" : "recordssixtimed.ser";

        m_moveListView = (ListView) findViewById(R.id.moveRecords);
        m_timeListView = (ListView) findViewById(R.id.timeRecords);

        m_moveAdapter = new RecordAdapter(this, m_moveRecords);
        m_timeAdapter = new RecordAdapter(this, m_timeRecords);
        m_moveListView.setAdapter(m_moveAdapter);
        m_timeListView.setAdapter(m_timeAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        readRecords();
        m_moveAdapter.notifyDataSetChanged();
        m_timeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_high_score, menu);
        return true;
    }

    /**
     * Starts a game
     * TODO: Be able to tell the intent what kind of game it is, moves/timed
     * @param view
     */
    public void startMovesGame(View view) {

        Intent intent = new Intent(this, MovesGameActivity.class);
        startActivity(intent);
    }

    /**
     * TODO: Document
     * @param view
     */
    public void startTimedGame(View view) {

        Intent intent = new Intent(this, MovesGameActivity.class);
        intent.putExtra("timed", true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, DotsPreferenceActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void readRecords() {
        try {
            FileInputStream fis = openFileInput(m_recordFileMoves);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Record> moveRecords = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            m_moveRecords.clear();
            for(Record r : moveRecords) {
                m_moveRecords.add(r);
            }

            fis = openFileInput(m_recordFileTimed);
            ois = new ObjectInputStream(fis);
            ArrayList<Record> timeRecords = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            m_timeRecords.clear();
            for(Record r : timeRecords) {
                m_timeRecords.add(r);
            }
        } catch(IOException ioex) {
            // TODO: Handle IOException
        } catch(ClassNotFoundException ex) {
            // TODO: Handle ClassNotFoundException
        }
    }
}
