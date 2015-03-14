	package com.eethan.ineedu.secondaryactivity;


import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.User;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.im.ConnectOpenfire;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.LoginJsonObject;
import com.eethan.ineedu.jackson.RegisterJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.NetCondition;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.MainActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.SysApplication;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;

public class LoginActivity extends BaseActivity{

	private Button loginButton;
	private Button quickLoginButton;
	private TextView registerTextView;
	private TextView forgetPasswordTextView;
	private EditText username;
	private EditText pwd;
	
	private String usernameString;
	private String pwdString;//不能超16位
	boolean checkresult;
	
	private View focusView = null;
	
	SharedPreferences mySharedPreferences;
	SharedPreferences.Editor localEditor;
	private LoadingDialog myLoadingDialog;
	//INeedU服务器返回的数据
	private int id;
	private String tele;
	private String password; 
	private String email;
	private String nickName;
	private String realName;
	private String sex;
	
	private boolean isFirstQQLogin;
	private static final int LOGIN_SUCCESS=1;
	private static final int LOGIN_FAILED=2;
	private static final int CONNECT_ERROR=3;
	private static final int QQ_LOGIN_SUCCESS=4;
	private static final int QQ_LOGIN_FAILED=5;
	//友盟快速第三方登陆
	UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		myLoadingDialog=new LoadingDialog(this);
		myLoadingDialog.setCancelable(false);
		
		findView();
		UMengInit();
		Init();
	}
	private void UMengInit() {
		// TODO Auto-generated method stub
		String appId="1102452990";
		String appSecret="KgWvoq6629aW17di";
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this, appId,
                appSecret);
		qqSsoHandler.addToSocialSDK(); 
	}
	private void Init() {
		// TODO Auto-generated method stub
		pwd = (EditText) findViewById(R.id.login_et_password);
		SharedPreferences lightDB = getSharedPreferences(Constant.INEEDUSPR,0);
		username.setText(lightDB.getString(Constant.EMAIL,""));
		username.setSelection(username.getText().length());
		pwd.setText(lightDB.getString(Constant.PASSWORD,""));
		pwd.setSelection(pwd.getText().length());
		mySharedPreferences = this.getSharedPreferences(Constant.INEEDUSPR,  0);
		localEditor = mySharedPreferences.edit();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		myLoadingDialog.dismiss();
	}
	private void findView() {
		// TODO Auto-generated method stub
		username = (EditText) findViewById(R.id.login_et_account);
		
		quickLoginButton = (Button)findViewById(R.id.quick_login_btn);
		quickLoginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final Context context = LoginActivity.this;
				mController.doOauthVerify(context, SHARE_MEDIA.QQ,new UMAuthListener() {
		            @Override
		            public void onError(SocializeException e, SHARE_MEDIA platform) {
		            }
		            @Override
		            public void onComplete(Bundle value, SHARE_MEDIA platform) {
		                if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
		                    MyToast.showToast(context, "授权成功~");
		                    getUserInfo();
		                } else {
		                    MyToast.showToast(context, "授权失败!");
		                }
		            }
		            private void getUserInfo() {	
						// TODO Auto-generated method stub
		            	mController.getPlatformInfo(context, SHARE_MEDIA.QQ, new UMDataListener() {
		            	    @Override
		            	    public void onStart() {
//		            	        Toast.makeText(context, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
		            	    }                                              
		            	    @Override
		            	        public void onComplete(int status, final Map<String, Object> info) {
		            	            if(status == 200 && info != null){
		            	                StringBuilder sb = new StringBuilder();
		            	                Set<String> keys = info.keySet();
		            	                for(String key : keys){
		            	                   sb.append(key+"="+info.get(key).toString()+"\r\n");
		            	                }
		            	                Log.d("TestData",sb.toString());
		            	                
		            	                new Thread(new Runnable() {
											
											@Override
											public void run() {
												// TODO Auto-generated method stub
												qqLogin(info);
											}
										}).start();
		            	                
		            	                
		            	            }else{
		            	            	MyToast.showToast(context, "未知错误!");
		            	               Log.d("TestData","发生错误："+status);
		            	           }
		            	        }
							private void qqLogin( Map<String, Object> info) {
								// TODO Auto-generated method stub
								  String uid = info.get("uid").toString();//uid，作为唯一标识
	            	              sex = info.get("gender").toString();
	            	              nickName = info.get("screen_name").toString();
	            	              realName = nickName;
	            	              String imageUrl = info.get("profile_image_url").toString();//头像地址
	            	              User registeruser=new User();
	  							UserInfo registeruserinfo=new UserInfo();
	  							UserDetailInfo registeruserdetailinfo= new UserDetailInfo();
	  							UserLocation registerUserLocation = new UserLocation();
	  							RegisterJsonObject jsonObject = new RegisterJsonObject();
	  							
	  							String emptyString = "暂无";
	  							registeruser.setEmail(emptyString);
	  							registeruser.setPassword(emptyString);
	  							registeruser.setTele(emptyString);
	  							registeruser.setUid(uid);
	  							registeruser.setImageUrl(imageUrl);
	  							
	  							registeruserdetailinfo.setAcademy("");
	  							registeruserdetailinfo.setGrade("");
	  							registeruserdetailinfo.setSchool("");
	  							
	  							registeruserinfo.setNickName(nickName);
	  							registeruserinfo.setSex(sex);
	  							registeruserinfo.setRealName(realName);
	  							
	  							registerUserLocation.setLng(LocateManager.getInstance().getLontitude());
	  							registerUserLocation.setLat(LocateManager.getInstance().getLatitude());
	  							
	  							jsonObject.setUser(registeruser);
	  							jsonObject.setUserInfo(registeruserinfo);
	  							jsonObject.setUserDetailInfo(registeruserdetailinfo);
	  							jsonObject.setUserLocation(registerUserLocation);
	  							
	  							String response;
	  							LoginJsonObject jsonObject2 = new LoginJsonObject();
	  							try {
									response = ServerCommunication.requestWithoutEncrypt(jsonObject, URLConstant.QQ_LOGIN_URL);
									jsonObject2 = JacksonUtil.json().fromJsonToObject(response, LoginJsonObject.class);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									Message message = mHandler.obtainMessage();
									message.what=CONNECT_ERROR;
									message.sendToTarget();
									return;
								}
	  							//登陆成功
	  							
	  							
	  							User user = jsonObject2.getUser();
	  							UserInfo userInfo = jsonObject2.getUserInfo();
	  							isFirstQQLogin = jsonObject2.getResult();
	  							
	  							SPHelper spHelper = new SPHelper(getContext());
	  							spHelper.put(SPHelper.ID, user.getId());
	  							spHelper.put(SPHelper.NICKNAME, userInfo.getNickName());
	  							spHelper.put(SPHelper.REALNAME, userInfo.getRealName());
	  							spHelper.put(SPHelper.SEX, userInfo.getSex());
	  							spHelper.put(SPHelper.PASSWORD, "123456");
	  							spHelper.put(SPHelper.IS_QQ_LOGIN, true);
	  							
	  							Message message = mHandler.obtainMessage();
	  							message.what = QQ_LOGIN_SUCCESS;
	  							message.sendToTarget();
							}
		            	});
					}
					@Override
		            public void onCancel(SHARE_MEDIA platform) {}
		            @Override
		            public void onStart(SHARE_MEDIA platform) {}
		});
			}
		});
		
		loginButton = (Button)findViewById(R.id.login_bt_login);
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!NetCondition.isNetworkConnected(LoginActivity.this))
				{
					MyToast.showToast(LoginActivity.this, "无网络连接");
					return;
				}
				loginCheck();
			}
			
			/**
		     * 如果所有的信息填写正确,返回true,否则false
		     * @return
		     */
			public void loginCheck()
			{
				if(Constant.OFFLINE_MODE)
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
				if(this.checkInfo()) {
					myLoadingDialog.show();
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								LoginJsonObject loginObject= new LoginJsonObject();
								User loginuser=new User();
								loginuser.setPassword(pwdString);
								if (usernameString.indexOf("@")!=-1)
									loginuser.setEmail(usernameString);
								else {
									loginuser.setTele(usernameString);
								}
								
								
								//开始请求数据
								String response = ServerCommunication.
										requestWithoutEncrypt(loginuser, URLConstant.LOGIN_URL);
								loginObject = JacksonUtil.json().fromJsonToObject(response, LoginJsonObject.class);
								checkresult=loginObject.getResult();
								if(checkresult){
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
									localEditor.putBoolean(Constant.IS_SHAKE, true);
									localEditor.putBoolean(Constant.IS_SOUND,false);
							        localEditor.commit();
								}
								Message msg = mHandler.obtainMessage();
								if(checkresult)
									msg.what=LOGIN_SUCCESS;//是否登录成功
								else
									msg.what=LOGIN_FAILED;
								msg.sendToTarget();
								
								
								
							} catch (PostException e) {
								e.printStackTrace();
								Message msg = mHandler.obtainMessage();
								msg.what=CONNECT_ERROR;
								msg.sendToTarget();
								return;
							}catch (Exception e) {
								e.printStackTrace();
								Message msg = mHandler.obtainMessage();
								msg.what=CONNECT_ERROR;
								msg.sendToTarget();
								return;
							}
							
						}
					}).start();
						
				}
				if(!this.checkInfo()) {
		    		focusView.requestFocus();
		    		return;
		    	}
			}
			private boolean checkInfo() {
				
			usernameString = username.getText().toString();
		    	pwdString = pwd.getText().toString();//不能超16位
		    	
		    	if(usernameString.isEmpty()){
		    		username.setError(Html.fromHtml("<font color=#E10979>请输入邮箱或手机号码</font>"));
		    		focusView = username;
		    		return false;
		    	}
		    	if(pwdString.length()==0||pwdString.length()>16){
		    		pwd.setError(Html.fromHtml("<font color=#E10979>请输入正确的密码</font>"));
		    		focusView = pwd;
		    		return false;
		    	}
				
				return true;
			}
		});
		//
		registerTextView = (TextView)findViewById(R.id.login_tv_register);
		registerTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(), RegisterFirstActivity.class));
				
			}
		});
		//
		forgetPasswordTextView = (TextView)findViewById(R.id.login_tv_forget_password);
		forgetPasswordTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LoginActivity.this,ForgetPsdActivity.class));
			}
		});
	}

	
	
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case LOGIN_SUCCESS:
				//startActivity(new Intent(getApplicationContext(), MainActivity.class));
				//登陆IneedU服务器成功以后登陆openfire
				localEditor
				.putBoolean(Constant.IS_AUTO_LOGIN, true);//自动设置自动登录
				localEditor.commit();
				
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//启动服务，放到connect之后去了
				startActivity(intent);
				SysApplication.getInstance().exitAll();
				break;
			case LOGIN_FAILED:
				pwd.setError(Html.fromHtml("<font color=#E10979>用户名或密码错误！</font>"));
				focusView = pwd;
				focusView.requestFocus();
				myLoadingDialog.dismiss();
				break;
			case CONNECT_ERROR:
				MyToast.showToast(LoginActivity.this, "连接服务器失败!");
				myLoadingDialog.dismiss();
				break;
			case QQ_LOGIN_SUCCESS:
				MyToast.showToast(LoginActivity.this, "登陆成功!");
				myLoadingDialog.dismiss();
				int mode;
				if(isFirstQQLogin)
					mode = Constant.REGISTER_MODE;
				else
					mode = Constant.LOGIN_MODE;
				localEditor
				.putBoolean(Constant.IS_AUTO_LOGIN, true);//自动设置自动登录
				localEditor.commit();
				new ConnectOpenfire(getApplicationContext(), mode, myLoadingDialog).execute();
				
				break;
			case QQ_LOGIN_FAILED:
				MyToast.showToast(LoginActivity.this, "登录失败!");
				myLoadingDialog.dismiss();
				break;
			default:
				break;
			}
			
		}
	};
	
}
