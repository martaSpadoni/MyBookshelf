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

import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;

@Dao
public interface BookReadDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addBookRead(BookRead book);

    @Transaction
    @Query("SELECT * from BookRead ORDER BY id DESC")
    LiveData<List<BookRead>> getBooksRead();

    @Update
    void updateBookRead(BookRead book);

    @Delete
    void deleteBookRead(BookRead book);
}
