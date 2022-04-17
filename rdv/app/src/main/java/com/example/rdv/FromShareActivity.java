package com.example.rdv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

public class FromShareActivity extends AppCompatActivity {
    // Music
    private MusicService myService;
    private ServiceConnection myServiceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            myService  =((MusicService.MyActivityBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name){
            myService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.from_share);

        Intent serviceIntent = new Intent(this, MusicService.class);
        bindService(serviceIntent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
