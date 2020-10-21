package it.unibo.sistemiMobile.mybookshelf.RecyclerView;

/**
 * Interface to manage the listener for the click on an element of the RecyclerView
 */
public interface OnItemListener {
    void onItemClick(int position);
    void onItemLongClick(int position);
}