package aarnav100.developer.readers.Adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import aarnav100.developer.readers.Classes.EventClass;
import aarnav100.developer.readers.R;
import aarnav100.developer.readers.database.EventDBHelper;
import aarnav100.developer.readers.database.tables.EventsTable;

/**
 * Created by aarnavjindal on 31/07/17.
 */

public class Events2Adapter extends BaseAdapter {
    private ArrayList<EventClass> events;
    private Context context;
    private EventDBHelper dbHelper;
    private Bundle savedInstanceState;
    private GeofencingClient geofencingClient;

    public Events2Adapter(ArrayList<EventClass> events, Context context,EventDBHelper dbHelper,Bundle savedInstanceState) {
        this.events = events;
        this.context = context;
        this.dbHelper=dbHelper;
        this.savedInstanceState=savedInstanceState;
        geofencingClient= LocationServices.getGeofencingClient(context);
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public EventClass getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater li=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final EventClass event=getItem(position);
        EventHolder holder;
        if(convertView==null)
        {
            holder=new EventHolder();
            convertView=li.inflate(R.layout.event_view2,null);
            holder.delEvent=(ImageView)convertView.findViewById(R.id.delEvent);
            holder.settEvent=(ImageView)convertView.findViewById(R.id.settingsEvent);
            holder.name=(TextView)convertView.findViewById(R.id.eventTitle);
            holder.loc=(TextView)convertView.findViewById(R.id.eventLoc);
            holder.desc=(TextView) convertView.findViewById(R.id.eventDesc);
            holder.mapView=(MapView)convertView.findViewById(R.id.mapView);
            convertView.setTag(holder);
        }
        else
            holder=(EventHolder)convertView.getTag();

        holder.name.setText(event.getName());
        holder.loc.setText(event.getLocation_name());
        holder.desc.setText(event.getDescription());
        View.OnClickListener ocl=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.delEvent){
                    ArrayList<String> l=new ArrayList<>();
                    l.add(event.getName());
                    geofencingClient.removeGeofences(l);
                    EventsTable.delEvent(event.getName(),dbHelper.getWritableDatabase());
                    EventsTable.getAllEvents(dbHelper.getReadableDatabase(),events);
                    notifyDataSetChanged();
                }
                else{
                    Intent i=new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Readers Book Event");
                    i.putExtra(Intent.EXTRA_TEXT ,event.getName()+'\n'+event.getLocation_name()+'\n'+event.getDescription()+'\n'+event.getLatlng()+'\n'+"Hope to you see there !!!");
                    context.startActivity(Intent.createChooser(i,"Share event detail"));
                }
            }
        };
        holder.delEvent.setOnClickListener(ocl);
        holder.settEvent.setOnClickListener(ocl);// needed to get the map to display immediately
        holder.mapView.onCreate(savedInstanceState);
        holder.mapView.onResume();
        holder.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker))
                        .position(new LatLng(Double.valueOf(event.getLatlng().split(",")[0]),Double.valueOf(event.getLatlng().split(",")[1])))
                        .draggable(false).visible(true));
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(event.getLatlng().split(",")[0]),Double.valueOf(event.getLatlng().split(",")[1])));
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(20);
                map.moveCamera(center);
                map.animateCamera(zoom);
            }
        });
        //Log.i("TAG",event.getName()+" "+event.getDescription()+" "+event.getLatlng());
        return convertView;
    }

    private class EventHolder
    {
        MapView mapView;
        ImageView delEvent,settEvent;
        TextView name,desc,loc;
    }
}
