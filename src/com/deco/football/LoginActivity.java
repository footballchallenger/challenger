package com.deco.football;

import java.util.Observable;
import java.util.Observer;

import com.deco.element.BottomBar;
import com.deco.model.BettingModel;
import com.deco.service.BettingService;
import com.deco.service.UserService;
import com.deco.sql.USER;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private Context _context = this;
	private BottomBar _botBar = new BottomBar(this);
	
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
		
		svUser.login(user.getText().toString(), pass.getText().toString());
	}
	
	class LoginWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			ContentValues result =  (ContentValues)arg;
			if (result.get("result")=="true"){
				_botBar.updateUserData();
				_botBar.updateBettingCount();
				
				// Update Betting List
				if (_botBar.getUser().size() > 0){
					BettingService svBetting = new BettingService(_context);
					BettingModel mdlBetting = new BettingModel(_context);
					String szUserId = _botBar.getUser().getAsString(USER.id);
					String szBettingId = mdlBetting.getLastBettingIdByUserId(szUserId);
					svBetting.getBettingList(szUserId, szBettingId);
				}					
				
				finish();
			}
			else{
				String szJson = result.getAsString("msg");
				TextView error = (TextView)findViewById(R.id.txtError);
				error.setText(szJson);
			}
			
			TextView btnLogin = (TextView)findViewById(R.id.btnLogin);
			btnLogin.setText("Log in");			
		} 
	}	
}
