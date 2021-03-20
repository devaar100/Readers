package aarnav100.developer.readers.database.tables;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import aarnav100.developer.readers.Classes.EventClass;
import aarnav100.developer.readers.Events;

import static aarnav100.developer.readers.database.EventDBHelper.Const.*;
/**
 * Created by aarnavjindal on 30/07/17.
 */

public class EventsTable {

    public static final String TABLE_NAME = " EventsTable ";
    public static final String TAG = "events";

    public interface Columns{
        String NAME = "eventName";
        String LATLONG = "eventLatLng";
        String PLACE = "eventPlace";
        String DESCRIPTION = "eventDescription";
        String SOUND = "sound";
    }

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + LBR +
                    Columns.NAME + TYPE_TEXT + COMMA +
                    Columns.PLACE + TYPE_TEXT + COMMA +
                    Columns.LATLONG + TYPE_INT + COMMA +
                    Columns.DESCRIPTION + TYPE_TEXT +
                    Columns.SOUND + TYPE_TEXT +
                    RBR + SEMICOL ;
    public static boolean addEvent(EventClass event,SQLiteDatabase db){
        if(db.isReadOnly())
            return false;
        delEvent(event.getName(),db);
        ContentValues obj=new ContentValues();
        obj.put(Columns.NAME,event.getName());
        obj.put(Columns.PLACE,event.getLocation_name());
        obj.put(Columns.LATLONG,event.getLatlng());
        obj.put(Columns.DESCRIPTION,event.getDescription());
        db.insert(
                TABLE_NAME, null, obj
        );
        db.close();
        return true;
    }
    public static boolean delEvent(String name , SQLiteDatabase db){
        if(db.isReadOnly())
            return false;

        String whereClause = Columns.NAME + " =?";
        db.delete(
                TABLE_NAME,
                whereClause,
                new String[]{name}
        );
        return true;
    }
    public static void getAllEvents(SQLiteDatabase db,ArrayList<EventClass> list){
        list.clear();
        Cursor cur=db.query(
                TABLE_NAME,
                new String[]{Columns.NAME,Columns.PLACE,Columns.LATLONG,Columns.DESCRIPTION},
                null,null,null,null,null
        );
        int n=cur.getColumnIndex(Columns.NAME);
        int p=cur.getColumnIndex(Columns.PLACE);
        int l=cur.getColumnIndex(Columns.LATLONG);
        int d=cur.getColumnIndex(Columns.DESCRIPTION);
        for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
            EventClass event=new EventClass();
            event.setName(cur.getString(n));
            event.setLocation_name(cur.getString(p));
            event.setLatlng(cur.getString(l));
            event.setDescription(cur.getString(d));
            list.add(event);
        }
        cur.close();
    }
    public static EventClass getEvent(SQLiteDatabase db,String name){
        EventClass eventClass=new EventClass();
        Cursor cur=db.query(
                TABLE_NAME,
                new String[]{Columns.NAME,Columns.PLACE,Columns.LATLONG,Columns.DESCRIPTION},
                Columns.NAME +" = \""+name+"\"",
                null,null,null,null
        );
        cur.moveToFirst();
        eventClass.setName(name);
        eventClass.setLocation_name(cur.getString(cur.getColumnIndex(Columns.PLACE)));
        eventClass.setDescription(cur.getString(cur.getColumnIndex(Columns.DESCRIPTION)));
        cur.close();
        return eventClass;
    }
}
