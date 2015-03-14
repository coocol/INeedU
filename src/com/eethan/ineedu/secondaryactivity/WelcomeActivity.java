package com.eethan.ineedu.secondaryactivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.databasebeans.User;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.LoginJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.DailyPraise;
import com.eethan.ineedu.model.NeedFragmentModel;
import com.eethan.ineedu.model.NeedPraise;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.NetCondition;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.MainActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.service.LocateService;
import com.eethan.ineedu.util.SysApplication;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

public class WelcomeActivity extends BaseActivity {
	// public ImageView in_image=null;
	// public ImageView ob_image=null;
	boolean checkresult;
	SharedPreferences setting;
	SharedPreferences.Editor localEditor;
	// INeedU服务器返回的数据
	private int id;
	
	private String tele;
	private String password;
	private String email;
	private boolean isAutoLogin;
	public static Activity context;

	private final int UPDATE_LOCATION_SUCCESS = 1;
	private final int UPDATE_LOCATION_FAILED = 2;
	private final int LOGIN_CHECK_SUCCESS = 3;
	private final int LOGIN_CHECK_FAILED = 4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		if (Constant.LOCAL_MODE)
			Constant.IP = Constant.LOCAL_IP;

		// NetCondition.check(this);
		if (!NetCondition.isNetworkConnected(this)) {
			MyToast.showToast(this, "无网络连接，请连接网络后在试!");
			Intent intent = new Intent();
			intent.setClass(WelcomeActivity.this, LoginActivity.class);
			startActivity(intent);
			finish();
			new SPHelper(this).put(SPHelper.IS_AUTO_LOGIN, false);
			return;
		}
		if (Constant.OFFLINE_MODE) {
			startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
		}
		CreateDb();

//		// 定位服务
		Intent locateService = new Intent(getApplicationContext(), LocateService.class);
		this.startService(locateService);
		
		// 初始化地理位置
		setting = this.getSharedPreferences(Constant.INEEDUSPR,
				Context.MODE_PRIVATE);
		String lat = setting.getString(Constant.LAT, Constant.DEFAULT_LAT);
		String lng = setting.getString(Constant.LNG, Constant.DEFAULT_LNG);

		LocateManager.getInstance().setLatitude(Double.parseDouble(lat));
		LocateManager.getInstance().setLontitude(Double.parseDouble(lng));

		// SendLocationToServer(Double.parseDouble(lng),
		// Double.parseDouble(lat));

		setting = this.getSharedPreferences(Constant.INEEDUSPR, 0);
		id = setting.getInt(Constant.ID, -1);
		tele = setting.getString(Constant.TELE, "");
		email = setting.getString(Constant.EMAIL, "");
		password = setting.getString(Constant.PASSWORD, "");
		context = WelcomeActivity.this;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				setting = getSharedPreferences(Constant.INEEDUSPR, 0);
				Boolean isFirstStart = true;
				isFirstStart = setting
						.getBoolean(Constant.IS_FIRST_START, true);

				if (isFirstStart) {
					SharedPreferences.Editor editor = setting.edit();
					editor.putBoolean(Constant.IS_FIRST_START, false);
					editor.putBoolean(Constant.IS_AUTO_LOGIN, false);
					editor.commit();
//					intent.setClass(WelcomeActivity.this,
//							LoginActivity.class);
//					startActivity(intent);
					
					 intent.setClass(WelcomeActivity.this,GuideViewActivity.class);
					 startActivity(intent);
				} else {
					isAutoLogin = setting.getBoolean(Constant.IS_AUTO_LOGIN,
							true);
					if (isAutoLogin) {
						loginCheck();// 自动登录
					} else {
						intent.setClass(WelcomeActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}

				}
				// 隐藏当前界面
				if (!isAutoLogin)
					WelcomeActivity.this.finish();
			}
		}, 500);
	}

	private void CreateDb() {
		// TODO Auto-generated method stub
		DbUtils dbUtils = DbUtil.getDbUtils(WelcomeActivity.this);
		try {
			dbUtils.save(new NeedFragmentModel());
			dbUtils.save(new NeedPraise());
			dbUtils.save(new DailyPraise());
			dbUtils.save(new com.eethan.ineedu.model.IMMessage());
			// dbUtils.save(new Notice());
		} catch (DbException e) {
			// TODO: handle exception
		}

	}

	private void SendLocationToServer(final double lng, final double lat) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					int userId;
					userId = new SPHelper(WelcomeActivity.this).GetUserId();
					UserLocation userLocation = new UserLocation(-1, userId,
							lng, lat);

					// 用填满后的DatabaseBean填满jsonObject
					// 将jsonObject转换成String
					Log.i("JSON", "UpdateLocation开始:");
					String result = ServerCommunication.requestWithoutEncrypt(userLocation,
							URLConstant.UPDATE_LOCATION_URL);
					Log.i("JSON", "UpdateLocation结束!");
					Boolean r = JacksonUtil.json().fromJsonToObject(result,
							Boolean.class);
					Message msg = mHandler.obtainMessage();
					if (r)
						msg.what = UPDATE_LOCATION_SUCCESS;
					else
						msg.what = UPDATE_LOCATION_FAILED;
					msg.sendToTarget();

				} catch (PostException e) {
					// TODO: handle exception
					Message msg = mHandler.obtainMessage();
					msg.what = UPDATE_LOCATION_SUCCESS;
					msg.sendToTarget();
				} catch (Exception e) {
					Message msg = mHandler.obtainMessage();
					msg.what = UPDATE_LOCATION_SUCCESS;
					msg.sendToTarget();

				}
			}

		}).start();
	}

	public void loginCheck() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					LoginJsonObject loginObject = new LoginJsonObject();
					User loginuser = new User();
					loginuser.setId(id);
					loginuser.setPassword(password);
					loginuser.setTele(tele);
					loginuser.setEmail(email);

					String response = ServerCommunication.requestWithoutEncrypt(loginuser,
							URLConstant.LOGIN_URL);
					loginObject = JacksonUtil.json().fromJsonToObject(response,
							LoginJsonObject.class);
					checkresult = loginObject.getResult();

					Message msg = mHandler.obtainMessage();
					if (checkresult)
						msg.what = LOGIN_CHECK_SUCCESS;
					else
						msg.what = LOGIN_CHECK_FAILED;
					msg.sendToTarget();
				} catch (PostException e) {
					e.printStackTrace();
					Message msg = mHandler.obtainMessage();
					msg.what = LOGIN_CHECK_FAILED;
					msg.sendToTarget();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = mHandler.obtainMessage();
					msg.what = LOGIN_CHECK_FAILED;
					msg.sendToTarget();
					return;
				}

			}
		}).start();

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case LOGIN_CHECK_SUCCESS:
				// 登陆IneedU服务器成功以后登陆openfire
//				new ConnectOpenfire(WelcomeActivity.this, Constant.LOGIN_MODE)
//						.execute();
				startActivity(new Intent(WelcomeActivity.this,
						MainActivity.class));
				SysApplication.getInstance().exitAll();
				break;
			case LOGIN_CHECK_FAILED:
				Editor editor = new SPHelper(WelcomeActivity.this).GetEditor();
				editor.putBoolean(Constant.IS_AUTO_LOGIN, false);
				editor.commit();
				MyToast.showToast(WelcomeActivity.this, "服务器连接失败!");
				startActivity(new Intent(WelcomeActivity.this,
						LoginActivity.class));
				WelcomeActivity.this.finish();
				break;
			case UPDATE_LOCATION_SUCCESS:

				break;
			case UPDATE_LOCATION_FAILED:

				break;
			case Constant.CONNECT_ERROR:

				break;
			default:
				break;
			}
		}
	};

}