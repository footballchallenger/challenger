package com.deco.football;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.deco.adapter.MatchAdapter;
import com.deco.helper.Helper;
import com.deco.model.MatchModel;
import com.deco.model.UserModel;
import com.deco.service.MatchService;
import com.deco.sql.MATCH;
import com.deco.sql.USER;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LivingActivity extends Activity {

	final Handler myHandler = new Handler();
	MatchAdapter _adapter;
	HashMap<String, String> _pUser = new HashMap<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_living);
		
		// Get Current User
		updateUserBar();
		
		// ListView Match
		ArrayList<HashMap<String, String>> lsMatch = new ArrayList<HashMap<String, String>>();
		_adapter = new MatchAdapter(this, 0, lsMatch);
		ListView listView = (ListView)findViewById(R.id.matchlist);
		listView.setAdapter(_adapter); 
		updateListView();
        
        // Init Timer
		Timer myTimer = new Timer();
		LivingMatchTimer getLivingMatchTimer = new LivingMatchTimer();
		myTimer.schedule(getLivingMatchTimer, 0, 10000);
		
		myTimer = new Timer();
		ComingMatchTimer getComingMatchTimer = new ComingMatchTimer();
		myTimer.schedule(getComingMatchTimer, 2000, 60000);		
	}
	
	public void updateUserBar()	{
		UserModel mdlUser = new UserModel(this);
		_pUser = mdlUser.getLastUser();
		if (_pUser.size() > 0){
			LinearLayout userbar = (LinearLayout)this.findViewById(R.id.userbar);
			userbar.removeAllViews();
			View userinfo = LayoutInflater.from(this).inflate(R.layout.user_bar, null);
			
			LinearLayout.LayoutParams params = 
					new LinearLayout.LayoutParams(
			        ViewGroup.LayoutParams.MATCH_PARENT,
			        ViewGroup.LayoutParams.MATCH_PARENT);
			
			userbar.addView(userinfo, params);
			
			TextView username = (TextView)this.findViewById(R.id.username);
			username.setText(_pUser.get(USER.name));
			TextView usercash = (TextView)this.findViewById(R.id.usercash);
			usercash.setText("$ " + _pUser.get(USER.cash));
		}
		
	}
	
	public void updateListView(){
		MatchModel mdlMatch = new MatchModel(this);
		
		ArrayList<HashMap<String, String>>lsMatch = mdlMatch.getLiving();
		int nLeague = 0;
		for (int i=0; i<lsMatch.size(); i++){
			int tmp = Integer.parseInt(lsMatch.get(i).get(MATCH.league_id));
			if (nLeague != tmp){
				nLeague = tmp;
				HashMap<String, String> item = new HashMap<String, String>();
				item.put(MATCH.league_id, Integer.toString(nLeague));
				item.put(MATCH.id, "");
				lsMatch.add(i, item);
				i++;
			}
		}
		
		if (_adapter.getCount() > lsMatch.size())
			_adapter.clear();
		
		for (int i=_adapter.getCount(); i<lsMatch.size(); i++)
			_adapter.add(null);
		
		_adapter.lsMatch = lsMatch;
		_adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	class LivingMatchTimer extends TimerTask {
		 public void run() {
			myHandler.post(getLivingMatch);
		 }
	}
	
	class ComingMatchTimer extends TimerTask {
		 public void run() {
			myHandler.post(getComingMatch);
		 }
	}	
	
	final Runnable getLivingMatch = new Runnable() {
	      public void run() {
	    	  MatchService svMatch = new MatchService(getApplicationContext());
	    	  LivingMatchWatcher wtcMatch = new LivingMatchWatcher();
	    	  svMatch.addObserver(wtcMatch);
	    	  svMatch.getLivingMatch();		
	      }
	};
	
	final Runnable getComingMatch = new Runnable() {
	      public void run() {
	    	  MatchService svMatch = new MatchService(getApplicationContext());
	    	  LivingMatchWatcher wtcMatch = new LivingMatchWatcher();
	    	  svMatch.addObserver(wtcMatch);
	    	  svMatch.getComingMatch();		
	      }
	};		
	
	class LivingMatchWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			String data = (String)arg;
			if (data == "d"){
				updateListView();
				return;
			}			
		} 
	}	
	
	public void onLoginBtnClick(View v){
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void onLogoutBtnClick(View v){
		UserModel mdlUser = new UserModel(this);
		mdlUser.signOut();
		
		LinearLayout userbar = (LinearLayout)this.findViewById(R.id.userbar);
		userbar.removeAllViews();
		View userinfo = LayoutInflater.from(this).inflate(R.layout.login_bar, null);
		
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(
		        ViewGroup.LayoutParams.MATCH_PARENT,
		        ViewGroup.LayoutParams.MATCH_PARENT);
		
		userbar.addView(userinfo, params);
    	LinearLayout panel = (LinearLayout)findViewById(R.id.userprofile);
    	Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_down);
    	panel.startAnimation(slide);		    	
    	panel.setVisibility(View.INVISIBLE);	
	}
	
	public void onUserBtnClick(View v){
    	LinearLayout panel = (LinearLayout)findViewById(R.id.userprofile);
    	panel.setVisibility(View.VISIBLE);
    	Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
    	panel.startAnimation(slide);				
	}	

	public void onCloseBtnClick(View v){
    	LinearLayout panel = (LinearLayout)findViewById(R.id.userprofile);
    	Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_down);
    	panel.startAnimation(slide);		    	
    	panel.setVisibility(View.INVISIBLE);
	}		
	
}
