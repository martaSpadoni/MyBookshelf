package it.unibo.sistemiMobile.mybookshelf;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.RecyclerView.BookReadAdapter;
import it.unibo.sistemiMobile.mybookshelf.RecyclerView.OnItemListener;
import it.unibo.sistemiMobile.mybookshelf.UpdateClasses.DetailsAndUpdateActivity;
import it.unibo.sistemiMobile.mybookshelf.ViewModel.BookReadViewModel;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

public class BooksReadFragment extends Fragment implements OnItemListener {
    private View view;
    private FragmentActivity activity;
    private BookReadAdapter adapter;
    private RecyclerView recyclerView;
    private BookReadViewModel model;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.books_fragment, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        if(activity != null){
            Utility.setUpBottomNavigation(activity);
            Utility.setUpToolbar((AppCompatActivity) activity, getString(R.string.books_read_title));
            setRecyclerView();
            model = new ViewModelProvider(activity).get(BookReadViewModel.class);
            model.getBooks().observe(activity, new Observer<List<BookRead>>() {
                @Override
                public void onChanged(List<BookRead> books) {
                    adapter.setData(books);
                }
            });
            ((FloatingActionButton)activity.findViewById(R.id.fab_add))
                    .setOnClickListener(Utility.getFABlistener(activity, Utility.ACTIVITY_ADD_READ_BOOK));
        }
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

    private void setRecyclerView(){
        recyclerView = activity.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        adapter = new BookReadAdapter(this, activity);
        recyclerView.setAdapter(adapter);
    }

    public void updateList(){
        adapter.updateList();
    }

    @Override
    public void onItemClick(int position) {
        BookRead clicked = (BookRead) adapter.getBooks().get(position);
        position = model.getBooks().getValue().indexOf(clicked);
        Intent i = new Intent(activity, DetailsAndUpdateActivity.class);
        i.putExtra("bookID", position);
        i.putExtra("bookType", Utility.ACTIVITY_ADD_READ_BOOK);
        activity.startActivityForResult(i, Utility.UPDATE_BOOK_READ);
    }

    @Override
    public void onItemLongClick(int position) {
        BookRead clicked = (BookRead) adapter.getBooks().get(position);
        position = model.getBooks().getValue().indexOf(clicked);
        final BookRead book = model.getBooks().getValue().get(position);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setTitle(getString(R.string.want_delete)+ book.getTitle() + "?")
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        model.deleteBook(book);
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
