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

package de.tudarmstadt.seemoo.mbr.audioactivity.send;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import de.tudarmstadt.seemoo.mbr.audioactivity.R;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.BaseAudioGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.BitstreamIterator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.ByteAudioGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.DsssEncoder;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.FskGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.GfskGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.Gnuradio;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.PskGenerator;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.RawReader;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.SimplePacket;
import de.tudarmstadt.seemoo.mbr.audioactivity.core.VaricodeIterator;
import de.tudarmstadt.seemoo.mbr.audioactivity.misc.FloatingActionButton;

public class SendActivity extends Activity implements View.OnClickListener, TextWatcher,
        BaseAudioGenerator.OnGeneratorStopListener, AdapterView.OnItemSelectedListener {
    private static final String NO_DIM_NAME = SendActivity.class.getName() + "#no_dim";
    private BaseAudioGenerator generator;
    private final ByteAudioGenerator byteAudioGenerator = new ByteAudioGenerator();
    private EditText messageText;
    private FloatingActionButton startButton;
    private FloatingActionButton stopButton;
    private final FskGenerator fskGenerator = new FskGenerator();
    private final GfskGenerator gfskGenerator = new GfskGenerator();
    private final PskGenerator pskGenerator = new PskGenerator();
    private byte[] whiteCode;
    private SharedPreferences preferences;
    private int psk31Center;
    private int fskCenter;
    private int fskDeviation;
    private int fskSymbolSize;
    private String fskPacketType;
    private int fskRepeat;
    private String fskInterleave;
    private int gfskCenter;
    private int gfskDeviation;
    private int gfskSymbolSize;
    private String gfskPacketType;
    private int gfskRepeat;
    private String gfskInterleave;
    private int pskCenter;
    private int pskSymbolSize;
    private String pskPacketType;
    private int pskRepeat;
    private String pskInterleave;
    private int dsssChipSize;
    private int dsssAmplitude;
    private Spinner templateSpinner;
    private String[] modulations;
    private String[] templates;
    private String modulation = "FSK";
    private TextView status;
    private boolean sending = false;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.playButton) {
            ((FloatingActionButton)v).hideFor((FloatingActionButton) v.getTag());
            status.setText(getString(R.string.text_sending));
            sending = true;
            send();
        } else if (v.getId() == R.id.stopButton) {
            sending = false;

            if (generator == null) {
                return;
            }

            generator.stopImmediate();

            ((FloatingActionButton)v).hideFor((FloatingActionButton) v.getTag());
            status.setText("");
        }
    }

    @Override
    public void onGeneratorStop() {
        stopButton.performClick();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        long viewId = parent.getId();

        if (viewId == R.id.templateSpinner) {
            messageText.removeTextChangedListener(this);
            messageText.setText(templates[position]);
            messageText.addTextChangedListener(this);
            if (position == 0 && !sending && startButton.getVisibility() != View.GONE) {
                startButton.hide();
            } else if (startButton.getVisibility() == View.GONE && !sending &&
                       messageText.getText().length() > 0) {
                startButton.show();
            }
        } else if (viewId == R.id.modulationSpinner) {
            modulation = modulations[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        AdapterView.OnItemSelectedListener listener = templateSpinner.getOnItemSelectedListener();
        templateSpinner.setOnItemSelectedListener(null);
        templateSpinner.setSelection(0, false);
        templateSpinner.setOnItemSelectedListener(listener);
        if (startButton.getVisibility() == View.GONE && s.length() > 0 && !sending) {
            startButton.show();
        } else if (s.length() == 0 && !sending) {
            startButton.hide();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        preferences = getPreferences(Context.MODE_PRIVATE);

        templates         = getResources().getStringArray(R.array.list_template_content);
        modulations       = getResources().getStringArray(R.array.list_modulation_name);
        startButton = (FloatingActionButton)findViewById(R.id.playButton);
        stopButton = (FloatingActionButton)findViewById(R.id.stopButton);
        templateSpinner   = (Spinner)findViewById(R.id.templateSpinner);
        messageText = (EditText)findViewById(R.id.text);
        status            = (TextView)findViewById(R.id.statusText);
        Spinner modulationSpinner = (Spinner) findViewById(R.id.modulationSpinner);

        startButton.setSoundEffectsEnabled(false);
        stopButton.setSoundEffectsEnabled(false);
        fskGenerator.setOnGeneratorStopListener(this);
        gfskGenerator.setOnGeneratorStopListener(this);
        pskGenerator.setOnGeneratorStopListener(this);
        byteAudioGenerator.setOnGeneratorStopListener(this);

        ArrayAdapter<CharSequence> modulationAdapter = ArrayAdapter.createFromResource(this,
                R.array.list_modulation_descriptions,
                android.R.layout.simple_spinner_dropdown_item);
        modulationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modulationSpinner.setAdapter(modulationAdapter);

        ArrayAdapter<CharSequence> templateAdapter = ArrayAdapter.createFromResource(this,
                R.array.list_template_description,
                android.R.layout.simple_spinner_dropdown_item);
        templateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        templateSpinner.setTag(false);
        templateSpinner.setAdapter(templateAdapter);

        modulationSpinner.setOnItemSelectedListener(this);
        templateSpinner.setOnItemSelectedListener(this);

        startButton.setTag(stopButton);
        stopButton.setTag(startButton);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

        whiteCode = RawReader.readRawAsset(this, "whitecode.bin");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send, menu);

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
    protected void onResume() {
        super.onResume();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        psk31Center    = Integer.parseInt(prefs.getString("pref_psk31_centerfreq", null));
        fskCenter      = Integer.parseInt(prefs.getString("pref_fsk_centerfreq", null));
        fskDeviation   = Integer.parseInt(prefs.getString("pref_fsk_dev", null));
        fskSymbolSize  = Integer.parseInt(prefs.getString("pref_fsk_symbolsize", null));
        gfskCenter     = Integer.parseInt(prefs.getString("pref_gfsk_centerfreq", null));
        gfskDeviation  = Integer.parseInt(prefs.getString("pref_gfsk_dev", null));
        gfskSymbolSize = Integer.parseInt(prefs.getString("pref_gfsk_symbolsize", null));
        pskCenter      = Integer.parseInt(prefs.getString("pref_psk_centerfreq", null));
        pskSymbolSize  = Integer.parseInt(prefs.getString("pref_psk_symbolsize", null));
        dsssChipSize   = Integer.parseInt(prefs.getString("pref_dsss_chipsize", null));
        dsssAmplitude  = Integer.parseInt(prefs.getString("pref_dsss_amplitude", null));
        fskPacketType  = prefs.getString("pref_fsk_packet", null);
        gfskPacketType = prefs.getString("pref_gfsk_packet", null);
        pskPacketType  = prefs.getString("pref_psk_packet", null);
        fskRepeat      = Integer.parseInt(prefs.getString("pref_fsk_repeat", null));
        gfskRepeat     = Integer.parseInt(prefs.getString("pref_gfsk_repeat", null));
        pskRepeat      = Integer.parseInt(prefs.getString("pref_psk_repeat", null));
        fskInterleave  = prefs.getString("pref_fsk_interleave", null);
        gfskInterleave = prefs.getString("pref_gfsk_interleave", null);
        pskInterleave  = prefs.getString("pref_psk_interleave", null);
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
            case R.id.settings:
                startActivity(new Intent(getBaseContext(), SendSettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void send() {
        byte[] payload;
        byte[] bytes;
        try {
            payload = messageText.getText().toString().getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        switch (modulation) {
            case "GFSK":
                generator = gfskGenerator;
                if (gfskPacketType.equals("gnuradio")) {
                    bytes = Gnuradio.makePacket(payload, gfskRepeat, gfskInterleave);
                } else {
                    bytes = SimplePacket.makePacket(payload);
                }

                gfskGenerator.setBytes(bytes, gfskSymbolSize);
                gfskGenerator.setDeviation(gfskDeviation);
                gfskGenerator.setCenterFrequency(gfskCenter);
                gfskGenerator.start();
                sending = true;
                break;
            case "FSK":
                generator = fskGenerator;
                if (fskPacketType.equals("gnuradio")) {
                    bytes = Gnuradio.makePacket(payload, fskRepeat, fskInterleave);
                } else {
                    bytes = SimplePacket.makePacket(payload);
                }
                fskGenerator.setBytes(bytes);
                fskGenerator.setBufferSize(fskSymbolSize);
                fskGenerator.setCenterFrequency(fskCenter);
                fskGenerator.setDeviation(fskDeviation);
                fskGenerator.start();
                sending = true;
                break;
            case "PSK":
                generator = pskGenerator;
                if (pskPacketType.equals("gnuradio")) {
                    bytes = Gnuradio.makePacket(payload, pskRepeat, pskInterleave);
                } else {
                    bytes = SimplePacket.makePacket(payload);
                }
                pskGenerator.setIterator(new BitstreamIterator(bytes));
                pskGenerator.setBufferSize(pskSymbolSize);
                pskGenerator.setSineFrequency(pskCenter);
                pskGenerator.start();
                sending = true;
                break;
            case "PSK31":
                generator = pskGenerator;
                pskGenerator.setSineFrequency(psk31Center);
                pskGenerator.setSymbolRate(31.25f);
                pskGenerator.setIterator(new VaricodeIterator(payload));
                pskGenerator.start();
                sending = true;
                break;
            case "DSSS":
                generator  = byteAudioGenerator;
                bytes      = SimplePacket.makePacket(payload);
                payload    = DsssEncoder.encode(bytes, whiteCode, dsssChipSize);
                byteAudioGenerator.setAmplitude((float) dsssAmplitude / 10000.0f);
                byteAudioGenerator.setBytes(payload);
                byteAudioGenerator.start();
                sending = true;
                break;
            case "eDSSS":
                generator  = byteAudioGenerator;
                bytes      = SimplePacket.makePacket(payload);
                payload    = DsssEncoder.encodeEdsss(bytes, whiteCode, dsssChipSize);
                byteAudioGenerator.setAmplitude((float) dsssAmplitude / 10000.0f);
                byteAudioGenerator.setBytes(payload);
                byteAudioGenerator.start();
                sending = true;
        }
    }
}
