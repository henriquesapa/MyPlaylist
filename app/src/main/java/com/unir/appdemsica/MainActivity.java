package com.unir.appdemsica;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ImageButton btn_playlist;
    private EditText editText;
    private LinearLayout buttonLayout;
    private List<String> playlists;

    private List<Button> playlistButtons = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_playlist = findViewById(R.id.btn_playlist);
        editText = findViewById(R.id.edt_playlist);
        buttonLayout = findViewById(R.id.button_layout);
        playlists = new ArrayList<>();

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editText.setHint("Escreva o nome da Playlist");
                } else {
                    editText.setHint("");
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    editText.setHint("");
                } else {
                    editText.setHint("Escreva o nome da Playlist");
                }
            }
        });

        btn_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = editText.getText().toString().trim();
                if (!playlistName.isEmpty()) {
                    playlists.add(playlistName);
                    addPlaylistButton(playlistName);
                    editText.setText(""); // Limpa o EditText após adicionar a playlist
                }
            }
        });
    }

    private void addPlaylistButton(String playlistName) {
        Button button = new Button(this);
        button.setText(playlistName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
                intent.putExtra("selectedPlaylist", playlistName); // Passa o nome da playlist selecionada
                startActivity(intent);
            }
        });
        buttonLayout.addView(button);
    }



}