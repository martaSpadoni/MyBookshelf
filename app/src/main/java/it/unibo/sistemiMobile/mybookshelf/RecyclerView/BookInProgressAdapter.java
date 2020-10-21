package it.unibo.sistemiMobile.mybookshelf.RecyclerView;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;

public class BookInProgressAdapter extends BookAdapter {

    private List<BookInProgress> bookInProgress = new ArrayList<>();
    private List<BookInProgress> bookList = new ArrayList<>();
    private Activity activity;

    public BookInProgressAdapter(OnItemListener listener, Activity activity) {
        super(listener, activity);
        this.activity = activity;
    }

    @Override
    public List<? extends Book> getBooks() {
        return bookInProgress;
    }

    @Override
    public void setData(List<? extends Book> books) {
        this.bookInProgress.clear();
        this.bookInProgress.addAll((Collection<? extends BookInProgress>) books);
        this.bookList.clear();
        this.bookList.addAll((Collection<? extends BookInProgress>) books);
        notifyDataSetChanged();
    }

    @Override
    public void setOptionView(BookViewHolder holder, int position) {
        Drawable drawable = activity.getDrawable(activity.getResources()
                .getIdentifier("ic_bookmark_black_24dp", "drawable",
                        activity.getPackageName()));
        holder.optionImageView.setImageDrawable(drawable);
        holder.optionTextView.setText(String.valueOf(bookInProgress.get(position).getBookmark()));
        if(bookInProgress.get(position).getBookmark() <= 0){
            holder.optionLayout.setVisibility(View.INVISIBLE);
        }else{
            holder.optionLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Filter getFilter() {
        return bookFilter;
    }

    private Filter bookFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BookInProgress> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(bookList);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BookInProgress item : bookList) {
                    if (item.getTitle().toLowerCase().contains(filterPattern) ||
                            item.getAuthor().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            bookInProgress.clear();
            List<?> result = (List<?>)results.values;
            for (Object object : result) {
                if (object instanceof BookInProgress) {
                    bookInProgress.add((BookInProgress) object);
                }
            }
            //warn the adapter that the dare are changed after the filtering
            notifyDataSetChanged();
        }
    };
}
