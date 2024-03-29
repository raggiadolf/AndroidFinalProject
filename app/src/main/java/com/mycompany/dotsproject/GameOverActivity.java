package com.mycompany.dotsproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

/**
 * The activity that is launched when we finish a game.
 * Handles writing the high score to local storage
 */
public class GameOverActivity extends AppCompatActivity {
    private int m_score;
    private ArrayList<Record> m_highScoreList = new ArrayList<>(5);
    private TextView m_scoreView;
    private TextView m_messageView;

    private String m_recordFile;
    private boolean m_isTimed;

    SharedPreferences m_sp;
    private boolean m_useSound;

    final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    int highScoreSound;
    private boolean loaded = false;

    private Handler soundHandler;
    private boolean playSound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        m_sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        m_useSound = m_sp.getBoolean("sound", false);
        highScoreSound = soundPool.load(this, R.raw.clapclap, 1);

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        m_scoreView = (TextView) findViewById(R.id.finalScore);
        m_messageView = (TextView) findViewById(R.id.message);

        if (savedInstanceState != null) {
            m_score = savedInstanceState.getInt("score");
        }

        Intent m_intent = getIntent();
        m_score = m_intent.getIntExtra("score", 0);
        m_isTimed = m_intent.getBooleanExtra("timed", false);

        m_scoreView.setText("" + m_score);
        if(!m_isTimed) {
            m_recordFile = m_sp.getBoolean("size", false) ? "recordseight.ser" : "recordssix.ser";
        } else {
            m_recordFile = m_sp.getBoolean("size", false) ? "recordseighttimed.ser" : "recordssixtimed.ser";
        }
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

        soundHandler = new Handler();
        soundHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(loaded) {
                    if(playSound) {
                        soundPool.play(highScoreSound, 0.99f, 0.99f, 0, 0, 1.0f);
                    }
                }
            }
        }, 2000);
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
    protected void onStart() {
        super.onStart();
        m_useSound = m_sp.getBoolean("sound", false);
    }

    /**
     * Send the user to main menu if back is pressed on game over
     * instead of back to the last game played.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Read the high score records from local storage
     */
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
            ioex.getMessage();
            ioex.printStackTrace();
        } catch(ClassNotFoundException ex) {
            ex.getMessage();
            ex.printStackTrace();
            // We should be printing these errors to logs instead of just out-ing them.
        }
    }

    /**
     * Write the new record list to local storage
     * @param newRecords the new list of high scores(does not necessarily have to be modified)
     */
    private void writeRecords(ArrayList<Record> newRecords) {
        try {
            FileOutputStream fos = openFileOutput(m_recordFile, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(newRecords);
            oos.close();
            fos.close();
        } catch(IOException ex) {
            ex.getMessage();
            ex.printStackTrace();
            // We should be printing these errors to logs instead of just out-ing them.
        }
    }

    /**
     * Find where this score we just got should go in the current high score list
     * @return
     */
    private int getScorePosition() {
        int i = 0;
        while(i < m_highScoreList.size() && m_highScoreList.get(i).getScore() > m_score) {
            i++;
        }
        return i;
    }

    /**
     * Set the message according to what place on the high score list we just got.
     * If we get first place we set the playSound variable to true, which informs the sound
     * handler that he should play a sound.
     * @param i the position that the current score got on the high score list
     */
    private void setScoreMessage(int i) {
        switch(i) {
            case 0:
                m_messageView.setText("Oh yeah! New Highscore!");
                if (m_useSound) {
                    playSound = true;
                }
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

    /**
     * Add the current score to it's appropriate position on the high score
     * @param i the position that the current score got on the high score list
     */
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

    /**
     * Start a new game of the same type we just finished.
     * @param view the view that was just pressed
     */
    public void replay(View view) {
        Intent intent = new Intent(this, MovesGameActivity.class);
        intent.putExtra("timed", m_isTimed);
        startActivity(intent);
    }

    /**
     * Go to main menu, set the flag FLAG_ACTIVITY_CLEAR_TOP for the
     * intent that makes sure that this intent will be the only intent
     * on the stack, since there is no reason for us to keep all of
     * the previously finished games on the stack once we go back to main menu.
     * @param view the view that was just pressed
     */
    public void mainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
