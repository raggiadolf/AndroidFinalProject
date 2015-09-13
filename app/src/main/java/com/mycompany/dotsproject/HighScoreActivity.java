package com.mycompany.dotsproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class HighScoreActivity extends AppCompatActivity {
    private ListView m_listView;

    private ArrayList<Record> m_data = new ArrayList<>();
    private RecordAdapter m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        m_listView = (ListView) findViewById(R.id.records);

        m_adapter = new RecordAdapter(this, m_data);
        m_listView.setAdapter(m_adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        readRecords();
        m_adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //writeRecords(); Probably not needed?
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

    private void writeRecords() {
        try {
            FileOutputStream fos = openFileOutput("records.ser", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(m_data);
            oos.close();
            fos.close();
        } catch(IOException ex) {
            // TODO: Handle exception
        }
    }

    private void readRecords() {
        try {
            FileInputStream fis = openFileInput("records.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Record> records = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
            m_data.clear();
            for(Record r : records) {
                m_data.add(r);
            }
        } catch(IOException ioex) {
            // TODO: Handle IOException
        } catch(ClassNotFoundException ex) {
            // TODO: Handle ClassNotFoundException
        }
    }
}
