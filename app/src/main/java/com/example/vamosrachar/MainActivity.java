package com.example.vamosrachar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher, TextToSpeech.OnInitListener {

    EditText total, quantity;
    TextView result;
    FloatingActionButton share, speak;

    TextToSpeech tts;
    DecimalFormat money = new DecimalFormat("#.00");

    double t = 0.00, q = 2.0, r = 0.00;
    int req = 1122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        total = (EditText) findViewById(R.id.total);
        quantity = (EditText) findViewById(R.id.quantity);
        result = (TextView) findViewById(R.id.result);
        share = (FloatingActionButton) findViewById(R.id.share);
        speak = (FloatingActionButton) findViewById(R.id.speak);

        Intent checkTTS = new Intent();
        checkTTS.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTS, req);

        total.addTextChangedListener(this);
        quantity.addTextChangedListener(this);

        share.setOnClickListener(view -> {
            Intent toShare = new Intent(Intent.ACTION_SEND);
            toShare.setType("text/plain");
            String text = getString(R.string.extra)+" "+result.getText().toString();
            toShare.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(toShare, getString(R.string.media)));
        });
        speak.setOnClickListener(view -> {
            if (tts!=null) {
                String text = getString(R.string.speaking)+" "+result.getText().toString();
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ID1");
            }
        });

    }

    // Cálculo ao editar

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @SuppressLint("DefaultLocale")
    @Override
    public void afterTextChanged(Editable editable) {
        try {
            t = Double.parseDouble(total.getText().toString().replace(',', '.'));
            q = Double.parseDouble(quantity.getText().toString());
        } catch (Exception e) { t = 0.0; }

        if (q==0.0) Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        else {
            r = t / q;
            String res = getString(R.string.RS)+" "+money.format(r);
            res = res.replace(" ,", " 0,");
            result.setText(res);
        }
    }

    // Sintetizador de Voz

    // se não há dados suficientes para o TTS, instala-os.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==req) {
            if (resultCode==TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                tts = new TextToSpeech(this, this);
            else {
                Intent installTTS = new Intent();
                installTTS.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTS);
            }
        }
    }
    @Override
    public void onInit(int i) {
        if (i==TextToSpeech.SUCCESS)
            Toast.makeText(this, R.string.success, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, R.string.fail, Toast.LENGTH_LONG).show();
    }
}