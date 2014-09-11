package com.deco.element;

import com.deco.football.BettingActivity;
import com.deco.football.LivingActivity;
import com.deco.football.LoginActivity;
import com.deco.football.R;
import com.deco.model.BettingModel;
import com.deco.model.UserModel;
import com.deco.sql.USER;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BottomBar implements  View.OnClickListener{
	
	private static ContentValues _pUser = new ContentValues();
	private static int _nBettingCount = 0;
	
	private Context _context;

	public BottomBar(Context context){
		_context = context;
	}

	public ContentValues getUser(){
		return _pUser;
	}
	
	public void onClick(View v) {
		Activity activity = (Activity)_context;
		
		// Show profile Click
		if (v.getId() == R.id.btnProfile || v.getId() == R.id.imgAvatar){
			ViewGroup panel = (ViewGroup)activity.findViewById(R.id.userprofile);
	    	panel.setVisibility(View.VISIBLE);
	    	Animation slide = AnimationUtils.loadAnimation(_context, R.anim.slide_up);
	    	panel.startAnimation(slide);				
		} 
		// Sign Out Click
		else if (v.getId() == R.id.btnSignOut){
			UserModel mdlUser = new UserModel(_context);
			mdlUser.signOut();
			Intent intent = new Intent(_context, LivingActivity.class);
			_context.startActivity(intent);
			Activity ac = (Activity)_context;
			ac.finish();				
		}
		// Close Profile Click
		else if (v.getId() == R.id.btnCloseProfile){
			ViewGroup panel = (ViewGroup)activity.findViewById(R.id.userprofile);
	    	Animation slide = AnimationUtils.loadAnimation(_context, R.anim.close_down);
	    	panel.startAnimation(slide);		    	
	    	panel.setVisibility(View.INVISIBLE);			
		}
		// Show Login Click
		else if (v.getId() == R.id.btnShowLogin){
			Intent intent = new Intent(_context, LoginActivity.class);
			Activity ac = (Activity)_context;
			ac.startActivityForResult(intent, 1);
		}
		else if (v.getId() == R.id.btnShowBetting){
			Intent intent = new Intent(_context, BettingActivity.class);
			_context.startActivity(intent);
		}
	}      
	
	public void updateUserData(){
		UserModel mdlUser = new UserModel(_context);
		_pUser = mdlUser.getLastUser();
	}
	
	public void updateBettingCount(){
		if (_pUser.size() > 0){
			BettingModel mdlBetting = new BettingModel(_context);
			_nBettingCount = mdlBetting.getBettingCount(_pUser.getAsString(USER.id));			
		}
	}
	
	public void updateBottomBar()
	{
		LinearLayout.LayoutParams params = 
				new LinearLayout.LayoutParams(
		        ViewGroup.LayoutParams.MATCH_PARENT,
		        ViewGroup.LayoutParams.MATCH_PARENT);		
		
		Activity activity = (Activity)_context;
		ViewGroup userbar = (ViewGroup)activity.findViewById(R.id.userbar);
		userbar.removeAllViews();
		
		if (_pUser.size() > 0){
			View vUserBar = LayoutInflater.from(_context).inflate(R.layout.user_bar, null);
			userbar.addView(vUserBar, params);
			
			// Set User Name, User Cash
			TextView username = (TextView)activity.findViewById(R.id.username);
			username.setText(_pUser.getAsString(USER.name));
			TextView usercash = (TextView)activity.findViewById(R.id.usercash);
			usercash.setText("$ " + _pUser.getAsString(USER.cash));
			
			// Set User Betting Count 
			TextView txtBettingCount = (TextView)activity.findViewById(R.id.txtBettingCount);
			txtBettingCount.setText(String.valueOf(_nBettingCount));
			
			// Set Listener
			ViewGroup btnProfile = (ViewGroup)activity.findViewById(R.id.btnProfile);
        	btnProfile.setOnClickListener(this);
        	
        	View imgAvatar = (View)activity.findViewById(R.id.imgAvatar);
        	imgAvatar.setOnClickListener(this);   	
        	
        	View btnSignOut = (View)activity.findViewById(R.id.btnSignOut);
        	btnSignOut.setOnClickListener(this);		
    		
        	View btnCloseProfile = (View)activity.findViewById(R.id.btnCloseProfile);
        	btnCloseProfile.setOnClickListener(this);

        	ViewGroup btnShowBetting = (ViewGroup)activity.findViewById(R.id.btnShowBetting);
        	btnShowBetting.setOnClickListener(this);        	
		}
		else{
			View vLoginBar = LayoutInflater.from(_context).inflate(R.layout.user_login_bar, null);
			userbar.addView(vLoginBar, params);
			
			// Set Listener
			View btnShowLogin = (View)activity.findViewById(R.id.btnShowLogin);
    		btnShowLogin.setOnClickListener(this);	  			
		}
	}
}
