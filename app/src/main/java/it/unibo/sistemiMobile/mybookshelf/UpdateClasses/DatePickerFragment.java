package it.unibo.sistemiMobile.mybookshelf.UpdateClasses;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private Calendar c;
    private long maxDate;
    private long minDate;

    public DatePickerFragment(Calendar initDate, long maxDate, long minDate) {
        this.c = initDate;
        this.maxDate = maxDate;
        this.minDate = minDate;
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), y,m,d);
        if(maxDate != 0){
            dialog.getDatePicker().setMaxDate(maxDate);
        }
        if(minDate != 0){
            dialog.getDatePicker().setMinDate(minDate);
        }
        return dialog;
    }
}
