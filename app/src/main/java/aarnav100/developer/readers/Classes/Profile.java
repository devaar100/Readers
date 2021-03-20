package aarnav100.developer.readers.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aarnavjindal on 14/07/17.
 */

public class Profile {
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("authors")
    @Expose
    private String authors;

    @SerializedName("publisher")
    @Expose
    private String publisher;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("categories")
    @Expose
    private String categories;

    @SerializedName("infoLink")
    @Expose
    private String infoLink;

    public Profile(String infoLink,String id,String title, String imageUrl, String description, String authors, String publisher,String categories) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.authors = authors;
        this.publisher = publisher;
        this.id=id;
        this.categories=categories;
        this.infoLink=infoLink;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCategories() {
        return categories;
    }

    public String getInfoLink() {
        return infoLink;
    }
}