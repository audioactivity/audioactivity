/*
 * Copyright (c) 2015, Markus Brandt
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package de.tudarmstadt.seemoo.mbr.audioactivity.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.tudarmstadt.seemoo.mbr.audioactivity.R;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.SineGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.misc.FloatingActionButton;

public class TestActivity extends Activity implements View.OnClickListener {
    private static final String NO_DIM_NAME = TestActivity.class.getName() + "#no_dim";
    private static final String SHOW_FREQ_NAME = TestActivity.class.getName() + "#show_freq";
    private SharedPreferences preferences;
    private final SineGenerator sineGenerator = new SineGenerator();
    private FloatingActionButton playButton;
    private Button yesButton;
    private Button noButton;
    private TextView description;
    private TextView hearInfo;
    private ProgressBar progress;
    private HashMap<Integer, Boolean> testResult;
    private final List<Integer> frequencies = new ArrayList<>();
    private TextView frequencyTextView;
    private int counter = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButton:
                startTest();

                break;
            case R.id.yesButton:
            case R.id.noButton:
                testResult.put(frequencies.get(counter - 1), v.getId() == R.id.yesButton);
                sineGenerator.setSineFrequency(frequencies.get(counter));
                frequencyTextView.setText(frequencies.get(counter) + " Hz");
                counter += 1;
                progress.setProgress(counter);

                if (counter >= frequencies.size()) {
                    stopTest(true);
                    break;
                }
        }
    }

    @Override
    public void onBackPressed() {
        if (playButton.isVisible()) {
            super.onBackPressed();
            return;
        }

        stopTest(false);
        playButton.show();
    }

    private void stopTest(boolean result) {
        yesButton.setEnabled(false);
        noButton.setEnabled(false);
        description.setEnabled(true);
        hearInfo.setEnabled(false);
        sineGenerator.stop();
        frequencyTextView.setText("");

        if (result) {
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("frequencies", testResult);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        preferences = getPreferences(Context.MODE_PRIVATE);

        playButton = (FloatingActionButton)findViewById(R.id.playButton);
        yesButton = (Button)findViewById(R.id.yesButton);
        noButton = (Button)findViewById(R.id.noButton);
        description = (TextView)findViewById(R.id.descTextView);
        hearInfo = (TextView)findViewById(R.id.hearTextView);
        progress          = (ProgressBar)findViewById(R.id.progressBar);
        frequencyTextView = (TextView)findViewById(R.id.frequencyTextView);

        playButton.setOnClickListener(this);
        yesButton.setOnClickListener(this);
        noButton.setOnClickListener(this);

        progress.setMax(1);
        progress.setProgress(1);

        initFrequencies();
    }

    private void initFrequencies() {
        for (int f = 10000; f < 20000; f += 500) {
            frequencies.add(f);
        }
        for (int i = 0; i < 6; i++) {
            frequencies.add(20001 + i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);

        MenuItem dim = menu.findItem(R.id.no_dim_screen);
        MenuItem freq = menu.findItem(R.id.show_frequencies);

        if (preferences.getBoolean(NO_DIM_NAME, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            dim.setChecked(true);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            dim.setChecked(false);
        }

        if (preferences.getBoolean(SHOW_FREQ_NAME, false)) {
            frequencyTextView.setVisibility(View.VISIBLE);
            freq.setChecked(true);
        } else {
            frequencyTextView.setVisibility(View.GONE);
            freq.setChecked(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.no_dim_screen:
                if (item.isChecked()) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    preferences.edit().putBoolean(NO_DIM_NAME, false).apply();
                    item.setChecked(false);
                } else {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    preferences.edit().putBoolean(NO_DIM_NAME, true).apply();
                    item.setChecked(true);
                }
                break;
            case R.id.show_frequencies:
                if (item.isChecked()) {
                    frequencyTextView.setVisibility(View.GONE);
                    preferences.edit().putBoolean(SHOW_FREQ_NAME, false).apply();
                    item.setChecked(false);
                } else {
                    frequencyTextView.setVisibility(View.VISIBLE);
                    preferences.edit().putBoolean(SHOW_FREQ_NAME, true).apply();
                    item.setChecked(true);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        playButton.setVisible();
    }

    @Override
    protected void onStop() {
        super.onStop();

        sineGenerator.stop();
    }

    @SuppressLint("UseSparseArrays")
    private void startTest() {
        testResult = new HashMap<>();
        yesButton.setEnabled(true);
        noButton.setEnabled(true);
        description.setEnabled(false);
        hearInfo.setEnabled(true);

        counter = 0;
        Collections.shuffle(frequencies);
        progress.setMax(frequencies.size());

        playButton.hide();

        sineGenerator.setSineFrequency(frequencies.get(counter));

        frequencyTextView.setText(frequencies.get(counter) + " Hz");

        counter += 1;
        progress.setProgress(counter);
        sineGenerator.start();
    }
}
