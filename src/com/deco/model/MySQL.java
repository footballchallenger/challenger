package com.deco.model;

import java.lang.String;

import com.deco.config.DATABASE;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQL extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION = DATABASE.VERSION;
    private static final String DATABASE_NAME = "football.db";		
	
    public MySQL(Context context) {
    	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    public void insert(String szTableName, ContentValues values)
    {
    	if (values.size() == 0)
    		return;

    	SQLiteDatabase db = this.getWritableDatabase();
       	db.insert(szTableName, null, values);
       	db.close();
    }  
    
    public int update(String szTableId, String szKeyName, int nKeyId, ContentValues values) {
    	if (values.size() == 0)
    		return 0;   
    	
	    SQLiteDatabase db = this.getWritableDatabase();
	    int nRet = db.update(szTableId, values, szKeyName + " = ?",
	            new String[] { String.valueOf(nKeyId) });
	    
	    db.close();
	    return nRet;
    }    
}
