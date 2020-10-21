package it.unibo.sistemiMobile.mybookshelf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*the app is just started*/
        if(savedInstanceState == null){
            Utility.setUpBottomNavigation(this);
            Utility.insertFragment(this, new BooksInProgressFragment(), BooksInProgressFragment.class.getName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode == Utility.ACTIVITY_ADD_INPROGRESS_BOOK || requestCode == Utility.UPDATE_INPROGRESS_BOOK )&& resultCode == RESULT_OK){
            BooksInProgressFragment booksInProgressFragment = (BooksInProgressFragment) getSupportFragmentManager().
                                                                findFragmentByTag(BooksInProgressFragment.class.getName());
            if(booksInProgressFragment != null){
                booksInProgressFragment.updateList();
            }
        }else if((requestCode == Utility.ACTIVITY_ADD_READ_BOOK || requestCode == Utility.UPDATE_BOOK_READ) && resultCode == RESULT_OK){
            BooksReadFragment booksReadFragment = (BooksReadFragment) getSupportFragmentManager().
                                                    findFragmentByTag(BooksReadFragment.class.getName());
            if(booksReadFragment != null){
                booksReadFragment.updateList();
            }
        }else if((requestCode == Utility.ACTIVITY_ADD_WISH || requestCode == Utility.UPDATE_BOOK_WISHLIST) && resultCode == RESULT_OK){
            BooksWishlistFragment booksWishlistFragment = (BooksWishlistFragment)getSupportFragmentManager().
                    findFragmentByTag(BooksWishlistFragment.class.getName());

            if(booksWishlistFragment != null){
                booksWishlistFragment.updateList();
            }
        }else{
            Log.i("MY", "Problemi con l'ultima Activity");
        }
    }
}
