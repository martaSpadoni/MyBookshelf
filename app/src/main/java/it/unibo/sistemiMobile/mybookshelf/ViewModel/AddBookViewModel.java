package it.unibo.sistemiMobile.mybookshelf.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;
import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.Database.BookRepository;

public class AddBookViewModel extends AndroidViewModel {

    private BookRepository repository;
    public AddBookViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BookRepository(application);
    }

    public void addBookInProgress(BookInProgress book){
        this.repository.addBookInProgress(book);
    }

    public void addBookRead(BookRead bookRead){
        this.repository.addBookRead(bookRead);
    }

    public void addBookInWishlist(Book book){
        this.repository.addBookInWishlist(book);
    }
}
