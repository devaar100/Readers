package aarnav100.developer.readers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import aarnav100.developer.readers.Adapters.FriendAdapter;
import aarnav100.developer.readers.Classes.Person;

public class Community extends Fragment {
    private ListView list;
    private Context context;
    private DatabaseReference reference;
    private View v;
    private ArrayList<Person> friends;
    private FriendAdapter adapter;
    private boolean is_last=false;
    private String last_key=null;
    private boolean loading=false;
    private LayoutInflater inflater;
    private SwipeRefreshLayout swipeRefreshLayout;
    public Community() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater lInflater,ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        inflater=lInflater;
        v=inflater.inflate(R.layout.fragment_community,container,false);
        context=getActivity();

        ((FloatingActionButton)v.findViewById(R.id.fab2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_SENDTO);
                i.setType("message/rfc822");
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"developer.aarnav100@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Event Name:\nEvent Latitude,Longitude\nEvent location\nEvent description");
            }
        });

        friends=new ArrayList<>();
        adapter=new FriendAdapter(context,friends);
        swipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.ref1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        list=(ListView)v.findViewById(R.id.f1list);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                View dialogView = inflater.inflate(R.layout.friend_dialog_layout, null);
                dialogBuilder.setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().getDecorView().getBackground().setAlpha(0);

                ImageView back,add;
                de.hdodenhof.circleimageview.CircleImageView frnd_img;
                TextView tv1,tv2,tv3,tv4,tv5,tv6;
                TextView name,place,desc;

                back=(ImageView)dialogView.findViewById(R.id.back);
                add=(ImageView)dialogView.findViewById(R.id.add);
                frnd_img=(de.hdodenhof.circleimageview.CircleImageView)dialogView.findViewById(R.id.edit_img);
                tv1=(TextView)dialogView.findViewById(R.id.tv1);
                tv2=(TextView)dialogView.findViewById(R.id.tv2);
                tv3=(TextView)dialogView.findViewById(R.id.tv3);
                tv4=(TextView)dialogView.findViewById(R.id.tv4);
                tv5=(TextView)dialogView.findViewById(R.id.tv5);
                tv6=(TextView)dialogView.findViewById(R.id.tv6);
                name=(TextView)dialogView.findViewById(R.id.dialogName);
                place=(TextView)dialogView.findViewById(R.id.dialogPlace);
                desc=(TextView)dialogView.findViewById(R.id.dialogDesc);

                final Person per=friends.get(position);
                tv1.setText(String.valueOf(per.getPreferenceArray()[0]));
                tv2.setText(String.valueOf(per.getPreferenceArray()[1]));
                tv3.setText(String.valueOf(per.getPreferenceArray()[2]));
                tv4.setText(String.valueOf(per.getPreferenceArray()[3]));
                tv5.setText(String.valueOf(per.getPreferenceArray()[4]));
                tv6.setText(String.valueOf(per.getPreferenceArray()[5]));
                name.setText(per.getName());
                place.setText(per.getLocation());
                desc.setText(per.getDescription());
                Glide.with(context).load(per.getImageUrl()).placeholder(R.drawable.download).into(frnd_img);
                View.OnClickListener ocl=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId()==R.id.back)
                            alertDialog.dismiss();
                        else{
                            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference database=FirebaseDatabase.getInstance().getReference("user/friends/"+user.getUid()+"/"+per.getUserId());
                            database.setValue(per.getImageUrl());
                            Toast.makeText(context, "Friend added", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                back.setOnClickListener(ocl);
                add.setOnClickListener(ocl);
                ImageView fb_img=(ImageView)dialogView.findViewById(R.id.fb_edit);
                ImageView q_img=(ImageView)dialogView.findViewById(R.id.q_edit);
                ImageView t_img=(ImageView)dialogView.findViewById(R.id.t_edit);
                ImageView g_img=(ImageView)dialogView.findViewById(R.id.g_edit);
                final String[] urls=friends.get(position).getUrls();
                View.OnClickListener ocl2=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url="";
                        switch (v.getId())
                        {
                            case R.id.fb_edit:
                                url=urls[0];
                                break;
                            case R.id.g_edit:
                                url=urls[1];
                                break;
                            case R.id.t_edit:
                                url=urls[2];
                                break;
                            case R.id.q_edit:
                                url=urls[3];
                                break;

                        }
                        if(Patterns.WEB_URL.matcher(url).matches())
                        {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            context.startActivity(browserIntent);
                        }
                        else
                            Toast.makeText(context,"Not provided", Toast.LENGTH_SHORT).show();
                    }
                };
                fb_img.setOnClickListener(ocl2);
                q_img.setOnClickListener(ocl2);
                g_img.setOnClickListener(ocl2);
                t_img.setOnClickListener(ocl2);
                alertDialog.show();
            }
        });

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(i+i1==i2&&is_last==false&&loading==false&&last_key!=null){
                    loading=true;
                    rangeRefresh();
                }
            }
        });

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        reference=database.getReference("user/profile");
        refresh();
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.refresh)
            refresh();
        return true;
    }
    private void refresh(){
        is_last=false;
        last_key=null;
        loading=false;
        swipeRefreshLayout.setRefreshing(true);
        friends.clear();
        adapter.notifyDataSetChanged();
        reference.limitToFirst(8).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeRefreshLayout.setRefreshing(false);
                Object obj=dataSnapshot.getValue();
                if(obj!=null) {
                    Gson gson = new Gson();
                    JSONObject object = null;
                    try {
                        object = new JSONObject(gson.toJson(obj));
                        Iterator<?> it = object.keys();
                        while (it.hasNext()) {
                            String key = (String) it.next();
                            JsonObject obj2 = gson.fromJson(object.get(key).toString(), JsonObject.class);
                            JsonArray array = obj2.getAsJsonArray("preferences");
                            Integer[] prefer = new Integer[6];
                            for (int i = 0; i < array.size(); i++)
                                prefer[i] = Integer.valueOf(array.get(i).getAsString());
                            JsonArray array2 = obj2.getAsJsonArray("urls");
                            String[] urls = new String[4];
                            for (int i = 0; i < array2.size(); i++)
                                urls[i] = array.get(i).getAsString();
                            Person f = gson.fromJson(obj2, Person.class);
                            f.setPreference(prefer);
                            f.setUserId(key);
                            f.setUrls(urls);
                            friends.add(f);
                            last_key=key;
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(friends.size()<8)
                    is_last=true;
                else
                    friends.remove(friends.size()-1);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG",databaseError.toString());
            }
        });
    }
    private void rangeRefresh(){
        final View load_view=inflater.inflate(R.layout.dialog_loader,null);
        list.addFooterView(load_view);
        reference.orderByKey().startAt(last_key).limitToFirst(8).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int c=0;
                ((SwipeRefreshLayout)v.findViewById(R.id.ref1)).setRefreshing(false);
                Object obj = dataSnapshot.getValue();
                if (obj != null) {
                    Gson gson = new Gson();
                    JSONObject object = null;
                    try {
                        object = new JSONObject(gson.toJson(obj));
                        Iterator<?> it = object.keys();
                        while (it.hasNext()) {
                            c++;
                            String key = (String) it.next();
                            JsonObject obj2 = gson.fromJson(object.get(key).toString(), JsonObject.class);
                            JsonArray array = obj2.getAsJsonArray("preferences");
                            Integer[] prefer = new Integer[6];
                            for (int i = 0; i < array.size(); i++)
                                prefer[i] = Integer.valueOf(array.get(i).getAsString());
                            JsonArray array2 = obj2.getAsJsonArray("urls");
                            String[] urls = new String[4];
                            for (int i = 0; i < array2.size(); i++)
                                urls[i] = array.get(i).getAsString();
                            Person f = gson.fromJson(obj2, Person.class);
                            f.setPreference(prefer);
                            f.setUserId(key);
                            f.setUrls(urls);
                            friends.add(f);
                            last_key = key;
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(c<8)
                    is_last=true;
                else
                    friends.remove(friends.size()-1);

                loading=false;
                list.removeFooterView(load_view);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG",databaseError.toString());
                list.removeFooterView(load_view);
            }
        });

    }
}
