package it.unibo.sistemiMobile.mybookshelf.AddClasses;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import it.unibo.sistemiMobile.mybookshelf.Books.Book;
import it.unibo.sistemiMobile.mybookshelf.Books.GoogleBook;
import it.unibo.sistemiMobile.mybookshelf.R;
import it.unibo.sistemiMobile.mybookshelf.Utilities.InternetUtilities;
import it.unibo.sistemiMobile.mybookshelf.Utilities.Utility;

public class AddActivity extends AppCompatActivity {

    private Uri currentPhotoUri;
    private Bitmap imageBitmap;
    private int bookType;

    RequestQueue requestQueue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        requestQueue = Volley.newRequestQueue(this);

        if(savedInstanceState == null) {
            Log.i("MY", "in activity saveInstace null");
            bookType = getIntent().getIntExtra("bookType", 0);
            switch (getIntent().getIntExtra("addType", -1)){
                case Utility.ADD_MANUALLY:
                    AddFragment fragment = new AddFragment();
                    Bundle b = new Bundle();
                    b.putInt("BookType", bookType);
                    fragment.setArguments(b);
                    Utility.insertFragment(this, fragment, AddFragment.class.getName());
                    break;
                case Utility.ADD_BY_SEARCH:
                    AddBySearchFragment f = new AddBySearchFragment();
                    Bundle b1 = new Bundle();
                    b1.putInt("BookType",bookType);
                    f.setArguments(b1);
                    Utility.insertFragment(this, f, AddBySearchFragment.class.getName());
                    break;
                default:
                    setResult(RESULT_CANCELED);
                    finish();
                    break;
            }

        }else{
            currentPhotoUri = savedInstanceState.getParcelable("currentPhotoUri");
            imageBitmap = savedInstanceState.getParcelable("image");
            AddFragment fragment = (AddFragment) getSupportFragmentManager().findFragmentByTag("addFragment");
            if (fragment != null) {
                Log.i("MY", "in activity ricarico e salvo l'image nel fragment " + String.valueOf(imageBitmap != null));
                fragment.setImageView(imageBitmap);
            }
        }
    }

    /**
     * Called after the picture is taken
     * @param requestCode requestcode od the intent
     * @param resultCode result of the intent
     * @param data data of the intent (picture)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utility.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
                try {
                    if (imageBitmap != null) {
                        //method to save the image in the gallery of the device
                        saveImage(imageBitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.d("LAB", String.valueOf(currentPhotoUri));
            // Load a specific media item, and show it in the ImageView
            Bitmap bitmap = Utility.getImageBitmap(this, currentPhotoUri);
            if (bitmap != null){
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Method called to save the image taken as a file in the gallery
     * @param bitmap the image taken
     * @throws IOException if there are some issue with the creation of the image file
     */
    public String saveImage(Bitmap bitmap) throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(new Date());
        String name = "JPEG_" + timeStamp + "_.png";

        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name + ".jpg");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        currentPhotoUri = imageUri;
        OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));

        //for the jpeg quality, it goes from 0 to 100
        //for the png one, the quality is ignored
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        if (fos != null) {
            fos.close();
        }
        return currentPhotoUri.toString();
    }

    //get the URI of the photo
    Uri getCurrentPhotoUri(){
        return currentPhotoUri;
    }

    /**
     * Method called before a configuration change
     * @param outState bundle where saved elements are stored
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("MY", "in activity salvo instance");
        //save the bitmap and the uri of the image taken
        outState.putParcelable("image", imageBitmap);
        outState.putParcelable("currentPhotoUri", currentPhotoUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getIntent().getIntExtra("addType",-1) == Utility.ADD_BY_SEARCH) {
            Log.i("MY", "registro Callback");
            InternetUtilities.registerNetworkCallback(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(getIntent().getIntExtra("addType",-1) == Utility.ADD_BY_SEARCH) {
            if (requestQueue != null) {
                requestQueue.cancelAll(InternetUtilities.GB_REQUEST_TAG);
            }
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

            if (connectivityManager != null) {
                connectivityManager.unregisterNetworkCallback(InternetUtilities.getNetworkCallback());
            }
        }
    }



    public void searchBook(String query){
        String queryFormatted = query.replace(" ", "%20");
        Log.i("MY", queryFormatted);
        String url = "https://www.googleapis.com/books/v1/volumes?q="+queryFormatted;
        Log.i("MY", url);

        final AddBySearchFragment fragment = (AddBySearchFragment) getSupportFragmentManager()
                .findFragmentByTag(AddBySearchFragment.class.getName());
        // Request a jsonObject response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<GoogleBook> bookList = new ArrayList<>();
                        try {
                            Log.i("MY", "onResponse");
                            JSONArray books = (JSONArray) response.get("items");
                            for(int i = 0; i < books.length(); i++){
                              final GoogleBook b = new GoogleBook(books.getJSONObject(i).getJSONObject("volumeInfo"));
                              if(b.isAvailable()) {
                                  bookList.add(b);
                              }
                                Log.i("MY", bookList.get(bookList.size()-1).getTitle());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(fragment != null){
                            fragment.setResult(bookList);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MY", error.toString());
                    }
                });

        jsonObjectRequest.setTag(InternetUtilities.GB_REQUEST_TAG);
        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }

    public void loadImage(NetworkImageView bookCover, String url){
        ImageLoader imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(
                    10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        bookCover.setImageUrl(url, imageLoader);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.saveButton:
                AddFragment fragment = (AddFragment) getSupportFragmentManager().findFragmentByTag(AddFragment.class.getName());
                if(fragment != null){
                    fragment.saveBook(this);
                }
                break;
            default:
                onBackPressed();
                break;
        }
        return true;
    }

    public void addNewGoogleBook(final GoogleBook book){
        ImageRequest imageRequest = new ImageRequest(book.getImageLink(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.i("MY", "ON response");
                if (response != null) {
                    try {
                        Log.i("MY", "sto per salvare l'immagine");
                        saveImage(response);
                        book.setImageLink(getCurrentPhotoUri().toString());
                        AddBySearchFragment fragment = (AddBySearchFragment) getSupportFragmentManager().findFragmentByTag(AddBySearchFragment.class.getName());
                        if(fragment != null){
                            fragment.addBook(bookType, new Book(book.getImageLink(), book.getTitle(), book.getAuthor(), book.getDescription()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("MY", "errore nel salvataggio");
                    }
                }
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("MY", "download fallito");
            }
        });

        requestQueue.add(imageRequest);
    }
}
