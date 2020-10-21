package it.unibo.sistemiMobile.mybookshelf.RecyclerView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.AddClasses.AddActivity;
import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.Books.GoogleBook;

public class SearchAdapter extends RecyclerView.Adapter<GoogleBookViewHolder> {

    private List<GoogleBook> bookList = new ArrayList<>();
    private Activity activity;
    private OnItemListener listener;
    public SearchAdapter(OnItemListener listener, Activity activity) {
        this.listener = listener;
        this.activity = activity;
    }

    public void setData(List<GoogleBook> books) {
        this.bookList.clear();
        this.bookList.addAll(books);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public GoogleBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.google_book_card, parent, false);
        return new GoogleBookViewHolder(layoutView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull GoogleBookViewHolder holder, int position) {
        final GoogleBook currentBook = bookList.get(position);
        ((AddActivity)activity).loadImage(holder.bookCover, currentBook.getImageLink());
        holder.titleTextView.setText(currentBook.getTitle());
        holder.authorTextView.setText(currentBook.getAuthor());
    }


    @Override
    public int getItemCount() {
        return this.bookList.size();
    }
}
