package com.eethan.ineedu.secondaryactivity;

import com.eethan.ineedu.mycontrol.MessageDialog;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.tencent.a.b.m;
import com.umeng.update.UmengUpdateAgent;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class CheckUpdateActivity extends BaseActivity{
	private Button checkButton;
	private Button handButton;
	private ImageButton backImageButton;
	private TextView currentVersion;
	private TextView newestVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("CheckupdateActivity");
		setContentView(R.layout.checkupdate_page);
		findView();
		setListener();
		String curVersion="";
		try {
			curVersion = getVersionName();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		currentVersion.setText("当前版本:"+curVersion);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		checkButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				UmengUpdateAgent.setDeltaUpdate(false);//全量更新
				UmengUpdateAgent.forceUpdate(CheckUpdateActivity.this);
			}
		});
		handButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MessageDialog messageDialog=new MessageDialog(getContext());
				messageDialog.setText("http://www.eethan.com");
				messageDialog.show();
			}
		});
		backImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckUpdateActivity.this.finish();
			}
		});
	}

	private void findView() {
		// TODO Auto-generated method stub
		checkButton=(Button)findViewById(R.id.cheup_bt_update_now);
		handButton=(Button)findViewById(R.id.hand_update);
		backImageButton = (ImageButton)findViewById(R.id.checkupdate_imgbt_back);
		currentVersion=(TextView)findViewById(R.id.cheup_tv_current_version);
		newestVersion=(TextView)findViewById(R.id.cheup_tv_newest_version);
	}
	 private String getVersionName() throws Exception
	   {
	           // 获取packagemanager的实例
	           PackageManager packageManager = getPackageManager();
	           // getPackageName()是你当前类的包名，0代表是获取版本信息
	           PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
	           String version = packInfo.versionName;
	           return version;
	   }
}
