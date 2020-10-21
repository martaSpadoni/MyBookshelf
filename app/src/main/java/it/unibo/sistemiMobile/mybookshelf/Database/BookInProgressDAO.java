package it.unibo.sistemiMobile.mybookshelf.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;

@Dao
public interface BookInProgressDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addBookInProgress(BookInProgress book);

    @Transaction
    @Query("SELECT * from BookInProgress ORDER BY id DESC")
    LiveData<List<BookInProgress>> getBooksInProgress();

    @Transaction
    @Query("SELECT * from BookInProgress WHERE id LIKE:id")
    BookInProgress getBook(int id);

    @Update
    void updateBookInProgress(BookInProgress book);

    @Delete
    void deleteBookInProgress(BookInProgress book);

}
