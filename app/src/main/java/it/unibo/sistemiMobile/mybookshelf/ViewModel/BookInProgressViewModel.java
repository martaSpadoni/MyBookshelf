package it.unibo.sistemiMobile.mybookshelf.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;
import it.unibo.sistemiMobile.mybookshelf.Database.BookRepository;

public class BookInProgressViewModel extends AndroidViewModel {

    private BookRepository repository;
    private LiveData<List<BookInProgress>> bookList;
    public BookInProgressViewModel(@NonNull Application application) {
        super(application);
        repository = new BookRepository(application);
        this.bookList = repository.getBooksInProgress();
    }

    public LiveData<List<BookInProgress>> getBooks(){
        return bookList;
    }

    public void setBookList(LiveData<List<BookInProgress>> bookList) {
        this.bookList = bookList;
    }

    public void deleteBook(BookInProgress book){
        repository.removeBookInProgress(book);
    }
}
