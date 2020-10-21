package it.unibo.sistemiMobile.mybookshelf.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.Database.BookRepository;

public class StatisticViewModel extends AndroidViewModel {
    private BookRepository repository;
    private LiveData<List<BookRead>> booksRead;

    public StatisticViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BookRepository(application);
        this.booksRead = repository.getBooksRead();
    }

    public LiveData<List<BookRead>> getBookRead(){
        return this.booksRead;
    }
}
