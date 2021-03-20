package aarnav100.developer.readers.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import aarnav100.developer.readers.Classes.EventClass;
import aarnav100.developer.readers.MyService;
import aarnav100.developer.readers.R;
import aarnav100.developer.readers.database.EventDBHelper;
import aarnav100.developer.readers.database.tables.EventsTable;

/**
 * Created by aarnavjindal on 16/07/17.
 */

public class EventsAdapter extends BaseAdapter {


    private GeofencingClient geofencingClient;
    private ArrayList<EventClass> events;
    private Activity activity;
    private EventDBHelper dbHelper;

    public EventsAdapter(ArrayList<EventClass> events, Activity activity) {
        this.events = events;
        this.activity = activity;
        dbHelper = new EventDBHelper(activity);
        geofencingClient = LocationServices.getGeofencingClient(activity);

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
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final EventClass event = getItem(position);
        EventHolder holder;
        if (convertView == null) {
            holder = new EventHolder();
            convertView = li.inflate(R.layout.event_view, null);
            holder.img = (ImageView) convertView.findViewById(R.id.eventImg);
            holder.name = (TextView) convertView.findViewById(R.id.eventName);
            holder.loc = (TextView) convertView.findViewById(R.id.eventDist);
            holder.btn = (Button) convertView.findViewById(R.id.eventBtn);
            convertView.setTag(holder);
        } else
            holder = (EventHolder) convertView.getTag();

        Glide.with(activity).load(event.getImg().trim()).placeholder(R.drawable.c3).into(holder.img);
        holder.name.setText(event.getName());
        holder.loc.setText(event.getLocation_name());
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                View dialogView = li.inflate(R.layout.event_dialog_view, null);
                dialogBuilder.setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();
                MapsInitializer.initialize(activity);
                Button btn = (Button) dialogView.findViewById(R.id.addEvent);
                ImageView cancel = (ImageView) dialogView.findViewById(R.id.cancel);
                View.OnClickListener ocl = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == R.id.cancel)
                            alertDialog.dismiss();
                        else {
                            EventsTable.addEvent(event,dbHelper.getWritableDatabase());
                            Toast.makeText(activity, "Added to list", Toast.LENGTH_SHORT).show();
                            ArrayList<String> l = new ArrayList<String>();
                            l.add(event.getName());
                            geofencingClient.removeGeofences(l);
                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                                        1);
                                if(ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION))
                                    Toast.makeText(activity,"Kindly enable location services to get location based proximity alerts",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Intent i=new Intent(activity,MyService.class);
                            i.putExtra("name",event.getName());
                            String[] temp=event.getLatlng().split(",");
                            geofencingClient.addGeofences(new GeofencingRequest.Builder().
                                    addGeofence(new Geofence.Builder()
                                            .setRequestId(event.getName())
                                            .setCircularRegion(Double.valueOf(temp[0]),Double.valueOf(temp[1]), 1000)
                                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                                            .build())
                                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                                    .build(), PendingIntent.getService(activity,0,i,PendingIntent.FLAG_UPDATE_CURRENT)
                            );
                        }
                    }
                };
                btn.setOnClickListener(ocl);
                cancel.setOnClickListener(ocl);
                MapView mapView=(MapView)dialogView.findViewById(R.id.mapView);
                mapView.onCreate(alertDialog.onSaveInstanceState());
                mapView.onResume();// needed to get the map to display immediately
                mapView.getMapAsync(new OnMapReadyCallback() {
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
                alertDialog.getWindow().getDecorView().getBackground().setAlpha(0);
                alertDialog.show();
            }
        });
        return convertView;
    }

    private class EventHolder
    {
        ImageView img;
        TextView name,loc;
        Button btn;
    }
}
