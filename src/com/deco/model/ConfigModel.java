package com.deco.model;

import java.lang.String;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.database.SQLException;

public class ConfigModel extends MySQL{
	private static final String TABLE_NAME = "config";
	Context _context;
	
    public ConfigModel(Context context) {
    	super(context);
    	_context = context;    	
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    } 
    
    public void create(){
    	SQLiteDatabase db = this.getWritableDatabase();
	   	String szQuery = "CREATE TABLE `" + TABLE_NAME + "` (" +
	   					 "	  `version` int(11) NOT NULL" +
	   					 ")";   
    	db.execSQL(szQuery);  
		ContentValues values = new ContentValues();
		values.put("version", "0");
		super.insert(TABLE_NAME, values);  
		db.close();

		TeamModel mdlTeam = new TeamModel(_context);
		mdlTeam.upgrade();		
		LeagueModel mdlLeague = new LeagueModel(_context);
		mdlLeague.upgrade();			
		MatchModel mdlMatch = new MatchModel(_context);
		mdlMatch.upgrade();
    }
    
    public void upgrade() {
    	SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        create();
    }
    
    public int getVersion(){
    	try {
	    	SQLiteDatabase db = this.getReadableDatabase();
	    	String szQuery = "SELECT * FROM " + TABLE_NAME;
	        Cursor cursor = db.rawQuery(szQuery, null);
	        if (cursor.getCount() > 0){
	        	cursor.moveToFirst();
	        	int nVersion = cursor.getInt(0);
	        	cursor.close();
	        	return nVersion;
	        }
	        return 0;
	    } catch (SQLException e) {
	    	upgrade();
	    	return 0;
	    }
    }    
    
    public void updateVersion(String szVersion) {
    	SQLiteDatabase db = this.getWritableDatabase();
	   	String szQuery = "UPDATE config SET version=" + szVersion;   
	   	db.execSQL(szQuery); 
	   	db.close();
    }
}