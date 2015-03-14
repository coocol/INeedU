package com.eethan.ineedu.secondaryactivity;

import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class FunctionIntroductionActivity extends BaseActivity{

	private ImageButton backButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("FunctionActivity");
		setContentView(R.layout.about_function_page);
		
		findView();
	}
	
	private void findView() {
		// TODO Auto-generated method stub
		
		backButton = (ImageButton)findViewById(R.id.fucntion_imgbt_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FunctionIntroductionActivity.this.finish();
			}
		});
	}

}
