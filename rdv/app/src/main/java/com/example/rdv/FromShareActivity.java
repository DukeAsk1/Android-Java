package com.example.rdv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FromShareActivity extends AppCompatActivity {
    // Database
    private DatabaseHelper myHelper;
    // Music
    private MusicService myService;
    private EditText import_share;
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
        import_share = (EditText)findViewById(R.id.sharedrdvdetails);

        myHelper = new DatabaseHelper(this);
        myHelper.open();
    }

    public void readLines(View v){
        String share= import_share.getText().toString();
        String[] lines = share.split("\\n");
        String line1 = lines[5];
        System.out.println(line1);
    }

    public void importShare(View v){
        String share= import_share.getText().toString();
        String[] lines = share.split("\\n");
        String category = lines[0];
        String title = lines[1];
        String contact = lines[2];
        String num = lines[3];
        String location = lines[4];
        String date = lines[5];
        String time = lines[6];
        String reminder = lines[7];
        String comments = lines[8];

        Moment moment = new Moment(category,title,contact, num, location,date,time, reminder, comments);
        myHelper.add(moment);

        Intent main = new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);

    }

    // cancel event
    public void onCancelClick(View v) {
        finish();
    }
}
