package com.example.navtry;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Student.db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null,1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table PREFHOT (ADDRESS TEXT,NAME TEXT,PLAY_COUNT INTEGER)");
        db.execSQL("create table PREFMOD (ADDRESS TEXT,NAME TEXT,PLAY_COUNT INTEGER)");
        db.execSQL("create table PREFCOOL (ADDRESS TEXT,NAME TEXT,PLAY_COUNT INTEGER)");
        db.execSQL("create table SONGSTOP (ADDRESS TEXT,LAST_POS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PREFHOT");
        db.execSQL("DROP TABLE IF EXISTS PREFMOD");
        db.execSQL("DROP TABLE IF EXISTS PREFCOOL");
        db.execSQL("DROP TABLE IF EXISTS SONGSTOP");
        onCreate(db);
    }

    public boolean SongEntry(String ADDRESS, String NAME, float Temperature ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ADDRESS", ADDRESS);
        contentValues.put("NAME", NAME);
        contentValues.put("PLAY_COUNT", 1);
        long result;
        if (Temperature <= 19.0)
        {
            result = db.insert("PREFCOOL", null, contentValues);
        }else if(Temperature>19.0&&Temperature<=29.0)
        {
            result = db.insert("PREFMOD", null, contentValues);
        }else
        {
            result = db.insert("PREFHOT", null, contentValues);
        }
        if(result==-1)
        {
            return false;
        }else
        {
            return true;
        }
    }

    public String cEntry(String ADDRESS,float Temperature)
    {
        String addr;
        addr="DK";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        if (Temperature <= 19.0)
        {
            res= db.rawQuery("select * from PREFCOOL",null);
        }else if(Temperature>19.0&&Temperature<=29.0)
        {
            res= db.rawQuery("select * from PREFMOD",null);
        }else
        {
            res= db.rawQuery("select * from PREFHOT",null);
        }
        if(res.getCount()==0)
            return "count nt found";
        while(res.moveToNext())
        {
            addr= res.getString(0);
            if(addr.equals(ADDRESS))
                return addr;
        }
        return "After while";
    }

    public boolean EntryUpdate( String ADDRESS, float Temperature)
    {
        int PLAY_COUNT=0;
        SQLiteDatabase db1 = this.getWritableDatabase();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;

        if (Temperature <= 19.0)
        {
            res=db1.rawQuery("select * from PREFCOOL",null);        }else if(Temperature>19.0&&Temperature<=29.0)
        {
            res=db1.rawQuery("select * from PREFMOD",null);        }else
        {
            res=db1.rawQuery("select * from PREFHOT",null);        }

        if(res.getCount()!=0)
        {
            while(res.moveToNext())
            {
                String addr = res.getString(0);
                if(addr.equals(ADDRESS))
                {
                    PLAY_COUNT = Integer.parseInt(res.getString(2));
                }
            }
        }

        PLAY_COUNT=PLAY_COUNT+1;

        ContentValues contentValues = new ContentValues();
        contentValues.put("PLAY_COUNT",PLAY_COUNT);

        long result;
        if (Temperature <= 19.0)
        {
            result = db.update("PREFCOOL",contentValues,"ADDRESS = ?",new String[] {ADDRESS});
        }else if(Temperature>19.0&&Temperature<=29.0)
        {
            result = db.update("PREFMOD",contentValues,"ADDRESS = ?",new String[] {ADDRESS});
        }else
        {
            result = db.update("PREFHOT",contentValues,"ADDRESS = ?",new String[] {ADDRESS});
        }
        if(result==-1)
        {
            return false;
        }else
        {
            return true;
        }
    }

    public String MarkPos(String ADDRESS, int Lpos)
    {
        SQLiteDatabase db1 = this.getWritableDatabase();
        Cursor res = db1.rawQuery("select * from SONGSTOP",null);
        ContentValues contentValues1= new ContentValues();
        ContentValues contentValues2= new ContentValues();
        contentValues1.put("LAST_POS",Lpos);
        contentValues2.put("ADDRESS",ADDRESS);
        contentValues2.put("LAST_POS",Lpos);
        String addr;
        if(res.getCount()!=0) {
            while (res.moveToNext()) {
                addr = res.getString(0);

                if( addr.equals(ADDRESS))  {
                    db1.update("SONGSTOP", contentValues1, "ADDRESS = ?", new String[]{ADDRESS});
                    return "Updated";
                }
            }
        }
        db1.insert("SONGSTOP",null,contentValues2);
        return "after while..";
    }

    public int ReadPos(String ADDRESS)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from SONGSTOP",null);
        int rpos=0;
        if(res.getCount()==0)
        {
            return rpos;
        }
        while(res.moveToNext())
        {
            if(res.getString(0).equals(ADDRESS))
                rpos=Integer.parseInt(res.getString(1));
        }
        return rpos;
    }

    public ArrayList<String> getRec(float temp)
    {
        ArrayList<String> mList=new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if (temp <= 19.0)
        {
            res= db.rawQuery("select NAME, PLAY_COUNT from PREFCOOL ORDER BY PLAY_COUNT DESC",null);
        }else if(temp>19.0&&temp<=29.0)
        {
            res= db.rawQuery("select NAME, PLAY_COUNT from PREFMOD ORDER BY PLAY_COUNT DESC",null);
        }else
        {
            res= db.rawQuery("select NAME, PLAY_COUNT from PREFHOT ORDER BY PLAY_COUNT DESC",null);
        }
        if(res.getCount()!=0)
        {
            while(res.moveToNext())
            {
                mList.add(res.getString(0)+" "+res.getString(1));
            }
        }
        return mList;
    }

}

