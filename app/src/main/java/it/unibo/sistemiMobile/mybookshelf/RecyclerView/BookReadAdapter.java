package it.unibo.sistemiMobile.mybookshelf.RecyclerView;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;

public class BookReadAdapter extends BookAdapter {

    private List<BookRead> bookReadList = new ArrayList<>();
    private List<BookRead> bookReadsFull =new ArrayList<>();
    private Activity activity;
    public BookReadAdapter(OnItemListener listener, Activity activity) {
        super(listener, activity);
        this.activity = activity;
    }

    @Override
    public List<? extends Book> getBooks() {
        return bookReadList;
    }

    @Override
    public void setData(List<? extends Book> books) {
        this.bookReadList.clear();
        this.bookReadList.addAll((Collection<? extends BookRead>) books);
        this.bookReadsFull.clear();
        bookReadsFull.addAll((Collection<? extends BookRead>) books);
        notifyDataSetChanged();
    }

    @Override
    public void setOptionView(BookViewHolder holder, int position) {
        Drawable drawable = activity.getDrawable(activity.getResources()
                .getIdentifier("ic_star_black_24dp", "drawable",
                        activity.getPackageName()));
        holder.optionImageView.setImageDrawable(drawable);
        holder.optionTextView.setText(String.valueOf(bookReadList.get(position).getStars()));
        if(bookReadList.get(position).getStars() <= 0){
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
            List<BookRead> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(bookReadsFull);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (BookRead item : bookReadsFull) {
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
            bookReadList.clear();
            List<?> result = (List<?>)results.values;
            for (Object object : result) {
                if (object instanceof BookRead) {
                    bookReadList.add((BookRead) object);
                }
            }
            //warn the adapter that the dare are changed after the filtering
            notifyDataSetChanged();
        }
    };
}
