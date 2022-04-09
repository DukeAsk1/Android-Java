package com.example.rdv;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    ListView lvMoments; // list of existing events
    private DatabaseHelper myHelper; // database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myHelper = new DatabaseHelper(this);
        myHelper.open();

        lvMoments = (ListView) findViewById(R.id.lvMoments);
        lvMoments.setEmptyView(findViewById(R.id.tvEmpty));
        chargeData();
        registerForContextMenu(lvMoments);

        lvMoments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                String idItem= ((TextView)view.findViewById(R.id.idMoment)).getText().toString();
                String categoryItem= ((TextView)view.findViewById(R.id.category)).getText().toString();
                String titleItem= ((TextView)view.findViewById(R.id.title)).getText().toString();
                String contactItem= ((TextView)view.findViewById(R.id.tvContact)).getText().toString();
                String numItem= ((TextView)view.findViewById(R.id.tvNum)).getText().toString();
                String locationItem= ((TextView)view.findViewById(R.id.tvLocation)).getText().toString();
                String dateItem= ((TextView)view.findViewById(R.id.tvDate)).getText().toString();
                String timeItem= ((TextView)view.findViewById(R.id.tvTime)).getText().toString();
                String reminderItem= ((TextView)view.findViewById(R.id.tvReminder)).getText().toString();
                String commentsItem= ((TextView)view.findViewById(R.id.comments)).getText().toString();
                Moment pMoment= new Moment(Long.parseLong(idItem),categoryItem,titleItem,contactItem,numItem,locationItem, dateItem,timeItem,reminderItem,commentsItem);
                Intent intent = new Intent(getApplicationContext(), MomentDetails.class);
                intent.putExtra("SelectedMoment",pMoment);
                intent.putExtra("fromAdd",false);
                startActivity(intent);
            }
        });
    }


//////////////// Menu ////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.rdv_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.new_moment:{
                Intent intent=new Intent(this,MomentDetails.class);
                intent.putExtra("fromAdd", true);
                startActivity(intent);
                return true;
            }
            case R.id.search: {
                Toast.makeText(this, "Search", Toast.LENGTH_LONG).show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu,menu);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        if (item.getItemId()==R.id.delete){
            myHelper.delete(info.id);
            chargeData();
            return true;
        }
        if (item.getItemId()==R.id.share){
            final ListView lv = (ListView) findViewById(R.id.lvMoments);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> myAdapter, View myView, int pos, long mylng) {
                    String selectedFromList =(lv.getItemAtPosition(pos).toString());
                    // this is your selected item
                    }
            });

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.id.item_menu));
            sendIntent.setType("text/plain");
            //startActivity(sendIntent);
            startActivity(Intent.createChooser(sendIntent, "Share App"));
            return true;
        }
        return super.onContextItemSelected(item);
    }


/////////////// Data //////////////////

    public void chargeData(){
        final String[] from = new String[]{DatabaseHelper._ID, DatabaseHelper.CATEGORY,
                DatabaseHelper.TITLE, DatabaseHelper.CONTACT,DatabaseHelper.NUM,DatabaseHelper.LOCATION,
                DatabaseHelper.MDATE, DatabaseHelper.MTIME, DatabaseHelper.REMINDER, DatabaseHelper.COMMENTS};
        final int[]to= new int[]{R.id.idMoment,R.id.category, R.id.title,R.id.tvContact,R.id.tvNum,R.id.tvLocation,
                R.id.tvDate,R.id.tvTime, R.id.tvReminder,R.id.comments};

        Cursor c = myHelper.getAllMoments();
        SimpleCursorAdapter adapter= new SimpleCursorAdapter(this,R.layout.rdv_item_menu,c,from,to,0);
        adapter.notifyDataSetChanged();
        lvMoments.setAdapter(adapter);
    }


}