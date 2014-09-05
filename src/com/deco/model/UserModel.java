package com.deco.model;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;

import com.deco.sql.USER;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class UserModel extends MySQL{
	private static final String TABLE_NAME = "user";
	private static final String KEY_ID = "user_id";
	
    public UserModel(Context context) {
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
	   			"CREATE TABLE `user` (" +
				  "`user_id` int(11) NOT NULL," +
				  "`team_id` int(11) NOT NULL," +
				  "`user_email` varchar(32) NOT NULL," +
				  "`user_name` varchar(32) NOT NULL," +
				  "`user_pass` varchar(32) NOT NULL," +
				  "`user_birthday` varchar(32) DEFAULT NULL," +
				  "`user_country` varchar(32) DEFAULT NULL," +
				  "`user_token` varchar(32) DEFAULT NULL," +
				  "`user_avatar` varchar(128) DEFAULT NULL," +
				  "`user_reg_time` varchar(32) DEFAULT NULL," +
				  "`user_cash` int(11) NOT NULL DEFAULT '0'," +
				  "`user_logged` int(11) NOT NULL DEFAULT '0'," +
				  "PRIMARY KEY (`user_id`)" +
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
    
    public HashMap<String, String> getLastUser(){
    	HashMap<String, String> pUserData = new HashMap<String, String>();
    	try {
    		ArrayList<String> lsSelect = new ArrayList<String>();
    		lsSelect.add(USER.id);
    		lsSelect.add(USER.team_id);
    		lsSelect.add(USER.email);
    		lsSelect.add(USER.name);
    		lsSelect.add(USER.pass);
    		lsSelect.add(USER.birthday);
    		lsSelect.add(USER.country);
    		lsSelect.add(USER.token);
    		lsSelect.add(USER.avatar);
    		lsSelect.add(USER.cash);
    		lsSelect.add(USER.logged);
    		
        	SQLiteDatabase db = this.getReadableDatabase();
        	String szSelect = TextUtils.join(",", lsSelect);
        	String szQuery = String.format("SELECT %s FROM %s WHERE user_logged=1", szSelect, TABLE_NAME);
    	   	Cursor cursor = db.rawQuery(szQuery, null);  
    	   
    	   	if (cursor.moveToFirst()){
            	for (int i=0; i<lsSelect.size(); i++){
            		pUserData.put(lsSelect.get(i), cursor.getString(i));
            	}
            	cursor.close();
            	db.close();
    	   	}
	        
	        return pUserData;
	    } catch (SQLException e) {
	    	upgrade();
	    }
    	return pUserData;
    }       
    
    public void insert(ContentValues values){
    	super.insert(TABLE_NAME, values);
    }
    
    public int update(int nId, ContentValues values) {
    	return super.update(TABLE_NAME, KEY_ID, nId, values);
    }
    
    public void signOut(){
    	SQLiteDatabase db = this.getWritableDatabase();
	   	String szQuery = String.format("UPDATE %s SET user_logged=0", TABLE_NAME);
	   	db.execSQL(szQuery);
	   	db.close();  	
    }
    
    public String getUserById(String szId){
    	SQLiteDatabase db = this.getReadableDatabase();
	   	String szQuery = String.format("SELECT * FROM `%s` WHERE %s=%s", TABLE_NAME, USER.id, szId);
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
