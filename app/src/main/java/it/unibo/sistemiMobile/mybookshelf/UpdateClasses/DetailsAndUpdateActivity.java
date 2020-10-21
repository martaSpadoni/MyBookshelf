package it.unibo.sistemiMobile.mybookshelf.UpdateClasses;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

public class DetailsAndUpdateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextInputEditText currentDateEditText;
    private Date currentDate;
    private Boolean onEdit = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity);
            switch (getIntent().getIntExtra("bookType", -1)) {
                case Utility.ACTIVITY_ADD_INPROGRESS_BOOK:
                    Utility.insertFragment(this, new UpdateInProgressBookFragment(getIntent()
                            .getIntExtra("bookID", -1)), UpdateInProgressBookFragment.class.getName());
                    break;
                case Utility.ACTIVITY_ADD_READ_BOOK:
                    Utility.insertFragment(this, new UpdateBookReadFragment(getIntent().
                            getIntExtra("bookID", -1)), UpdateBookReadFragment.class.getName());
                    break;
                case Utility.ACTIVITY_ADD_WISH:
                    Utility.insertFragment(this, new UpdateBookInWishlist(getIntent().getIntExtra("bookID", -1)),
                            UpdateBookInWishlist.class.getName());
                    break;
            }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit:
                UpdateInProgressBookFragment updateInProgressBookFragment = (UpdateInProgressBookFragment)getSupportFragmentManager()
                        .findFragmentByTag(UpdateInProgressBookFragment.class.getName());
                if(updateInProgressBookFragment != null){
                    Log.i("MY", "fragmentOttenuto");
                    if(!onEdit) {
                        onEdit=true;
                        updateInProgressBookFragment.setAllEnable(true);
                        item.setIcon(R.drawable.ic_save_24dp);
                    }else{
                        onEdit=false;
                        updateInProgressBookFragment.saveBook();
                    }

                }
                UpdateBookReadFragment updateBookReadFragment = (UpdateBookReadFragment)getSupportFragmentManager()
                        .findFragmentByTag(UpdateBookReadFragment.class.getName());
                if(updateBookReadFragment != null){
                    if(!onEdit) {
                        onEdit=true;
                        updateBookReadFragment.setAllEnable(true);
                        item.setIcon(R.drawable.ic_save_24dp);
                    }else{
                        onEdit=false;
                        updateBookReadFragment.saveBook();
                    }
                }
                break;
            default:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void getNewDate(TextInputEditText dateEditText, Date date, long maxDate, long minDate){
        currentDateEditText = dateEditText;
        currentDate = date;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        new DatePickerFragment(c, maxDate, minDate).show(getSupportFragmentManager(), "date picker");

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(currentDateEditText != null){
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, month);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            currentDate.setTime(date.getTimeInMillis());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
            currentDateEditText.setText(simpleDateFormat.format(date.getTime()));
        }
    }
}
