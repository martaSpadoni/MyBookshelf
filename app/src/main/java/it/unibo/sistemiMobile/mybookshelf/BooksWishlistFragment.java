package it.unibo.sistemiMobile.mybookshelf;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.AddClasses.AddActivity;
import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.GoogleBook;
import it.unibo.sistemiMobile.mybookshelf.RecyclerView.OnItemListener;
import it.unibo.sistemiMobile.mybookshelf.RecyclerView.WishlistAdapter;
import it.unibo.sistemiMobile.mybookshelf.UpdateClasses.DetailsAndUpdateActivity;
import it.unibo.sistemiMobile.mybookshelf.ViewModel.WishlistViewModel;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

public class BooksWishlistFragment extends Fragment implements OnItemListener {
    private View view;
    private FragmentActivity activity;
    private WishlistAdapter adapter;
    private RecyclerView recyclerView;
    private WishlistViewModel model;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.books_fragment, container, false);
        Utility.setUpBottomNavigation(getActivity());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_search_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * Called when the user submits the query. This could be due to a key press on the keyboard
             * or due to pressing a submit button.
             * @param query the query text that is to be submitted
             * @return true if the query has been handled by the listener, false to let the
             * SearchView perform the default action.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /**
             * Called when the query text is changed by the user.
             * @param newText the new content of the query text field.
             * @return false if the SearchView should perform the default action of showing any
             * suggestions if available, true if the action was handled by the listener.
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }

        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        if(activity != null){
            Utility.setUpBottomNavigation(activity);
            Utility.setUpToolbar((AppCompatActivity) activity, "Wishlist");
            setRecyclerView();
            model = new ViewModelProvider(activity).get(WishlistViewModel.class);
            model.getBooks().observe(activity, new Observer<List<Book>>() {
                @Override
                public void onChanged(List<Book> books) {
                    adapter.setData(books);
                }
            });
            ((FloatingActionButton)activity.findViewById(R.id.fab_add)).setOnClickListener(Utility.getFABlistener(activity, Utility.ACTIVITY_ADD_WISH));
        }
    }
    public void startReadingBook(int position){
        model.startReadingBook(model.getBooks().getValue().get(position));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setRecyclerView(){
        recyclerView = activity.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        adapter = new WishlistAdapter(this, activity);
        recyclerView.setAdapter(adapter);
    }

    public void updateList(){
        adapter.updateList();
    }

    @Override
    public void onItemClick(int position) {
       /* Intent i = new Intent(activity, DetailsAndUpdateActivity.class);
        i.putExtra("bookID", position);
        i.putExtra("bookType", Utility.ACTIVITY_ADD_WISH);
        activity.startActivityForResult(i, Utility.UPDATE_BOOK_WISHLIST);*/
        Book clicked = (Book) adapter.getBooks().get(position);
        position = model.getBooks().getValue().indexOf(clicked);
        final Book book = model.getBooks().getValue().get(position);
        final int finalPosition = position;
        new AlertDialog.Builder(activity)
                .setTitle(getString(R.string.do_you_want_reading) + book.getTitle() + "\"?")
                .setView(getDialogView(book))
                .setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startReadingBook(finalPosition);
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private View getDialogView(Book book){
        ScrollView scrollView = new ScrollView(activity);;
        LinearLayout layout = new LinearLayout(activity);
        scrollView.addView(layout);
        layout.setScrollContainer(true);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50,30,50,10);
        LinearLayout hl = new LinearLayout(activity);
        hl.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(hl);
        ImageView bookCover = new ImageView(activity);
        Utility.setBookCoverImageView(bookCover, book, activity);
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
        Book clicked = (Book) adapter.getBooks().get(position);
        position = model.getBooks().getValue().indexOf(clicked);
        final Book book = model.getBooks().getValue().get(position);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setTitle(getString(R.string.want_delete)+ book.getTitle())
                .setMessage("Are you sure?")
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        model.removeBook(book);
                        adapter.notifyDataSetChanged();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
