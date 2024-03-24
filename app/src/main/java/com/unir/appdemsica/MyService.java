package com.unir.appdemsica;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaParser;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class MyService extends Service {

    MediaPlayer mediaPlayer;
    boolean isPlaying = true;
    public MyService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String filePath = intent.getStringExtra("filePath");
        String musica = intent.getStringExtra("nome");
        if (isPlaying && filePath != null){
            mediaPlayer = MediaPlayer.create(this, Uri.parse(filePath));
            tocarSom();
            mediaPlayer.setLooping(true);
            isPlaying = false;
        }

        final String CHANNELID = "Music Player ID";
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_DEFAULT
        );

        getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);

        Notification.Builder notification = new Notification.Builder(this, CHANNELID);
        notification.setContentText("Tocando agora: " + musica);
        notification.setContentTitle("Qual é a música");
        notification.setSmallIcon(R.drawable.icon);
        startForeground(1001, notification.build());

        return super.onStartCommand(intent, flags, startId);
    }




    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void tocarSom(){
        if (mediaPlayer != null){
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = true;
        }
    }
}