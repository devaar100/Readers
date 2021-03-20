package aarnav100.developer.readers.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Stack;

/**
 * Created by aarnavjindal on 15/07/17.
 */

public class Person {
    private String userId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("location")
    @Expose
    private String location;

    private Integer[] preference;

    private String[] urls;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("img_url")
    @Expose
    private String imageUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPreference() {
        Stack<Integer> stack=new Stack<>();
        if(preference[0]>preference[1])
        {
            stack.push(1);
            stack.push(0);
        }
        else {
            stack.push(0);
            stack.push(1);
        }
        for(int i=2;i<6;i++)
            if(preference[i]>=preference[stack.peek()])
                stack.push(i);

        String[] genres=new String[]{"Thriller","Horror","Romantic","Mystery","Fantasy","Fiction"};
        return genres[stack.pop()]+" , "+genres[stack.pop()];
    }

    public Integer[] getPreferenceArray()
    {
        return preference;
    }

    public void setPreference(Integer[] preference) {
        this.preference = preference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }
}
