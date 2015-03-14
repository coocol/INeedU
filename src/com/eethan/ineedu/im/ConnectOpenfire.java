package com.eethan.ineedu.im;

import java.io.File;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.manager.ServiceManager;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.primaryactivity.MainActivity;
import com.eethan.ineedu.secondaryactivity.LoginActivity;
import com.eethan.ineedu.util.SysApplication;
import com.eethan.ineedu.util.UploadHelper;


/**
 * 登录异步任务
 * @author
 */

public class ConnectOpenfire extends AsyncTask<String, Integer, Integer> {
	private Context context;
	private int mode;
	private boolean isAutoLogin;
	private LoadingDialog myLoadingDialog;
	//
	private ServiceManager serviceManager;


	private static final String TAG = "UploadActivity";
	
	private int userId;
	private String accounts;
	private String password;

	public ConnectOpenfire(Context context, int mode,Dialog myLoadingDialog) {
		this.context = context;
		this.mode = mode;
		this.myLoadingDialog = (LoadingDialog) myLoadingDialog;
		this.isAutoLogin=context.getSharedPreferences(Constant.INEEDUSPR, 0)
				.getBoolean(Constant.IS_AUTO_LOGIN, false);
		//取得openfire账号密码
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.INEEDUSPR, Context.MODE_PRIVATE);
		accounts = String.valueOf(sharedPre.getInt(Constant.ID, -1));
		password = sharedPre.getString(Constant.PASSWORD, "");
		serviceManager = new ServiceManager(context);
	}
	
	public ConnectOpenfire(Context context, int mode,LoadingDialog myLoadingDialog,int userId) {
		this.context = context;
		this.mode = mode;
		this.myLoadingDialog = myLoadingDialog;
		this.isAutoLogin=context.getSharedPreferences(Constant.INEEDUSPR, 0)
				.getBoolean(Constant.IS_AUTO_LOGIN, false);
		//取得openfire账号密码
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.INEEDUSPR, Context.MODE_PRIVATE);
		accounts = String.valueOf(sharedPre.getInt(Constant.ID, -1));
		password = sharedPre.getString(Constant.PASSWORD, "");
		this.userId=userId;
		serviceManager = new ServiceManager(context);
	}
	public ConnectOpenfire(Context context,int mode)
	{
		this.context = context;
		this.mode = mode;
		this.isAutoLogin=true;
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.INEEDUSPR, Context.MODE_PRIVATE);
		accounts = String.valueOf(sharedPre.getInt(Constant.ID, -1));
		password = sharedPre.getString(Constant.PASSWORD, "");
		serviceManager = new ServiceManager(context);
	}

	/**
	 * 登录异步任务执行前准备,用于显示进度条
	 */
	@Override
	protected void onPreExecute() {
		
		super.onPreExecute();
	}

	/**
	 * 登录异步任务主函数,调用login(),并返回login()的返回值
	 */
	@Override
	protected Integer doInBackground(String... params) {
		
		if(!Constant.IS_USE_OPENFIRE)
			return Constant.LOGIN_SUCCESS;
		if(mode == Constant.LOGIN_MODE) 
		{
			return loginOpenfire();
		}
		else {
			return registerOpenfire();
		} 
}
	
	/**
	 * 进度条函数,此处函数体是空的
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
	}

	/**
	 * 任务结束后的处理,根据服务器返回结果处理
	 */
	@Override
	protected void onPostExecute(Integer result) {
		
		//销毁进度对话框
		Intent intent = new Intent();
		switch (result) {
		case Constant.LOGIN_SUCCESS: // 登录成功
			serviceManager.startService();
//			MyToast.showToast(context, "登陆成功,准备信息中");
			intent.setClass(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//启动服务，放到connect之后去了
			context.startActivity(intent);
			SysApplication.getInstance().exitAll();
			break;
		case Constant.REGISTER_SUCCESS:
			serviceManager.startService();
			MyToast.showToast(context, "注册成功");
			intent.setClass(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			SysApplication.getInstance().exitAll();
			
			File file=new File("sdcard/DCIM/big_-1.png");
			new UploadHelper(userId).uploadBigHead (file); 
			//file.delete();
			
			break;
		case Constant.QQ_REGISTER_SUCCESS:
			serviceManager.startService();
			MyToast.showToast(context, "注册成功");
			intent.setClass(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			SysApplication.getInstance().exitAll();
			
			break; 
		case Constant.REGISTER_CONFLICT:
			serviceManager.stopService();
			MyToast.showToast(context, "用户名已经存在");
			break;
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// 账户或者密码错误
			serviceManager.stopService();
			MyToast.showToast(
					context,
					"账号或密码错误");
			if(isAutoLogin)
				context.startActivity(new Intent(context,LoginActivity.class));			
			new SPHelper(context).put(Constant.IS_AUTO_LOGIN, false);
			break;
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
			serviceManager.stopService();
			if(isAutoLogin)
				context.startActivity(new Intent(context,LoginActivity.class));			
			new SPHelper(context).put(Constant.IS_AUTO_LOGIN, false);
			MyToast.showToast(
					context,
					"无法连接到服务器");
			break;
		case Constant.CONNECT_ERROR:// 未知异常
		case Constant.NOT_CONNECTED_TO_SERVER:
		case Constant.ALREADY_LOGGED:
			serviceManager.stopService();
			if(isAutoLogin)
			{
				context.startActivity(new Intent(context,LoginActivity.class));
				SysApplication.getInstance().exitAll();
			}
			new SPHelper(context).put(Constant.IS_AUTO_LOGIN, false);
			
			MyToast.showToast(context, "登陆聊天服务器失败,请退出后重试...");
			break;
		
		default:
			serviceManager.stopService();
			if(isAutoLogin)
				context.startActivity(new Intent(context,LoginActivity.class));
			new SPHelper(context).put(Constant.IS_AUTO_LOGIN, false);
			MyToast.showToast(
					context,"屌爆了,这个错误程序员也没遇到过");
			break;
		}
		if(!isAutoLogin)//自动登录不需要
			myLoadingDialog.dismiss();
		super.onPostExecute(result);
	}

	
	
	
	private int registerOpenfire() {
		Log.i("NET","RegisterOpenFire");
		Registration reg = new Registration();
		reg.setType(IQ.Type.SET);
		reg.setTo(XmppConnection.getConnection().getServiceName());
		reg.setUsername(accounts);
		reg.setPassword(password);
		
		reg.addAttribute("android", "geolo_createUser_android");
		PacketFilter filter = new AndFilter(new PacketIDFilter(
		                                reg.getPacketID()), new PacketTypeFilter(
		                                IQ.class));
		PacketCollector collector = XmppConnection.getConnection().
		createPacketCollector(filter);
		XmppConnection.getConnection().sendPacket(reg);
		IQ result = (IQ) collector.nextResult(SmackConfiguration
		                                .getPacketReplyTimeout());
		                        // Stop queuing results
		collector.cancel();// 停止请求results（是否成功的结果）
		
		if (result == null) {
			return Constant.SERVER_UNAVAILABLE;
		} 
		else if (result.getType() == IQ.Type.ERROR) {
			if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
				return Constant.REGISTER_CONFLICT;
		    } else {
		    	return Constant.CONNECT_ERROR;
		    }
		} else if (result.getType() == IQ.Type.RESULT) {
			try {
				XmppConnection.getConnection().login(accounts, password);
				Presence presence = new Presence(Presence.Type.available);
				XmppConnection.getConnection().sendPacket(presence);
				if(mode == Constant.QQ_REGISTER_MODE)
					return Constant.QQ_REGISTER_SUCCESS;
				else
					return Constant.REGISTER_SUCCESS;
			} catch (XMPPException e) {
				
				e.printStackTrace();
				return Constant.SERVER_UNAVAILABLE;
			}	
		}else {
			//未知错误
			return Constant.CONNECT_ERROR;
		}
	}
	
	//登陆openfire
		private int loginOpenfire(){
//			SmackAndroid.init(context);
			try {
				//XmppConnection.closeConnection();
				// 连接服务器//用INeedU得到的id登陆openfire
				
				XmppConnection.getConnection().login(accounts, password);
				// 连接服务器成功，更改在线状态
				Presence presence = new Presence(Presence.Type.available);
				XmppConnection.getConnection().sendPacket(presence);
				
				return Constant.LOGIN_SUCCESS;
			}
			catch (XMPPException e) 
			{
				if(XmppConnection.getConnection().isConnected())
					XmppConnection.closeConnection();
				e.printStackTrace();
				return Constant.CONNECT_ERROR;
			}	
			catch (IllegalStateException e) {
				// TODO: handle exception
				e.printStackTrace();
				if(XmppConnection.getConnection().isConnected())
					XmppConnection.closeConnection();
				if(e.getMessage().equals("Not connected to server."))
					return Constant.NOT_CONNECTED_TO_SERVER;
				else//Already logged in to server.
					return Constant.ALREADY_LOGGED;
			}
			
		}
		
		
		
}
