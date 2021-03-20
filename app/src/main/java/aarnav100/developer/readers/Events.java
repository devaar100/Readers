package aarnav100.developer.readers;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import aarnav100.developer.readers.Adapters.EventsAdapter;
import aarnav100.developer.readers.Classes.EventClass;


/**
 * A simple {@link Fragment} subclass.
 */
public class Events extends Fragment {
    //private Context context;
    private ListView listView;
    private ArrayList<EventClass> events;
    private Gson gson;
    private DatabaseReference reference;
    private EventsAdapter ea;
    private View v;
    private SwipeRefreshLayout swipeRefreshLayout;
    public Events() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_events, container, false);
        setHasOptionsMenu(true);
        //context=getActivity();
        gson=new Gson();
        listView=(ListView)v.findViewById(R.id.eventList);
        events=new ArrayList<>();
        ea=new EventsAdapter(events,getActivity());
        listView.setAdapter(ea);
        reference= FirebaseDatabase.getInstance().getReference("events");
        refresh();
        swipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.ref2);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.refresh)
            refresh();
        return true;
    }
    private void refresh(){
        ((SwipeRefreshLayout)v.findViewById(R.id.ref2)).setRefreshing(true);
        events.clear();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((SwipeRefreshLayout)v.findViewById(R.id.ref2)).setRefreshing(false);
                if(dataSnapshot.getValue()!=null) {
                    for(DataSnapshot data:dataSnapshot.getChildren())
                        events.add(gson.fromJson(gson.toJson(data.getValue()),EventClass.class));
                }
                ea.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG",databaseError.getDetails());
            }
        });
    }
}
