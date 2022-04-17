package com.example.rdv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

public class PreferenceActivity extends AppCompatActivity {
    private UserSettings settings;

    Switch background_color;

    Button play_pause;

    private MusicService myService = null;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        settings = (UserSettings) getApplication();

        initWidgets();
        loadSharedPreferences();
        initSwitchListener();


        if(isPlaying()){
            play_pause.setText(R.string.MusicOff);
        }
        else{
            play_pause.setText(R.string.MusicOn);
        }


        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isPlaying()){
                    startMusic();
                }
                else{
                    stopMusic();
                }
            }
        });


    }

    private void initSwitchListener() {

        background_color.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    settings.setCustomTheme(UserSettings.DARK_THEME);
                }
                else{
                    settings.setCustomTheme(UserSettings.LIGHT_THEME);
                }

                SharedPreferences.Editor editor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
                editor.apply();
                updateView();
            }
        });
    }

    private void updateView() {
        final int black = ContextCompat.getColor(this,R.color.black);
        final int white = ContextCompat.getColor(this,R.color.white);

        if(settings.getCustomTheme().equals(UserSettings.DARK_THEME)){
            background_color.setTextColor(white);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            background_color.setChecked(true);
            background_color.setText(R.string.darkTheme);
        }
        else{
            background_color.setTextColor(black);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            background_color.setChecked(false);
            background_color.setText(R.string.lightTheme);
        }
    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
        String theme = sharedPreferences.getString(UserSettings.CUSTOM_THEME, UserSettings.LIGHT_THEME);
        settings.setCustomTheme(theme);
        updateView();
    }

    private void initWidgets() {
        background_color = findViewById(R.id.background_color);
        play_pause = findViewById(R.id.play_pause);
    }


    public void startMusic(){
        Intent serviceIntent = new Intent(getApplicationContext(), MusicService.class);
        startService(serviceIntent);
        bindService(serviceIntent,myServiceConnection, Context.BIND_AUTO_CREATE);
    }


    public void stopMusic(){
        Intent serviceIntent = new Intent(getApplicationContext(), MusicService.class);
        stopService(serviceIntent);
        unbindService(myServiceConnection);
    }

    @Override
    protected void onStop(){
        super.onStop();
        stopMusic();
    }

    public boolean isPlaying(){
        return !(myService == null);
    }
}
