package com.example.tripvanguard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;
    Cursor c1=null;
    Cursor c2=null;
    // for object creation outside the box is avoided
    private DatabaseAccess(Context context)
    {
         this.openHelper=new DatabaseOpenHelper(context);

    }
    //to return the single instance of database
    public static DatabaseAccess getInstance(Context context)
    {
        if(instance==null)
        {
            instance=new DatabaseAccess(context);
        }
        return instance;
    }
    //to open the database
    public void open()
    {
        this.db= openHelper.getWritableDatabase();
    }
    public void close()
    {
        if (db!=null)
        {
            this.db.close();
        }
    }
    //now method for query
    public ArrayList getLat()
    {
        ArrayList<Double> arrayList = new ArrayList<>();
        c = db.rawQuery("SELECT Lat FROM latlong",new String[]{});

        while (c.moveToNext())
        {
            double name = c.getDouble(0);
            arrayList.add(name);

        }
        return arrayList;
    }

    public ArrayList getLong()
    {
        ArrayList<Double> arrayList = new ArrayList<>();
        c1 = db.rawQuery("SELECT Long FROM latlong",new String[]{});

        while (c1.moveToNext())
        {
            double name = c1.getDouble(0);
            arrayList.add(name);

        }
        return arrayList;
    }

    public ArrayList getPlaceName()
    {
        ArrayList<String> arrayList = new ArrayList<>();
        c2 = db.rawQuery("SELECT Place_Name FROM latlong",new String[]{});

        while (c2.moveToNext())
        {
            String name = c2.getString(0);
            arrayList.add(name);

        }
        return arrayList;
    }




}
