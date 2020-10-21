package it.unibo.sistemiMobile.mybookshelf.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Database.BookRepository;

public class WishlistViewModel extends AndroidViewModel {
    private BookRepository repository;
    private LiveData<List<Book>> wishlist;
    public WishlistViewModel(@NonNull Application application) {
        super(application);
        repository = new BookRepository(application);
        wishlist = repository.getWishlist();
    }

    public LiveData<List<Book>> getBooks(){
        return wishlist;
    }

    public void removeBook(Book book){
        repository.removeBookInWishlist(book);
    }

    public void startReadingBook(Book book){
        repository.setBookInProgress(book);
    }
}
