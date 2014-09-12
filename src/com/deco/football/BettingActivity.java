package com.deco.football;

import java.util.ArrayList;

import com.deco.adapter.BettingAdapter;
import com.deco.element.BottomBar;
import com.deco.model.MatchModel;
import com.deco.sql.USER;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class BettingActivity extends Activity {

	private BottomBar 	_botBar = new BottomBar(this);
	BettingAdapter 		_adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_betting);
		
		// Get Current User
		_botBar.updateUserData();
		_botBar.updateBettingCount();
		_botBar.updateBottomBar();
		
		// ListView Match 
		MatchModel mdlMatch = new MatchModel(this);
		ArrayList<ContentValues>lsBetting = mdlMatch.getBettingList(_botBar.getUser().getAsString(USER.id));
		_adapter = new BettingAdapter(this, 0, lsBetting);
		ListView listView = (ListView)findViewById(R.id.matchlist);
		listView.setAdapter(_adapter); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.betting, menu);
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
	
	@Override
	protected void onResume() {
		_botBar.updateBottomBar();
		super.onResume();
	}
}
