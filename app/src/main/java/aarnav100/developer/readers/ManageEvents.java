package aarnav100.developer.readers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;

import aarnav100.developer.readers.Adapters.Events2Adapter;
import aarnav100.developer.readers.Classes.EventClass;
import aarnav100.developer.readers.database.EventDBHelper;
import aarnav100.developer.readers.database.tables.EventsTable;

public class ManageEvents extends AppCompatActivity {
    private ListView list;
    private Events2Adapter adapter;
    private ArrayList<EventClass> eventClasses=new ArrayList<>();
    private EventDBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.headColor));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.headColor)));
        final SharedPreferences spref=getSharedPreferences("MYPREF",MODE_PRIVATE);
        if(spref.getBoolean("show_dialog",true)){
            final View v=((LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.info_dialog,null);
            final AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setView(v)
                    .setCancelable(false)
                    .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(((RadioButton)v.findViewById(R.id.r_btn)).isChecked())
                                spref.edit().putBoolean("show_dialog",false).apply();
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
        dbHelper=new EventDBHelper(this);
        list=(ListView)findViewById(R.id.list);
        adapter=new Events2Adapter(eventClasses,this,dbHelper,savedInstanceState);
        list.setAdapter(adapter);
        ((FloatingActionButton)findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageEvents.this,Social.class));
            }
        });
        refreshEvents();
    }
    private void refreshEvents(){
        EventsTable.getAllEvents(dbHelper.getReadableDatabase(),eventClasses);
        adapter.notifyDataSetChanged();
    }
}
