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
import com.deco.model.TeamModel;
import com.deco.sql.TEAM;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;


public class TeamService extends Observable{
	private static Context _context;
	
	public TeamService(Context context) {
		if (context != null)
			_context = context;
	}
	
	public void getAllTeam(){
		get("http://footballchallenger.net/service.php?nav=team&info=all");
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
	        	return "";
	        } 
	        catch (IOException e) {
	        	return "";
	        }
	        
			try {	        	
				JSONObject objTeam = new JSONObject((String)responseString);
				JSONArray teams = objTeam.getJSONArray("teams");
				int nCount = teams.length();
				TeamModel mdlTeam = new TeamModel(_context);
				mdlTeam.upgrade();
				for (int i = 0; i < nCount; i++) {
					JSONObject team = teams.getJSONObject(i);
					ContentValues values = new ContentValues();
					values.put(TEAM.id, team.getString(TEAM.id));
					values.put(TEAM.league_id, team.getString(TEAM.league_id));
					values.put(TEAM.name, team.getString(TEAM.name));
					values.put(TEAM.short_name, team.getString(TEAM.short_name));
					values.put(TEAM.city, team.getString(TEAM.city));
					values.put(TEAM.stadium, team.getString(TEAM.stadium));
					values.put(TEAM.avatar, team.getString(TEAM.avatar));
					values.put(TEAM.fans_num, team.getString(TEAM.fans_num));
					values.put(TEAM.visible, "1");
					mdlTeam.insert(values);
					publishProgress((i + 1) * 100 / nCount);
				}	 
			} catch (JSONException e) {
	        }	        	
	        
	        return "";
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
