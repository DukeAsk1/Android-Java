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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

public class RdvDetailsFragment extends Fragment {
    // Database
    private DatabaseHelper myHelper;

    // Events attributes
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

    // From add button or not
    private Boolean fromAdd;

    // temp moment for Fragment
    private Moment a_Moment;

    // time and date pickers
    int hours, minutes;
    int year,month,day;
    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            hours = i;
            minutes = i1;
            etTime.setText(new StringBuilder().append(hours).append(":").append(minutes));
        }
    };
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


    // buttons
    ImageButton btnPickTime;
    ImageButton btnPickDate;
    ImageButton btnPickContact;
    ImageButton btnPickLocation;
    ImageButton btnCallNumber;


    // contact list
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri contactData= data.getData();
                    Cursor c = getContext().getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        TextView tvName= getView().findViewById(R.id.etContact);
                        String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        tvName.setText(name);
                    }
                }
            });


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rdv_details, container, false);
        return view;
    }

    public void setMoment(Moment pMoment, Boolean f){
        this.a_Moment = pMoment;
        this.fromAdd  = f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void chargeAll(){
        //setContentView(R.layout.rdv_details);
        etTitle = (EditText) getView().findViewById(R.id.etTitle);
        etContact = (EditText) getView().findViewById(R.id.etContact);
        etNum = (EditText) getView().findViewById(R.id.etNum);
        etLocation = (EditText) getView().findViewById(R.id.etLocation);
        etDate = (EditText) getView().findViewById(R.id.etDate);
        etTime = (EditText) getView().findViewById(R.id.etTime);
        spReminder = (Spinner) getView().findViewById(R.id.spReminder);
        etComments = (EditText) getView().findViewById(R.id.etComments);
        tvId = (TextView)getView().findViewById(R.id.tvId);
        spCategory = (Spinner) getView().findViewById(R.id.spCategory);
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getContext(), R.array.spinner, android.R.layout.
                        simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> reminder =
                ArrayAdapter.createFromResource(getContext(), R.array.reminder, android.R.layout.
                        simple_spinner_item);

        reminder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spReminder.setAdapter(reminder);
        spCategory.setAdapter(adapter);
        myHelper = new DatabaseHelper(getContext());
        myHelper.open();
        //Intent intent = getIntent();

        //fromAdd= intent.getBooleanExtra("fromAdd",false);
        if(!fromAdd){
            //Bundle b= intent.getExtras();
            //Moment selectedMoment= b.getParcelable("SelectedMoment");
            //tvId.setText(String.valueOf(selectedMoment.getId()));
            spCategory.setSelection(adapter.getPosition(a_Moment.getCategory()));
            etTitle.setText(a_Moment.getTitle());
            etContact.setText(a_Moment.getContact());
            etNum.setText(a_Moment.getNum());
            etLocation.setText(a_Moment.getLocation());
            etDate.setText(a_Moment.getDate());
            etTime.setText(a_Moment.getTime());
            spReminder.setSelection(reminder.getPosition(a_Moment.getCategory()));
            etComments.setText(a_Moment.getComments());
        }

        btnPickDate=(ImageButton)getView().findViewById(R.id.btnPickDate);
        etDate= (EditText) getView().findViewById(R.id.etDate);

        btnPickTime=(ImageButton) getView().findViewById(R.id.btnPickTime);
        etTime= (EditText) getView().findViewById(R.id.etTime);

        btnPickContact = (ImageButton)getView().findViewById(R.id.btnPickContact);
        etContact = (EditText) getView().findViewById(R.id.etContact);

        btnPickLocation = (ImageButton) getView().findViewById(R.id.btnPickLocation);

        btnCallNumber = (ImageButton) getView().findViewById(R.id.btnCallNumber);
        etNum = (EditText) getView().findViewById(R.id.etNum);
    }


    ////////////////// BUTTONS /////////////////////

    // pick a contact
    public void pickContact(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //startActivityForResult(intent, CONTACT_PICK_CODE);
        someActivityResultLauncher.launch(intent);
    }

    // pick a date
    public void pickDate(View view){
        showDatePicker();
    }
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
        date.show(getFragmentManager(),"Date Picker");
    }

    // pick a time
    public void pickTime(View view){
        showTimePicker();
    }
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
        time.show(getFragmentManager(),"Time Picker");
    }

    // choose a location
    public void launchMaps(View view) {
        String map = "http://maps.google.co.in/maps?q=" + etLocation.getText() ;
        Uri gmmIntentUri = Uri.parse(map);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        startActivity(mapIntent);

    }
    // call a phone number
    public void callNumber(View arg)
    {

        // getting phone number from edit text
        // and changing it to String
        String phone_number
                = etNum.getText().toString();

        // Getting instance of Intent
        // with action as ACTION_CALL
        Intent phone_intent
                = new Intent(Intent.ACTION_CALL);

        // Set data of Intent through Uri
        // by parsing phone number
        phone_intent
                .setData(Uri.parse("tel:"
                        + phone_number));

        // start Intent
        startActivity(phone_intent);
    }

    // save event
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

            Intent main = new Intent(getActivity(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }
        else {
            Long id = Long.parseLong(tvId.getText().toString());
            Moment moment = new Moment(id,category,title,contact, num, location,date,time, reminder, comments);
            int n = myHelper.update(moment);

            Intent main = new Intent(getActivity(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
        }
    }

    // cancel event
    public void onCancelClick(View v) {
        //finish();
        Intent main = new Intent(getActivity(),MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
    }



}
