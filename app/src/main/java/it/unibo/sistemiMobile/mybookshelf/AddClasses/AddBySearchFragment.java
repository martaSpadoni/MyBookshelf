package it.unibo.sistemiMobile.mybookshelf.AddClasses;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;
import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.Books.GoogleBook;
import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.RecyclerView.OnItemListener;
import it.unibo.sistemiMobile.mybookshelf.RecyclerView.SearchAdapter;
import it.unibo.sistemiMobile.mybookshelf.Utilities.InternetUtilities;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;
import it.unibo.sistemiMobile.mybookshelf.ViewModel.AddBookViewModel;

public class AddBySearchFragment extends Fragment implements OnItemListener {

    private FragmentActivity activity;
    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private AddBookViewModel model;
    private List<GoogleBook> list;
    private String lastQuery;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if(savedInstanceState != null){
            lastQuery = savedInstanceState.getString("query");
            if(getActivity() != null){
                ((AddActivity)getActivity()).searchBook(lastQuery);
            }
        }
        return inflater.inflate(R.layout.add_by_search_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("query", lastQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_search_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!InternetUtilities.getIsNetworkConnected()){
                    InternetUtilities.getSnackbar().show();
                }else {
                    lastQuery = query;
                    ((AddActivity) activity).searchBook(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.activity = getActivity();
        if(activity != null){
            Utility.setUpToolbar((AppCompatActivity) getActivity(), "");
            InternetUtilities.makeSnackbar(activity);
            setRecyclerView();
            model = new ViewModelProvider(activity).get(AddBookViewModel.class);

        }
    }

    public void setResult(List<GoogleBook> books){
        list = books;
        adapter.setData(books);
    }

    private void setRecyclerView(){
        recyclerView = activity.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        adapter = new SearchAdapter(this, activity);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(final int position) {
        final GoogleBook book = this.list.get(position);

        new AlertDialog.Builder(activity)
                .setTitle(getString(R.string.do_you_want_add) + book.getTitle() + "\" ?")
                .setView(getDialogView(book))
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String imageUri = "ic_launcher_foreground";
                        Log.i("MY", "book image link: " + book.getImageLink());
                        if(!book.getImageLink().startsWith("ic_")) {
                            ((AddActivity) activity).addNewGoogleBook(book);
                        }else{
                            addBook(getArguments().getInt("BookType"), new Book(book.getImageLink(),
                                    book.getTitle(), book.getAuthor(), book.getDescription()));
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private View getDialogView(GoogleBook book){
        ScrollView scrollView = new ScrollView(activity);;
        LinearLayout layout = new LinearLayout(activity);
        scrollView.addView(layout);
        layout.setScrollContainer(true);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50,30,50,10);
        LinearLayout hl = new LinearLayout(activity);
        hl.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(hl);
        NetworkImageView bookCover = new NetworkImageView(activity);
        ((AddActivity) activity).loadImage(bookCover, book.getImageLink());
        hl.addView(bookCover);
        LinearLayout tvl = new LinearLayout(activity);
        tvl.setOrientation(LinearLayout.VERTICAL);
        tvl.setPadding(50,0,50,0);
        hl.addView(tvl);
        TextView titleTextView = new TextView(activity);
        titleTextView.setText(book.getTitle());
        titleTextView.setTextSize(16f);
        titleTextView.setTextColor(Color.BLACK);
        tvl.addView(titleTextView);
        TextView authorTextView = new TextView(activity);
        authorTextView.setText(book.getAuthor());
        tvl.addView(authorTextView);
        if(!book.getDescription().isEmpty()){
            TextView descTextView = new TextView(activity);
            descTextView.setText(book.getDescription());
            layout.addView(descTextView);
        }
        return scrollView;
    }

    @Override
    public void onItemLongClick(int position) {

    }

    public void addBook(int bookType, Book book) {
        switch (bookType){
            case Utility.ACTIVITY_ADD_INPROGRESS_BOOK:
                model.addBookInProgress(new BookInProgress(book.getImageResource(), book.getTitle(), book.getAuthor(), book.getDescription()));
                break;
            case Utility.ACTIVITY_ADD_READ_BOOK:
                model.addBookRead(new BookRead(book.getImageResource(), book.getTitle(), book.getAuthor(), book.getDescription()));
                break;
            case Utility.ACTIVITY_ADD_WISH:
                model.addBookInWishlist(book);
                break;
            case 0:
                activity.setResult(Activity.RESULT_CANCELED);
                activity.finish();

        }

        activity.setResult(Activity.RESULT_OK);
        activity.finish();
    }

}
