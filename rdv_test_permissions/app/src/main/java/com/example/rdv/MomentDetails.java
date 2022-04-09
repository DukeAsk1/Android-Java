package com.example.rdv;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class MomentDetails extends AppCompatActivity {
    private DatabaseHelper myHelper;
    private EditText etTitle;
    private EditText etContact;
    private EditText etNum;
    private EditText etLocation;
    private EditText etDate;
    private EditText etTime;
    private Spinner spReminder;
    private EditText etComments;
    private TextView tvId;
    private Spinner spCategory;
    private Boolean fromAdd;


    int hours, minutes;
    int year,month,day;
    //EditText etDate;
    //EditText etTime;
    Button btnPickTime;
    Button btnPickDate;
    Button btnPickContact;
    Button btnPickLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rdv_details);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etContact = (EditText) findViewById(R.id.etContact);
        etNum = (EditText) findViewById(R.id.etNum);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        spReminder = (Spinner) findViewById(R.id.spReminder);
        etComments = (EditText) findViewById(R.id.etComments);
        tvId = (TextView) findViewById(R.id.tvId);
        spCategory = (Spinner) findViewById(R.id.spCategory);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.spinner, android.R.layout.
                        simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> reminder =
                ArrayAdapter.createFromResource(this, R.array.reminder, android.R.layout.
                        simple_spinner_item);

        reminder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spReminder.setAdapter(reminder);
        spCategory.setAdapter(adapter);
        myHelper = new DatabaseHelper(this);
        myHelper.open();
        Intent intent = getIntent();

        fromAdd= intent.getBooleanExtra("fromAdd",false);
        if(!fromAdd){
            Bundle b= intent.getExtras();
            Moment selectedMoment= b.getParcelable("SelectedMoment");
            tvId.setText(String.valueOf(selectedMoment.getId()));
            spCategory.setSelection(adapter.getPosition(selectedMoment.getCategory()));
            etTitle.setText(selectedMoment.getTitle());
            etContact.setText(selectedMoment.getContact());
            etNum.setText(selectedMoment.getNum());
            etLocation.setText(selectedMoment.getLocation());
            etDate.setText(selectedMoment.getDate());
            etTime.setText(selectedMoment.getTime());
            spReminder.setSelection(reminder.getPosition(selectedMoment.getCategory()));
            etComments.setText(selectedMoment.getComments());
        }

        btnPickDate=(Button)findViewById(R.id.btnPickDate);
        etDate= (EditText) findViewById(R.id.etDate);

        btnPickTime=(Button)findViewById(R.id.btnPickTime);
        etTime= (EditText) findViewById(R.id.etTime);

        btnPickContact = (Button)findViewById(R.id.btnPickContact);
        etContact = (EditText) findViewById(R.id.etContact);

        btnPickLocation = (Button) findViewById(R.id.btnPickLocation);
    }
    public void onCancelClick(View v) {
        finish();
    }

    public void saveMoment(View view) {
        String category = spCategory.getSelectedItem().toString();
        String title= etTitle.getText().toString();
        String contact= etContact.getText().toString();
        String num= etNum.getText().toString();
        String location= etLocation.getText().toString();
        String date=etDate.getText().toString();
        String time = etTime.getText().toString();
        String reminder = spReminder.getSelectedItem().toString();
        String comments = etComments.getText().toString();
        if(fromAdd) {
            Moment moment = new Moment(category,title,contact, num, location,date,time, reminder, comments);
            myHelper.add(moment);

            Intent main = new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }
        else {
            Long id = Long.parseLong(tvId.getText().toString());
            Moment moment = new Moment(id,category,title,contact, num, location,date,time, reminder, comments);
            int n = myHelper.update(moment);

            Intent main = new Intent(this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }
    }
    public void pickTime(View view){
        showTimePicker();
    }

    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            hours = i;
            minutes = i1;

            etTime.setText(new StringBuilder().append(hours).append(":").append(minutes));
        }
    };

    private void showTimePicker() {

        com.example.rdv.TimePickerFragment time= new com.example.rdv.TimePickerFragment();

        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        Bundle args = new Bundle();
        args.putInt("hours",hours);
        args.putInt("minutes",minutes);

        time.setArguments(args);
        time.setCallBack(onTime);
        time.show(getSupportFragmentManager(),"Time Picker");
    }


    public void pickDate(View view){
        showDatePicker();
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
        {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            etDate.setText(new StringBuilder().append(month +1).
                    append("-").append(day).append("-").append(year).append(" "));
        }
    };

    private void showDatePicker() {
        com.example.rdv.DatePickerFragment date= new com.example.rdv.DatePickerFragment();
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        Bundle args = new Bundle();
        args.putInt("year",year);
        args.putInt("month",month);
        args.putInt("day",day);
        date.setArguments(args);
        date.setCallBack(onDate);
        date.show(getSupportFragmentManager(),"Date Picker");
    }

    static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 62;
    private static final int CONTACT_PERMISSION_CODE = 1;
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri contactData= data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        TextView tvName= findViewById(R.id.etContact);
                        String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        tvName.setText(name);
                    }
                }
            });


    public void pickContact(View v) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    CONTACT_PERMISSION_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            //startActivityForResult(intent, CONTACT_PICK_CODE);
            someActivityResultLauncher.launch(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case CONTACT_PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    someActivityResultLauncher.launch(intent);
                }
                else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void launchMaps(View view) {
        String map = "http://maps.google.co.in/maps?q=" + etLocation.getText() ;
        Uri gmmIntentUri = Uri.parse(map);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        startActivity(mapIntent);

    }


}
