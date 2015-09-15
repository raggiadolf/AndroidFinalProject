package com.mycompany.dotsproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class GameOverActivity extends AppCompatActivity {
    private int m_score;
    private ArrayList<Record> m_highScoreList = new ArrayList<>(5);
    private TextView m_scoreView;
    private TextView m_messageView;

    private String m_recordFile;

    SharedPreferences m_sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        m_sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        m_recordFile = m_sp.getBoolean("size", false) ? "recordseight.ser" : "recordssix.ser";

        m_scoreView = (TextView) findViewById(R.id.finalScore);
        m_messageView = (TextView) findViewById(R.id.message);

        if (savedInstanceState != null) {
            m_score = savedInstanceState.getInt("score");
        }

        Intent m_intent = getIntent();
        m_score = m_intent.getIntExtra("score", 0);

        m_scoreView.setText("" + m_score);

        readRecords();

        Collections.sort(m_highScoreList, new Comparator<Record>() {
            @Override
            public int compare(Record lhs, Record rhs) {
                return rhs.getScore() - lhs.getScore();
            }
        });

        int i = getScorePosition();

        setScorePosition(i);

        setScoreMessage(i);

        writeRecords(m_highScoreList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_over, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("score", m_score);
    }

    @Override
    public void onBackPressed() {
        // Send back to main activity
    }

    private void readRecords() {
        try {
            FileInputStream fis = openFileInput(m_recordFile);
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
            FileOutputStream fos = openFileOutput(m_recordFile, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(newRecords);
            oos.close();
            fos.close();
        } catch(IOException ex) {
            // TODO: Handle exception
        }
    }

    private int getScorePosition() {
        int i = 0;
        while(i < m_highScoreList.size() && m_highScoreList.get(i).getScore() > m_score) {
            i++;
        }
        return i;
    }

    private void setScoreMessage(int i) {
        switch(i) {
            case 0:
                m_messageView.setText("Oh yeah! New Highscore!");
                break;
            case 1:
                m_messageView.setText("Nice! 2nd highest score!");
                break;
            case 2:
                m_messageView.setText("3rd highest score.");
                break;
            case 3:
                m_messageView.setText("4th highest score.");
                break;
            case 4:
                m_messageView.setText("5th highest score.");
                break;
            default:
                m_messageView.setText("Better luck next time, no high score!");
                break;
        }
    }

    private void setScorePosition(int i) {
        if(m_highScoreList.isEmpty()) {
            m_highScoreList.add(new Record("Raggi", m_score, new Date()));
        } else if(m_highScoreList.size() < 5) {
            Record tmpRec = m_highScoreList.get(m_highScoreList.size() - 1); // We store the last record in case we need to add it to the back of the list
            if(i == m_highScoreList.size()) {
                m_highScoreList.add(new Record("Raggi", m_score, new Date())); // Adding to the back of the list
            } else if(i < m_highScoreList.size()) {
                for(int j = m_highScoreList.size() - 1; j > i; j--) {
                    m_highScoreList.set(j, m_highScoreList.get(j - 1));
                }
                m_highScoreList.set(i, new Record("Raggi", m_score, new Date()));
                m_highScoreList.add(tmpRec);
            }
        } else { // We're adding to a full list and don't need to worry about the last element popping of the list
            if(i < m_highScoreList.size()) {
                for(int j = m_highScoreList.size() - 1; j > i; j--) {
                    m_highScoreList.set(j, m_highScoreList.get(j - 1));
                }
                m_highScoreList.set(i, new Record("Raggi", m_score, new Date()));
            }
        }
    }

    public void replay(View view) {
        Intent intent = new Intent(this, MovesGameActivity.class);
        startActivity(intent);
    }

    public void mainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
