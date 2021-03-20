package aarnav100.developer.readers.Classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import aarnav100.developer.readers.R;

/**
 * Created by aarnavjindal on 14/07/17.
 */

@Layout(R.layout.tinder_card_view)
public class TinderCard {
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.title)
    private TextView title;

    @View(R.id.author)
    private TextView author;

    @View(R.id.category)
    private TextView category;

    @View(R.id.publisher)
    private TextView publisher;

    @View(R.id.description)
    private TextView description;

    @View(R.id.readmore)
    private TextView readmore;

    @View(R.id.add2Fav)
    private Button add2fav;

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private String userId;

    public TinderCard(String userId,Context context, Profile profile, SwipePlaceHolderView swipeView) {
        this.mContext = context;
        this.mProfile = profile;
        this.mSwipeView = swipeView;
        this.userId = userId;
    }

    @Resolve
    private void onResolve(){
        Glide.with(mContext).load(mProfile.getImageUrl()).into(profileImageView);
        title.setText(mProfile.getTitle());
        author.setText(mProfile.getTitle());
        category.setText(mProfile.getCategories());
        publisher.setText(mProfile.getPublisher());
        description.setText(mProfile.getDescription());
        readmore.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(mProfile.getInfoLink()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });
        add2fav.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                final DatabaseReference mref=database.getReference("user/books/"+userId+"/"+mProfile.getTitle());
                Map<String,Object> map=new HashMap<>();
                map.put("img_url",mProfile.getImageUrl());
                map.put("link",mProfile.getInfoLink());
                mref.setValue(map);
                Toast.makeText(mContext, "Book added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");
        //Add review
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }
}