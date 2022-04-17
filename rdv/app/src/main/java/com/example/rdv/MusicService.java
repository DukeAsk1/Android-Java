package com.example.rdv;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicService extends Service {

    MediaPlayer player;
    IBinder binder = new MyActivityBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                player = MediaPlayer.create(getApplicationContext(),R.raw.lofi);
                player.setLooping(true);
            }
        });
        t.start();

        return START_STICKY;
    }

    public void pauseMusic(){
        player.pause();
    }
    public void playMusic(){
        player.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }

    public class MyActivityBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

}


