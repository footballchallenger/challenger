package com.deco.football;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import com.deco.model.UserModel;
import com.deco.service.UserService;
import com.deco.sql.USER;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private Context _context = this;
	private String _szPass = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onLoginBtnClick(View v){
		TextView btnLogin = (TextView)v;
		btnLogin.setText("Logging in ....");
		
		UserService svUser = new UserService(_context);
		LoginWatcher wtcLogin = new LoginWatcher();
		svUser.addObserver(wtcLogin);
		
		TextView user = (TextView)findViewById(R.id.txtEmail);
		TextView pass = (TextView)findViewById(R.id.txtPassword);
		_szPass =  pass.getText().toString();
		
		svUser.login(user.getText().toString(), pass.getText().toString());
	}
	
	class LoginWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			HashMap<String, String> result =  (HashMap<String, String>)arg;
			if (result.get("result")=="true"){
				try {				
					String szJson = result.get("data");
					JSONObject objData = new JSONObject(szJson);
					JSONObject objUser = objData.getJSONObject("user");
					String szId 	  = objUser.getString(USER.id);
					String szTeamId   = objUser.getString(USER.team_id);
					String szEmail 	  = objUser.getString(USER.email);
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
					values.put(USER.pass, _szPass);					
					values.put(USER.birthday, szBirthday);
					values.put(USER.country, szCountry);
					values.put(USER.token, szToken);
					values.put(USER.avatar, szAvatar);
					values.put(USER.reg_time, szRegTime);
					values.put(USER.cash, szCash);
					values.put(USER.logged, "1");
					
					UserModel mdlUser = new UserModel(_context);
					mdlUser.upgrade();
					String tmp = mdlUser.getUserById(szId);
					if (tmp == ""){
						mdlUser.insert(values);
					}
					else{
						mdlUser.update(Integer.parseInt(tmp), values);
					}
					
					Intent intent = new Intent(_context, LivingActivity.class);
					startActivity(intent);
					finish();
				} catch (JSONException e) {
				}					
			}
			else{
				String szJson = result.get("msg");
				TextView error = (TextView)findViewById(R.id.txtError);
				error.setText(szJson);
			}
			
			TextView btnLogin = (TextView)findViewById(R.id.btnLogin);
			btnLogin.setText("Log in");			
		} 
	}	
}
