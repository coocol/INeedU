package com.eethan.ineedu.secondaryactivity;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.setting.ShakeAndSound;
import com.eethan.ineedu.util.CustomApplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class RemindActivity extends BaseActivity{
	
	private ImageButton backButton;
	private ImageButton shakeButton;
	private ImageButton soundButton;
	private SPHelper SPHelper=new SPHelper(this);
	private boolean isShake;
	private boolean isSound;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("RemindActivity");
		setContentView(R.layout.remind_page);
		isShake=SPHelper.isShake();
		isSound=SPHelper.isSound();
		
		backButton=(ImageButton)findViewById(R.id.remind_imgbt_back);
		shakeButton=(ImageButton)findViewById(R.id.remind_page_shake_button);
		soundButton=(ImageButton)findViewById(R.id.remind_page_sound_button);
		
		if(isShake)
			shakeButton.setImageDrawable(getResources().getDrawable(R.drawable.notice_open));
		else
			shakeButton.setImageDrawable(getResources().getDrawable(R.drawable.notice_close));
		if(isSound)
			soundButton.setImageDrawable(getResources().getDrawable(R.drawable.notice_open));
		else
			soundButton.setImageDrawable(getResources().getDrawable(R.drawable.notice_close));
		
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		shakeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isShake)
					shakeButton.setImageDrawable(getResources().getDrawable(R.drawable.notice_open));
				else
					shakeButton.setImageDrawable(getResources().getDrawable(R.drawable.notice_close));
				isShake=!isShake;
			}
		});

		soundButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isSound)
					soundButton.setImageDrawable(getResources().getDrawable(R.drawable.notice_open));
				else
					soundButton.setImageDrawable(getResources().getDrawable(R.drawable.notice_close));
				isSound=!isSound;
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SPHelper.changeShake(isShake);
		SPHelper.changeSound(isSound);
	}
}
