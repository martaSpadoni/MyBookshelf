package it.unibo.sistemiMobile.mybookshelf.Books;

import androidx.room.Entity;

import java.util.Calendar;

@Entity
public class BookRead extends BookInProgress {
    private long endDate;
    private float stars;
    private String review;
    public BookRead(String imageResource, String title, String author, String description) {
        super(imageResource, title, author, description);
        endDate = Calendar.getInstance().getTimeInMillis();
        stars = 0;
        review = "";
    }

    public  BookRead( BookInProgress bookInProgress ){
        this(bookInProgress.getImageResource(), bookInProgress.getTitle(), bookInProgress.getAuthor(), bookInProgress.getDescription());
        setStartDate(bookInProgress.getStartDate());
        setQuotes(bookInProgress.getQuotes());

    }
    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
