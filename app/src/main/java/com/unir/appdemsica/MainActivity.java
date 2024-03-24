package com.unir.appdemsica;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private ImageButton btn_importar, btn_parar, btn_tocar;
    private RadioGroup radioGroup;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_importar = findViewById(R.id.btn_importar);
        btn_parar = findViewById(R.id.btn_parar);
        btn_tocar = findViewById(R.id.btn_tocar);
        radioGroup = findViewById(R.id.radioGroup);

        intent = new Intent(MainActivity.this, MyService.class);

        btn_importar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        });

        btn_tocar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = findViewById(radioGroup.getCheckedRadioButtonId());
                String filePath = (String) rb.getTag(); // Caminho do arquivo de áudio
                String musica = rb.getText().toString();

                intent.putExtra("filePath", filePath);
                intent.putExtra("nome", musica);
                startForegroundService(intent); // Inicia o serviço apenas quando o botão for acionado
            }
        });

        btn_parar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
            }
        });

        String[] playlists = {}; // Exemplo de nomes de playlists
        for (String playlist : playlists) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(playlist);
            radioButton.setTextColor(Color.WHITE); // Define a cor do texto para branco
            radioGroup.addView(radioButton);
            radioButton.setChecked(true); // Marca o RadioButton como selecionado
        }

    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedFileUri = data.getData();
            String displayName = null;

            // Obter o nome do arquivo original
            Cursor cursor = getContentResolver().query(selectedFileUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                cursor.close();
            }

            if (displayName != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                    File file = new File(getFilesDir(), displayName);
                    OutputStream outputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                    inputStream.close();

                    // Obter o caminho absoluto do arquivo
                    String absoluteFilePath = file.getAbsolutePath();

                    // Iniciar o serviço com o caminho do arquivo
                    Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                    serviceIntent.putExtra("filePath", absoluteFilePath);
                    serviceIntent.putExtra("nome", displayName);
                    startForegroundService(serviceIntent);

                    // Criar RadioButton para a música importada
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(displayName);
                    radioButton.setTextColor(Color.WHITE);
                    radioButton.setTag(absoluteFilePath); // Armazenar o caminho do arquivo como tag
                    radioGroup.addView(radioButton);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
