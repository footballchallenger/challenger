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
import com.deco.model.UserModel;
import com.deco.sql.USER;

import android.content.ContentValues;
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
	
	class LoginTask extends AsyncTask<String, String, ContentValues>{
	    @Override
	    protected ContentValues doInBackground(String... params) {
	    	ContentValues result = new ContentValues();
	    	result.put("result", "false");
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(params[0]);
	        
	        String szEmail = params[1];
	        String szPass = params[2];
	        
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
				JSONObject objData = new JSONObject((String)responseString);
				String szResult = objData.getString("result");
				if (szResult.equals("false"))
				{
					String szMsg = objData.getString("msg");
					result.put("msg", szMsg);
					return result;
				}
				
				JSONObject objUser = objData.getJSONObject("user");
				String szId 	  = objUser.getString(USER.id);
				String szTeamId   = objUser.getString(USER.team_id);
				String szName 	  = objUser.getString(USER.name);
				String szBirthday = objUser.getString(USER.birthday);
				String szCountry  = objUser.getString(USER.country);
				String szToken    = objUser.getString(USER.token);
				String szAvatar   = objUser.getString(USER.avatar);
				String szRegTime  = objUser.getString(USER.reg_time);
				String szCash 	  = objUser.getString(USER.cash);
				
				ContentValues values = new ContentValues();
				values.put(USER.id, szId);
				values.put(USER.team_id, szTeamId);
				values.put(USER.email, szEmail);
				values.put(USER.name, szName);
				values.put(USER.pass, szPass);					
				values.put(USER.birthday, szBirthday);
				values.put(USER.country, szCountry);
				values.put(USER.token, szToken);
				values.put(USER.avatar, szAvatar);
				values.put(USER.reg_time, szRegTime);
				values.put(USER.cash, szCash);
				values.put(USER.logged, "1");
				
				UserModel mdlUser = new UserModel(_context);
				//mdlUser.upgrade();
				String szUserId = mdlUser.getUserById(szId);
				if (szUserId == ""){
					mdlUser.insert(values);
				}
				else{
					mdlUser.update(szUserId, values);
				}				
				
	 
			} catch (JSONException e) {
				return result;
	        }	        	
			
			result.put("result", "true");
			//result.put("data", responseString);
	        return result;
	    }
	    
	    @Override
	    protected void onPostExecute(ContentValues result) {
	        super.onPostExecute(result);
			setChanged();
			notifyObservers(result);	        
	    }	   
	}
}
