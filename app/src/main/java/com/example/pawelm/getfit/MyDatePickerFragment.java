package com.example.pawelm.getfit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import org.joda.time.LocalDate;

import java.util.Calendar;

import androidx.fragment.app.DialogFragment;

public class MyDatePickerFragment extends DialogFragment {
    private LocalDate date;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        date = null;

        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    date = new LocalDate(year, month, day);
                }
            };

    public LocalDate getDate() {
        return date;
    }
}
