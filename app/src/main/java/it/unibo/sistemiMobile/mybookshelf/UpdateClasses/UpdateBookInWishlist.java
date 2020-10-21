package it.unibo.sistemiMobile.mybookshelf.UpdateClasses;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.ViewModel.UpdateViewModel;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

public class UpdateBookInWishlist extends Fragment {
    private int bookID;
    private Book book;
    private FragmentActivity activity;
    private UpdateViewModel model;

    public UpdateBookInWishlist(int bookID) {
        this.bookID = bookID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.wishlist_book_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        if(activity != null){
            model = new ViewModelProvider(activity).get(UpdateViewModel.class);
            model.getWishlist().observe(activity, new Observer<List<Book>>() {
                @Override
                public void onChanged(List<Book> wishlist) {
                    if(wishlist.size() > bookID){
                        Log.i("MY", "bookID: " + bookID);
                        book = wishlist.get(bookID);
                        Utility.setBookDetailsView(activity, book);
                    }
                }
            });

            activity.findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((SwitchMaterial)activity.findViewById(R.id.startReadingswitch)).isChecked()){
                        model.markBookAsInProgress(book);
                    }
                    activity.setResult(Activity.RESULT_OK);
                    activity.finish();
                }
            });
        }
    }

}
