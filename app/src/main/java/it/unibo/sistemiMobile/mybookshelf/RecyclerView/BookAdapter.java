package it.unibo.sistemiMobile.mybookshelf.RecyclerView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

/**
 * Adapter linked to the RecyclerView of the homePage, that extends a custom ViewHolder and
 * implements Filterable for the SearchView
 */
public abstract class BookAdapter extends RecyclerView.Adapter<BookViewHolder> implements Filterable {

    private OnItemListener listener;
    private Activity activity;

    public BookAdapter(OnItemListener listener, Activity activity) {
        this.listener = listener;
        this.activity = activity;
    }

    abstract public List<? extends Book> getBooks();
    public abstract void setData(List<? extends Book> books);
    public abstract void setOptionView(BookViewHolder holder, int position);
    /**
     *
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param parent ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false);
        return new BookViewHolder(layoutView, listener);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect
     * the item at the given position.
     *
     * @param holder ViewHolder which should be updated to represent the contents of the item at
     *               the given position in the data set.
     * @param position position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        final Book currentBook = getBooks().get(position);
        String imagePath = currentBook.getImageResource();
        if (imagePath.contains("ic_")) {
            Drawable drawable = activity.getDrawable(activity.getResources()
                    .getIdentifier(currentBook.getImageResource(), "drawable",
                            activity.getPackageName()));
            holder.bookCover.setImageDrawable(drawable);
        } else {
            Bitmap bitmap = Utility.getImageBitmap(activity, Uri.parse(imagePath));
            if (bitmap != null){
                holder.bookCover.setImageBitmap(bitmap);
            }
        }
        setOptionView(holder, position);
        holder.titleTextView.setText(currentBook.getTitle());
    }

    @Override
    public int getItemCount() {
        return getBooks().size();
    }

    public void updateList(){
        notifyDataSetChanged();
    }

    abstract public Filter getFilter();
}
