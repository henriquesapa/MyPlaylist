package com.unir.appdemsica;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaylistActivity extends AppCompatActivity {

    private ImageButton btn_importar, btn_parar, btn_tocar, btn_voltar,btn_deletar;
    private RadioGroup radioGroup;

    private List<String> playlists = new ArrayList<>();

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        btn_importar = findViewById(R.id.btn_importar);
        btn_parar = findViewById(R.id.btn_parar);
        btn_tocar = findViewById(R.id.btn_tocar);
        btn_voltar = findViewById(R.id.btn_voltar);
        btn_deletar = findViewById(R.id.btn_deletar);
        radioGroup = findViewById(R.id.radioGroup);

        intent = new Intent(PlaylistActivity.this, MyService.class);

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
                String filePath = (String) rb.getTag();
                String musica = rb.getText().toString();

                intent.putExtra("filePath", filePath);
                intent.putExtra("nome", musica);
                startForegroundService(intent);
            }
        });

        btn_parar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
            }
        });

        btn_deletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String displayName = selectedRadioButton.getText().toString();
                    String filePath = (String) selectedRadioButton.getTag();

                    radioGroup.removeView(selectedRadioButton);

                    removeRadioButtonData(displayName, filePath);
                }
            }
        });


        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra("playlists", new ArrayList<>(playlists));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        loadRadioButtonData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedFileUri = data.getData();
            String displayName = null;

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

                    String absoluteFilePath = file.getAbsolutePath();

                    String selectedPlaylist = getIntent().getStringExtra("selectedPlaylist");

                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setText(displayName);
                    radioButton.setTextColor(Color.WHITE);
                    radioButton.setTag(absoluteFilePath); // Armazenar o caminho do arquivo como tag
                    radioGroup.addView(radioButton);

                    saveRadioButtonData(selectedPlaylist, displayName, absoluteFilePath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private void saveRadioButtonData(String playlistName, String displayName, String filePath) {
        SharedPreferences sharedPreferences = getSharedPreferences("radioButtonData_" + playlistName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(displayName, filePath);
        editor.apply();
    }


    private void loadRadioButtonData() {
        String selectedPlaylist = getIntent().getStringExtra("selectedPlaylist");
        SharedPreferences sharedPreferences = getSharedPreferences("radioButtonData_" + selectedPlaylist, MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String displayName = entry.getKey();
            String filePath = (String) entry.getValue();
            addRadioButton(displayName, filePath);
        }
    }



    private void addRadioButton(String displayName, String filePath) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(displayName);
        radioButton.setTextColor(Color.WHITE);
        radioButton.setTag(filePath); // Armazenar o caminho do arquivo como tag
        radioGroup.addView(radioButton);
    }

    private void removeRadioButtonData(String displayName, String filePath) {
        // Recuperar o nome da playlist selecionada
        String selectedPlaylist = getIntent().getStringExtra("selectedPlaylist");

        // Acessar o SharedPreferences com o nome da playlist
        SharedPreferences sharedPreferences = getSharedPreferences("radioButtonData_" + selectedPlaylist, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(displayName); // Remove o dado específico do SharedPreferences
        editor.apply(); // Aplica as alterações
    }



}
