package com.deco.football;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.deco.adapter.MatchAdapter;
import com.deco.element.BottomBar;
import com.deco.model.MatchModel;
import com.deco.service.MatchService;
import com.deco.sql.MATCH;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class LivingActivity extends Activity {

	final Handler myHandler = new Handler();
	MatchAdapter _adapter;
	ContentValues _pUser = new ContentValues();
	BottomBar _userBar = new BottomBar(this);
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_living);
		
		// Get Current User
		BottomBar bar = new BottomBar(this);
		bar.updateBottomBar();
		
		// ListView Match
		ArrayList<ContentValues> lsMatch = new ArrayList<ContentValues>();
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
	
	public void updateListView(){
		MatchModel mdlMatch = new MatchModel(this);
		
		ArrayList<ContentValues>lsMatch = mdlMatch.getLiving();
		int nLeague = 0;
		for (int i=0; i<lsMatch.size(); i++){
			int tmp = lsMatch.get(i).getAsInteger(MATCH.league_id);
			if (nLeague != tmp){
				nLeague = tmp;
				ContentValues item = new ContentValues();
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1) {
	        if(resultCode == RESULT_OK){
	    		BottomBar bar = new BottomBar(this);
	    		bar.updateBottomBar();
	        } 
	        if (resultCode == RESULT_CANCELED) {

	        } 
	    } 
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
}
