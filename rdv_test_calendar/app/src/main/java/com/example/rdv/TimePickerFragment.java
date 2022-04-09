package com.example.rdv;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {
    private int hours,minutes;

    TimePickerDialog.OnTimeSetListener onTimeSet;


    public TimePickerFragment() {

    }

    public void setCallBack( TimePickerDialog.OnTimeSetListener onTime ){
        onTimeSet=onTime;
    }

    @Override
    public void setArguments(Bundle args){
        super.setArguments(args);

        hours= args.getInt("hours");
        minutes=args.getInt("minutes");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new TimePickerDialog(getActivity(),onTimeSet, hours, minutes,false);
    }
}
