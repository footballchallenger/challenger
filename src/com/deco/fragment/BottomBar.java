package com.deco.fragment;

import com.deco.football.LivingActivity;
import com.deco.football.LoginActivity;
import com.deco.football.R;
import com.deco.model.UserModel;
import com.deco.sql.USER;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BottomBar extends Fragment implements  View.OnClickListener{
	
	ContentValues _pUser = new ContentValues();
	
	private View 	_userBar;
	private View 	_loginBar;
	
	private Context _context;
	private LayoutInflater _inflater;
	
	public BottomBar(Context context){
		_context = context;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	_inflater = inflater;
    	return updateBar();
    }	
    
	public void onClick(View v) {
		Activity activity = (Activity)_context;
		
		// Show profile Click
		if (v.getId() == R.id.btnProfile || v.getId() == R.id.imgAvatar){
	    	LinearLayout panel = (LinearLayout)activity.findViewById(R.id.userprofile);
	    	panel.setVisibility(View.VISIBLE);
	    	Animation slide = AnimationUtils.loadAnimation(_context, R.anim.slide_up);
	    	panel.startAnimation(slide);				
		} 
		// Sign Out Click
		else if (v.getId() == R.id.btnSignOut){
			UserModel mdlUser = new UserModel(_context);
			mdlUser.signOut();
			Intent intent = new Intent(_context, LivingActivity.class);
			startActivity(intent);
			Activity ac = (Activity)_context;
			ac.finish();				
		}
		// Close Profile Click
		else if (v.getId() == R.id.btnCloseProfile){
	    	LinearLayout panel = (LinearLayout)activity.findViewById(R.id.userprofile);
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
	}      
	
	public View updateBar()	{
		// Set Parents Click Listener
    	Activity activity = (Activity)_context;
    	Button btnSignOut = (Button)activity.findViewById(R.id.btnSignOut);
    	btnSignOut.setOnClickListener(this);		
		
    	TextView btnCloseProfile = (TextView)activity.findViewById(R.id.btnCloseProfile);
    	btnCloseProfile.setOnClickListener(this);		
    	
    	// Set Login Bar Listener
		_userBar = _inflater.inflate(R.layout.user_bar, null);
    	LinearLayout btnProfile = (LinearLayout)_userBar.findViewById(R.id.btnProfile);
    	btnProfile.setOnClickListener(this);
    	ImageView imgAvatar = (ImageView)_userBar.findViewById(R.id.imgAvatar);
    	imgAvatar.setOnClickListener(this);
    	
		_loginBar = _inflater.inflate(R.layout.login_bar, null);
		TextView btnShowLogin = (TextView)_loginBar.findViewById(R.id.btnShowLogin);
		btnShowLogin.setOnClickListener(this);	     	
    	
    	UserModel mdlUser = new UserModel(_context);
    	_pUser = mdlUser.getLastUser();
    	if (_pUser.size() > 0){
			TextView username = (TextView)_userBar.findViewById(R.id.username);
			TextView usercash = (TextView)_userBar.findViewById(R.id.usercash);
			
			username.setText(_pUser.getAsString(USER.name));
			usercash.setText("$ " + _pUser.get(USER.cash));
	    	
			return _userBar;
    	}
    	else{
    		return _loginBar;
    	}
	}	
}
