package com.deco.model;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;

import com.deco.sql.LEAGUE;
import com.deco.sql.MATCH;
import com.deco.sql.TEAM;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class MatchModel extends MySQL{
	private static final String TABLE_NAME = "match";
	
    public MatchModel(Context context) {
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
	   	String szQuery = "CREATE TABLE `match` (" +
						  "`match_id` int(11) NOT NULL," +
						  "`league_id` int(11) NOT NULL DEFAULT '0'," +
						  "`team_home_id` int(11) NOT NULL," +
						  "`team_away_id` int(11) NOT NULL," +
						  "`match_home_goals` int(11) NOT NULL," +
						  "`match_away_goals` int(11) NOT NULL," +
						  "`match_first_result` char(50) DEFAULT NULL," +
						  "`match_first_time` timestamp NULL DEFAULT '0000-00-00 00:00:00'," +
						  "`match_second_time` timestamp NULL DEFAULT '0000-00-00 00:00:00'," +
						  "`match_handicap` int(10) DEFAULT NULL," +
						  "`match_home_back` int(10) DEFAULT NULL," +
						  "`match_away_back` int(10) DEFAULT NULL," +
						  "`match_status` int(11) NOT NULL DEFAULT '0'," +
						  "`match_paid` int(11) NOT NULL DEFAULT '0'," +
						  "PRIMARY KEY (`match_id`)" +
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
    	return super.update(TABLE_NAME, MATCH.id, nId, values);
    }
    
    public Boolean checkExist(String match_id){
    	SQLiteDatabase db = this.getReadableDatabase();
    	String szQuery = String.format("SELECT %s FROM %s WHERE %s=%s", MATCH.id, TABLE_NAME, MATCH.id, match_id);
	   	Cursor cursor = db.rawQuery(szQuery, null);  
	   	if (cursor.getCount() == 0)
	   		return false;
	   	
	   	cursor.close();
	   	db.close();
	   	return true;
    }    
    
    public HashMap<String, String> getMatchById(String szId, ArrayList<String> lsSelect){
    	HashMap<String, String> ret = new HashMap<String, String>();
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	String szSelect = TextUtils.join(",", lsSelect);
    	String szQuery = String.format("SELECT %s FROM %s WHERE %s=%s", szSelect, TABLE_NAME, MATCH.id, szId);
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
    
    public ArrayList<HashMap<String, String>> getLiving(ArrayList<String> lsSelect){
    	ArrayList<HashMap<String, String>> lsLivingMatch = new ArrayList<HashMap<String, String>>();
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	String szSelect = TextUtils.join(",", lsSelect);
	   	String szQuery = String.format("SELECT %s FROM `%s` WHERE match_status > 0 ORDER BY %s ASC, %s ASC", szSelect, TABLE_NAME, LEAGUE.id, MATCH.first_time);
	   	Cursor cursor = db.rawQuery(szQuery, null);
	   	
        if (cursor.moveToFirst()) {
            do {
            	HashMap<String, String> item = new HashMap<String, String>();
            	for (int i=0; i<lsSelect.size(); i++){
            		item.put(lsSelect.get(i), cursor.getString(i));
            	}
            	lsLivingMatch.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }	   	
    	
        db.close();
    	return lsLivingMatch;
    }  
    
    public HashMap<String, String> getMatchById(String szMatchId){
    	HashMap<String, String> item = new HashMap<String, String>();
    	
    	SQLiteDatabase db = this.getReadableDatabase();
    	String szView = String.format(
    			"SELECT `match`.*,team_name as team_home_name from `match`, `team` " +
    			"WHERE match_id=%s AND team_home_id = team_id " +
    			"GROUP BY match_id", szMatchId);
    	String szQuery = String.format(
    			"SELECT match_id, match_view.league_id, team_home_id,team_away_id,match_home_goals,match_away_goals,match_first_result,match_first_time,match_second_time,match_handicap,match_home_back,match_away_back,match_status,team_home_name,team_name as team_away_name " +
    			"FROM `team`,(%s) AS match_view " +
    			"WHERE team_away_id=team_id " +
    			"ORDER BY match_view.%s ASC, %s ASC", szView, LEAGUE.id, MATCH.first_time);
    	
	   	Cursor cursor = db.rawQuery(szQuery, null);
        if (cursor.moveToFirst()) {
            int i = 0;
            item.put(MATCH.id, cursor.getString(i++));
            item.put(MATCH.league_id, cursor.getString(i++));
            item.put(MATCH.team_home_id, cursor.getString(i++));
            item.put(MATCH.team_away_id, cursor.getString(i++));
            item.put(MATCH.home_goals, cursor.getString(i++));
            item.put(MATCH.away_goals, cursor.getString(i++));
            item.put(MATCH.first_result, cursor.getString(i++));
            item.put(MATCH.first_time, cursor.getString(i++));
            item.put(MATCH.second_time, cursor.getString(i++));
            item.put(MATCH.handicap, cursor.getString(i++));
            item.put(MATCH.home_back, cursor.getString(i++));
            item.put(MATCH.away_back, cursor.getString(i++));
            item.put(MATCH.status, cursor.getString(i++));
            item.put(MATCH.home_name, cursor.getString(i++));
            item.put(MATCH.away_name, cursor.getString(i++));
            cursor.close();
        }	   	
    	
        db.close();
    	return item;
    }     
    
    public ArrayList<HashMap<String, String>> getLiving(){
    	ArrayList<HashMap<String, String>> lsLivingMatch = new ArrayList<HashMap<String, String>>();
    	SQLiteDatabase db = this.getReadableDatabase();
    	
    	String szView = String.format(
    			"SELECT `match`.*,team_name as team_home_name from `match`, `team` " +
    			"WHERE team_home_id = team_id AND match_status > 0 AND DATETIME(match_first_time, '+1 day') > date('now') " +
    			"GROUP BY match_id");
    	String szQuery = String.format(
    			"SELECT match_id, match_view.league_id, team_home_id,team_away_id,match_home_goals,match_away_goals,match_first_result,match_first_time,match_second_time,match_handicap,match_home_back,match_away_back,match_status,team_home_name,team_name as team_away_name " +
    			"FROM `team`,(%s) AS match_view " +
    			"WHERE team_away_id=team_id " +
    			"ORDER BY match_view.%s ASC, %s ASC", szView, LEAGUE.id, MATCH.first_time);
    	
	   	Cursor cursor = db.rawQuery(szQuery, null);
        if (cursor.moveToFirst()) {
            do {
            	HashMap<String, String> item = new HashMap<String, String>();
            	int i = 0;
            	item.put(MATCH.id, cursor.getString(i++));
            	item.put(MATCH.league_id, cursor.getString(i++));
            	item.put(MATCH.team_home_id, cursor.getString(i++));
            	item.put(MATCH.team_away_id, cursor.getString(i++));
            	item.put(MATCH.home_goals, cursor.getString(i++));
            	item.put(MATCH.away_goals, cursor.getString(i++));
            	item.put(MATCH.first_result, cursor.getString(i++));
            	item.put(MATCH.first_time, cursor.getString(i++));
            	item.put(MATCH.second_time, cursor.getString(i++));
            	item.put(MATCH.handicap, cursor.getString(i++));
            	item.put(MATCH.home_back, cursor.getString(i++));
            	item.put(MATCH.away_back, cursor.getString(i++));
            	item.put(MATCH.status, cursor.getString(i++));
            	item.put(MATCH.home_name, cursor.getString(i++));
            	item.put(MATCH.away_name, cursor.getString(i++));
            	lsLivingMatch.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }	   	
    	
        db.close();
    	return lsLivingMatch;
    }    
}
