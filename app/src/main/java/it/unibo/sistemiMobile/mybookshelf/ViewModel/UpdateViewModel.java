package it.unibo.sistemiMobile.mybookshelf.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;
import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.Database.BookRepository;

public class UpdateViewModel extends AndroidViewModel {
    private BookRepository repository;
    private LiveData<List<BookInProgress>> inProgressList;
    public UpdateViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BookRepository(application);
        this.inProgressList = repository.getBooksInProgress();
    }

    public LiveData<List<BookInProgress>> getBooksInProgress(){
        return inProgressList;
    }

    public void updateBookInProgress(BookInProgress book){
        this.repository.updateBookInProgress(book);
    }

    public LiveData<List<Book>> getWishlist(){
        return this.repository.getWishlist();
    }

    public LiveData<List<BookRead>> getBookRead(){
        return this.repository.getBooksRead();
    }

    public void updateBookRead(BookRead bookRead){
        this.repository.updateBookRead(bookRead);
    }

    public void markBookAsRead(BookInProgress book){
        repository.setBookRead(book);
    }

    public void markBookAsInProgress(Book book){
        repository.setBookInProgress(book);
    }
}
