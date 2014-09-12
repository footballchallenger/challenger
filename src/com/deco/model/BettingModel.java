package com.deco.model;

import java.lang.String;
import java.util.ArrayList;

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
				  "`betting_time` timestamp NULL DEFAULT '0000-00-00 00:00:00'," +
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
    
    public int update(String szId, ContentValues values) {
    	return super.update(TABLE_NAME, KEY_ID, szId, values);
    }
    
    public Boolean checkBettingExist(String szBettingId){
    	try {
        	SQLiteDatabase db = this.getReadableDatabase();
        	String szQuery = String.format("SELECT %s FROM %s WHERE %s=%s", BETTING.id, TABLE_NAME, BETTING.id, szBettingId);
    	   	Cursor cursor = db.rawQuery(szQuery, null);  
    	   
    	   	if (cursor.getCount() == 0){
    	   		cursor.close();
    		   	db.close();
    	   		return false;
    	   	}
    	   	
        	cursor.close();
        	db.close();
	        return true;
	    } catch (SQLException e) {
	    	upgrade();
	    	return false;
	    }    	
    }
    
    public String getLastBettingIdByUserId(String szUserId){
    	try {
        	SQLiteDatabase db = this.getReadableDatabase();
        	String szQuery = String.format
        			("SELECT %s FROM %s WHERE %s=%s ORDER BY %s DESC LIMIT 1", 
        			BETTING.id, 
        			TABLE_NAME, 
        			USER.id, 
        			szUserId,
        			BETTING.id);
    	   	
        	Cursor cursor = db.rawQuery(szQuery, null);
    	   
    	   	if (cursor.moveToFirst()) {
    	   		String szBettingId = cursor.getString(0);
            	cursor.close();
            	db.close();
            	return szBettingId;
    	   	}
    	   	
        	cursor.close();
        	db.close();
	        return "0";
	    } catch (SQLException e) {
	    	upgrade();
	    	return "0";
	    } 
    }
    
    public ArrayList<ContentValues> getBettingByMatchId(String szUserId, String szMatchId){
    	ArrayList<ContentValues> lsBettingData = new ArrayList<ContentValues>();

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
        	String szQuery = String.format("SELECT %s FROM %s WHERE %s=%s AND %s=%s AND %s=0", 
        			szSelect, TABLE_NAME, 
        			USER.id, szUserId,
        			BETTING.match_id, szMatchId,
        			BETTING.status);
    	   	Cursor cursor = db.rawQuery(szQuery, null);  
    	   
    	   	if (cursor.moveToFirst()) {
	            do {
	            	ContentValues item = new ContentValues();
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
    
    public ContentValues getBettingById(String szId){
    	ContentValues pBettingData = new ContentValues();
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
        	String szQuery = String.format("SELECT %s FROM %s WHERE %s=%s", szSelect, TABLE_NAME, BETTING.id, szId);
    	   	Cursor cursor = db.rawQuery(szQuery, null);  
    	   
    	   	if (cursor.moveToFirst()){
            	for (int i=0; i<lsSelect.size(); i++){
            		pBettingData.put(lsSelect.get(i), cursor.getString(i));
            	}
    	   	}
    	   	
        	cursor.close();
        	db.close();    	   	
	        
	        return pBettingData;
	    } catch (SQLException e) {
	    	upgrade();
	    }
    	return pBettingData;
    }
    
    public int getBettingCount(String szUserId){
    	try {
        	SQLiteDatabase db = this.getReadableDatabase();
        	String szQuery = String.format("SELECT COUNT(%s) FROM %s WHERE %s=%s AND %s=0", BETTING.id, TABLE_NAME, USER.id, szUserId, BETTING.status);
    	   	Cursor cursor = db.rawQuery(szQuery, null);  
    	   
    	   	if (cursor.moveToFirst()){
    	   		int nRet = cursor.getInt(0);
            	cursor.close();
            	db.close();    	   		
    	   		return nRet;
    	   	}
    	   	
        	cursor.close();
        	db.close();
	        
	        return 0;
	    } catch (SQLException e) {
	    	upgrade();
	    	return 0;
	    }
    }
}
