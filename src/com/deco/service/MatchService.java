package com.deco.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.deco.config.SERVER;
import com.deco.model.MatchModel;
import com.deco.sql.MATCH;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;


public class MatchService extends Observable{
	
	private static Context _context;
	
	public MatchService(Context context) {
		if (context != null)
			_context = context;
	}
	
	public void getLivingMatch(){
		String szServiceUrl = SERVER.SERVICE_URL + "nav=match&info=live&zip=0";
		get(szServiceUrl, "1");
	}

	public void getComingMatch(){
		String szServiceUrl = SERVER.SERVICE_URL + "nav=match&info=coming";
		get(szServiceUrl, "0");
	}	
	
	public void get(String URL, String szLive){
		String[] values = new String[] {URL, szLive};
		new RequestTask().execute(values);
	}
	
	class RequestTask extends AsyncTask<String, Integer, ContentValues>{
		private boolean _bUpdate = false;
		
	    @Override
	    protected ContentValues doInBackground(String... params) {
	    	ContentValues result = new ContentValues();
	    	result.put("result", "false");	    	
	    	
	    	_bUpdate = false;
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(params[0]));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	        	return result;
	        } 
	        catch (IOException e) {
	        	return result;
	        }
	        
			try {	        
				JSONObject objLeague = new JSONObject((String)responseString);
				JSONArray matches = objLeague.getJSONArray("matches");

				int nCount = matches.length();
				MatchModel mdlMatch = new MatchModel(_context);
				for (int i = 0; i < nCount; i++){
					JSONObject match = matches.getJSONObject(i);
					ContentValues values = new ContentValues();
					
					String match_id = getObjValue(match, MATCH.id);
					if (match_id == "")
						continue;
					
					ArrayList<String> lsSelect = new ArrayList<String>();
					lsSelect.add(MATCH.status);
					lsSelect.add(MATCH.home_goals);
					lsSelect.add(MATCH.away_goals);
					lsSelect.add(MATCH.first_result);
					lsSelect.add(MATCH.second_time);
					lsSelect.add(MATCH.handicap);
					lsSelect.add(MATCH.home_back);
					lsSelect.add(MATCH.away_back);
					
					ContentValues rsMatch = mdlMatch.getMatchById(match_id, lsSelect);
					
					if (rsMatch.size() == 0){
						values.put(MATCH.id, match_id);
						values.put(MATCH.league_id, getObjValue(match, MATCH.league_id));
						values.put(MATCH.team_home_id, getObjValue(match, MATCH.team_home_id));
						values.put(MATCH.team_away_id, getObjValue(match, MATCH.team_away_id));
						values.put(MATCH.home_goals, getObjValue(match, MATCH.home_goals));
						values.put(MATCH.away_goals, getObjValue(match, MATCH.away_goals));
						values.put(MATCH.first_result, getObjValue(match, MATCH.first_result));
						values.put(MATCH.first_time, getObjValue(match,MATCH.first_time));
						values.put(MATCH.second_time, getObjValue(match, MATCH.second_time));
						values.put(MATCH.status, getObjValue(match, MATCH.status));
						if (Integer.parseInt(params[1]) == 0){
							values.put(MATCH.handicap, getObjValue(match, MATCH.handicap));
							values.put(MATCH.home_back, getObjValue(match, MATCH.home_back));
							values.put(MATCH.away_back, getObjValue(match, MATCH.away_back));
						}
						mdlMatch.insert(values);
						_bUpdate = true;
					}
					else{
						int nStatus = rsMatch.getAsInteger(MATCH.status);
						if (nStatus < 5){
							if (checkDataChanged(getObjValue(match, MATCH.home_goals),  rsMatch.getAsString(MATCH.home_goals)))
								values.put(MATCH.home_goals, getObjValue(match, MATCH.home_goals));

							if (checkDataChanged(getObjValue(match, MATCH.away_goals),  rsMatch.getAsString(MATCH.away_goals)))
								values.put(MATCH.away_goals, getObjValue(match, MATCH.away_goals));

							if (checkDataChanged(getObjValue(match, MATCH.first_result),  rsMatch.getAsString(MATCH.first_result)))
								values.put(MATCH.first_result, getObjValue(match, MATCH.first_result));
				
							if (checkDataChanged(getObjValue(match, MATCH.second_time),  rsMatch.getAsString(MATCH.second_time)))
								values.put(MATCH.second_time, getObjValue(match, MATCH.second_time));
							
							if (checkDataChanged(getObjValue(match, MATCH.status),  rsMatch.getAsString(MATCH.status)))
								values.put(MATCH.status, getObjValue(match, MATCH.status));							
						}
						else if (nStatus == 17){
							if (checkDataChanged(getObjValue(match, MATCH.handicap),  rsMatch.getAsString(MATCH.handicap))){
								values.put(MATCH.handicap, getObjValue(match, MATCH.handicap));
								values.put(MATCH.home_back, getObjValue(match, MATCH.home_back));
								values.put(MATCH.away_back, getObjValue(match, MATCH.away_back));
							}
							
							if (checkDataChanged(getObjValue(match, MATCH.status),  rsMatch.getAsString(MATCH.status)))
								values.put(MATCH.status, getObjValue(match, MATCH.status));								
						} 
						
						if (values.size() > 0){
							mdlMatch.update(match_id, values);
							_bUpdate = true;
						}
					}
				}	 
			} catch (JSONException e) {
				return result;
			}	        	
	        
			result.put("result", "true");
	        return result;
	    }
	    
	    @Override
	    protected void onProgressUpdate(Integer... values) {
	        super.onProgressUpdate(values);
	    }	    
	    
	    @Override
	    protected void onPostExecute(ContentValues result) {
	        super.onPostExecute(result);
	        if (_bUpdate){
	        	setChanged();
				notifyObservers(result);
	        }
	    }
	    
	    String getObjValue(JSONObject obj, String key){
			try {	        	
				return obj.getString(key);
			} catch (JSONException e) {
				return "";
			}	 
	    }
	    
	    Boolean checkDataChanged(String a, String b){
	    	if (a.equals(b))
	    		return false;
	    	
	    	return true;
	    }
	}
}
