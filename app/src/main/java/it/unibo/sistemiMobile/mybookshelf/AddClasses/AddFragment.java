package it.unibo.sistemiMobile.mybookshelf.AddClasses;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.BookInProgress;
import it.unibo.sistemiMobile.mybookshelf.Books.BookRead;
import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.ViewModel.AddBookViewModel;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

public class AddFragment extends Fragment {
    private Activity activity;
    private TextInputEditText titleEditText;
    private TextInputLayout titleInputLayout;
    private AddBookViewModel model;
    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.add_fragment, container, false);
        if (savedInstanceState != null) {
            Log.i("MY", "ricarico " + String.valueOf(bitmap != null));
            ImageView imageView = view.findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_add_menu, menu);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Activity activity = getActivity();
        if(activity != null){
            Utility.setUpToolbar((AppCompatActivity) activity, "Add new Book");
            ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) activity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
            model = new ViewModelProvider((ViewModelStoreOwner) activity).get(AddBookViewModel.class);
            titleEditText = ((TextInputEditText)activity.findViewById(R.id.titleTextInputEditText));
            titleInputLayout = (TextInputLayout)activity.findViewById(R.id.titleTextInputLayout);
            titleEditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(Utility.inputNotEmpty(titleEditText.getText())){
                        titleInputLayout.setError(null);
                    }else{
                        titleInputLayout.setError("Title can not be empty!");
                    }
                    return false;
                }
            });
            activity.findViewById(R.id.captureButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(activity);
                }
            });
        }
    }


    public void saveBook(Activity activity){
        if(Utility.inputNotEmpty(titleEditText.getText())){
            Uri imageUri = ((AddActivity)activity).getCurrentPhotoUri();
            String imageUriString;
            if (imageUri == null){
                //if the image was not taken, i save a drawable
                imageUriString = "ic_launcher_foreground";
            } else {
                imageUriString = imageUri.toString();
            }
            Log.d("LAB", imageUriString);
            addBook(getArguments().getInt("BookType"),imageUriString,titleEditText.getText().toString(),
                    Objects.requireNonNull(((TextInputEditText)activity.
                            findViewById(R.id.authorTextInputEditText)).getText()).toString(),
                    Objects.requireNonNull(((TextInputEditText)activity.
                            findViewById(R.id.descriptionTextInputEditText)).getText()).toString());
            activity.setResult(Activity.RESULT_OK);
            activity.finish();
        }else{
            titleInputLayout.setError("Title can not be empty!");
        }
    }
    /**
     * Method called when the use click on the take picture button.
     * An intent is created
     * @param activity the activity displayed
     */
    private void dispatchTakePictureIntent(Activity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, Utility.REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Method use to set the bitmap that will be displayed on the ImageView
     * @param bitmap bitmap representing the picture taken.
     */
    void setImageView(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private void addBook(int bookType, String imageResource, String title, String author, String desc) {
        switch (bookType){
            case Utility.ACTIVITY_ADD_INPROGRESS_BOOK:
                model.addBookInProgress(new BookInProgress(imageResource, title, author, desc));
                break;
            case Utility.ACTIVITY_ADD_READ_BOOK:
                model.addBookRead(new BookRead(imageResource, title, author, desc));
                break;
            case Utility.ACTIVITY_ADD_WISH:
                model.addBookInWishlist(new Book(imageResource, title, author, desc));
                break;
            case 0:
                activity.setResult(Activity.RESULT_CANCELED);
                activity.finish();

        }
    }
}
