package com.example.rdv;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ListView lvMoments; // list of existing events
    private DatabaseHelper myHelper; // database

    // permissions
    private static final int WRITE_CALENDAR_PERMISSION_CODE = 62;
    private static final int PHONE_PERMISSION_CODE = 30;
    private static final int CONTACT_PERMISSION_CODE = 1;

    // notifications
    static String CHANNEL_ID= "channel_notif";
    static int NOTIFICATION_ID=100;
    static int REQUEST_CODE= 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myHelper = new DatabaseHelper(this);
        myHelper.open();



        requestReadContactPermissions(); // request for permissions
        createNotificationChannel(); // create a notification channel

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

                RdvDetailsFragment fragment = (RdvDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.detailsFragment);

                if (fragment != null && fragment.isInLayout()) {
                    fragment.setMoment(pMoment,false);
                    fragment.chargeAll();
                } else {
                    Intent intent = new Intent(getApplicationContext(), RdvDetailsActivity.class);
                    intent.putExtra("SelectedMoment",pMoment);
                    intent.putExtra("fromAdd",false);
                    startActivity(intent);
                }

            }
        });
    }


//////////////// Menu ////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.rdv_menu,menu);

        /*// Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default*/

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.new_moment:{
                RdvDetailsFragment fragment = (RdvDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.detailsFragment);

                if (fragment != null && fragment.isInLayout()) {
                    fragment.setMoment(null,true);
                    fragment.chargeAll();
                } else {
                    Intent intent=new Intent(this, RdvDetailsActivity.class);
                    intent.putExtra("fromAdd", true);
                    startActivity(intent);
                    return true;
                }

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

        try {
            compareTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    ////////////////// PERMISSIONS /////////////////////

    public void requestReadContactPermissions()
    {

        // READ CONTACTS PERMISSION
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED)
        {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_CODE);
        }


    }

    public void requestWriteCalendarPermissions()
    {
        // WRITE CALENDAR PERMISSION
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) !=
                PackageManager.PERMISSION_GRANTED)
        {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_CALENDAR}, WRITE_CALENDAR_PERMISSION_CODE);
        }
    }

    public void requestCallPhonePermissions()
    {

        // CALL PHONE PERMISSION
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED)
        {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.CALL_PHONE}, PHONE_PERMISSION_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case CONTACT_PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
                else{
                    requestWriteCalendarPermissions();
                }
            }

            case WRITE_CALENDAR_PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
                else{
                    requestCallPhonePermissions();
                }
            }

            case PHONE_PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //////////////// NOTIFICATIONS //////////////////
    private void createNotificationChannel() {
        // Create a NotificationChannel, only for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "RDV Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription("Notification channel description");
            // register the channel
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showNotification() {
        Intent intent= new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=
                PendingIntent.getActivity(this,REQUEST_CODE,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("RDV Alarm")
                .setContentText("A RDV is close !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notifBuilder.build());
    }

    public void compareTime() throws ParseException {
        ArrayList<String> mArrayList = new ArrayList<String>();
        String[] dateArray;
        String[] timeArray;
        String[] reminderArray;

        /// DATE
        Cursor cur = myHelper.getAllMoments();
        int id = cur.getColumnIndex(myHelper.MDATE);
        for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            mArrayList.add(cur.getString(id));
        }
        dateArray = mArrayList.toArray(new String[0]);
        mArrayList.clear();
        cur.close();

        /// TIME
        cur = myHelper.getAllMoments();
        id = cur.getColumnIndex(myHelper.MTIME);
        for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            mArrayList.add(cur.getString(id));
        }
        timeArray = mArrayList.toArray(new String[0]);
        mArrayList.clear();
        cur.close();

        /// REMINDER
        cur = myHelper.getAllMoments();
        id = cur.getColumnIndex(myHelper.REMINDER);
        for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            mArrayList.add(cur.getString(id));
        }
        reminderArray = mArrayList.toArray(new String[0]);
        mArrayList.clear();
        cur.close();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm");
        int nbMoments = timeArray.length;
        Calendar c = Calendar.getInstance();

        /// NOTIF
        for(int i = 0; i < nbMoments; i++){

            String targetDate = dateArray[i] + " " + timeArray[i];
            Date d = dateFormat.parse(targetDate); // target date

            int r = Integer.parseInt(reminderArray[i].charAt(0)+""); // the number of days reminder

            c.setTime(d);
            c.add(Calendar.HOUR, 24*r); // turn back r days in time

            Date totalDate = Calendar.getInstance().getTime();
            //Date limitDate = dateFormat.parse(totalDate.toString());
            // current date parsed the right way

            if(totalDate.after(d)){
                Log.d("test",""+i);
                showNotification();
            }

        }
    }

}