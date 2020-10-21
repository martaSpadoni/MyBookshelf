package it.unibo.sistemiMobile.mybookshelf.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;

@Dao
public interface WishlistDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addBookInWishlist(Book book);
    @Transaction
    @Query("SELECT * from Book ORDER BY id DESC")
    LiveData<List<Book>> getWishlist();

    @Delete
    void deleteFromWishlist(Book book);

}
