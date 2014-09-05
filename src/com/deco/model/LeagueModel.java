package com.deco.model;

import java.lang.String;

import com.deco.sql.LEAGUE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LeagueModel extends MySQL{

	private static final String TABLE_NAME = "league";
	private static final String KEY_ID = "league_id";
	
    public LeagueModel(Context context) {
    	super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    } 
    
    public void create()
    {
    	SQLiteDatabase db = this.getWritableDatabase();
	   	String szQuery = "CREATE TABLE `league` (" +
						  "	  `league_id` int(11) NOT NULL," +
						  "	  `nation_id` int(11) NOT NULL," +
						  "	  `league_name` text NOT NULL," +
						  "	  `league_short_name` text NOT NULL," +
						  "	  `league_visible` int(11) NOT NULL DEFAULT '1'," +
						  "	  PRIMARY KEY (`league_id`)" +
						  ")";    	
	   	
	   	 db.execSQL(szQuery); 
	   	 db.close();
    }
    
    public void upgrade()
    {
    	SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.close();
        create();  
    }
    
    public void insert(ContentValues values){
    	super.insert(TABLE_NAME, values);
    }
    
    public int update(int nId, ContentValues values) {
    	return super.update(TABLE_NAME, KEY_ID, nId, values);
    }     
    
    public String getLeagueById(String szId){
    	SQLiteDatabase db = this.getReadableDatabase();
	   	String szQuery = String.format("SELECT %s FROM %s WHERE %s=%s", LEAGUE.name, TABLE_NAME, LEAGUE.id, szId);
	   	Cursor cursor = db.rawQuery(szQuery, null);    	
	   	
	   	if (cursor.moveToFirst()){
	   		String szRet = cursor.getString(0);
	   		cursor.close();
	   		db.close();
	   		return szRet;
	   	}
	   
	   	return "";
    }    
}
