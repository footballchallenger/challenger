package com.deco.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import com.deco.model.LeagueModel;
import com.deco.sql.LEAGUE;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;


public class LeagueService extends Observable{
	
	private static Context _context;
	
	public LeagueService(Context context) {
		if (context != null)
			_context = context;
	}
	
	public void getAllLeague(){
		get("http://footballchallenger.net/service.php?nav=league&info=all");
	}
	
	public void get(String URL){
		new RequestTask().execute(URL);
	}
	
	class RequestTask extends AsyncTask<String, Integer, String>{
	    @Override
	    protected String doInBackground(String... uri) {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(uri[0]));
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
	        } 
	        catch (IOException e) {
	        }
	        
			try {	        	
				JSONObject objLeague = new JSONObject((String)responseString);
				JSONArray leagues = objLeague.getJSONArray("leagues");
				int nCount = leagues.length();
				LeagueModel mdlLeague = new LeagueModel(_context);
				mdlLeague.upgrade();
				for (int i = 0; i < nCount; i++) {
					JSONObject league = leagues.getJSONObject(i);
					ContentValues values = new ContentValues();
					values.put(LEAGUE.id, league.getString(LEAGUE.id));
					values.put(LEAGUE.nation_id, league.getString(LEAGUE.nation_id));
					values.put(LEAGUE.name, league.getString(LEAGUE.name));
					values.put(LEAGUE.short_name, league.getString(LEAGUE.short_name));
					values.put(LEAGUE.visible, league.getString(LEAGUE.visible));
					mdlLeague.insert(values);
					publishProgress((i + 1) * 100 / nCount);
				}	 
			} catch (JSONException e) {
			}	        	
	        
	        return responseString;
	    }
	    
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
			setChanged();
			notifyObservers(Integer.toString(values[0]));            
        }	    
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
			setChanged();
			notifyObservers("d");
	    }	   
	}
}
