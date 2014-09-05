package com.deco.football;

import java.util.Observable;
import java.util.Observer;
import com.deco.model.ConfigModel;
import com.deco.service.ConfigService;
import com.deco.service.LeagueService;
import com.deco.service.TeamService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class InitActivity extends Activity {
	private Context _context = this;
	private int nCount = 0;
	final Handler myHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		ConfigModel mdlConfig = new ConfigModel(this);
		int nVersion = mdlConfig.getVersion();
		
		ConfigService svConfig = new ConfigService(_context);
		ConfigWatcher wtcConfig = new ConfigWatcher();
		svConfig.addObserver(wtcConfig);
		svConfig.getConfig(nVersion);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.living, menu);
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
	
	final Runnable myRunnable = new Runnable() {
	      public void run() {
	    	  nCount++;
	    	  if (nCount == 2){
	    		  Intent intent = new Intent(_context, LivingActivity.class);
	    		  startActivity(intent);
	    		  finish();
	    	  }
	      }
	};		
	
	class ConfigWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			try {	        	
				JSONObject objConfig = new JSONObject((String)arg);
				String szVersion = objConfig.getString("version");
				ConfigModel mdlConfig = new ConfigModel(_context);
				mdlConfig.updateVersion(szVersion);
				
			    try
			    {
					objConfig.getString("league");
					LeagueService svLeague = new LeagueService(_context);
					LeagueWatcher leaguecatch = new LeagueWatcher(); 
					svLeague.addObserver(leaguecatch);
					svLeague.getAllLeague();
			    }
			    catch (JSONException e){
			    	myHandler.post(myRunnable);
			    }
			    
			    try
			    {
					objConfig.getString("team");
					TeamService svTeam = new TeamService(_context);
					TeamWatcher teamcatch = new TeamWatcher(); 
					svTeam.addObserver(teamcatch); 
					svTeam.getAllTeam();		
			    }
			    catch (JSONException e){
			    	myHandler.post(myRunnable);
			    } 	
			    
			} catch (JSONException e) {
			}				
		} 
	}		
	
	class MatchWatcher implements Observer { 
		public void update(Observable obj, Object arg) {

		} 
	}		
	
	class TeamWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			String data = (String)arg;
			if (data == "d"){
				myHandler.post(myRunnable);
				return;
			}
			TextView mytext = (TextView)findViewById(R.id.loading);
			mytext.setText("Loading Team : " + (String)arg + "%");
			ProgressBar progress = (ProgressBar)findViewById(R.id.progress);
			progress.setProgress(Integer.parseInt((String)arg));
		} 
	}
	
	class LeagueWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			String data = (String)arg;
			if (data == "d"){
				myHandler.post(myRunnable);
				return;
			}			
			TextView _mytext = (TextView)findViewById(R.id.loading);
			_mytext.setText("Loading League : " + (String)arg + "%");	
			ProgressBar progress = (ProgressBar)findViewById(R.id.progress);
			progress.setProgress(Integer.parseInt((String)arg));		
		} 
	}		
}
