package com.eethan.ineedu.secondaryactivity;

import com.baidu.a.a.a.a;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.umeng.fb.FeedbackAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class FeedBackActivity extends BaseActivity{

	private ImageButton backButton;
	FeedbackAgent agent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("FeedBackActivity");
		setContentView(R.layout.activity_feedback);
		agent=new FeedbackAgent(FeedBackActivity.this);
		
		findView();
	}
	
	private void findView() {
		// TODO Auto-generated method stub
		
		backButton = (ImageButton)findViewById(R.id.feedback_imgbt_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FeedBackActivity.this.finish();
			}
		});
	}

}
