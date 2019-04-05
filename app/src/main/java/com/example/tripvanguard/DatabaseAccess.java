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
        c = db.rawQuery("SELECT lat FROM latlongplace",new String[]{});

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
        c1 = db.rawQuery("SELECT long FROM latlongplace",new String[]{});

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
        c2 = db.rawQuery("SELECT place_Name FROM latlongplace",new String[]{});

        while (c2.moveToNext())
        {
            String name = c2.getString(0);
            arrayList.add(name);

        }
        return arrayList;
    }

    //method from shimul
    public ArrayList<String> getInfo(String from, String to){
        Cursor cursor = db.rawQuery("SELECT place1 FROM route2 WHERE start_place = '"+from+"' and destinat = '"+to+"'",new String[]{});
        Cursor cursor2 = db.rawQuery("SELECT place2 FROM route2 WHERE start_place = '"+from+"' and destinat = '"+to+"'",new String[]{});
        Cursor cursor3 = db.rawQuery("SELECT place3 FROM route2 WHERE start_place = '"+from+"' and destinat = '"+to+"'",new String[]{});

        ArrayList<String> arrayList = new ArrayList<>();
        while (cursor.moveToNext()){
            String data = cursor.getString(0);
            if(data!=null){
                arrayList.add(data);
            }
        }
        while (cursor2.moveToNext()){
            String data2 = cursor2.getString(0);
            if(data2!=null){
                arrayList.add(data2);
            }
        }
        while (cursor3.moveToNext()){
            String data3 = cursor3.getString(0);
            if(data3!=null){
                arrayList.add(data3);
            }
        }
        return  arrayList;
    }

    public String getBusInfo(String routeName){
        Cursor cursorName = db.rawQuery("SELECT bus_name FROM '"+routeName+"'",new String[]{});
        Cursor cursorCost = db.rawQuery("SELECT bus_cost FROM '"+routeName+"'",new String[]{});

        ArrayList<String> arrayList = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();

        while (cursorName.moveToNext() && cursorCost.moveToNext()){
            String busName = cursorName.getString(0);
            String busCost = cursorCost.getString(0);
            if(busName!=null && busCost!=null){
                buffer.append(busName+" ------ "+busCost+"\n");
            }
        }
        return  buffer.toString();
    }

    public String getOtherVehicle(String routeName){
        Cursor cursorName = db.rawQuery("SELECT other_vehicle FROM '"+routeName+"'",new String[]{});
        Cursor cursorCost = db.rawQuery("SELECT vehicle_cost FROM '"+routeName+"'",new String[]{});

        String busName = null;
        String busCost=null;

        ArrayList<String> arrayList = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();

        while (cursorName.moveToNext() && cursorCost.moveToNext()){
            busName = cursorName.getString(0);
            busCost = cursorCost.getString(0);
            if(busName!=null && busCost!=null){
                buffer.append(busName+":\n"+busCost+"\n\n");
            }
        }
        return  buffer.toString();
    }

    public String getTrainInfo(String routeName){
        Cursor cursorName = db.rawQuery("SELECT train_name FROM '"+routeName+"'",new String[]{});
        Cursor cursorCost = db.rawQuery("SELECT train_cost FROM '"+routeName+"'",new String[]{});

        ArrayList<String> arrayList = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();

        while (cursorName.moveToNext() && cursorCost.moveToNext()){
            String trainName = cursorName.getString(0);
            String trainCost = cursorCost.getString(0);
            if(trainName!=null && trainCost!=null){
                buffer.append(trainName+" ------ "+trainCost+"\n");
            }
        }
        return  buffer.toString();
    }


}
