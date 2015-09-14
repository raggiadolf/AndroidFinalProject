package com.mycompany.dotsproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MovesGameActivity extends AppCompatActivity {

    private Vibrator m_vibrator;
    private Boolean m_use_vibrator = false;
    private Boolean m_use_sound = false;
    SharedPreferences m_sp;

    private BoardView m_bv;
    private TextView m_scoreCountView;
    private TextView m_moveCountView;

    private ArrayList<Record> m_highScoreList = new ArrayList<>();

    private int m_moveCount = 30;
    private int m_scoreCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moves_game);

        m_vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        m_sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        m_scoreCountView = (TextView) findViewById(R.id.scoreCount);
        m_moveCountView = (TextView) findViewById(R.id.movesCount);

        m_scoreCountView.setText("Score " + m_scoreCount);
        m_moveCountView.setText("Moves " + m_moveCount);

        m_bv = (BoardView) findViewById(R.id.boardview);
        m_bv.setMoveEventHandler(new OnMoveEventHandler() {
            @Override
            public void onMove(int moveScore) {
                m_moveCount--;
                m_scoreCount += moveScore;
                m_scoreCountView.setText("Score " + m_scoreCount);
                m_moveCountView.setText("Moves " + m_moveCount);

                if(useVibrator()) {
                    m_vibrator.vibrate(500);
                }
                if(useSound()) {
                    // TODO: Play sound for score
                }

                if(m_moveCount <= 0) {
                // TODO: User is out of moves, display an overlay(or a new activity?) with his final score. freeze the board.
                Toast.makeText(getApplicationContext(), "new top score: " + m_scoreCount, Toast.LENGTH_SHORT).show();

                readRecords();

                m_highScoreList.add(new Record("Raggi", m_scoreCount, new Date()));
                writeRecords(m_highScoreList);
            }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        m_use_vibrator = m_sp.getBoolean("vibrate", false);
        m_use_sound = m_sp.getBoolean("sound", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_moves_game, menu);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("moves", m_moveCount);
        savedInstanceState.putInt("score", m_scoreCount);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        m_moveCount  = savedInstanceState.getInt("moves");
        m_scoreCount = savedInstanceState.getInt("score");
    }

    private void readRecords() {
        try {
            FileInputStream fis = openFileInput("records.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Record> records = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            m_highScoreList.clear();
            for(Record r : records) {
                m_highScoreList.add(r);
            }

        } catch(IOException ioex) {
            // TODO: Handle IOException
        } catch(ClassNotFoundException ex) {
            // TODO: Handle ClassNotFoundException
        }
    }

    private void writeRecords(ArrayList<Record> newRecords) {
        try {
            FileOutputStream fos = openFileOutput("records.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(newRecords);
            oos.close();
            fos.close();
        } catch(IOException ex) {
            // TODO: Handle exception
        }
    }

    public boolean useSound() {
        return m_use_sound;
    }

    public boolean useVibrator() {
        return m_use_vibrator;
    }
}
