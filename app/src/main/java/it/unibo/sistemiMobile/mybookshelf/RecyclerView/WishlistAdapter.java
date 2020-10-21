package it.unibo.sistemiMobile.mybookshelf.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.widget.CompoundButton;
import android.widget.Filter;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.BooksWishlistFragment;
import it.unibo.sistemiMobile.mybookshelf.R;

public class WishlistAdapter extends BookAdapter {

    private List<Book> wishlist = new ArrayList<>();
    private List<Book> fullList = new ArrayList<>();
    private Activity activity;
    public WishlistAdapter(OnItemListener listener, Activity activity) {
        super(listener, activity);
        this.activity = activity;
    }

    @Override
    public List<? extends Book> getBooks() {
        return wishlist;
    }

    @Override
    public void setData(List<? extends Book> books) {
        this.wishlist.clear();
        this.wishlist.addAll(books);
        this.fullList.clear();
        this.fullList.addAll(books);
        notifyDataSetChanged();
    }

    @Override
    public void setOptionView(BookViewHolder holder, final int position) {

        holder.optionLayout.removeAllViewsInLayout();
        SwitchMaterial startReadingSwitch = new SwitchMaterial(activity);
        startReadingSwitch.setPadding(40,0,0,0);
        startReadingSwitch.setId(R.id.startReadingswitch);
        startReadingSwitch.setText(R.string.start_reading);
        startReadingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    new AlertDialog.Builder(activity)
                            .setMessage(R.string.want_start_this)
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    buttonView.setChecked(false);
                                    dialog.dismiss();
                                }
                            }).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BooksWishlistFragment fragment = (BooksWishlistFragment) ((FragmentActivity)activity).getSupportFragmentManager()
                                    .findFragmentByTag(BooksWishlistFragment.class.getName());
                            if(fragment != null){
                                fragment.startReadingBook(position);
                            }
                        }
                    }).show();
                }
            }
        });
        holder.optionLayout.addView(startReadingSwitch);
    }

    @Override
    public Filter getFilter() {
        return bookFilter;
    }

    private Filter bookFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Book> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(fullList);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Book item : fullList) {
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
            wishlist.clear();
            List<?> result = (List<?>)results.values;
            for (Object object : result) {
                if (object instanceof Book) {
                    wishlist.add((Book) object);
                }
            }
            //warn the adapter that the dare are changed after the filtering
            notifyDataSetChanged();
        }
    };
}
