package vtask.com;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by akhil on 8/30/2016.
 */

public class ReminderDb  extends SQLiteOpenHelper {


    private static final  String DATABASE_NAME = "reminder";
    private static final  int DATABASE_VERSION = 1;

    private static final String TABLE_NAME="reminders";
    private static final String TITLE ="title";
    private static final String DATE_TIME = "date_time";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String ID = "id";


    public ReminderDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        String DATABASE_CREATE =  "create table " + TABLE_NAME + " ("
                + ID + " integer primary key autoincrement, "
                + TITLE + " text not null, "
                + DATE + " text not null, "
                + TIME + " text not null, "
                + DATE_TIME + " text not null);";
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    //add new events
    public Long insertEvent(ReminderItem item){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, item.getTitle()); // Contact Name
        values.put(DATE_TIME, item.getDateTime());
        values.put(DATE, item.getDate());
        values.put(TIME,item.getTime());
       return db.insert(TABLE_NAME,null,values);

    }

    //get all events
    public List<ReminderItem> getAllEvents(){

        SQLiteDatabase db = getReadableDatabase();
        List<ReminderItem> contactList = new ArrayList<ReminderItem>();
        // Select All Query
        String selectQuery = "SELECT id, title, date, time, date_time   FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ReminderItem contact = new ReminderItem();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setTitle(cursor.getString(1));
                contact.setDate(cursor.getString(2));
                contact.setTime(cursor.getString(3));
                contact.setDateTime(cursor.getString(4));
                // Adding contact to list
                //Log.d("rem",cursor.getString(1)+"--"+cursor.getString(2));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactList;
    }

    //get single event
    public ReminderItem getEvent(Long id){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { ID,
                        TITLE, DATE_TIME }, ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ReminderItem item = new ReminderItem();
        item.setDateTime(cursor.getString(2));
        item.setTitle(cursor.getString(1));
        item.setId(cursor.getInt(0));
        return item;
    }

    //delete events
    public void deleteEvent(){



    }

    //update Events
    public void updateEvents(){

    }





}
