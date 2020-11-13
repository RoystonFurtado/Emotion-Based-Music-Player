package com.example.navtry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;

public class Dbhelper extends SQLiteOpenHelper {
    SecondActivity mActivity = new SecondActivity();

    public Dbhelper(@Nullable Context context) {
        super(context,"Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table user (email text primary key,password text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists user");
    }
    public boolean insert(String email,String password){
        long ins = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email",email);
        contentValues.put("password",password);

        ins = db.insert("user", null, contentValues);

        if(ins==-1) return false;
        return true;
    }
    public boolean chkemail(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from user where email=?",new String[]{email});
        if (cursor.getCount()>0){
            return false;
        }
        return true;
    }
    public boolean emailpassword(String email , String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from user where email=? and password=?",new String[]{email,password});
        if (cursor.getCount()>0) return true;
        return false;
    }
}
