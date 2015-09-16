package com.mycompany.dotsproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
     * Starts a game
     * TODO: Be able to tell the intent what kind of game it is, moves/timed
     * @param view
     */
    public void startMovesGame(View view) {
        Intent intent = new Intent(this, MovesGameActivity.class);
        startActivity(intent);
    }

    /**
     * Views the highscores
     * TODO: Figure out how to store the high scores in local storage.
     * @param view
     */
    public void viewHighScores(View view) {
        Intent intent = new Intent(this, HighScoreActivity.class);
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

    /**
     * TODO: Document
     * @param view
     */
    public void settings(View view) {
        Intent intent = new Intent(this, DotsPreferenceActivity.class);
        startActivity(intent);
    }
}
