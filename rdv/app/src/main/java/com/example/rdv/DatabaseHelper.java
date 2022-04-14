package com.example.rdv;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase database;

    // Table Name
    public static final String TABLE_NAME = "RDV";
    // Table columns
    public static final String _ID = "_id";
    public static final String CATEGORY = "category";
    public static final String TITLE = "title";
    public static final String CONTACT = "contact";
    public static final String NUM = "number";
    public static final String LOCATION = "location";
    public static final String MDATE = "date";
    public static final String MTIME = "time";
    public static final String REMINDER = "reminder";
    public static final String COMMENTS = "comments";
    // Database Information
    static final String DB_NAME = "rdv.DB";
    // database version
    static final int DB_VERSION = 1;
    // Creating table query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + _ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CATEGORY + " TEXT, " + TITLE +
            " TEXT NOT NULL, " + CONTACT + " TEXT, " + NUM +" TEXT, " + LOCATION +
            " TEXT, " + MDATE + " TEXT NOT NULL, " + MTIME + " TEXT NOT NULL, " +
            REMINDER + " TEXT, " + COMMENTS + " CHAR(250));";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void open() throws SQLException {
        database = this.getWritableDatabase();
    }

    public void close() {
        database.close();
    }

    public void add(Moment moment){
        ContentValues contentValues= new ContentValues();
        contentValues.put(CATEGORY,moment.getCategory());
        contentValues.put(TITLE,moment.getTitle());
        contentValues.put(CONTACT,moment.getContact());
        contentValues.put(NUM,moment.getNum());
        contentValues.put(LOCATION,moment.getLocation());
        contentValues.put(MDATE,moment.getDate());
        contentValues.put(MTIME,moment.getTime());
        contentValues.put(REMINDER,moment.getReminder());
        contentValues.put(COMMENTS,moment.getComments());
        database.insert(TABLE_NAME,null,contentValues);

    }

    public void delete(long _id)
    {
        database.delete(TABLE_NAME, _ID + "=" + _id, null);
    }
    public Moment share(long _id){
        Moment pMoment = new Moment();
        Cursor cur = database.rawQuery("Select * from "+ TABLE_NAME+" where "+this._ID+" =" + _id + "", null);
        for(cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
            Log.d("Test",cur.getString(0));
            Log.d("Test",cur.getString(1));
            pMoment.setId(Long.parseLong(cur.getString(0)));
            pMoment.setCategory(cur.getString(1));
            pMoment.setTitle(cur.getString(2));
            pMoment.setContact(cur.getString(3));
            pMoment.setNum(cur.getString(4));
            pMoment.setLocation(cur.getString(5));
            pMoment.setDate(cur.getString(6));
            pMoment.setTime(cur.getString(7));
            pMoment.setReminder(cur.getString(8));
            pMoment.setComments(cur.getString(9));


        }
        return  pMoment;
    }

    public int update(Moment moment) {
        Long _id= moment.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CATEGORY,moment.getCategory());
        contentValues.put(TITLE,moment.getTitle());
        contentValues.put(CONTACT,moment.getContact());
        contentValues.put(NUM,moment.getNum());
        contentValues.put(LOCATION,moment.getLocation());
        contentValues.put(MDATE,moment.getDate());
        contentValues.put(MTIME,moment.getTime());
        contentValues.put(REMINDER,moment.getReminder());
        contentValues.put(COMMENTS,moment.getComments());
        int count = database.update(TABLE_NAME, contentValues, this._ID + " = " + _id, null);
        return count;
    }

    public Cursor getAllMoments(){
        String[] projection = {_ID,CATEGORY,TITLE,CONTACT,NUM,LOCATION,MDATE,MTIME,REMINDER,COMMENTS};
        Cursor cursor = database.query(TABLE_NAME,projection,null,null,null,null,null,null);
        return cursor;
    }
}
