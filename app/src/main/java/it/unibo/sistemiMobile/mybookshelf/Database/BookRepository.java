package it.unibo.sistemiMobile.mybookshelf.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;
import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;

public class BookRepository {
    private BookReadDAO bookReadDAO;
    private LiveData<List<BookRead>> bookReadList;
    private WishlistDAO wishlistDAO;
    private  LiveData<List<Book>> wishlist;
    private BookInProgressDAO bookInProgressDAO;
    private LiveData<List<BookInProgress>> bookInProgressList;

    public BookRepository(Application application) {
        BookDatabase db = BookDatabase.getDatabase(application);
        bookReadDAO = db.bookReadDAO();
        bookReadList = bookReadDAO.getBooksRead();
        wishlistDAO = db.wishlistDAO();
        wishlist = wishlistDAO.getWishlist();
        bookInProgressDAO = db.bookInProgressDAO();
        bookInProgressList = bookInProgressDAO.getBooksInProgress();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Book>> getWishlist(){
        return wishlist;
    }

    public LiveData<List<BookInProgress>> getBooksInProgress() {return bookInProgressList;}

    public LiveData<List<BookRead>> getBooksRead() {return bookReadList;}

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void addBookInWishlist(final Book book) {
        BookDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                wishlistDAO.addBookInWishlist(book);
            }
        });
    }
    public void addBookInProgress(final BookInProgress book) {
        BookDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                bookInProgressDAO.addBookInProgress(book);
            }
        });
    }

    public void addBookRead(final BookRead book) {
        BookDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                bookReadDAO.addBookRead(book);
            }
        });
    }

    public void removeBookInWishlist(final Book book) {
        BookDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                wishlistDAO.deleteFromWishlist(book);
            }
        });
    }
    public void removeBookInProgress(final BookInProgress book) {
        BookDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                bookInProgressDAO.deleteBookInProgress(book);
            }
        });
    }

    public void removeBookRead(final BookRead book) {
        BookDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                bookReadDAO.deleteBookRead(book);
            }
        });
    }

    public void updateBookInProgress(final BookInProgress book){
        BookDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                bookInProgressDAO.updateBookInProgress(book);
            }
        });
    }

    public void updateBookRead(final BookRead book){
        BookDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                bookReadDAO.updateBookRead(book);
            }
        });
    }

    public void setBookInProgress(final Book book){
        final BookInProgress newBook = new BookInProgress(book.getImageResource(),
                book.getTitle(), book.getAuthor(), book.getDescription());
        removeBookInWishlist(book);
        addBookInProgress(newBook);
    }

    public void setBookRead(BookInProgress book){
        final BookRead newBook = new BookRead(book);
        removeBookInProgress(book);
        addBookRead(newBook);
    }
}
