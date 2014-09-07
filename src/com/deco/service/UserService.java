package com.deco.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.deco.config.SERVER;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONException;


public class UserService extends Observable{
	private static Context _context;
	
	public UserService(Context context) {
		if (context != null)
			_context = context;
	}
	
	public void login(String email, String password){
		String szServiceUrl = SERVER.SERVICE_URL + "nav=user&action=login";
		String[] values = new String[] {szServiceUrl, email, password};
		new LoginTask().execute(values);
	}
	
	class LoginTask extends AsyncTask<String, String, HashMap<String, String>>{
	    @Override
	    protected HashMap<String, String> doInBackground(String... params) {
	    	HashMap<String, String> result = new HashMap<String, String>();
	    	result.put("result", "false");
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(params[0]);
	        
	        HttpResponse response;
	        String responseString = null;
	        try {
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	            nameValuePairs.add(new BasicNameValuePair("email", params[1]));
	            nameValuePairs.add(new BasicNameValuePair("password", params[2]));
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            response = httpclient.execute(httppost);
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
				JSONObject objUser = new JSONObject((String)responseString);
				String szResult = objUser.getString("result");
				if (szResult.equals("false"))
				{
					String szMsg = objUser.getString("msg");
					result.put("msg", szMsg);
					return result;
				}
				
	 
			} catch (JSONException e) {
				return result;
	        }	        	
			
			result.put("result", "true");
			result.put("data", responseString);
	        return result;
	    }
	    
	    @Override
	    protected void onPostExecute(HashMap<String, String> result) {
	        super.onPostExecute(result);
			setChanged();
			notifyObservers(result);	        
	    }	   
	}
}
