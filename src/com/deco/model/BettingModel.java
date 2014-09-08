package com.deco.model;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;

import com.deco.sql.BETTING;
import com.deco.sql.USER;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class BettingModel extends MySQL{
	private static final String TABLE_NAME = "betting";
	private static final String KEY_ID = "user_id";
	
    public BettingModel(Context context) {
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
    
    public void create(){
    	SQLiteDatabase db = this.getWritableDatabase();
	   	String szQuery = 
	   			"CREATE TABLE `betting` (" +
				  "`betting_id` int(11) NOT NULL," +
				  "`user_id` int(11) NOT NULL," +
				  "`match_id` int(11) NOT NULL," +
				  "`odds_title` varchar(32) NOT NULL," +
				  "`betting_cash` int(11) NOT NULL," +
				  "`betting_get` int(11) NOT NULL," +
				  "`betting_status` int(11) DEFAULT NULL," +
				  "`betting_time` varchar(32) DEFAULT NULL," +
				  "PRIMARY KEY (`betting_id`)" +
				")";
	   	db.execSQL(szQuery);
	   	db.close();	   	
    }
    
    public void upgrade(){
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
    
    public ArrayList<HashMap<String, String>> getBettingByUserId(String szUserId){
    	ArrayList<HashMap<String, String>> lsBettingData = new ArrayList<HashMap<String, String>>();

    	try {
    		ArrayList<String> lsSelect = new ArrayList<String>();
    		lsSelect.add(BETTING.id);
    		lsSelect.add(BETTING.user_id);
    		lsSelect.add(BETTING.match_id);
    		lsSelect.add(BETTING.odds_title);
    		lsSelect.add(BETTING.cash);
    		lsSelect.add(BETTING.get);
    		lsSelect.add(BETTING.status);
    		lsSelect.add(BETTING.time);  		
    		
        	SQLiteDatabase db = this.getReadableDatabase();
        	String szSelect = TextUtils.join(",", lsSelect);
        	String szQuery = String.format("SELECT %s FROM %s WHERE %s=%s", szSelect, TABLE_NAME, BETTING.user_id, szUserId);
    	   	Cursor cursor = db.rawQuery(szQuery, null);  
    	   
            do {
            	HashMap<String, String> item = new HashMap<String, String>();
               	for (int i=0; i<lsSelect.size(); i++){
               		item.put(lsSelect.get(i), cursor.getString(i));
            	}
               	lsBettingData.add(item);
            } while (cursor.moveToNext());
	        
            cursor.close();
            db.close();
            
	        return lsBettingData;
	    } catch (SQLException e) {
	    	upgrade();
	    }
    	return lsBettingData;
    }       
    
    public ArrayList<HashMap<String, String>> getBettingByMatchId(String szUserId, String szMatchId){
    	ArrayList<HashMap<String, String>> lsBettingData = new ArrayList<HashMap<String, String>>();

    	try {
    		ArrayList<String> lsSelect = new ArrayList<String>();
    		lsSelect.add(BETTING.id);
    		lsSelect.add(BETTING.user_id);
    		lsSelect.add(BETTING.match_id);
    		lsSelect.add(BETTING.odds_title);
    		lsSelect.add(BETTING.cash);
    		lsSelect.add(BETTING.get);
    		lsSelect.add(BETTING.status);
    		lsSelect.add(BETTING.time);  		
    		
        	SQLiteDatabase db = this.getReadableDatabase();
        	String szSelect = TextUtils.join(",", lsSelect);
        	String szQuery = String.format("SELECT %s FROM %s WHERE %s=%s AND %s=%s", 
        			szSelect, TABLE_NAME, 
        			USER.id, szUserId,
        			BETTING.match_id, szMatchId);
    	   	Cursor cursor = db.rawQuery(szQuery, null);  
    	   
    	   	if (cursor.moveToFirst()) {
	            do {
	            	HashMap<String, String> item = new HashMap<String, String>();
	               	for (int i=0; i<lsSelect.size(); i++){
	               		item.put(lsSelect.get(i), cursor.getString(i));
	            	}
	               	lsBettingData.add(item);
	            } while (cursor.moveToNext());
    	   	}
	        
            cursor.close();
            db.close();
            
	        return lsBettingData;
	    } catch (SQLException e) {
	    	upgrade();
	    }
    	return lsBettingData;
    }        
    
    public HashMap<String, String> getBettingById(String szId){
    	HashMap<String, String> pBettingData = new HashMap<String, String>();
    	try {
    		ArrayList<String> lsSelect = new ArrayList<String>();
    		lsSelect.add(BETTING.id);
    		lsSelect.add(BETTING.user_id);
    		lsSelect.add(BETTING.match_id);
    		lsSelect.add(BETTING.odds_title);
    		lsSelect.add(BETTING.cash);
    		lsSelect.add(BETTING.get);
    		lsSelect.add(BETTING.status);
    		lsSelect.add(BETTING.time);  		
    		
        	SQLiteDatabase db = this.getReadableDatabase();
        	String szSelect = TextUtils.join(",", lsSelect);
        	String szQuery = String.format("SELECT %s FROM %s WHERE %=%", szSelect, TABLE_NAME, BETTING.id, szId);
    	   	Cursor cursor = db.rawQuery(szQuery, null);  
    	   
    	   	if (cursor.moveToFirst()){
            	for (int i=0; i<lsSelect.size(); i++){
            		pBettingData.put(lsSelect.get(i), cursor.getString(i));
            	}
            	cursor.close();
            	db.close();
    	   	}
	        
	        return pBettingData;
	    } catch (SQLException e) {
	    	upgrade();
	    }
    	return pBettingData;
    }
}
