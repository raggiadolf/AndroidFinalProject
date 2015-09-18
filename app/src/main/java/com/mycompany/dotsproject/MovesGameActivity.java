package com.mycompany.dotsproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MovesGameActivity extends AppCompatActivity {
    private Animation m_animRotate;

    private int m_boardSize = 6;

    private Vibrator m_vibrator;
    private Boolean m_useVibrator = false;
    private Boolean m_useSound = false;
    SharedPreferences m_sp;

    private BoardView m_bv;
    private TextView m_scoreCountView;
    private TextView m_moveCountView;

    private int m_moveCount = 5;
    private int m_scoreCount = 0;
    private boolean m_isTimed = false;
    private long m_millisLeft = 30000;
    private CountDownTimer m_timer;
    private boolean m_wasPaused = false;

    final SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final int dotsgone = soundPool.load(this, R.raw.dotsgone, 1);

        m_boardSize = m_sp.getBoolean("size", false) ? 8 : 6;

        setContentView(R.layout.activity_moves_game);
        m_animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        if (savedInstanceState != null) {
            m_scoreCount = savedInstanceState.getInt("score");
            m_moveCount  = savedInstanceState.getInt("moves");
            if(m_isTimed) {
                m_millisLeft = savedInstanceState.getLong("millisleft");
            }
        }

        Intent m_intent = getIntent();
        m_isTimed = m_intent.getBooleanExtra("timed", false);


        m_vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        m_scoreCountView = (TextView) findViewById(R.id.scoreCount);
        m_moveCountView = (TextView) findViewById(R.id.movesCount);

        m_scoreCountView.setText("Score " + m_scoreCount);
        if(!m_isTimed) {
            m_moveCountView.setText("Moves " + m_moveCount);
        } else {
            m_timer = new CountDownTimer(30000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        m_moveCountView.setText("Time "  + millisUntilFinished / 1000);
                        m_millisLeft = millisUntilFinished;
                    }

                    public void onFinish() {
                        Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                        intent.putExtra("score", m_scoreCount);
                        intent.putExtra("timed", m_isTimed);
                        startActivity(intent);
                    }
                }.start();
        }

        m_bv = (BoardView) findViewById(R.id.boardview);
        m_bv.setMoveEventHandler(new OnMoveEventHandler() {
            @Override
            public void onMove(int moveScore) {
                m_moveCount--;
                m_scoreCount += moveScore;
                m_scoreCountView.setText("Score " + m_scoreCount);
                if(!m_isTimed) {
                    m_moveCountView.setText("Moves " + m_moveCount);
                }

                if (useVibrator()) {
                    m_vibrator.vibrate(500);
                }
                if (useSound()) {
                    soundPool.play(dotsgone, 0.99f, 0.99f, 0, 0, 1.0f);
                }

                if (m_moveCount <= 0 && !m_isTimed) {
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

        m_useVibrator = m_sp.getBoolean("vibrate", false);
        m_useSound = m_sp.getBoolean("sound", false);

        m_boardSize = m_sp.getBoolean("size", false) ? 8 : 6;
        if(m_boardSize != m_bv.getBoardSize()) {
            reload();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(m_isTimed) {
            m_wasPaused = true;
            m_timer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(m_isTimed && m_wasPaused) {
            m_timer = new CountDownTimer(m_millisLeft, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    m_moveCountView.setText("Time " + millisUntilFinished / 1000);
                    m_millisLeft = millisUntilFinished;
                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(getApplicationContext(), GameOverActivity.class);
                    intent.putExtra("score", m_scoreCount);
                    intent.putExtra("timed", m_isTimed);
                    startActivity(intent);
                }
            }.start();
            m_wasPaused = false;
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
        if(m_isTimed) {
            savedInstanceState.putLong("millisleft", m_millisLeft);
        }
    }

    @Override
    public void onBackPressed() {
        if(m_isTimed) {
            m_timer.cancel();
            m_wasPaused = true;
        }
        Intent intent = new Intent(this, PauseActivity.class);
        intent.putExtra("score", m_scoreCount);
        if(!m_isTimed) {
            intent.putExtra("moves", m_moveCount);
        } else {
            intent.putExtra("millisleft", m_millisLeft);
            intent.putExtra("timed", m_isTimed);
        }
        startActivity(intent);
    }

    public boolean useSound() {
        return m_useSound;
    }

    public boolean useVibrator() {
        return m_useVibrator;
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


    public void pauseGame(View view) {
        view.startAnimation(m_animRotate);
        onBackPressed();
    }

    public void shuffleBoard(View view) {
        view.startAnimation(m_animRotate);
        m_bv.shuffleBoard();
    }
}
