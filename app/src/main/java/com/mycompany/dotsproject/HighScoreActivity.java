package com.mycompany.dotsproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Handles displaying the highscore lists.
 * There are four different high score lists that we have
 * access to; for moves and timed games, and for both of those
 * we have a 6x6 and 8x8 variants.
 *
 * We display both timed and moves, but size is decided simply
 * by which is selected in the settings when we load up
 * this activity, since that is most likely what the user
 * is interested in.
 */
public class HighScoreActivity extends AppCompatActivity {
    private ListView m_moveListView;
    private ListView m_timeListView;
    private TextView m_boardSizeView;

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

        m_moveListView  = (ListView) findViewById(R.id.moveRecords);
        m_timeListView  = (ListView) findViewById(R.id.timeRecords);
        m_boardSizeView = (TextView) findViewById(R.id.boardSize);

        m_moveAdapter = new RecordAdapter(this, m_moveRecords);
        m_timeAdapter = new RecordAdapter(this, m_timeRecords);
        m_moveListView.setAdapter(m_moveAdapter);
        m_timeListView.setAdapter(m_timeAdapter);

        m_boardSizeView.setText(m_sp.getBoolean("size", false) ? "8x8" : "6x6");
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

    /**
     * Reads the high score records from the local storage
     */
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
            ioex.getMessage();
            ioex.printStackTrace();
        } catch(ClassNotFoundException ex) {
            ex.getMessage();
            ex.printStackTrace();
            // We should be printing these errors to logs instead of just out-ing them.
        }
    }
}
