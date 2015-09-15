package com.mycompany.dotsproject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MovesGameActivity extends AppCompatActivity {

    private int m_boardSize = 6;

    private Vibrator m_vibrator;
    private Boolean m_use_vibrator = false;
    private Boolean m_use_sound = false;
    SharedPreferences m_sp;

    private BoardView m_bv;
    private TextView m_scoreCountView;
    private TextView m_moveCountView;

    private int m_moveCount = 30;
    private int m_scoreCount = 0;

    final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        m_boardSize = m_sp.getBoolean("size", false) ? 8 : 6;

        setContentView(R.layout.activity_moves_game);

        if (savedInstanceState != null) {
            m_scoreCount = savedInstanceState.getInt("score");
            m_moveCount  = savedInstanceState.getInt("moves");
        }

        final int sound = soundPool.load(this, R.raw.dotsgone, 1);

        m_vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

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

                if (useVibrator()) {
                    m_vibrator.vibrate(500);
                }
                if (useSound()) {
                    soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1.0f);
                }

                if (m_moveCount <= 0) {
                    Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                    intent.putExtra("score", m_scoreCount);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        m_use_vibrator = m_sp.getBoolean("vibrate", false);
        m_use_sound = m_sp.getBoolean("sound", false);

        m_boardSize = m_sp.getBoolean("size", false) ? 8 : 6;
        if(m_boardSize != m_bv.getBoardSize()) {
            reload();
        }
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
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("moves", m_moveCount);
        savedInstanceState.putInt("score", m_scoreCount);
    }

    @Override
    public void onBackPressed() {
        // Start pause activity?
        Intent intent = new Intent(this, PauseActivity.class);
        intent.putExtra("score", m_scoreCount);
        intent.putExtra("moves", m_moveCount);
        startActivity(intent);
    }

    public boolean useSound() {
        return m_use_sound;
    }

    public boolean useVibrator() {
        return m_use_vibrator;
    }

    public int getSize() {
        return m_boardSize;
    }

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
