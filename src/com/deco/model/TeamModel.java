package com.deco.model;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;

import com.deco.sql.MATCH;
import com.deco.sql.TEAM;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class TeamModel extends MySQL{
	private static final String TABLE_NAME = "team";
	private static final String KEY_ID = "team_id";
	
    public TeamModel(Context context) {
    	super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    } 
    
    public void beginTransaction(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.beginTransaction();
    }

    public void endTransaction(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.endTransaction();
    }    
    
    public void create()
    {
    	SQLiteDatabase db = this.getWritableDatabase();
	   	String szQuery = "CREATE TABLE `team` (" +
				  "`team_id` int(11) NOT NULL," +
				  "`league_id` int(11) NOT NULL," +
				  "`team_name` varchar(128) NOT NULL," +
				  "`team_short_name` varchar(32) DEFAULT NULL," +
				  "`team_city` varchar(32) DEFAULT NULL," +
				  "`team_stadium` varchar(128) DEFAULT NULL," +
				  "`team_avatar` varchar(128) DEFAULT NULL," +
				  "`team_fans_num` int(11) NOT NULL," +
				  "`team_visible` int(11) NOT NULL DEFAULT '1'," +
				  "PRIMARY KEY (`team_id`)" +
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
    
    public HashMap<String, String> getTeamById(String szId, ArrayList<String> lsSelect){
    	HashMap<String, String> ret = new HashMap<String, String>();
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	String szSelect = TextUtils.join(",", lsSelect);
    	String szQuery = String.format("SELECT %s FROM %s WHERE %s=%s", szSelect, TABLE_NAME, TEAM.id, szId);
	   	Cursor cursor = db.rawQuery(szQuery, null);  
	   
	   	if (cursor.moveToFirst()){
        	for (int i=0; i<lsSelect.size(); i++){
        		ret.put(lsSelect.get(i), cursor.getString(i));
        	}
        	cursor.close();
	   	}
	   	
	   	db.close();
	   	return ret;
    }      
    
    public String getTeamNameById(String szId){
    	SQLiteDatabase db = this.getReadableDatabase();
	   	String szQuery = String.format("SELECT %s FROM `%s` WHERE %s=%s", TEAM.name, TABLE_NAME, TEAM.id, szId);
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
