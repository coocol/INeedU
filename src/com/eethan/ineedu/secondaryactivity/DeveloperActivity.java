package com.eethan.ineedu.secondaryactivity;


import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class DeveloperActivity extends BaseActivity{

	private ImageButton backButton;
	RelativeLayout relativeLayout;
	SPHelper spHelper=new SPHelper(DeveloperActivity.this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("DeveloperActivity");
		setContentView(R.layout.developer_page);
		findView();
	}
	
	private void findView() {
		// TODO Auto-generated method stub
		backButton = (ImageButton)findViewById(R.id.developer_imgbt_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DeveloperActivity.this.finish();
			}
		});
		
		
	}

}
