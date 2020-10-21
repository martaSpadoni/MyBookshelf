package it.unibo.sistemiMobile.mybookshelf.Utilities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import it.unibo.sistemiMobile.mybookshelf.AddClasses.AddActivity;
import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;
import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.BooksInProgressFragment;
import it.unibo.sistemiMobile.mybookshelf.BooksReadFragment;
import it.unibo.sistemiMobile.mybookshelf.BooksWishlistFragment;
import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.StatisticsFragment;

public class Utility {
    public static final int ACTIVITY_ADD_INPROGRESS_BOOK = 1;
    public static final int ACTIVITY_ADD_READ_BOOK = 2;
    public static final int ACTIVITY_ADD_WISH = 3;
    public static final int REQUEST_IMAGE_CAPTURE = 4;
    public static final int UPDATE_INPROGRESS_BOOK = 11;
    public static final int UPDATE_BOOK_READ = 12;
    public static final int UPDATE_BOOK_WISHLIST = 13 ;
    public static final int ADD_BY_SEARCH = 5;
    public static final int ADD_MANUALLY = 6;
    public static final int REQUEST_CHOOSE_IMAGE = 7 ;

    static public void setUpBottomNavigation(final Activity activity){
        ((BottomNavigationView) activity.findViewById(R.id.bottomNav)).
                setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.book_in_progress:
                                insertFragment((AppCompatActivity) activity, new BooksInProgressFragment(), BooksInProgressFragment.class.getName());
                                break;
                            case R.id.book_read:
                                insertFragment((AppCompatActivity) activity, new BooksReadFragment(), BooksReadFragment.class.getName());
                                break;
                            case R.id.wishlist:
                                insertFragment((AppCompatActivity) activity, new BooksWishlistFragment(), BooksWishlistFragment.class.getName());
                                break;
                            case R.id.statistics:
                                insertFragment((AppCompatActivity) activity, new StatisticsFragment(), StatisticsFragment.class.getName());
                                break;
                        }
                        return true;
                    }
                });
    }
    static public  void setUpToolbar(AppCompatActivity activity, String title) {
        Toolbar toolbar = activity.findViewById(R.id.app_bar);
        toolbar.setTitle(title);
        //Set a Toolbar to act as the ActionBar for the Activity
        activity.setSupportActionBar(toolbar);
    }
    static public void insertFragment(AppCompatActivity activity, Fragment fragment, String tag) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    static public View.OnClickListener getFABlistener(final Activity activity, final int bookType){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(activity, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.add_manually:
                                activity.startActivityForResult(new Intent(activity, AddActivity.class).
                                        putExtra("bookType", bookType).putExtra("addType", Utility.ADD_MANUALLY), bookType);
                                return true;
                            case R.id.search_book:
                                activity.startActivityForResult(new Intent(activity, AddActivity.class).
                                        putExtra("bookType", bookType).putExtra("addType", Utility.ADD_BY_SEARCH), bookType);
                                return true;
                            default:
                                return false;

                        }
                    }
                });
                menu.getMenuInflater().inflate(R.menu.fab_menu, menu.getMenu());
                menu.show();
            }
        };
    }

    static public boolean inputNotEmpty(@Nullable Editable text) {
        return text != null && text.length() > 0;
    }

    static public void setBookDetailsView(final Activity activity, final Book book){
        ((TextView) activity.findViewById(R.id.titleTextView)).setText(book.getTitle());
        if(!book.getAuthor().isEmpty()){
            ((TextView) activity.findViewById(R.id.authorTextView)).setText(book.getAuthor());
        }
        if(!book.getDescription().isEmpty()){
            ((TextView) activity.findViewById(R.id.descriptionTextView)).setText(book.getDescription());
        }else{

            activity.findViewById(R.id.descriptionTextView).setVisibility(View.GONE);
            activity.findViewById(R.id.textViewDesc).setVisibility(View.GONE);
        }

        setBookCoverImageView((ImageView)activity.findViewById(R.id.bookCover), book, activity);
    }

    public static void setBookCoverImageView(ImageView imageView, Book book, Activity activity){
        String imagePath = book.getImageResource();
        Log.i("MY", "Utily set image, path: " + imagePath);
        if (imagePath.contains("ic_")) {
            Drawable drawable = activity.getDrawable(activity.getResources()
                    .getIdentifier(book.getImageResource(), "drawable",
                            activity.getPackageName()));
            imageView.setImageDrawable(drawable);
        }else {
            Bitmap bitmap = Utility.getImageBitmap(activity, Uri.parse(imagePath));
            if (bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    private static void addNewQuote(Activity activity, String s){
        TextInputLayout textInputLayout = new TextInputLayout(activity);
        TextInputEditText editText = new TextInputEditText(activity);
        editText.setText(s);
        //textInputLayout.setEnabled(false);
        textInputLayout.addView(editText);
        LinearLayout layout = activity.findViewById(R.id.quotesLayout);
        layout.addView(textInputLayout);
    }



    static public List<String> setQuotesView(final Activity activity, final BookInProgress book){
        List<String> quotes = new ArrayList<>();
        if(!book.getQuotes().isEmpty()){
            Log.i("MY", "ci sono quote");
            quotes.addAll(Arrays.asList(book.getQuotes().split("/")));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                quotes.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        addNewQuote(activity, s);
                    }
                });
            }
        }
        return quotes;
    }

    static public void setStartDateView(final Activity activity, final BookInProgress book){
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(book.getStartDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        ((TextInputEditText) activity.findViewById(R.id.startDateTextEdit)).setText(simpleDateFormat.format(startDate.getTime()));
        ((TextInputEditText)activity.findViewById(R.id.startDateTextEdit)).setShowSoftInputOnFocus(false);
    }
    static public void setEndDateView(final Activity activity, final BookRead book){
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(book.getEndDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
        ((TextInputEditText) activity.findViewById(R.id.endDateTextEdit)).setText(simpleDateFormat.format(endDate.getTime()));
        ((TextInputEditText)activity.findViewById(R.id.endDateTextEdit)).setShowSoftInputOnFocus(false);
    }

    public static Bitmap getImageBitmap(Activity activity, Uri currentPhotoUri){
        Log.i("MY", "Utility URI " + currentPhotoUri);
        ContentResolver resolver = activity.getApplicationContext()
                .getContentResolver();
        try {
            InputStream stream = resolver.openInputStream(currentPhotoUri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            Objects.requireNonNull(stream).close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setQuotesViewEnable(Activity activity, boolean b) {

        activity.findViewById(R.id.addNewQuoteButton).setVisibility(b?View.VISIBLE:View.INVISIBLE);
        LinearLayout lay = ((LinearLayout)activity.findViewById(R.id.quotesLayout));
        for(int i = 0; i < lay.getChildCount(); i++){
            lay.getChildAt(i).setEnabled(b);
        }
    }
}
