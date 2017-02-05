package com.sudarshanhs.myalaram.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sudarshanhs.myalaram.module.Alarm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sudarshan on 16-Dec-16.
 */



public class AlarmDb {

public static final String KEY_ROWID="_id";
public static final String KEY_TIME_HR="timehr";
public static final String KEY_TIME_MIN="timemin";
public static final String KEY_REPEAT ="repeat";
public static final String KEY_STATE="stste";
private static final String DATABASE_NAME="myAlarm";
private static final String DATABASE_TABLE="basictable";
private static final int DATABASE_VERSION=1;
private DBHelper ourHelper;
private  final Context ourContext;
private SQLiteDatabase ourDatabase;

    /**
     * Created by Sudarshan on 18-12-2016.
     */

public static class DBHelper extends SQLiteOpenHelper
{

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); 	//Spcify db name and version
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        db.execSQL("CREATE TABLE "+ DATABASE_TABLE + " ("+
                KEY_ROWID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_TIME_HR+" TEXT NOT NULL, "
                +KEY_TIME_MIN+","
                +KEY_REPEAT+","
                +KEY_STATE+" TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion)
    {
        db.execSQL("DROP TABLE IF EXIST "+DATABASE_TABLE);
        onCreate(db);
    }


}

    public AlarmDb(Context c)
    {
        ourContext=c;
    }
    public AlarmDb open() throws SQLException
    {
        ourHelper = new DBHelper(ourContext);
        ourDatabase=ourHelper.getWritableDatabase();
        return this;
    }
    public void close()
    {
        ourHelper.close();
    }
    public long createEntry(String timeInHR,String timeInMin,String repeat,String state)
    {
        ContentValues cv= new ContentValues();
        cv.put(KEY_TIME_HR,timeInHR);
        cv.put(KEY_TIME_MIN,timeInMin);
        cv.put(KEY_REPEAT, repeat);
        cv.put(KEY_STATE, state);
        return	ourDatabase.insert(DATABASE_TABLE, null, cv);
    }


    public  void updateState(long rowId,String state)
    {

        ContentValues cvUpdate=new ContentValues();
        cvUpdate.put(KEY_STATE, state);
        ourDatabase.update(DATABASE_TABLE, cvUpdate,KEY_ROWID+" = "+rowId ,null);
    }


    public List<Alarm> getData()
    {

        List<Alarm> list=new ArrayList<Alarm>();

        String[] columns=new String[]{KEY_ROWID,KEY_TIME_HR,KEY_TIME_MIN,KEY_REPEAT,KEY_STATE};
        Cursor c=ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);


        int iRow=c.getColumnIndex(KEY_ROWID);
        int iHr=c.getColumnIndex(KEY_TIME_HR);
        int iMin=c.getColumnIndex(KEY_TIME_MIN);
        int iRepeat=c.getColumnIndex(KEY_REPEAT);
        int iState=c.getColumnIndex(KEY_STATE);



        for (c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {

           Alarm alarm = new Alarm();
            alarm.setId(c.getString(iRow));
            alarm.setTimeHr(c.getString(iHr));
            alarm.setTimeMin(c.getString(iMin));
            alarm.setRepeat(c.getString(iRepeat));
            alarm.setState(c.getString(iState));
            list.add(alarm);
        }


        return list;
    }

    public void deleteEntry(Long rowId)
    {
        ourDatabase.delete(DATABASE_TABLE, KEY_ROWID+" = "+rowId, null);
    }
}