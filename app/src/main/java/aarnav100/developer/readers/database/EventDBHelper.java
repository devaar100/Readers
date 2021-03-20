package aarnav100.developer.readers.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import aarnav100.developer.readers.database.tables.EventsTable;

/**
 * Created by aarnavjindal on 30/07/17.
 */

public class EventDBHelper extends SQLiteOpenHelper {

    public EventDBHelper(Context context) {
        super(context,"EventTable",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EventsTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public interface Const{
        String LBR = " ( ";
        String RBR = " ) ";
        String COMMA = " , ";
        String SEMICOL = " ; ";
        String TYPE_TEXT = " TEXT ";
        String TYPE_INT = " INTEGER ";
        String TYPE_PK = " PRIMARY KEY ";
        String TYPE_AUTO = " AUTOINCREMENT ";
        String TYPE_BOOL = " BOOLEAN ";
    }

}