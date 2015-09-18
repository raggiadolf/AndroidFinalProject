package com.mycompany.dotsproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class PauseActivity extends AppCompatActivity {
    private Animation m_animRotate;

    private int m_moveCount;
    private int m_scoreCount;
    private boolean m_isTimed;
    private long m_millisleft;

    private TextView m_scoreCountView;
    private TextView m_moveCountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);
        m_animRotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);

        if (savedInstanceState != null) {
            m_scoreCount = savedInstanceState.getInt("score");
            m_moveCount  = savedInstanceState.getInt("moves");
            m_isTimed    = savedInstanceState.getBoolean("timed");
            if(m_isTimed) {
                m_millisleft = savedInstanceState.getLong("millisleft");
            }
        }

        Intent m_intent = getIntent();
        m_scoreCount = m_intent.getIntExtra("score", 0);
        m_moveCount  = m_intent.getIntExtra("moves", 0);
        m_isTimed    = m_intent.getBooleanExtra("timed", false);
        if(m_isTimed) {
            m_millisleft = m_intent.getLongExtra("millisleft", 0);
        }

        m_scoreCountView = (TextView) findViewById(R.id.scoreCount);
        m_moveCountView = (TextView) findViewById(R.id.movesCount);

        m_scoreCountView.setText("Score " + m_scoreCount);
        if(!m_isTimed) {
            m_moveCountView.setText("Moves " + m_moveCount);
        } else {
            m_moveCountView.setText("Time " + m_millisleft / 1000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pause, menu);
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
        savedInstanceState.putInt("moves", m_moveCount);
        savedInstanceState.putInt("score", m_scoreCount);
        savedInstanceState.putBoolean("timed", m_isTimed);
        savedInstanceState.putLong("millisleft", m_millisleft);
    }

    public void continueGame(View view) {
        finish();
    }

    public void restartGame(View view) {
        Intent intent = new Intent(this, MovesGameActivity.class);
        intent.putExtra("timed", m_isTimed);
        startActivity(intent);
    }

    public void mainMenu(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void settings(View view) {
        view.startAnimation(m_animRotate);
        Intent intent = new Intent(this, DotsPreferenceActivity.class);
        intent.putExtra("ingame", true);
        startActivity(intent);
    }
}
