package it.unibo.sistemiMobile.mybookshelf.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.Database.BookRepository;

public class BookReadViewModel extends AndroidViewModel {
    private BookRepository repository;
    private LiveData<List<BookRead>> bookList;
    public BookReadViewModel(@NonNull Application application) {
        super(application);
        repository = new BookRepository(application);
        this.bookList = repository.getBooksRead();
    }

    public LiveData<List<BookRead>> getBooks(){
        return bookList;
    }

    //aggiungere metodo per aggiungere recensione/update

    public void deleteBook(BookRead book){
        repository.removeBookRead(book);
    }
}
