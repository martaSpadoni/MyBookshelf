package it.unibo.sistemiMobile.mybookshelf.UpdateClasses;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;
import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.ViewModel.UpdateViewModel;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

public class UpdateInProgressBookFragment extends Fragment {
    private int bookID;
    private BookInProgress book;
    private Date newStartDate = new Date();
    private List<String> quotes = new ArrayList<>();
    private FragmentActivity activity;
    private UpdateViewModel model;

    public UpdateInProgressBookFragment(int bookID) {
        this.bookID = bookID;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.book_inprogress_details, container, false );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu_update, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        if(activity != null){
            Utility.setUpToolbar((AppCompatActivity) activity, "");
            ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) activity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
            Log.i("MY", "BookID" + String.valueOf(bookID));
            model = new ViewModelProvider(activity).get(UpdateViewModel.class);
            model.getBooksInProgress().observe(activity, new Observer<List<BookInProgress>>() {
                @Override
                public void onChanged(List<BookInProgress> bookInProgresses) {
                    if(bookInProgresses.size() > bookID){
                        book = bookInProgresses.get(bookID);
                        setBookInProgressView(bookInProgresses.get(bookID));
                        newStartDate.setTime(book.getStartDate());
                    }
                }
            });
            activity.findViewById(R.id.startDateTextEdit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DetailsAndUpdateActivity) activity).getNewDate((TextInputEditText) activity.findViewById(R.id.startDateTextEdit),
                            newStartDate, Calendar.getInstance().getTimeInMillis(), 0);
                }
            });
            activity.findViewById(R.id.addNewQuoteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextInputLayout textInputLayout = new TextInputLayout(activity);
                    TextInputEditText editText = new TextInputEditText(activity);
                    editText.requestFocus();
                    textInputLayout.addView(editText);
                    LinearLayout layout = activity.findViewById(R.id.quotesLayout);
                    layout.addView(textInputLayout);
                }
            });
        }
    }

    private void saveNewQuotes(LinearLayout layout, BookInProgress book){
        //if(layout.getChildCount() >=  quotes.size()){
            String quotesString = "";
            for(int i = 0; i < layout.getChildCount(); i++){
                TextInputLayout textInputLayout = (TextInputLayout) layout.getChildAt(i);
                if(Utility.inputNotEmpty(textInputLayout.getEditText().getText())){
                    quotesString += textInputLayout.getEditText().getText().toString()+"/";
                }
          //  }
            book.setQuotes(quotesString);
            model.updateBookInProgress(book);
        }
    }


    private void setBookInProgressView(BookInProgress book){
        Utility.setBookDetailsView(activity, book);

        if(book.getBookmark() != 0){
            ((EditText)activity.findViewById(R.id.bookmarkEditText)).setText(String.valueOf(book.getBookmark()));
        }
        quotes = Utility.setQuotesView(activity, book);
        Utility.setStartDateView(activity, book);
        setAllEnable(false);
    }

    public void saveBook(){
        saveNewQuotes((LinearLayout) activity.findViewById(R.id.quotesLayout), book);
        if(newStartDate != null && newStartDate.getTime() != 0){
            book.setStartDate(newStartDate.getTime());
            model.updateBookInProgress(book);
        }
        if(((SwitchMaterial)activity.findViewById(R.id.segnaComeLettoSwitch)).isChecked()){
            model.markBookAsRead(book);
        }else if(Utility.inputNotEmpty(((EditText)activity.findViewById(R.id.bookmarkEditText)).getText())){
            book.setBookmark(Integer.valueOf(((EditText)activity.findViewById(R.id.bookmarkEditText)).getText().toString()));
            model.updateBookInProgress(book);

        }
        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    public void setAllEnable(boolean b) {
        ((EditText)activity.findViewById(R.id.bookmarkEditText)).setEnabled(b);
        activity.findViewById(R.id.startDateTextEdit).setEnabled(b);
        activity.findViewById(R.id.segnaComeLettoSwitch).setEnabled(b);
        Utility.setQuotesViewEnable(activity, b);

    }
}
