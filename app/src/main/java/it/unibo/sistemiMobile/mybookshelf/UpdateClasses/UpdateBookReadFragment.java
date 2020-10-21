package it.unibo.sistemiMobile.mybookshelf.UpdateClasses;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.ViewModel.UpdateViewModel;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

public class UpdateBookReadFragment extends Fragment {
    private int bookID;
    private BookRead book;
    private Date newStartDate = new Date();
    private Date newEndDate = new Date();
    private FragmentActivity activity;
    private UpdateViewModel model;
    private List<String> quotes;

    public UpdateBookReadFragment(int bookID) {
        this.bookID = bookID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.book_read_details, container, false);
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
            model = new ViewModelProvider(activity).get(UpdateViewModel.class);
            model.getBookRead().observe(activity, new Observer<List<BookRead>>() {
                @Override
                public void onChanged(List<BookRead> bookReads) {
                    if(bookReads.size() > bookID){
                        Log.i("MY", "bookID: " + bookID);
                        book = bookReads.get(bookID);
                        Utility.setBookDetailsView(activity, book);
                        quotes = Utility.setQuotesView(activity, book);
                        Utility.setStartDateView(activity, book);
                        Utility.setEndDateView(activity, book);
                        newStartDate.setTime(book.getStartDate());
                        newEndDate.setTime(book.getEndDate());
                        activity.findViewById(R.id.shareButton).setVisibility(View.GONE);
                        if(book.getStars() > 0){
                            RatingBar ratingBar = activity.findViewById(R.id.ratingBar);
                            ratingBar.setRating(book.getStars());
                            activity.findViewById(R.id.shareButton).setVisibility(View.VISIBLE);

                        }
                        if(!book.getReview().isEmpty()){
                            TextInputEditText review = activity.findViewById(R.id.reviewEditText);
                            review.setText(book.getReview());
                        }
                        setAllEnable(false);
                    }
                }
            });
            activity.findViewById(R.id.startDateTextEdit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DetailsAndUpdateActivity) activity).getNewDate((TextInputEditText) activity.findViewById(R.id.startDateTextEdit),
                            newStartDate, book.getEndDate(), 0);
                }
            });

            activity.findViewById(R.id.endDateTextEdit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DetailsAndUpdateActivity) activity).getNewDate((TextInputEditText) activity.findViewById(R.id.endDateTextEdit),
                            newEndDate,Calendar.getInstance().getTimeInMillis(),newStartDate.getTime());
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

            activity.findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "I recommend you read this book: \n" + book.getTitle();
                    if(!book.getAuthor().isEmpty()){
                        text += " by " + book.getAuthor();
                    }
                     text+="\nI rate this: " + book.getStars() + " stars";
                    if(!book.getReview().isEmpty()){
                        text += " and this is my review: " + book.getReview();
                    }
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                    sendIntent.setType("text/plain");
                    if (v.getContext() != null &&
                            sendIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                        v.getContext().startActivity(Intent.createChooser(sendIntent, null));
                    }
                }
            });

        }
    }

    private void saveNewQuotes(LinearLayout layout, BookRead book){
        if(layout.getChildCount() >=  quotes.size()){
            String quotesString = book.getQuotes();
            for(int i = quotes.size(); i < layout.getChildCount(); i++){
                TextInputLayout textInputLayout = (TextInputLayout) layout.getChildAt(i);
                if(Utility.inputNotEmpty(textInputLayout.getEditText().getText())){
                    quotesString += textInputLayout.getEditText().getText().toString()+"/";
                }
            }
            book.setQuotes(quotesString);
            model.updateBookRead(book);
        }
    }

    public void saveBook(){
        RatingBar ratingBar = activity.findViewById(R.id.ratingBar);
        if(ratingBar.getRating() > 0){
            Log.i("MY", "rate: " + ratingBar.getRating());
            book.setStars(ratingBar.getRating());
            Log.i("MY", "book rate: " + book.getStars());
            model.updateBookRead(book);
        }

        if(newStartDate.getTime() != book.getStartDate()){
            book.setStartDate(newStartDate.getTime());
            model.updateBookRead(book);
        }
        if(newEndDate.getTime() != book.getEndDate()){
            book.setEndDate(newEndDate.getTime());
            model.updateBookRead(book);
        }

        TextInputEditText review = activity.findViewById(R.id.reviewEditText);
        if(Utility.inputNotEmpty(review.getText())){
            book.setReview(review.getText().toString());
            model.updateBookRead(book);
        }

        saveNewQuotes((LinearLayout) activity.findViewById(R.id.quotesLayout), book);
        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

    public void setAllEnable(boolean b) {
        activity.findViewById(R.id.startDateTextEdit).setEnabled(b);
        activity.findViewById(R.id.endDateTextEdit).setEnabled(b);
        activity.findViewById(R.id.ratingBar).setEnabled(b);
        activity.findViewById(R.id.reviewEditText).setEnabled(b);
        if(b){
            activity.findViewById(R.id.shareButton).setVisibility(View.GONE);
        }
        Utility.setQuotesViewEnable(activity, b);

    }
}
