package aarnav100.developer.readers;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import aarnav100.developer.readers.Classes.EventClass;
import aarnav100.developer.readers.database.EventDBHelper;
import aarnav100.developer.readers.database.tables.EventsTable;


public class MyService extends IntentService {

    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        EventDBHelper helper=new EventDBHelper(this);
        EventClass evnt= EventsTable.getEvent(helper.getReadableDatabase(),intent.getStringExtra("name"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.launcher)
                .setColor(Color.RED)
                .setContentTitle("Readers : Event proximity reminder")
                .setContentText(evnt.getName()+'\n'+evnt.getLocation_name()+'\n'+evnt.getDescription());
        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
}
