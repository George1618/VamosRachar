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

    EditText total, quantity; // campos de entrada para a quantia total e o número de pessoas
    TextView result; // texto com o valor do resultado da divisão dos de cima
    FloatingActionButton share, speak; //botões de compartilhar e falar

    TextToSpeech tts; // TTS (conversor de texto em voz)
    DecimalFormat money = new DecimalFormat("#.00"); // formata para o padrão monetário

    double t = 0.00, q = 2.0, r = 0.00; // valor do campo "total", do "quantity", e o resultado
    int req = 1122; // código para o conversor de texto em voz requisitar dados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        total = (EditText) findViewById(R.id.total);
        quantity = (EditText) findViewById(R.id.quantity);
        result = (TextView) findViewById(R.id.result);
        share = (FloatingActionButton) findViewById(R.id.share);
        speak = (FloatingActionButton) findViewById(R.id.speak);

        // procura pelos dados do conversor (ex.: voz em língua local) no dispositivo
        Intent checkTTS = new Intent();
        checkTTS.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTS, req);

        // adiciona listeners para edições dos campos, com o método definido em afterTextChanged
        total.addTextChangedListener(this);
        quantity.addTextChangedListener(this);

        // adiciona listener para os cliques nos botões, com métodos definidos no próprio parâmetro.
        // Cada botão utiliza uma classe própria em vez de definir a MainActivity como o watcher
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
        try { // tenta converter os textos dos campos "total" e "quantity" em número
            t = Double.parseDouble(total.getText().toString().replace(',', '.'));
            q = Double.parseDouble(quantity.getText().toString());
        } catch (Exception e) { t = 0.0; } // se houver erro, deixa o valor 0 como padrão

        if (q==0.0) Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        else { // efetua a divisão dos novos valores e a mostra em "result" já formatada
            r = t / q;
            String res = getString(R.string.RS)+" "+money.format(r);
            res = res.replace(" ,", " 0,");
            result.setText(res);
        }
    }

    // Conversor de Texto em Voz

    // se não há dados suficientes para o TTS, instala-os.
    // Isto é a resolução/continuação da procura feita no onCreate pelo startActivityForResult
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
    // Verifica se obteve sucesso ao conseguir os dados do TTS
    @Override
    public void onInit(int i) {
        if (i==TextToSpeech.SUCCESS)
            Toast.makeText(this, R.string.success, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, R.string.fail, Toast.LENGTH_LONG).show();
    }
}