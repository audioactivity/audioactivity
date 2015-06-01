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

package de.tudarmstadt.seemoo.mbr.audioactivity.audiotest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.seemoo.mbr.audioactivity.R;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.DiracGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.NoiseGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.RandomSineGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.SilenceGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.SineGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.misc.FloatingActionButton;

import static android.widget.SeekBar.OnSeekBarChangeListener;

public class AudioTestActivity extends Activity implements OnSeekBarChangeListener,
        AudioTestAdapter.OnAudioStartListener, View.OnClickListener {
    private static final String NO_DIM_NAME = AudioTestActivity.class.getName() + "#no_dim";
    private SharedPreferences preferences;
    private final SineGenerator sineGenerator = new SineGenerator();
    private final RandomSineGenerator randomSineGenerator = new RandomSineGenerator();
    private final RandomSineGenerator usRandomSineGenerator = new RandomSineGenerator();
    private final NoiseGenerator whiteNoiseGenerator = new NoiseGenerator();
    private final NoiseGenerator pinkNoiseGenerator = new NoiseGenerator();
    private final DiracGenerator diracGenerator = new DiracGenerator();
    private final SilenceGenerator silenceGenerator = new SilenceGenerator();
    private List<AudioTestAdapter.AudioTestEntry> entries;
    private TextView textView;
    private TextView textView2;
    private FloatingActionButton stopButton;
    private AudioTestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiotest);
        preferences = getPreferences(Context.MODE_PRIVATE);

        SeekBar frequencySeekBar = (SeekBar) findViewById(R.id.frequencySeekBar);
        SeekBar amplitudeSeekBar = (SeekBar) findViewById(R.id.amplitudeSeekBar);
        textView   = (TextView) findViewById(R.id.frequencyText);
        textView2  = (TextView) findViewById(R.id.amplitudeText);
        stopButton = (FloatingActionButton) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);

        ListView list = (ListView)findViewById(R.id.listView);
        entries = new ArrayList<>();
        entries.add(new AudioTestAdapter.AudioTestEntry(getString(R.string.text_sine), sineGenerator));
        entries.add(new AudioTestAdapter.AudioTestEntry(getString(R.string.text_random_sine), randomSineGenerator));
        entries.add(new AudioTestAdapter.AudioTestEntry(getString(R.string.text_random_sine_us),
                                                        usRandomSineGenerator));
        entries.add(new AudioTestAdapter.AudioTestEntry(getString(R.string.text_white_noise), whiteNoiseGenerator));
        entries.add(new AudioTestAdapter.AudioTestEntry(getString(R.string.text_pink_noise), pinkNoiseGenerator));
        entries.add(new AudioTestAdapter.AudioTestEntry(getString(R.string.text_dirac), diracGenerator));
        entries.add(new AudioTestAdapter.AudioTestEntry(getString(R.string.text_digital_silence), silenceGenerator));

        frequencySeekBar.setOnSeekBarChangeListener(this);
        frequencySeekBar.setProgress(12761); // 440 Hz
        amplitudeSeekBar.setOnSeekBarChangeListener(this);
        amplitudeSeekBar.setProgress(amplitudeSeekBar.getMax());

        adapter = new AudioTestAdapter(entries);
        adapter.setOnAudioStartListener(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(adapter);

        usRandomSineGenerator.setLowestFrequency(17000);
        usRandomSineGenerator.setHighestFrequency(30000);
        pinkNoiseGenerator.setPinkNoise();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_audiotest, menu);

        MenuItem dim = menu.findItem(R.id.no_dim_screen);

        if (preferences.getBoolean(NO_DIM_NAME, false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            dim.setChecked(true);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            dim.setChecked(false);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopAll();
        stopButton.hide();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        double fProgress;
        if (seekBar.getId() == R.id.frequencySeekBar) {
            fProgress = Math.pow(progress / (double) seekBar.getMax(), 2);
            double freq = 50 + (23950 * fProgress);

            sineGenerator.setSineFrequency(freq);
            textView.setText(String.format("%1.1f Hz", freq));
        } else {
            fProgress = Math.pow(progress / (double) seekBar.getMax(), 2);
            float db = 0;
            for (AudioTestAdapter.AudioTestEntry entry : entries) {
                entry.getGenerator().setAmplitude((float) (fProgress * fProgress));
                db = entry.getGenerator().getAmplitudeDb();
            }
            textView2.setText(String.format("%1.1f dB", db));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onAudioStart() {
        if (stopButton.getVisibility() == View.GONE) {
            stopButton.show();
        }
    }

    @Override
    public void onClick(View v) {
        adapter.stopAll();
        stopButton.hide();
    }
}
