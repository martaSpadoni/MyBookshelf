package it.unibo.sistemiMobile.mybookshelf.Books;

import androidx.room.Entity;

import java.util.Calendar;
@Entity
public class BookInProgress extends Book {
    private long startDate;
    private String quotes;
    private int bookmark;
    public BookInProgress(String imageResource, String title, String author, String description) {
        super(imageResource, title, author, description);
        startDate = Calendar.getInstance().getTimeInMillis();
        bookmark = 0;
        quotes="";
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getQuotes() {
        return quotes;
    }

    public void setQuotes(String quotes) {
        this.quotes = quotes;
    }

    public int getBookmark() {
        return bookmark;
    }

    public void setBookmark(int bookmark) {
        this.bookmark = bookmark;
    }
}
