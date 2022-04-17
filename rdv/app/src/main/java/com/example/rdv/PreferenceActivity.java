package com.example.rdv;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class PreferenceActivity extends AppCompatActivity {
    Switch music;
    Switch background_color;
    Button play_pause;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        music = findViewById(R.id.music);
        background_color = findViewById(R.id.background_color);
        play_pause = findViewById(R.id.play_pause);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.lofi);
        if(mediaPlayer.isPlaying()){
            play_pause.setText(R.string.MusicOff);
        }
        else{
            play_pause.setText(R.string.MusicOn);
        }

        if(!mediaPlayer.isPlaying() && music.isChecked()){
            music.setText(R.string.PlayMusic);
        }
        else if(mediaPlayer.isPlaying() && !music.isChecked()){
            music.setText(R.string.StopMusic);
        }

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    pauseMusic();
                    play_pause.setText(R.string.MusicOn);

                }
                else{
                    playMusic();
                    play_pause.setText(R.string.MusicOff);
                }
            }
        });

        music.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    stopMusic();
                    music.setText(R.string.PlayMusic);
                    play_pause.setText(R.string.MusicStop);
                }
                else{
                    //playMusic();
                    music.setText(R.string.StopMusic);
                    play_pause.setText(R.string.MusicOn);
                }
            }
        });

        background_color.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    background_color.setText(R.string.darkTheme);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    background_color.setText(R.string.lightTheme);
                }
            }
        });
    }


    public void playMusic(){
        mediaPlayer.start();
    }
    public void pauseMusic(){
        mediaPlayer.pause();
    }
    public void stopMusic(){
        mediaPlayer.stop();
    }
}
