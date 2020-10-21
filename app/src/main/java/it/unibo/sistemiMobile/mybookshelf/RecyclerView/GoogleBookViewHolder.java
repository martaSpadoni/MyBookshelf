package it.unibo.sistemiMobile.mybookshelf.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import it.unibo.sistemiMobile.mybookshelf.R;

/**
 * A ViewHolder describes an item view and the metadata about its place within the RecyclerView.
 * Every item in the list has a listener for the onclick event
 */
public class GoogleBookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    NetworkImageView bookCover;
    TextView titleTextView;
    TextView authorTextView;
    private OnItemListener itemListener;

    public GoogleBookViewHolder(@NonNull View itemView, OnItemListener listener) {
        super(itemView);
        bookCover = itemView.findViewById(R.id.book_cover);
        titleTextView = itemView.findViewById(R.id.titleTextView);
        authorTextView = itemView.findViewById(R.id.authorTextView);
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
