package com.eethan.ineedu.secondaryactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.eethan.ineedu.CommonUse.RegisterToOpenfire;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.User;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.LoginJsonObject;
import com.eethan.ineedu.jackson.RegisterJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.MainActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.SysApplication;

public class RegisterThirdActivity extends BaseActivity{

	private EditText school;
	private EditText major;
	private EditText grade;
	
	private String schoolString;
	private String majorString;
	private String gradeString;
	
	String phoneString;
	String emailString;
	private Button finishButton;
	private ImageButton backButton;
	
	SharedPreferences mySharedPreferences;
	SharedPreferences.Editor localEditor;
	
	//INeedU服务器返回的数据
	private int id;
	private String tele;
	private String password; 
	private String email;
	private String nickName;
	private String realName;
	private String sex;
	LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register3);
		
		mySharedPreferences= this.getSharedPreferences(Constant.INEEDUSPR,  0);
    		localEditor = mySharedPreferences.edit();
    	
		findView();
	}
	
	private void findView() {
		// TODO Auto-generated method stub
		school = (EditText) findViewById(R.id.register3_et_university);
		major = (EditText) findViewById(R.id.register3_et_academy);
		grade = (EditText) findViewById(R.id.register3_et_grade);
		
		backButton = (ImageButton)findViewById(R.id.register3_bt_back);
		backButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RegisterThirdActivity.this.finish();
			}
			
		});
		loadingDialog = new LoadingDialog(RegisterThirdActivity.this);
		finishButton = (Button)findViewById(R.id.register3_bt_register);
		finishButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(school.getText().toString()==null || school.getText().toString().equals("")){
					school.setError(Html.fromHtml("<font color=#E10979>请输入学校</font>"));
					return;
				}
				if(major.getText().toString()==null || major.getText().toString().equals("")){
					major.setError(Html.fromHtml("<font color=#E10979>请输入院系</font>"));
					return;
				}
				if(grade.getText().toString()==null || grade.getText().toString().equals("")){
					grade.setError(Html.fromHtml("<font color=#E10979>请输入年级</font>"));
					return;
				}
				schoolString = school.getText().toString().trim();
				majorString = major.getText().toString().trim();
				gradeString = grade.getText().toString().trim();
				
				
				if (schoolString.length()==0||majorString.length()==0||gradeString.length()==0) {
					MyToast.showToast(RegisterThirdActivity.this,"请完善真实信息");
				}else {
					
					loadingDialog.show();
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								
								Intent intent = getIntent();
								phoneString = intent.getStringExtra("phoneString");
								String pwdString = intent.getStringExtra("pwdString");
								emailString = intent.getStringExtra("emailString");
								String nicknameString = intent.getStringExtra("nicknameString");
								String realnameString = intent.getStringExtra("realnameString");
								String sexString = intent.getStringExtra("sexString");
								User registeruser=new User();
								UserInfo registeruserinfo=new UserInfo();
								UserDetailInfo registeruserdetailinfo= new UserDetailInfo();
								UserLocation registerUserLocation = new UserLocation();
								RegisterJsonObject jsonObject = new RegisterJsonObject();
								
								registeruser.setTele(phoneString);
								registeruser.setPassword(pwdString);
								registeruser.setEmail(emailString);
								
								registeruserinfo.setNickName(nicknameString);
								registeruserinfo.setSex(sexString);
								registeruserinfo.setRealName(realnameString);
								
								registeruserdetailinfo.setSchool(schoolString);
								registeruserdetailinfo.setAcademy(majorString);
								registeruserdetailinfo.setGrade(gradeString);
								
								registerUserLocation.setLng(LocateManager.getInstance().getLontitude());
								registerUserLocation.setLat(LocateManager.getInstance().getLatitude());
								
								jsonObject.setUser(registeruser);
								jsonObject.setUserInfo(registeruserinfo);
								jsonObject.setUserDetailInfo(registeruserdetailinfo);
								jsonObject.setUserLocation(registerUserLocation);
								
								
								String result = ServerCommunication.request(jsonObject, URLConstant.RIGISTER_URL);
								if (result==null || result.length()==0) {
									Message msg = mHandler.obtainMessage();
									msg.what = 0;
									msg.sendToTarget();
									return;
								}
								
								
								//将信息保存入SharedPreferences
								LoginJsonObject loginObject= new LoginJsonObject();
								loginObject = JacksonUtil.json().fromJsonToObject(result,LoginJsonObject.class);
								id = loginObject.getUser().getId();
								tele = loginObject.getUser().getTele();
								password = loginObject.getUser().getPassword();
								email = loginObject.getUser().getEmail();
								nickName = loginObject.getUserInfo().getNickName();
								realName = loginObject.getUserInfo().getRealName();
								sex = loginObject.getUserInfo().getSex();
								localEditor.putInt(Constant.ID, id);
								localEditor.putString(Constant.TELE,tele );
								localEditor.putString(Constant.PASSWORD,password );
								localEditor.putString(Constant.EMAIL, email);
								localEditor.putString(Constant.NICKNAME,nickName );
								localEditor.putString(Constant.REALNAME, realName);
								localEditor.putString(Constant.SEX,sex );
							    localEditor.commit();
							    
								Message msg = mHandler.obtainMessage();
								msg.what = 1;
								msg.sendToTarget();
							} catch (Exception e) {
								// TODO: handle exception
								Message msg = mHandler.obtainMessage();
								msg.what = Constant.CONNECT_FAILED;
								msg.obj= e.getMessage();
								msg.sendToTarget();
							} 
							
						}
					}).start();
					
				}
				
			
				//startActivity(new Intent(getApplicationContext(), MainActivity.class));
			}
		});
	}
	
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			loadingDialogDismiss();
			super.handleMessage(msg);
			// TODO Auto-generated method stub
			if(msg.what==Constant.CONNECT_FAILED)
			{
				MyToast.showToast(getContext(), (String)msg.obj);
				return;
			}
			if (msg.what==1) {
//				MyToast.showToast(RegisterThirdActivity.this, "注册成功！",true);
//				Intent intent = new Intent(RegisterThirdActivity.this, MainActivity.class);
//				intent.putExtra("mode", "reg");
//				startActivity(intent);
				//注册INeedU服务器成功以后得到id再注册openfire
//				LoadingDialog myLoadingDialog = new LoadingDialog(RegisterThirdActivity.this);
//				myLoadingDialog.show();
				//new ConnectOpenfire(getApplicationContext(), Constant.REGISTER_MODE,myLoadingDialog,id).execute();

				new RegisterToOpenfire(getApplicationContext(),id,emailString,loadingDialog).execute();
			}else if (msg.what==0) {//失败
				MyToast.showToast(RegisterThirdActivity.this, "注册失败,请重新注册", true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//删除已经注册了得用户
						try {
							String result = ServerCommunication.request(emailString, URLConstant.DELETE_EMPTY_USER);
						} catch (PostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				
				Intent intent = new Intent();
				intent.setClass(RegisterThirdActivity.this, RegisterFirstActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				RegisterThirdActivity.this.startActivity(intent);
				SysApplication.getInstance().exitAll();
				
			}
			
		}
	};
	
	

}
