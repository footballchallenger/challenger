package com.deco.adapter;

import com.deco.football.R;
import com.deco.sql.BETTING;
import com.deco.sql.MATCH;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BettingAdapter extends ArrayAdapter<ContentValues> implements OnClickListener{
	public ArrayList<ContentValues> lsBetting;
	private LayoutInflater mInflater;
	Context _context;

	public BettingAdapter(Context context, int textViewResourceId, ArrayList<ContentValues> objects) {
		super(context, textViewResourceId, objects);
		_context = context;
		this.lsBetting = objects;
		mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View v, ViewGroup parent){ 

		ContentValues bettingData = lsBetting.get(position);
		
    	// Get Data
    	String szFirstTime = (String)bettingData.get(MATCH.first_time);
    	String szHomeGoals = "";
    	String szAwayGoals = "";
    	int nMatchStatus = bettingData.getAsInteger(MATCH.status);
    	if (nMatchStatus != 17){
	    	szHomeGoals = (String)bettingData.get(MATCH.home_goals);
	    	szAwayGoals = (String)bettingData.get(MATCH.away_goals);		    		
    	}
    	String szDate = "";
    	String szTime = ""; 

        try {
        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        	Date date = df.parse(szFirstTime);
        	df = new SimpleDateFormat("MM-dd");
        	szDate = df.format(date);
        	df = new SimpleDateFormat("HH:mm");
        	szTime = df.format(date);		
        } catch (ParseException e) {
        }
        
    	// Get Team From DB
    	String szHomeName = (String)bettingData.get(MATCH.home_name);
    	String szAwayName = (String)bettingData.get(MATCH.away_name);
    	
		// Set Holder
    	BettingHolder bettingholder;
    	if (v == null){
    		v = mInflater.inflate(R.layout.betting_list_item, null);
    		bettingholder = new BettingHolder();
    		bettingholder.txtTime = (TextView)v.findViewById(R.id.txtTime);
    		bettingholder.txtDate = (TextView)v.findViewById(R.id.txtDate);
    		bettingholder.txtHome = (TextView)v.findViewById(R.id.txtHome);
    		bettingholder.txtAway = (TextView)v.findViewById(R.id.txtAway);
    		bettingholder.txtHomeGoals = (TextView)v.findViewById(R.id.txtHomeGoals);
    		bettingholder.txtAwayGoals = (TextView)v.findViewById(R.id.txtAwayGoals);
    		bettingholder.txtBetType = (TextView)v.findViewById(R.id.txtBetType);
    		bettingholder.txtBetTo = (TextView)v.findViewById(R.id.txtBetTo);
    		bettingholder.txtCash = (TextView)v.findViewById(R.id.txtCash);
    		bettingholder.txtGetBack = (TextView)v.findViewById(R.id.txtGetBack);
			v.setTag(bettingholder);
			v.setOnClickListener(this);
    	}
    	else{
    		bettingholder = (BettingHolder) v.getTag();
    	}
    	
    	// Set Data To View
    	bettingholder.nBetId = bettingData.getAsInteger(MATCH.id);
    	bettingholder.txtTime.setText(szTime);
    	bettingholder.txtDate.setText(szDate);
    	bettingholder.txtHome.setText(szHomeName);
    	bettingholder.txtAway.setText(szAwayName);
    	bettingholder.txtHomeGoals.setText(szHomeGoals);
    	bettingholder.txtAwayGoals.setText(szAwayGoals);
    	bettingholder.txtCash.setText(bettingData.getAsString(BETTING.cash));
    	
		String szTitle = bettingData.getAsString(BETTING.odds_title);
		String szType = szTitle.substring(0, 1);
		if (szType.equals("m")){
			bettingholder.txtBetType.setText("Match");
			String szBetTo = getBetToFromTitle(szTitle);
			bettingholder.txtBetTo.setText(szBetTo);
		}
		else if (szType.equals("c")){
			bettingholder.txtBetType.setText("Score");
			String szBetTo = szTitle.substring(2);
			bettingholder.txtBetTo.setText(szBetTo);
		}
		else{
			bettingholder.txtBetType.setText("Handi");
			String szBetTo = getBetToFromTitle(szTitle);
			bettingholder.txtBetTo.setText(szBetTo);
		}
    	
    	int nBetStatus = bettingData.getAsInteger(BETTING.status);
    	bettingholder.txtCash.setText(bettingData.getAsString(BETTING.cash));
    	if (nBetStatus == 0){
    		bettingholder.txtGetBack.setText("IN PROGESS");
    		bettingholder.txtGetBack.setTextColor(Color.BLUE);
    	}
    	else if (nBetStatus == 1){
    		String tmp = String.format("W %s", bettingData.getAsString(BETTING.get));
    		bettingholder.txtGetBack.setText(tmp);
    		bettingholder.txtGetBack.setTextColor(Color.BLUE);
    	}
    	else{
    		bettingholder.txtGetBack.setText("LOSE");
    		bettingholder.txtGetBack.setTextColor(Color.RED);
    	}

		return v;
	}
	
	@Override
	public void onClick(View v) {
	}    
	
	private String getBetToFromTitle(String szTitle){
		String szType = szTitle.substring(2);
		if (szType.equals("h"))
			return "Home";
		else if (szType.equals("a"))
			return "Away";
		else
			return "Draw";
	}
	
    static class BettingHolder{
    	int					nBetId;
    	public TextView 	txtTime;
    	public TextView 	txtDate;
    	public TextView 	txtHome;
    	public TextView 	txtAway;
    	public TextView 	txtHomeGoals;
    	public TextView 	txtAwayGoals;
    	public TextView		txtBetType;
    	public TextView		txtBetTo;
    	public TextView		txtCash;
    	public TextView		txtGetBack;
    }
}