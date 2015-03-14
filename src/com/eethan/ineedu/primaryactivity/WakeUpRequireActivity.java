package com.eethan.ineedu.primaryactivity;

import com.eethan.ineedu.CommonUse.CheckTextUtil;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.Enum.Scope;
import com.eethan.ineedu.mycontrol.MyToast;

import u.aly.ba;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class WakeUpRequireActivity extends BaseActivity{
	ImageButton backButton;
	Button finishButton;
	EditText content;
	RadioGroup rangeGroup;
	RadioButton nearButton;
	RadioButton collegeButton;
	RadioButton countryButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wakeup_requirement);
		findView();
		String requireString = WakeUpActivity.requireString;
		content.setText(requireString);
		
	}

	private void findView() {
		// TODO Auto-generated method stub
		backButton = (ImageButton) findViewById(R.id.imgbt_back);
		finishButton = (Button) findViewById(R.id.bt_finish);
		content = (EditText) findViewById(R.id.et_requirement);
		rangeGroup = (RadioGroup) findViewById(R.id.rg_scope);
		nearButton = (RadioButton) findViewById(R.id.rb_near);
		collegeButton = (RadioButton) findViewById(R.id.rb_college);
		countryButton = (RadioButton) findViewById(R.id.rb_country);
		
		
		finishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckTextUtil checkTextUtil = CheckTextUtil.getInstance();
				checkTextUtil.setEmptyText("要求内容不能为空!");
				if(!checkTextUtil.checkText(content))
					return;
				WakeUpActivity.requireString = content.getText().toString();
				MyToast.showToast(getContext(), "更改成功");
				finish();
			}
		});
		rangeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				// TODO Auto-generated method stub
				if(R.id.rb_near==checkedId)
					WakeUpActivity.scope = Scope.NEAR;
				if(R.id.rb_college==checkedId)
					WakeUpActivity.scope = Scope.COLLEGE;
				if(R.id.rb_country==checkedId)
					WakeUpActivity.scope = Scope.COUNTRY;
			}
			
		});
		
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
}
