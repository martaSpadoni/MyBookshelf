package it.unibo.sistemiMobile.mybookshelf.RecyclerView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.unibo.sistemiMobile.mybookshelf.R;
/**
 * A ViewHolder describes an item view and the metadata about its place within the RecyclerView.
 * Every item in the list has a listener for the onclick event
 */
public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    ImageView bookCover;
    TextView titleTextView;
    TextView optionTextView;
    ImageView optionImageView;
    LinearLayout optionLayout;
    private OnItemListener itemListener;

    public BookViewHolder(@NonNull View itemView, OnItemListener listener) {
        super(itemView);
        bookCover = itemView.findViewById(R.id.book_cover);
        titleTextView = itemView.findViewById(R.id.titleTextView);
        optionImageView = itemView.findViewById(R.id.optionImageView);
        optionTextView = itemView.findViewById(R.id.optionTextView);
        optionLayout = itemView.findViewById(R.id.optionLayout);
        itemListener = listener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemListener.onItemClick(getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View v) {
        itemListener.onItemLongClick(getAdapterPosition());
        return true;
    }
}
