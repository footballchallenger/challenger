package com.deco.football;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent; 
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import com.deco.element.BottomBar;
import com.deco.helper.Helper;
import com.deco.model.BettingModel;
import com.deco.model.MatchModel;
import com.deco.model.UserModel;
import com.deco.service.BettingService;
import com.deco.service.TeamImgService;
import com.deco.sql.BETTING;
import com.deco.sql.MATCH;
import com.deco.sql.USER;

public class MatchActivity extends Activity {
	
	// Data From SQLite
	ContentValues 				_pMatch = new ContentValues();
	ArrayList<ContentValues> 	_pBetting = new ArrayList<ContentValues>();
	  
	// Bet Info
	private int 	_nBetType = 0;
	private int 	_nBetTeam = 0;
	private int 	_nHomeGoals = 0;
	private int 	_nAwayGoals = 0;
	private int 	_nHandicap = 0;
	private int 	_nHomeback = 0;
	private int 	_nAwayback = 0;
	private String 	_szOddTitle = "";
	
	// Correct Score Panel
	View _vMatchResultPanel;
	View _vCorrectPanel;
	View _vHandicapPanel;
	
	// Variable
	private Context 	_context = this;
	private BottomBar 	_botBar = new BottomBar(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match);
		
		_botBar.updateBottomBar();
		
	    Intent intent = getIntent();
	    String szMatchId = intent.getStringExtra("matchid");
	    
	    // Get Match Info From Database
	    MatchModel mdlModel = new MatchModel(this);
	    _pMatch = mdlModel.getMatchById(szMatchId);

		// Get Betting From Database
		if (_botBar.getUser().size() > 0){
			BettingModel mdlBetting = new BettingModel(this);
			_pBetting = mdlBetting.getBettingByMatchId(_botBar.getUser().getAsString(USER.id), szMatchId);
		}
		
	    String tmp = _pMatch.getAsString(MATCH.handicap);
	    if (tmp!=null && !"".equals(tmp) && !"null".equals(tmp)){
	    	_nHandicap = Integer.parseInt(tmp);
	    	_nHomeback = _pMatch.getAsInteger(MATCH.home_back);
	    	_nAwayback = _pMatch.getAsInteger(MATCH.away_back);
			_vCorrectPanel = LayoutInflater.from(this).inflate(R.layout.match_odds_panel, null);
			_vMatchResultPanel = LayoutInflater.from(this).inflate(R.layout.match_odds_panel, null);
			_vHandicapPanel = LayoutInflater.from(this).inflate(R.layout.match_odds_panel, null);
			initOddsPanel(); 
			setMatchResultOdds();
	    }		
		
		// Set Team Name 
		TextView homename = (TextView) findViewById(R.id.homename);
		TextView awayname = (TextView) findViewById(R.id.awayname);
		String szHomeName = _pMatch.getAsString(MATCH.home_name);
		String szAwayName = _pMatch.getAsString(MATCH.away_name);		
		homename.setText(szHomeName);
		awayname.setText(szAwayName);
		
		// Set Time to Gui
		String szFirstTime = _pMatch.getAsString(MATCH.first_time);
		setTimeToGui(szFirstTime);
        
		// Set Image Avatar
		TeamImgService svImage = new TeamImgService(this);
		ImageWatcher wtcImage = new ImageWatcher();
		svImage.addObserver(wtcImage);

		if (!setTeamImage(_pMatch.getAsString(MATCH.team_home_id), R.id.homeimg))
			svImage.getImageFromId(_pMatch.getAsString(MATCH.team_home_id));
		
		if (!setTeamImage(_pMatch.getAsString(MATCH.team_away_id), R.id.awayimg))
			svImage.getImageFromId(_pMatch.getAsString(MATCH.team_away_id));
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
	
	@Override
	protected void onResume() {
	  _botBar.updateBottomBar();
	  super.onResume();
	}	
	
	@Override
	protected void onPause() {
	  super.onPause();
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
	
	public void onCloseClick(View view){
    	LinearLayout panel = (LinearLayout)findViewById(R.id.betpanel);
    	Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.close_down);
    	panel.startAnimation(slide);		
    	panel.setVisibility(View.INVISIBLE);
	}
	
	public void onConfirmBtnClicked(View view){
		// Set Image Avatar
		TextView btnConfirm = (TextView)view;
		btnConfirm.setText("Betting ...");
		
		BettingService svBetting = new BettingService(this);
		BetWatcher wtcBetting = new BetWatcher();
		svBetting.addObserver(wtcBetting);
		
		String[] betTypes = new String[] {"m", "c", "h"};
		String[] betTeam = new String[] {"h", "a", "d"};
		String szCorrectScore = String.format(Locale.US, "%d-%d", _nHomeGoals, _nAwayGoals);
		String szUserId = _botBar.getUser().getAsString(USER.id);
		String szToken = _botBar.getUser().getAsString(USER.token);
		String szMatchId = _pMatch.getAsString(MATCH.id);
		TextView txtPlace = (TextView)findViewById(R.id.txtPlace);
		String szCash = txtPlace.getText().toString();
		if (Integer.parseInt(szCash) < 1){
			return;
		}
		String szType = betTypes[_nBetType];
		String szBetTo = betTeam[_nBetTeam];
		
		if (szType.equals("c"))
			_szOddTitle = szType + "_" + szCorrectScore;
		else 
			_szOddTitle = szType + "_" + szBetTo;
		
		svBetting.bet(szUserId, szToken, szMatchId, szCash, _szOddTitle);
	}
	
	public void onMoreCashBtnClick(View view){
    	int nCashValue =  Integer.parseInt((String)view.getTag());
    	
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
    		double nTmp = Helper.genMatchResult(_nHandicap, _nHomeback, _nAwayback, _nBetTeam);
    		nGetBack =  (int)(nPlace * nTmp);
    		txtGetBack.setText(String.valueOf(nGetBack));
    	}
    	if (_nBetType == 1){
    		double nTmp = Helper.genCorrectScore(_nHandicap, _nHomeback, _nAwayback, _nHomeGoals, _nAwayGoals);
    		nGetBack =  (int)(nPlace * nTmp);
    		txtGetBack.setText(String.valueOf(nGetBack));   
    	}
    	else{
    		int nTmp = 0;
    		if (_nBetTeam == 0)
    			nTmp = _nHomeback;
    		else
    			nTmp = _nAwayback;

    		nGetBack =  nPlace + (int)(nPlace * nTmp / 100);
    		txtGetBack.setText(String.valueOf(nGetBack)); 		
    	}    	
	}
	
	public void onOddsResetBtn(View view){
    	TextView txtYourCash = (TextView)findViewById(R.id.txtYourCash);
    	TextView txtGetBack = (TextView)findViewById(R.id.txtGetBack);
    	TextView txtPlace = (TextView)findViewById(R.id.txtPlace);
    	
    	txtYourCash.setText(_botBar.getUser().getAsString(USER.cash));
    	txtGetBack.setText("0");
    	txtPlace.setText("0");		
	}
	
    public void onOddsBtnClick(View view){
    	if (_botBar.getUser().size() < 1)
    		return;
    	
    	LinearLayout panel = (LinearLayout)findViewById(R.id.betpanel);
    	panel.setVisibility(View.VISIBLE);
    	
    	Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
    	panel.startAnimation(slide);
    	
    	TextView txtBetType = (TextView)findViewById(R.id.txtBetType);
    	TextView txtBetTo = (TextView)findViewById(R.id.txtBetTo);
    	TextView txtYourCash = (TextView)findViewById(R.id.txtYourCash);
    	TextView txtGetBack = (TextView)findViewById(R.id.txtGetBack);
    	TextView txtPlace = (TextView)findViewById(R.id.txtPlace);
    	
    	txtYourCash.setText(_botBar.getUser().getAsString(USER.cash));
    	txtGetBack.setText("0");
    	txtPlace.setText("0");
    	
    	if (_nBetType == 0){
    		txtBetType.setText("Match Result");
    		_nBetTeam = view.getId();
    		if (_nBetTeam == 0)
    			txtBetTo.setText("Home");
    		else if (_nBetTeam == 1)
    			txtBetTo.setText("Away");
    		else
    			txtBetTo.setText("Draw");
    	}
    	else if (_nBetType == 1){
       		txtBetType.setText("Correct Score");
    		_nHomeGoals = view.getId() / 10;
    		_nAwayGoals = view.getId() % 10;
    		String szResult = String.format(Locale.US, "%d-%d", _nHomeGoals, _nAwayGoals);
    		txtBetTo.setText(szResult);    		    		
    	}
    	else{
    		txtBetType.setText("Handicap");
    		_nBetTeam = view.getId();
    		if (_nBetTeam == 0)
    			txtBetTo.setText("Home");
    		else
    			txtBetTo.setText("Away");
    	}
    }		
	
	public void onTabClick(View view){
		LinearLayout tabs = (LinearLayout)findViewById(R.id.typetab);
		for (int i=0; i<tabs.getChildCount(); i++){
			TextView tab = (TextView)tabs.getChildAt(i);
			tab.setTextColor(Color.parseColor("#666666"));
			tab.setTypeface(null, Typeface.NORMAL);
		}
		
		TextView curTab = (TextView)view;
		curTab.setTextColor(Color.BLACK);
		curTab.setTypeface(null, Typeface.BOLD);
		String tag = (String)curTab.getTag();
		
		if (_nHomeback != 0 && _nAwayback !=0){
			int nIndex = Integer.parseInt(tag);
			_nBetType = nIndex;

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
	}
	
	public void initOddsPanel(){
		initMatchResultPanel();
		initHandicapPanel();
		initCorrectScorePanel();
	}
	
	public void initMatchResultPanel()
	{
		// Init Match Result Panel
		LinearLayout column1 = (LinearLayout)_vMatchResultPanel.findViewById(R.id.oddscolumn1);
		LinearLayout column2 = (LinearLayout)_vMatchResultPanel.findViewById(R.id.oddscolumn2);
		LinearLayout column3 = (LinearLayout)_vMatchResultPanel.findViewById(R.id.oddscolumn3);  		
		View wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		TextView oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		TableRow btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
		
		oddsresult.setText("Home");
		btnOdds.setId(0);
		column1.addView(wrapper);	
		
		wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
		oddsresult.setText("Draw");
		btnOdds.setId(2);
		column2.addView(wrapper);			
				
		wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
		oddsresult.setText("Away");
		btnOdds.setId(1);
		column3.addView(wrapper);		
	}
	
	public void initHandicapPanel()
	{
		// Init Match Result Panel
		LinearLayout column1 = (LinearLayout)_vHandicapPanel.findViewById(R.id.oddscolumn1);
		LinearLayout column3 = (LinearLayout)_vHandicapPanel.findViewById(R.id.oddscolumn3);  		
		
		View wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		TextView oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		TableRow btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
		oddsresult.setText("Home");
		btnOdds.setId(0);
		column1.addView(wrapper);	

		wrapper = LayoutInflater.from(this).inflate(R.layout.match_odds_button, null);
		oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
		btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
		oddsresult.setText("Away");
		btnOdds.setId(1);
		column3.addView(wrapper);		
	}
	
	public void initCorrectScorePanel()
	{
		LinearLayout column1 = (LinearLayout)_vCorrectPanel.findViewById(R.id.oddscolumn1);
		LinearLayout column2 = (LinearLayout)_vCorrectPanel.findViewById(R.id.oddscolumn2);
		LinearLayout column3 = (LinearLayout)_vCorrectPanel.findViewById(R.id.oddscolumn3);  	    	
		for (int i=0; i<5; i++){
			for (int j=0; j<5; j++){
				if (i>j && j<3){
					View wrapper = LayoutInflater.from(_context).inflate(R.layout.match_odds_button, null);
					TextView oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
					TableRow btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
					String tmp = String.format(Locale.US, "%d-%d", i, j);
					oddsresult.setText(tmp);
					btnOdds.setId(i * 10 + j);
					column1.addView(wrapper);
					
					wrapper = LayoutInflater.from(_context).inflate(R.layout.match_odds_button, null);
					oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
					btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
					tmp = String.format(Locale.US, "%d-%d", j, i);
					oddsresult.setText(tmp);
					btnOdds.setId(j * 10 + i);
					column3.addView(wrapper);					
				}
				
				if (i==j && i < 3){
					View wrapper = LayoutInflater.from(_context).inflate(R.layout.match_odds_button, null);
					TextView oddsresult = (TextView)wrapper.findViewById(R.id.oddsresult);
					TableRow btnOdds = (TableRow)wrapper.findViewById(R.id.btnOdds);
					String tmp = String.format(Locale.US, "%d-%d", i, j);
					oddsresult.setText(tmp);
					btnOdds.setId(i * 10 + j);
					column2.addView(wrapper);					
				}  
			}
		}			
	}		
	
    public void setMatchResultOdds()
    {
    	View btnOdds = (View)_vMatchResultPanel.findViewById(0);
    	TextView winback = (TextView)btnOdds.findViewById(R.id.winback);
		double dbOdds = Helper.genMatchResult(_nHandicap, _nHomeback, _nAwayback, 0);
		winback.setText("1/" + String.format("%.2f", dbOdds));   
		checkBettingButton(btnOdds, "h");
		
		btnOdds = (View)_vMatchResultPanel.findViewById(1);
    	winback = (TextView)btnOdds.findViewById(R.id.winback);
		dbOdds = Helper.genMatchResult(_nHandicap, _nHomeback, _nAwayback, 1);
		winback.setText("1/" + String.format("%.2f", dbOdds));
		checkBettingButton(btnOdds, "a");
		
		btnOdds = (View)_vMatchResultPanel.findViewById(2);
    	winback = (TextView)btnOdds.findViewById(R.id.winback);
		dbOdds = Helper.genMatchResult(_nHandicap, _nHomeback, _nAwayback, 2);
		winback.setText("1/" + String.format("%.2f", dbOdds));
		checkBettingButton(btnOdds, "d");
		
		ScrollView v = (ScrollView)findViewById(R.id.oddspanel);
		v.removeAllViews();
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(
		        ViewGroup.LayoutParams.MATCH_PARENT,
		        ViewGroup.LayoutParams.MATCH_PARENT);
		v.addView(_vMatchResultPanel, params);			
    } 
    
    public void setHandicapOdds()
    {
    	View btnOdds = (View)_vHandicapPanel.findViewById(0);
    	TextView winback = (TextView)btnOdds.findViewById(R.id.winback);
		double tmp = 1 + (double)_nHomeback / 100;
		winback.setText("1/" + String.format("%.2f", tmp));   	
		checkBettingButton(btnOdds, "h");
		
		btnOdds = (View)_vHandicapPanel.findViewById(1);
    	winback = (TextView)btnOdds.findViewById(R.id.winback);
		tmp = 1 + (double)_nAwayback / 100;
		winback.setText("1/" + String.format("%.2f", tmp));   
		checkBettingButton(btnOdds, "a");
		
		ScrollView v = (ScrollView)findViewById(R.id.oddspanel);
		v.removeAllViews();
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(
		        ViewGroup.LayoutParams.MATCH_PARENT,
		        ViewGroup.LayoutParams.MATCH_PARENT);
		v.addView(_vHandicapPanel, params);	    	
    }
    
	public void setCorrectScoreOdds(){
		for (int i=0; i<5; i++){
			for (int j=0; j<5; j++){
				if (i > j && j < 3){
					View btnOdds = (View)_vCorrectPanel.findViewById(i * 10 + j);
					TextView winback = (TextView)btnOdds.findViewById(R.id.winback);
					double dbOdds = Helper.genCorrectScore(_nHandicap, _nHomeback, _nAwayback, i, j);
					winback.setText("1/" + String.valueOf((int)dbOdds));
					String tmp = String.format(Locale.US, "%d-%d", i, j);
					checkBettingButton(btnOdds, tmp);
					
					btnOdds = (View)_vCorrectPanel.findViewById(j * 10 + i);
					winback = (TextView)btnOdds.findViewById(R.id.winback);
					dbOdds = Helper.genCorrectScore(_nHandicap, _nHomeback, _nAwayback, j, i);
					winback.setText("1/" + String.valueOf((int)dbOdds));
					tmp = String.format(Locale.US, "%d-%d", j, i);
					checkBettingButton(btnOdds, tmp);					
				}
					
				if (i==j && i < 3){
					View btnOdds = (View)_vCorrectPanel.findViewById(i * 10 + j);
					TextView winback = (TextView)btnOdds.findViewById(R.id.winback);					
					double dbOdds = Helper.genCorrectScore(_nHandicap, _nHomeback, _nAwayback, i, j);
					winback.setText("1/" + String.valueOf((int)dbOdds));
					String tmp = String.format(Locale.US, "%d-%d", i, j);
					checkBettingButton(btnOdds, tmp);					
				}  					
			}
		}
		ScrollView v = (ScrollView)findViewById(R.id.oddspanel);
		v.removeAllViews();
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(
		        ViewGroup.LayoutParams.MATCH_PARENT,
		        ViewGroup.LayoutParams.MATCH_PARENT);
		v.addView(_vCorrectPanel, params);	   		
	}
	
	public void checkBettingButton(View v, String szData){
		for (int i=0; i<_pBetting.size(); i++){
			String szTitle = _pBetting.get(i).getAsString(BETTING.odds_title);
			String szType = szTitle.substring(0, 1);
			if (_nBetType == 0 && szType.equals("m")){
				if (szTitle.substring(2).equals(szData)){
					v.setBackgroundResource(R.drawable.style_button_odds_bet);
					break;
				}
			}

			else if (_nBetType == 1 && szType.equals("c")){
				if (szTitle.substring(2).equals(szData)){
					v.setBackgroundResource(R.drawable.style_button_odds_bet);
					break;
				}
			}					
			
			else if (_nBetType == 2 && szType.equals("h")){
				if (szTitle.substring(2).equals(szData)){
					v.setBackgroundResource(R.drawable.style_button_odds_bet);
					break;
				}
			}
		}
	}
	
	class ImageWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			ContentValues result =  (ContentValues)arg;
			if (result.get("result")=="true"){
				String szTeamId = result.getAsString("teamid");
				String szFilePath = "team" + szTeamId + ".png";
				File imgFile = _context.getFileStreamPath(szFilePath);
				if(imgFile.exists()){
				    if (szTeamId.equals(_pMatch.getAsString(MATCH.team_home_id)))
				    	setTeamImage(_pMatch.getAsString(MATCH.team_home_id), R.id.homeimg);
				    else
				    	setTeamImage(_pMatch.getAsString(MATCH.team_away_id), R.id.awayimg);
				}	
			}
		} 
	}
	
	class BetWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			ContentValues result =  (ContentValues)arg;
			if (result.get("result")=="true"){
				try {				
					String szJson = result.getAsString("data");
					JSONObject objData = new JSONObject(szJson);
					String szId 	  = objData.getString(BETTING.id); 					
					
					String szUserId = _botBar.getUser().getAsString(USER.id);
					String szMatchId = _pMatch.getAsString(MATCH.id);
					TextView txtPlace = (TextView)findViewById(R.id.txtPlace);
					String szCash = txtPlace.getText().toString();
					if (Integer.parseInt(szCash) < 1){
						return;
					}
					
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
					String szTime = sdf.format(c.getTime());			        	

					ContentValues values = new ContentValues();
					values.put(BETTING.id, szId);
					values.put(BETTING.user_id, szUserId);
					values.put(BETTING.match_id, szMatchId);
					values.put(BETTING.odds_title, _szOddTitle);
					values.put(BETTING.cash, szCash);					
					values.put(BETTING.get, 0);
					values.put(BETTING.status, 0);
					values.put(BETTING.time, szTime);
					
					BettingModel mdlBetting = new BettingModel(_context);
					mdlBetting.insert(values);
					onCloseClick(null);
					
					// Get Betting From Database
					_pBetting = mdlBetting.getBettingByMatchId(_botBar.getUser().getAsString(USER.id), _pMatch.getAsString(MATCH.id));
					if (_nBetType == 0)
						setMatchResultOdds();
					else if (_nBetType == 1)
						setCorrectScoreOdds();
					else 
						setHandicapOdds();
					
					values = new ContentValues();
					values.put(USER.cash, _botBar.getUser().getAsInteger(USER.cash) - Integer.parseInt(szCash));
					UserModel mdlUser = new UserModel(_context);
					mdlUser.update(_botBar.getUser().getAsString(USER.id), values);
					_botBar.updateUserData();
					_botBar.updateBettingCount();
					_botBar.updateBottomBar();
				} catch (JSONException e) {
				}					
			}
			else{
				//String szJson = result.getAsString("msg");
			}
			
			TextView btnLogin = (TextView)findViewById(R.id.btnConfirm);
			btnLogin.setText("Confirm");
			
		} 
	}	
}
