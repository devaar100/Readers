package aarnav100.developer.readers;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;

import aarnav100.developer.readers.Classes.*;
import aarnav100.developer.readers.Classes.Profile;

/**
 * A simple {@link Fragment} subclass.
 */
public class Book extends Fragment {
    private RequestQueue requestQueue;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    private FirebaseAuth mAuth;
    private String req="mystery",count="10";
    private ArrayList<String> nums=new ArrayList<>();
    private ProgressBar pro;
    public Book() {
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        mAuth=FirebaseAuth.getInstance();
        for(int i=10;i<=40;i++)
            nums.add(String.valueOf(i));
        View v=inflater.inflate(R.layout.fragment_book,container,false);
        pro=(ProgressBar)v.findViewById(R.id.pro_book);
        mContext=getActivity();
        mSwipeView = (SwipePlaceHolderView)v.findViewById(R.id.swipeView);
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f));
        v.findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });
        v.findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });
        requestQueue= Volley.newRequestQueue(mContext);
        getBooks();
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.search)
        {
            View t_view=((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.book_query_edit_dialog,null);
            AlertDialog dialog=new AlertDialog.Builder(mContext).setView(t_view).create();
            final Spinner spin=(Spinner)t_view.findViewById(R.id.q_spin);
            final EditText edit=(EditText)t_view.findViewById(R.id.q_title);
            edit.setText(req);
            spin.setAdapter(new ArrayAdapter<String>(
                    mContext,
                    R.layout.support_simple_spinner_dropdown_item,
                    nums
            ));
            spin.setSelection(Integer.valueOf(count)-10);
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Find", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    count=nums.get(spin.getSelectedItemPosition());
                    req=edit.getText().toString();
                    dialog.dismiss();
                    getBooks();
                }
            });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        return true;
    }
    private void getBooks(){
        pro.setVisibility(View.VISIBLE);
        StringRequest request=new StringRequest(Request.Method.GET,
                "https://www.googleapis.com/books/v1/volumes?q="+req+"+subject&fields=items(volumeInfo(infoLink,title,publisher,authors,description,imageLinks,categories),id)&maxResults="+count+"&searchBy=relevance",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pro.setVisibility(View.GONE);
                        Gson gson=new Gson();
                        JsonObject o= gson.fromJson(response,JsonObject.class);
                        JsonArray a=o.getAsJsonArray("items");
                        for(int i=0;i<a.size();i++) {
                            JsonObject obj= (JsonObject) a.get(i);
                            String id=obj.get("id").getAsString();
                            obj=obj.getAsJsonObject("volumeInfo");
                            JsonElement title=obj.get("title");
                            JsonElement link=obj.get("imageLinks");
                            JsonElement description=obj.get("description");
                            JsonElement author=obj.get("authors");
                            JsonElement publisher=obj.get("publisher");
                            JsonElement categories=obj.get("categories");
                            JsonElement infolink=obj.get("infoLink");
                            if(title==null)
                                continue;
                            if(link==null)
                                continue;
                            if(description==null)
                                continue;
                            if(author==null)
                                continue;
                            if(publisher==null)
                                continue;
                            if(categories==null)
                                continue;
                            if(infolink==null)
                                continue;
                            Profile p=new Profile(
                                    infolink.getAsString(),
                                    id,
                                    title.getAsString(),
                                    link.getAsJsonObject().get("smallThumbnail").getAsString(),
                                    description.getAsString(),
                                    author.getAsJsonArray().toString(),
                                    publisher.getAsString(),
                                    categories.getAsString());
                            mSwipeView.addView(new TinderCard(mAuth.getCurrentUser().getUid(),mContext,p,mSwipeView));

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG",error.toString());
            }
        });
        requestQueue.add(request);
    }
}
