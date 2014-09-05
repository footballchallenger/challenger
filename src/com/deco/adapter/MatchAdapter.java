package com.deco.adapter;

import com.deco.football.LivingActivity;
import com.deco.football.MatchActivity;
import com.deco.football.R;
import com.deco.model.LeagueModel;
import com.deco.sql.MATCH;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MatchAdapter extends ArrayAdapter<HashMap<String, String>> implements OnClickListener{
	public ArrayList<HashMap<String, String>> lsMatch;
	private LayoutInflater mInflater;
	Context _Context;
	
	public MatchAdapter(Context context, int textViewResourceId, ArrayList<HashMap<String, String>> objects) {
		super(context, textViewResourceId, objects);
		_Context = context;
		this.lsMatch = objects;
		mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View v, ViewGroup parent){ 
		if (lsMatch.size() < 2){
			return mInflater.inflate(R.layout.living_league_item, null);
		}
		
		HashMap<String, String> matchinfo = lsMatch.get(position);
		if (matchinfo != null) {
			String szMatchId = (String)matchinfo.get(MATCH.id);
			if (szMatchId != ""){
		    	// Get Data
		    	String szFirstTime = (String)matchinfo.get(MATCH.first_time);
		    	String szHomeGoals = "";
		    	String szAwayGoals = "";
		    	int nMatchStatus = Integer.parseInt((String)matchinfo.get(MATCH.status));
		    	if (nMatchStatus != 17){
			    	szHomeGoals = (String)matchinfo.get(MATCH.home_goals);
			    	szAwayGoals = (String)matchinfo.get(MATCH.away_goals);		    		
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
		    	String szHomeName = (String)matchinfo.get(MATCH.home_name);
		    	String szAwayName = (String)matchinfo.get(MATCH.away_name);
		    	
				// Set Holder
		    	MatchHolder matchholder = null;
		    	if (v != null && v.getId() != 0)
		    		v = null;
		    	
		    	if (v == null){
		    		v = mInflater.inflate(R.layout.living_match_item, null);
					matchholder = new MatchHolder();
					matchholder.row = (TableLayout)v.findViewById(R.id.listrow);
					matchholder.time = (TextView)v.findViewById(R.id.time);
					matchholder.date = (TextView)v.findViewById(R.id.date);
					matchholder.home = (TextView)v.findViewById(R.id.home);
					matchholder.away = (TextView)v.findViewById(R.id.away);
					matchholder.homegoals = (TextView)v.findViewById(R.id.homegoals);
					matchholder.awaygoals = (TextView)v.findViewById(R.id.awaygoals);
					v.setTag(matchholder);
					v.setOnClickListener(this);
					v.setId(0);
		    	}
		    	else{
		    		matchholder = (MatchHolder) v.getTag();
		    	}
		    	
		    	matchholder.match_id = Integer.parseInt(matchinfo.get(MATCH.id));
				matchholder.time.setText(szTime);
				matchholder.date.setText(szDate);
				matchholder.home.setText(szHomeName);
				matchholder.away.setText(szAwayName);
				matchholder.homegoals.setText(szHomeGoals);
				matchholder.awaygoals.setText(szAwayGoals);
				
				//if (nMatchStatus == 4){
					//matchholder.row.setBackgroundColor(Color.parseColor("#00ff00"));
				//}
				//else{
					//matchholder.row.setBackgroundColor(Color.parseColor("#0000FF"));
				//}
		    }
			else
			{
				LeaguehHolder leagueholder = null;
		    	if (v != null && v.getId() != 1)
		    		v = null;		
		    	
		    	if (v == null){				
					v = mInflater.inflate(R.layout.living_league_item, null);
					leagueholder = new LeaguehHolder();
					leagueholder.league_name = (TextView)v.findViewById(R.id.league_name);
					v.setTag(leagueholder);
					v.setId(1);
		    	}
		    	else{
		    		leagueholder = (LeaguehHolder) v.getTag();
		    	}
		    	
		    	String szLeagueId = (String)matchinfo.get("league_id");
		    	LeagueModel mdlLeague = new LeagueModel(_Context);
		    	String szLeagueName = mdlLeague.getLeagueById(szLeagueId);		    	
		    	leagueholder.league_name.setText(szLeagueName);
			}
		}			
		return v;
	}
	
	@Override
	public void onClick(View v) {
		if (v == null)
			return;
		
		if (v.getId() == 1)
			return;
		
		MatchHolder holder = (MatchHolder)v.getTag();
		Intent intent = new Intent(_Context, MatchActivity.class);
		intent.putExtra("matchid", Integer.toString(holder.match_id));	
		_Context.startActivity(intent);		
	}    
	
    static class MatchHolder{
    	int					match_id;
    	public TableLayout 	row;
    	public TextView 	time;
    	public TextView 	date;
    	public TextView 	home;
    	public TextView 	away;
    	public TextView 	homegoals;
    	public TextView 	awaygoals;	    	
    }
    
    static class LeaguehHolder{
    	public TextView league_name;
    }    
}