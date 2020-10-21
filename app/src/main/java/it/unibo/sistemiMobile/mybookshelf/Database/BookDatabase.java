package it.unibo.sistemiMobile.mybookshelf.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;
import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;

@Database(entities = {Book.class, BookInProgress.class, BookRead.class}, version = 1, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    abstract WishlistDAO wishlistDAO();
    abstract BookInProgressDAO bookInProgressDAO();
    abstract BookReadDAO bookReadDAO();

    //Singleton instance
    private static volatile BookDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    //ExecutorService with a fixed thread pool that you will use to run database operations
    // asynchronously on a background thread.
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //get the singleton instance
    static BookDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BookDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BookDatabase.class, "book_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
