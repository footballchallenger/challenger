package com.deco.football;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.deco.helper.Helper;
import com.deco.model.MatchModel;
import com.deco.model.UserModel;
import com.deco.service.TeamImgService;
import com.deco.sql.MATCH;
import com.deco.sql.USER;

public class MatchActivity extends Activity {
	HashMap<String, String> _pMatch = new HashMap<String, String>();
	HashMap<String, String> _pUser = new HashMap<String, String>();
	private Context _context = this; 
	
	// 
	private int _nBetType = 0;
	private int _nBetTeam = 0;
	private int _nHomeGoals = 0;
	private int _nAwayGoals = 0;
	
	private int _handicap = 4;
	private int _homeback = 90;
	private int _awayback = 90;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match);
		
	    Intent intent = getIntent();
	    String szMatchId = intent.getStringExtra("matchid");
	    
	    // Get Match Info From Database
	    MatchModel mdlModel = new MatchModel(this);
	    _pMatch = mdlModel.getMatchById(szMatchId);

		// Get User From Database;
		UserModel mdlUser = new UserModel(this);
		_pUser = mdlUser.getLastUser();		
		
		// Set Team Name 
		TextView homename = (TextView) findViewById(R.id.homename);
		TextView awayname = (TextView) findViewById(R.id.awayname);
		String szHomeName = _pMatch.get(MATCH.home_name);
		String szAwayName = _pMatch.get(MATCH.away_name);		
		homename.setText(szHomeName);
		awayname.setText(szAwayName);
		
		// Set Time to Gui
		String szFirstTime = _pMatch.get(MATCH.first_time);
		setTimeToGui(szFirstTime);
	    
        // Set Odds List
        setMatchResultOdds();
        
		// Set Image Avatar
		TeamImgService svImage = new TeamImgService(this);
		ImageWatcher wtcImage = new ImageWatcher();
		svImage.addObserver(wtcImage);

		if (!setTeamImage(_pMatch.get(MATCH.team_home_id), R.id.homeimg))
			svImage.getImageFromId(_pMatch.get(MATCH.team_home_id));
		
		if (!setTeamImage(_pMatch.get(MATCH.team_away_id), R.id.awayimg))
			svImage.getImageFromId(_pMatch.get(MATCH.team_away_id));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.match, menu);
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
	
	public void setTimeToGui(String szFirstTime)
	{
        try {
	    	String szDate = "";
	    	String szTime = "";         	
        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        	Date date = df.parse(szFirstTime);
        	df = new SimpleDateFormat("MMM dd yyyy");
        	szDate = df.format(date);
        	df = new SimpleDateFormat("HH:mm");
        	szTime = df.format(date);
    		TextView dateview = (TextView) findViewById(R.id.matchdate);
    		dateview.setText(szDate);
    		TextView timeview = (TextView) findViewById(R.id.matchtime);
    		timeview.setText(szTime);
        } catch (ParseException e) {
        }		
	}
	
	public boolean setTeamImage(String szTeamId, int nViewId){
		String szFilePath = "team" + szTeamId + ".png";
		File imgFile = _context.getFileStreamPath(szFilePath);
		if(imgFile.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    if (myBitmap == null)
		    	return false;
		    
		    ImageView myImage = (ImageView) findViewById(nViewId);
		    myImage.setImageBitmap(myBitmap);
		    return true;
		}		
		return false;
	}
	
	public void onNullClick(View view){
	}	
	
	public void onCloseClick(View view){
    	LinearLayout panel = (LinearLayout)findViewById(R.id.betpanel);
    	Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_down);
    	panel.startAnimation(slide);		
    	panel.setVisibility(View.INVISIBLE);
	}
	
	public void onMoreCashBtnClick(View view){
    	int nCashValue =  Integer.parseInt((String)view.getTag());
    	
    	TextView txtBetTo = (TextView)findViewById(R.id.txtBetTo);
    	TextView txtYourCash = (TextView)findViewById(R.id.txtYourCash);
    	TextView txtGetBack = (TextView)findViewById(R.id.txtGetBack);
    	TextView txtPlace = (TextView)findViewById(R.id.txtPlace);    
    	
    	int nYourCash =  Integer.parseInt(txtYourCash.getText().toString());
    	int nGetBack =  Integer.parseInt(txtGetBack.getText().toString());
    	int nPlace =  Integer.parseInt(txtPlace.getText().toString());
    	
    	if (nYourCash < nCashValue)
    		return;
    	
    	txtPlace.setText(String.valueOf(nPlace + nCashValue));
    	txtYourCash.setText(String.valueOf(nYourCash - nCashValue));
    	
    	nPlace =  Integer.parseInt(txtPlace.getText().toString());
    	if (_nBetType == 0){
    		double nTmp = Helper.genMatchResult(_handicap, _homeback, _awayback, _nBetTeam);
    		nGetBack =  (int)(nPlace * nTmp);
    		txtGetBack.setText(String.valueOf(nGetBack));
    	}
    	if (_nBetType == 1){
    		int nTmp = 0;
    		if (_nBetTeam == 0)
    			nTmp = _homeback;
    			//nTmp = Integer.parseInt(_pMatch.get(MATCH.home_back));
    		else
    			nTmp = _awayback;
    			//nTmp = Integer.parseInt(_pMatch.get(MATCH.away_back));

    		nGetBack =  nPlace + (int)(nPlace * nTmp / 100);
    		txtGetBack.setText(String.valueOf(nGetBack));    		
    	}
    	else{
    		double nTmp = Helper.genCorrectScore(_handicap, _homeback, _awayback, _nHomeGoals, _nAwayGoals);
    		nGetBack =  (int)(nPlace * nTmp);
    		txtGetBack.setText(String.valueOf(nGetBack));    		
    	}    	
	}
	
	public void onOddsResetBtn(View view){
    	TextView txtYourCash = (TextView)findViewById(R.id.txtYourCash);
    	TextView txtGetBack = (TextView)findViewById(R.id.txtGetBack);
    	TextView txtPlace = (TextView)findViewById(R.id.txtPlace);
    	
    	txtYourCash.setText(_pUser.get(USER.cash));
    	txtGetBack.setText("0");
    	txtPlace.setText("0");		
	}
	
    public void onOddsBtnClick(View view){
    	HashMap<String, String> tag = (HashMap<String, String>)view.getTag();
    	
    	LinearLayout panel = (LinearLayout)findViewById(R.id.betpanel);
    	panel.setVisibility(View.VISIBLE);
    	
    	Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
    	panel.startAnimation(slide);
    	
    	TextView txtBetType = (TextView)findViewById(R.id.txtBetType);
    	TextView txtBetTo = (TextView)findViewById(R.id.txtBetTo);
    	TextView txtYourCash = (TextView)findViewById(R.id.txtYourCash);
    	TextView txtGetBack = (TextView)findViewById(R.id.txtGetBack);
    	TextView txtPlace = (TextView)findViewById(R.id.txtPlace);
    	
    	txtYourCash.setText(_pUser.get(USER.cash));
    	txtGetBack.setText("0");
    	txtPlace.setText("0");
    	
    	if (tag.get("type").equals("m")){
    		_nBetType = 0;
    		txtBetType.setText("Match Result");
    		String szBet = tag.get("bet");
    		if (szBet.equals("h")){
    			txtBetTo.setText("Home");
    			_nBetTeam = 0;
    		}
    		else if (szBet.equals("a")){
    			txtBetTo.setText("Away");
    			_nBetTeam = 1;
    		}
    		else{
    			txtBetTo.setText("Draw");
    			_nBetTeam = 2;
    		}
    	}
    	else if (tag.get("type").equals("h")){
    		_nBetType = 1;
    		txtBetType.setText("Handicap");
    		String szBet = tag.get("bet");
    		if (szBet.equals("h")){
    			txtBetTo.setText("Home");
    			_nBetTeam = 0;
    		}
    		else{
    			txtBetTo.setText("Away");
    			_nBetTeam = 1;
    		}
    	}
    	else if (tag.get("type").equals("c")){
    		_nBetType = 2;
    		txtBetType.setText("Correct Score");
    		String szHomeGoals = tag.get("home");
    		String szAwayGoals = tag.get("away");
    		_nHomeGoals = Integer.parseInt(szHomeGoals);
    		_nAwayGoals = Integer.parseInt(szAwayGoals);
    		String szResult = String.format("%s-%s", szHomeGoals, szAwayGoals);
    		txtBetTo.setText(szResult);
    	}    
    }		
	
	public void onTabClick(View view){
		LinearLayout tabs = (LinearLayout)findViewById(R.id.typetab);
		for (int i=0; i<tabs.getChildCount(); i++){
			TextView tab = (TextView)tabs.getChildAt(i);
			tab.setTextColor(Color.parseColor("#AAAAAA"));
			tab.setTypeface(null, Typeface.NORMAL);
		}
		
		TextView curTab = (TextView)view;
		curTab.setTextColor(Color.parseColor("#FFFFFF"));
		curTab.setTypeface(null, Typeface.BOLD);
		String tag = (String)curTab.getTag();
		int nIndex = Integer.parseInt(tag);
		switch (nIndex){
		case 0:
			setMatchResultOdds();
			break;
		case 1:
			setCorrectScoreOdds();
			break;
		case 2:
			setHandicapOdds();
			break;			
		}
	}	
	
    public void setMatchResultOdds()
    {
		LinearLayout column1 = (LinearLayout)this.findViewById(R.id.oddscolumn1);
		LinearLayout column2 = (LinearLayout)this.findViewById(R.id.oddscolumn2);
		LinearLayout column3 = (LinearLayout)this.findViewById(R.id.oddscolumn3);
		column1.removeAllViews();
		column2.removeAllViews();
		column3.removeAllViews();
		
		// Set Home Odds
		View wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		TextView oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		TextView winback = (TextView)wrapper.findViewById(R.id.winback);
		TableRow btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
		
		oddsresult.setText("Home");
		double dbOdds = Helper.genMatchResult(_handicap, _homeback, _awayback, 0);
		winback.setText("1/" + String.valueOf(dbOdds));
    	HashMap<String, String> tag = new HashMap<String, String>();
    	tag.put("type", "m");		
		tag.put("bet", "h");
		btnOdds.setTag(tag);
		column1.addView(wrapper);	
		
		// Set Away Odds
		wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		winback = (TextView)wrapper.findViewById(R.id.winback);
		btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);

		oddsresult.setText("Away");
		dbOdds = Helper.genMatchResult(_handicap, _homeback, _awayback, 1);
		winback.setText("1/" + String.valueOf(dbOdds));
    	tag = new HashMap<String, String>();
    	tag.put("type", "m");		
		tag.put("bet", "a");
		btnOdds.setTag(tag);
		column3.addView(wrapper);	
		
		// Set Draw Odds
		wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		winback = (TextView)wrapper.findViewById(R.id.winback);
		btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);

		oddsresult.setText("Draw");
		dbOdds = Helper.genMatchResult(_handicap, _homeback, _awayback, 2);
		winback.setText("1/" + String.valueOf(dbOdds));
    	tag = new HashMap<String, String>();
    	tag.put("type", "m");			
		tag.put("bet", "d");
		btnOdds.setTag(tag);
		column2.addView(wrapper);			
    }    
    
    public void setHandicapOdds()
    {
		LinearLayout column1 = (LinearLayout)this.findViewById(R.id.oddscolumn1);
		LinearLayout column2 = (LinearLayout)this.findViewById(R.id.oddscolumn2);
		LinearLayout column3 = (LinearLayout)this.findViewById(R.id.oddscolumn3);
		column1.removeAllViews();
		column2.removeAllViews();
		column3.removeAllViews();

		// Set Home Odds
		View wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		TextView oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		TextView winback = (TextView)wrapper.findViewById(R.id.winback);
		TableRow btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
		
		oddsresult.setText("Home");
		double tmp = 1 + (double)_homeback / 100;
		winback.setText("1/" + String.valueOf(tmp));
    	HashMap<String, String> tag = new HashMap<String, String>();
    	tag.put("type", "h");		
		tag.put("bet", "h");
		btnOdds.setTag(tag);	
		column1.addView(wrapper);	
		
		// Set Away Odds
		wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		winback = (TextView)wrapper.findViewById(R.id.winback);
		btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);

		oddsresult.setText("Away");
		tmp = 1 + (double)_awayback / 100;
		winback.setText("1/" + String.valueOf(tmp));
    	tag = new HashMap<String, String>();
    	tag.put("type", "h");			
		tag.put("bet", "a");
		btnOdds.setTag(tag);		
		column3.addView(wrapper);				
    }
    
	public void setCorrectScoreOdds(){
		LinearLayout column1 = (LinearLayout)this.findViewById(R.id.oddscolumn1);
		LinearLayout column2 = (LinearLayout)this.findViewById(R.id.oddscolumn2);
		LinearLayout column3 = (LinearLayout)this.findViewById(R.id.oddscolumn3);
		column1.removeAllViews();
		column2.removeAllViews();
		column3.removeAllViews();
		new LoadOdds().execute();
	}	
	
	class LoadOdds extends AsyncTask<Integer, Integer, Integer>{
	    @Override
	    protected Integer doInBackground(Integer... input) {
			for (int i=0; i<5; i++){
				for (int j=0; j<5; j++){
					Integer[] values = new Integer[] {i, j};
					publishProgress(values);
				}
			}	    	
	        return 0;
	    }
	    
        @Override
        protected void onProgressUpdate(Integer... values) {
        	
    		LinearLayout column1 = (LinearLayout)findViewById(R.id.oddscolumn1);
    		LinearLayout column2 = (LinearLayout)findViewById(R.id.oddscolumn2);
    		LinearLayout column3 = (LinearLayout)findViewById(R.id.oddscolumn3);      
    		int i = values[0];
    		int j = values[1];
    		
        	HashMap<String, String> tag = new HashMap<String, String>();
        	tag.put("type", "c");
        	tag.put("home", String.valueOf(i));
        	tag.put("away", String.valueOf(j));
    		
			View wrapper = LayoutInflater.from(_context).inflate(R.layout.match_odds_button, null);
			TextView oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
			TextView winback = (TextView)wrapper.findViewById(R.id.winback);
			TableRow btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
			btnOdds.setTag(tag);
			
			String tmp = String.format("%d-%d", i, j);
			oddsresult.setText(tmp);
			
			if (i > j && j < 3){
				double dbOdds = Helper.genCorrectScore(_handicap, _homeback, _awayback, i, j);
				winback.setText("1/" + String.valueOf((int)dbOdds));					
				column1.addView(wrapper);
			}
			
			if (i < j && i < 3){
				double dbOdds = Helper.genCorrectScore(_handicap, _homeback, _awayback, i, j);
				winback.setText("1/" + String.valueOf((int)dbOdds));
				column3.addView(wrapper);
			}
				
			if (i==j && i < 3){
				double dbOdds = Helper.genCorrectScore(_handicap, _homeback, _awayback, i, j);
				winback.setText("1/" + String.valueOf((int)dbOdds));
				column2.addView(wrapper);					
			}    		
        }	    
	}		
	
	class ImageWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			HashMap<String, String> result =  (HashMap<String, String>)arg;
			if (result.get("result")=="true"){
				String szTeamId = result.get("teamid");
				String szFilePath = "team" + szTeamId + ".png";
				File imgFile = _context.getFileStreamPath(szFilePath);
				if(imgFile.exists()){
				    if (szTeamId.equals(_pMatch.get(MATCH.team_home_id)))
				    	setTeamImage(_pMatch.get(MATCH.team_home_id), R.id.homeimg);
				    else
				    	setTeamImage(_pMatch.get(MATCH.team_away_id), R.id.awayimg);
				}	
			}
		} 
	}		
}
