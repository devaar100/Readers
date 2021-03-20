package aarnav100.developer.readers;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import aarnav100.developer.readers.Adapters.FriendAdapter;
import aarnav100.developer.readers.Adapters.ImageAdapter;
import aarnav100.developer.readers.Adapters.RecyclerAdapter;
import aarnav100.developer.readers.Classes.Person;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {
    private TextView tv1,tv2,tv3,tv4,tv5,tv6,name,place,description;
    private ImageView img;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    public static final String TAG="tag";
    private Context context;
    private ImageView fb_img,t_img,q_img,g_img;
    private String[] urls;
    private View.OnClickListener ocl;
    private GridView friendgrid;
    private RecyclerView grid;
    private ArrayList<String> books=new ArrayList<>();
    private ArrayList<String> friends=new ArrayList<>();
    private ImageAdapter friendAdapter;
    private RecyclerAdapter adapter;
    private Uri imgUri;
    private View dialogView;
    private String imgUrl;
    private AlertDialog dialog;
    private ImageView plusbook,plusfriend;
    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        final View v=inflater.inflate(R.layout.fragment_profile, container, false);
        context=getActivity();
        DrawerLayout drawerLayout=(DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView=((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.loader_dialog,null);
        dialogBuilder.setView(dialogView);
        dialog=dialogBuilder.create();
        dialog.getWindow().getDecorView().getBackground().setAlpha(0);
        dialog.setCancelable(false);
        dialog.show();
        plusbook=(ImageView)v.findViewById(R.id.addRed1);
        plusfriend=(ImageView)v.findViewById(R.id.addRed2);
        mAuth= FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        grid=(RecyclerView) v.findViewById(R.id.bookList);
        grid.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        friendgrid=(GridView)v.findViewById(R.id.friendList);
        friendAdapter=new ImageAdapter(context,friends);
        adapter=new RecyclerAdapter(context,books,mAuth.getCurrentUser());
        grid.setAdapter(adapter);
        friendgrid.setAdapter(friendAdapter);
        fb_img=(ImageView)v.findViewById(R.id.fb_img);
        q_img=(ImageView)v.findViewById(R.id.q_img);
        t_img=(ImageView)v.findViewById(R.id.t_img);
        g_img=(ImageView)v.findViewById(R.id.g_img);
        urls=new String[4];
        ocl=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="";
                switch (v.getId())
                {
                    case R.id.fb_img:
                        url=urls[0];
                        break;
                    case R.id.g_img:
                        url=urls[1];
                        break;
                    case R.id.t_img:
                        url=urls[2];
                        break;
                    case R.id.q_img:
                        url=urls[3];
                        break;

                }
                if(Patterns.WEB_URL.matcher(url).matches())
                {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                    startActivity(browserIntent);
                }
                else
                    Toast.makeText(context,"Not provided", Toast.LENGTH_SHORT).show();
            }
        };
        fb_img.setOnClickListener(ocl);
        q_img.setOnClickListener(ocl);
        g_img.setOnClickListener(ocl);
        t_img.setOnClickListener(ocl);
        View.OnClickListener ocl2=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,Social.class);
                if(v.getId()==R.id.addRed1)
                    i.putExtra("fragment","book");
                else
                    i.putExtra("fragment","friend");
                startActivity(i);
            }
        };
        plusbook.setOnClickListener(ocl2);
        plusfriend.setOnClickListener(ocl2);
        img=(ImageView)v.findViewById(R.id.profImg);
        tv1=(TextView)v.findViewById(R.id.tv1);
        tv2=(TextView)v.findViewById(R.id.tv2);
        tv3=(TextView)v.findViewById(R.id.tv3);
        tv4=(TextView)v.findViewById(R.id.tv4);
        tv5=(TextView)v.findViewById(R.id.tv5);
        tv6=(TextView)v.findViewById(R.id.tv6);
        name=(TextView)v.findViewById(R.id.profName);
        place=(TextView)v.findViewById(R.id.profPlace);
        description=(TextView)v.findViewById(R.id.profDesc);
        final FirebaseUser user=mAuth.getCurrentUser();
        final DatabaseReference mref=database.getReference("user/profile/"+user.getUid());
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if(dataSnapshot.getValue()==null)
                {
                    Map<String,Object> map=new HashMap<String,Object>();
                    map.put("name","Name");
                    map.put("location","City, Country");
                    map.put("img_url","https://firebasestorage.googleapis.com/v0/b/readers-23ea8.appspot.com/o/download.png?alt=media&token=dd1a3085-148a-49a5-ad56-551e7d0cda7b");
                    map.put("description","Description\nShort piece\nWhat you love");
                    ArrayList<String> preferences=new ArrayList<String>();
                    preferences.add("5");
                    preferences.add("5");
                    preferences.add("5");
                    preferences.add("5");
                    preferences.add("5");
                    preferences.add("5");
                    map.put("preferences",preferences);
                    ArrayList<String> urls=new ArrayList<String>();
                    urls.add("Provide Facebook link here");
                    urls.add("Provide Google link here");
                    urls.add("Provide Twitter link here");
                    urls.add("Provide Quora link here");
                    map.put("urls",urls);
                    mref.updateChildren(map);
                }
                else
                {
                    dialog.dismiss();
                   for(DataSnapshot data:dataSnapshot.getChildren())
                   {
                       String s;
                       ArrayList list;
                       switch (data.getKey())
                       {
                           case "name":
                               name.setText(data.getValue().toString());
                               break;
                           case "location":
                               place.setText(data.getValue().toString());
                               break;
                           case "img_url":
                               imgUrl=data.getValue().toString().trim();
                               Glide.with(context).load(imgUrl).placeholder(R.drawable.download).into(img);
                               break;
                           case "preferences":
                               s=String.valueOf(data.getValue());
                               s=s.substring(1,s.length()-1);
                               list=new ArrayList(Arrays.asList(s.split(",")));
                               tv1.setText(list.get(0).toString().trim());
                               tv2.setText(list.get(1).toString().trim());
                               tv3.setText(list.get(2).toString().trim());
                               tv4.setText(list.get(3).toString().trim());
                               tv5.setText(list.get(4).toString().trim());
                               tv6.setText(list.get(5).toString().trim());
                               break;
                           case "urls":
                               s=String.valueOf(data.getValue());
                               s=s.substring(1,s.length()-1);
                               list=new ArrayList(Arrays.asList(s.split(",")));
                               for(int i=0;i<list.size();i++)
                                   urls[i]=list.get(i).toString().trim();
                               break;
                           case "description":
                               description.setText(data.getValue().toString());
                               break;
                       }
                   }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                dialog.dismiss();
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
        final DatabaseReference ref2=database.getReference("user/books/"+user.getUid());
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    Map<String,Object> map=new HashMap<>();
                    map.put("img_url","https://firebasestorage.googleapis.com/v0/b/readers-23ea8.appspot.com/o/book1.jpg?alt=media&token=2e6f69d8-2626-4ee7-a5f3-26b4ad483ca5");
                    map.put("link","https://books.google.co.in/books/about/2_States.html?id=AsNSz-1gUcMC");
                    ref2.child("2 States").setValue(map);
                    map.clear();
                    map.put("img_url","https://firebasestorage.googleapis.com/v0/b/readers-23ea8.appspot.com/o/book2.jpg?alt=media&token=8d361b10-c4a0-44a7-a59f-17549ad4161d");
                    //HERE
                    map.put("link","https://books.google.co.in/books/about/Frankenstein_or_The_Modern_Prometheus.html?id=2Zc3AAAAYAAJ");
                    ref2.child("Frankenstein").setValue(map);
                    map.clear();
                    map.put("img_url","https://firebasestorage.googleapis.com/v0/b/readers-23ea8.appspot.com/o/book3.jpg?alt=media&token=966644c8-180a-41dc-b141-71ee514bf49a");
                    map.put("link","https://books.google.co.in/books/about/Harry_Potter_and_the_Cursed_Child_Parts.html?id=2sSMCwAAQBAJ&redir_esc=y");
                    ref2.child("Harry Potter").setValue(map);
                    map.clear();
                    map.put("img_url","https://firebasestorage.googleapis.com/v0/b/readers-23ea8.appspot.com/o/book4.jpg?alt=media&token=e1c03efe-8bda-482f-9e29-e02b1ccc3b42");
                    map.put("link","https://books.google.co.in/books/about/The_Force_Awakens_Star_Wars.html?id=heJhCAAAQBAJ&redir_esc=y");
                    ref2.child("Star Wars").setValue(map);
                }
                else{
                    books.clear();
                    ((ProgressBar)v.findViewById(R.id.prg1)).setVisibility(View.GONE);
                    for(DataSnapshot snap:dataSnapshot.getChildren()){
                            books.add(snap.child("link").getValue()+";;;"+snap.child("img_url").getValue()+";;;"+snap.getKey());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,databaseError.getDetails());
            }
        });
        final DatabaseReference ref3=database.getReference("user/friends/"+user.getUid());
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((ProgressBar) v.findViewById(R.id.prg2)).setVisibility(View.GONE);
                ((ImageView)v.findViewById(R.id.addfrndimg)).setVisibility(View.GONE);
                if(dataSnapshot.getValue()!=null) {
                    String s;
                    friends.clear();
                    s = String.valueOf(dataSnapshot.getValue());
                    s = s.substring(1, s.length() - 1);
                    ArrayList<?> list = new ArrayList(Arrays.asList(s.split(",")));
                    for (int i = 0; i < list.size(); i++)
                        friends.add(list.get(i).toString().trim());
                    friendAdapter.notifyDataSetChanged();
                }
                else
                    ((ImageView)v.findViewById(R.id.addfrndimg)).setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,databaseError.getDetails());
            }
        });
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.edit)
        {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogView =((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.edit_profile, null);
            dialogBuilder.setView(dialogView);
            ImageView editImg=(ImageView)dialogView.findViewById(R.id.edit_img);
            final ImageView fb_edit,g_edit,t_edit,q_edit;
            final EditText dia_name,dia_place,dia_desc,linkf,linkg,linkq,linkt;

            linkf=(EditText)dialogView.findViewById(R.id.linkf);
            linkg=(EditText)dialogView.findViewById(R.id.linkg);
            linkq=(EditText)dialogView.findViewById(R.id.linkq);
            linkt=(EditText)dialogView.findViewById(R.id.linkt);

            fb_edit=(ImageView)dialogView.findViewById(R.id.fb_edit);
            g_edit=(ImageView)dialogView.findViewById(R.id.g_edit);
            q_edit=(ImageView)dialogView.findViewById(R.id.q_edit);
            t_edit=(ImageView)dialogView.findViewById(R.id.t_edit);

            linkf.setText(urls[0]);
            linkg.setText(urls[1]);
            linkt.setText(urls[2]);
            linkq.setText(urls[3]);

            dia_name=(EditText)dialogView.findViewById(R.id.dialogName);
            dia_place=(EditText)dialogView.findViewById(R.id.dialogPlace);
            SeekBar s1,s2,s3,s4,s5,s6;
            final TextView tv21,tv22,tv23,tv24,tv25,tv26;
            s1=(SeekBar)dialogView.findViewById(R.id.s1);
            s2=(SeekBar)dialogView.findViewById(R.id.s2);
            s3=(SeekBar)dialogView.findViewById(R.id.s3);
            s4=(SeekBar)dialogView.findViewById(R.id.s4);
            s5=(SeekBar)dialogView.findViewById(R.id.s5);
            s6=(SeekBar)dialogView.findViewById(R.id.s6);
            tv21=(TextView)dialogView.findViewById(R.id.tv21);
            tv22=(TextView)dialogView.findViewById(R.id.tv22);
            tv23=(TextView)dialogView.findViewById(R.id.tv23);
            tv24=(TextView)dialogView.findViewById(R.id.tv24);
            tv25=(TextView)dialogView.findViewById(R.id.tv25);
            tv26=(TextView)dialogView.findViewById(R.id.tv26);
            dia_name.setText(name.getText());
            dia_place.setText(place.getText());
            dia_desc=(EditText)dialogView.findViewById(R.id.dialogDesc);
            dia_desc.setText(description.getText().toString());
            s1.setProgress(Integer.valueOf(tv1.getText().toString()));
            s2.setProgress(Integer.valueOf(tv2.getText().toString()));
            s3.setProgress(Integer.valueOf(tv3.getText().toString()));
            s4.setProgress(Integer.valueOf(tv4.getText().toString()));
            s5.setProgress(Integer.valueOf(tv5.getText().toString()));
            s6.setProgress(Integer.valueOf(tv6.getText().toString()));
            tv21.setText(tv1.getText());
            tv22.setText(tv2.getText());
            tv23.setText(tv3.getText());
            tv24.setText(tv4.getText());
            tv25.setText(tv5.getText());
            tv26.setText(tv6.getText());
            SeekBar.OnSeekBarChangeListener sbcl= new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    switch (seekBar.getId())
                    {
                        case R.id.s1:
                            tv21.setText(String.valueOf(progress));
                            break;
                        case R.id.s2:
                            tv22.setText(String.valueOf(progress));
                            break;
                        case R.id.s3:
                            tv23.setText(String.valueOf(progress));
                            break;
                        case R.id.s4:
                            tv24.setText(String.valueOf(progress));
                            break;
                        case R.id.s5:
                            tv25.setText(String.valueOf(progress));
                            break;
                        case R.id.s6:
                            tv26.setText(String.valueOf(progress));
                            break;
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };
            s1.setOnSeekBarChangeListener(sbcl);
            s2.setOnSeekBarChangeListener(sbcl);
            s3.setOnSeekBarChangeListener(sbcl);
            s4.setOnSeekBarChangeListener(sbcl);
            s5.setOnSeekBarChangeListener(sbcl);
            s6.setOnSeekBarChangeListener(sbcl);
            final AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.setCancelable(false);
            View.OnClickListener ocl=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId())
                    {
                        case R.id.back:
                            alertDialog.dismiss();
                            break;
                        case R.id.save:
                            FirebaseUser user=mAuth.getCurrentUser();
                            final DatabaseReference mref=database.getReference("user/profile/"+user.getUid());
                            final Map<String,Object> map=new HashMap<String,Object>();
                            map.put("name",dia_name.getText().toString());
                            map.put("location",dia_place.getText().toString());
                            map.put("description",dia_desc.getText().toString());
                            ArrayList<String> preferences=new ArrayList<String>();
                            preferences.add(tv21.getText().toString());
                            preferences.add(tv22.getText().toString());
                            preferences.add(tv23.getText().toString());
                            preferences.add(tv24.getText().toString());
                            preferences.add(tv25.getText().toString());
                            preferences.add(tv26.getText().toString());
                            map.put("preferences",preferences);
                            ArrayList<String> link_urls=new ArrayList<String>();
                            link_urls.add(linkf.getText().toString());
                            link_urls.add(linkg.getText().toString());
                            link_urls.add(linkq.getText().toString());
                            link_urls.add(linkt.getText().toString());
                            map.put("urls",link_urls);
                            map.put("img_url",imgUrl);
                            alertDialog.dismiss();
                            dialog.show();
                            if(imgUri!=null) {
                                StorageReference reference = FirebaseStorage.getInstance().getReference();
                                StorageReference riversRef = reference.child("user/image/" + user.getUid());
                                riversRef.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful())
                                            imgUrl = task.getResult().getDownloadUrl().toString();
                                        map.put("img_url",imgUrl);
                                        mref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                    Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                            mref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }});
                            break;
                        case R.id.edit_img:
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select picture"),1);
                    }
                }
            };
            ((ImageView)dialogView.findViewById(R.id.save)).setOnClickListener(ocl);
            ((ImageView)dialogView.findViewById(R.id.back)).setOnClickListener(ocl);
            editImg.setOnClickListener(ocl);
            View.OnClickListener ocl2=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linkf.setVisibility(View.GONE);
                    linkq.setVisibility(View.GONE);
                    linkt.setVisibility(View.GONE);
                    linkg.setVisibility(View.GONE);
                    switch (v.getId())
                    {
                        case R.id.fb_edit:
                            linkf.setVisibility(View.VISIBLE);
                            break;
                        case R.id.g_edit:
                            linkg.setVisibility(View.VISIBLE);
                            break;
                        case R.id.q_edit:
                            linkq.setVisibility(View.VISIBLE);
                            break;
                        case R.id.t_edit:
                            linkt.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            };
            fb_edit.setOnClickListener(ocl2);
            g_edit.setOnClickListener(ocl2);
            q_edit.setOnClickListener(ocl2);
            t_edit.setOnClickListener(ocl2);
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK&&requestCode==1)
            if(data.getData()!=null)
            {
                imgUri=data.getData();
                ((de.hdodenhof.circleimageview.CircleImageView)dialogView.findViewById(R.id.edit_img)).setImageURI(imgUri);
            }
    }
}
