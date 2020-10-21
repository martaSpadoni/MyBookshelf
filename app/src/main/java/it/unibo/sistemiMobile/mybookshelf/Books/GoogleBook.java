package it.unibo.sistemiMobile.mybookshelf.Books;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleBook {
    private String title = "";
    private String author = "";
    private String description = "";
    private String imageLink = "";
    private Bitmap bitmap;
    private boolean available = true;

    public GoogleBook(JSONObject book) {
        try {
            title = book.get("title").toString();
        } catch (JSONException e) {
            e.printStackTrace();
            available = false;
        }

        try {
            JSONArray autori = book.getJSONArray("authors");
            for(int i = 0; i < autori.length(); i++){
                author += autori.get(i).toString() + " ";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            description = book.get("description").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String linkHttp = book.getJSONObject("imageLinks").get("thumbnail").toString();
            imageLink = linkHttp.substring(0,4) + "s" + linkHttp.substring(4);
            Log.i("MY", "imageLink: "+ imageLink);
        } catch (JSONException e) {
            e.printStackTrace();
            imageLink = "ic_launcher_foreground";
        }
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isAvailable() {
        return available;
    }
}
