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
import android.content.Context;
import android.os.AsyncTask;



public class ConfigService extends Observable{

	private static Context _context;
	private String _CONFIG_URL = "http://footballchallenger.net/service.php?nav=config&ver=";
	
	public ConfigService(Context context) {
		if (context != null)
			_context = context;
	}
	
	public void getConfig(int nVer){
		get(_CONFIG_URL + Integer.toString(nVer));
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
	        	return "fail";
	        } 
	        catch (IOException e) {
	        	return "fail";
	        }
	        
	        return responseString;
	    }
	    
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }	    
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
			setChanged();
			notifyObservers(result);
	    }	   
	}
}
