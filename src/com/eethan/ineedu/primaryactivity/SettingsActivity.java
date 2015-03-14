package com.eethan.ineedu.primaryactivity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.im.XmppConnection;
import com.eethan.ineedu.manager.ServiceManager;
import com.eethan.ineedu.secondaryactivity.AboutActivity;
import com.eethan.ineedu.secondaryactivity.CheckUpdateActivity;
import com.eethan.ineedu.secondaryactivity.DeveloperActivity;
import com.eethan.ineedu.secondaryactivity.FunctionIntroductionActivity;
import com.eethan.ineedu.secondaryactivity.LoginActivity;
import com.eethan.ineedu.secondaryactivity.RemindActivity;
import com.eethan.ineedu.util.SysApplication;
import com.umeng.fb.FeedbackAgent;

public class SettingsActivity extends BaseActivity implements OnClickListener{

	//
	private ImageButton backImageButton;
	
	private TextView remindTextView;
	private TextView adviceTextView;
	private TextView checkUpdateTextView;
	private TextView functionTextView;
	private TextView developerTextView;
	private TextView aboutTextView;
	
	private RelativeLayout remindLayout;
	private RelativeLayout adviceLayout;
	private RelativeLayout checkUpdateLayout;
	private RelativeLayout functionLayout;
	private RelativeLayout developerLayout;
	private RelativeLayout aboutLayout;
	
	private Button exitButton;
	
	//跳转intent
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("SettingsActivity");
		setContentView(R.layout.setting_page);
		findView();
	}
	
	
	private void findView() {
		// TODO Auto-generated method stub
		backImageButton = (ImageButton)findViewById(R.id.setting_page_back_button);
		backImageButton.setOnClickListener(this);
		remindTextView = (TextView)findViewById(R.id.setting_page_remind);
		remindTextView.setOnClickListener(this);
		adviceTextView = (TextView)findViewById(R.id.setting_page_advice);
		adviceTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new FeedbackAgent(SettingsActivity.this).startFeedbackActivity();
			}
		});
		
		checkUpdateTextView = (TextView)findViewById(R.id.setting_page_update);
		checkUpdateTextView.setOnClickListener(this);
		functionTextView = (TextView)findViewById(R.id.setting_page_function);
		functionTextView.setOnClickListener(this);
		developerTextView = (TextView)findViewById(R.id.setting_page_developer);
		developerTextView.setOnClickListener(this);
		aboutTextView = (TextView)findViewById(R.id.setting_page_about);
		aboutTextView.setOnClickListener(this);
		exitButton = (Button)findViewById(R.id.setting_page_exit_button);
		exitButton.setOnClickListener(this);
		
		remindLayout=(RelativeLayout)findViewById(R.id.re_layout_remind);
		adviceLayout=(RelativeLayout)findViewById(R.id.re_layout_feedback);
		checkUpdateLayout=(RelativeLayout)findViewById(R.id.re_layout_checkupdate);
		functionLayout=(RelativeLayout)findViewById(R.id.re_layout_function);
		developerLayout=(RelativeLayout)findViewById(R.id.re_layout_teamwork);
		aboutLayout=(RelativeLayout)findViewById(R.id.re_layout_about);
		
		remindLayout.setOnClickListener(this);
		checkUpdateLayout.setOnClickListener(this);
		functionLayout.setOnClickListener(this);
		developerLayout.setOnClickListener(this);
		aboutLayout.setOnClickListener(this);
		adviceLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new FeedbackAgent(SettingsActivity.this).startFeedbackActivity();
			}
		});
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setting_page_back_button:
			SettingsActivity.this.finish();
			break;
		case R.id.setting_page_remind:
		case R.id.re_layout_remind:
			intent = new Intent(getApplicationContext(), RemindActivity.class);
			break;
		case R.id.re_layout_checkupdate:
		case R.id.setting_page_update:
			intent = new Intent(getApplicationContext(), CheckUpdateActivity.class);
			break;
		case R.id.re_layout_function:
		case R.id.setting_page_function:
			intent = new Intent(getApplicationContext(), FunctionIntroductionActivity.class);
			break;
		case R.id.re_layout_teamwork:
		case R.id.setting_page_developer:
			intent = new Intent(getApplicationContext(), DeveloperActivity.class);
			break;
		case R.id.re_layout_about:
		case R.id.setting_page_about:
			intent = new Intent(getApplicationContext(), AboutActivity.class);
			break;		
		case R.id.setting_page_exit_button:
			
			Editor editor=new SPHelper(SettingsActivity.this).GetEditor();
			editor.putBoolean(Constant.IS_AUTO_LOGIN, false);//取消自动登录
			editor.remove(Constant.ID);
			editor.remove(Constant.TELE);
			//editor.remove(Constant.PASSWORD);
			//editor.remove(Constant.EMAIL);
			editor.remove(Constant.REALNAME);
			editor.remove(Constant.NICKNAME);
			editor.remove(Constant.SEX);
			
			editor.commit();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(XmppConnection.getConnection().isConnected())
						XmppConnection.closeConnection();//退出OpenFire服务器
				}
			}).start();
			
			//关闭之前的服务，否则聊天会有可能收不到当前的通知
			ServiceManager serviceManager = new ServiceManager(getApplicationContext());
			serviceManager.stopService();
			
			intent = new Intent(getApplicationContext(), LoginActivity.class);
			
			break;	
		default:
			break;
		}
		if (v.getId()!=R.id.setting_page_back_button) {
			startActivity(intent);
		}
		
		if(v.getId()==R.id.setting_page_exit_button)
			SysApplication.getInstance().exitAll();
	}

}
