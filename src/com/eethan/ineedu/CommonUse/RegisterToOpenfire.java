package com.eethan.ineedu.CommonUse;

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

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.drm.DrmStore.RightsStatus;
import android.os.AsyncTask;
import android.util.Log;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.im.XmppConnection;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.MainActivity;
import com.eethan.ineedu.secondaryactivity.RegisterFirstActivity;
import com.eethan.ineedu.util.SysApplication;
import com.eethan.ineedu.util.UploadHelper;

public class RegisterToOpenfire extends AsyncTask<String, Integer, Integer> {

	private Context context;

	private static final String TAG = "UploadActivity";
	
	private static int regNum = 0;

	private String accounts;
	private String password;
	private String emailString;
	private LoadingDialog loadingDialog;

	public RegisterToOpenfire(Context context,int id,String em,LoadingDialog loadingDialog) {
		this.context = context;
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.INEEDUSPR, Context.MODE_PRIVATE);
		accounts = String.valueOf(id);
		password = sharedPre.getString(Constant.PASSWORD, "");
		emailString = em;
		this.loadingDialog = loadingDialog;
	}

	@Override
	protected Integer doInBackground(String... params) {
		return registerOpenfire();
	}

	@Override
	protected void onPostExecute(Integer result) {
		Intent intent = new Intent();
		super.onPostExecute(result);
		switch (result) {
		case Constant.REGISTER_SUCCESS:
			if(loadingDialog!=null && loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			MyToast.showToast(context, "注册成功");
			intent.setClass(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			SysApplication.getInstance().exitAll();
			File file=new File("sdcard/DCIM/big_-1.png");
			new UploadHelper(Integer.parseInt(accounts)).uploadBigHead (file); 
			break;
		case Constant.REGISTER_CONFLICT:
			if(loadingDialog!=null && loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			MyToast.showToast(context, "用户名已经存在，请重新注册"+regNum);
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
			Intent failIntent = new Intent();
			failIntent.setClass(context, RegisterFirstActivity.class);
			failIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(failIntent);
			SysApplication.getInstance().exitAll();
			break;
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
			//MyToast.showToast(context, "无法连接到服务器"+regNum);
			if(regNum<100)
			{
				new RegisterToOpenfire(context, Integer.parseInt(accounts), emailString,loadingDialog);
				regNum++;
			}else {
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
				if(loadingDialog!=null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				MyToast.showToast(context, "注册失败，请重新注册");
				
				intent.setClass(context, RegisterFirstActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				SysApplication.getInstance().exitAll();
			}
			break;
		case Constant.CONNECT_ERROR:// 未知异常
		case Constant.NOT_CONNECTED_TO_SERVER:
		    //MyToast.showToast(context, "未知异常或未连接服务器"+regNum);
			if(regNum<100)
			{
				new RegisterToOpenfire(context, Integer.parseInt(accounts), emailString,loadingDialog);
				regNum++;
			}else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						//删除已经注册了得用户
						try {
							String result = ServerCommunication.request(emailString, URLConstant.DELETE_EMPTY_USER);
						} catch (PostException e) {
							e.printStackTrace();
						}
					}
				}).start();
				if(loadingDialog!=null && loadingDialog.isShowing()){
					loadingDialog.dismiss();
				}
				MyToast.showToast(context, "注册失败，请重新注册");
			
				intent.setClass(context, RegisterFirstActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				SysApplication.getInstance().exitAll();
			}
			break;
		case Constant.ALREADY_LOGGED:
			//MyToast.showToast(context, "已经登陆了");
			if(loadingDialog!=null && loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			break;
		default:
			MyToast.showToast(context, "恭喜你遇到一个从来没有出现过的问题");
			if(loadingDialog!=null && loadingDialog.isShowing()){
				loadingDialog.dismiss();
			}
			break;
		}

		
	}

	private int registerOpenfire() {
		try {
			Log.i("NET", "RegisterOpenFire");
			Registration reg = new Registration();
			reg.setType(IQ.Type.SET);
			reg.setTo(XmppConnection.getConnection().getServiceName());
			reg.setUsername(accounts);
			reg.setPassword(password);

			reg.addAttribute("android", "geolo_createUser_android");
			PacketFilter filter = new AndFilter(new PacketIDFilter(
					reg.getPacketID()), new PacketTypeFilter(IQ.class));
			PacketCollector collector = XmppConnection.getConnection()
					.createPacketCollector(filter);
			XmppConnection.getConnection().sendPacket(reg);
			IQ result = (IQ) collector.nextResult(SmackConfiguration
					.getPacketReplyTimeout());
			// Stop queuing results
			collector.cancel();// 停止请求results（是否成功的结果）

			if (result == null) {
				return Constant.SERVER_UNAVAILABLE;
			} else if (result.getType() == IQ.Type.ERROR) {
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
				
						return Constant.REGISTER_SUCCESS;
				} catch (XMPPException e) {

					e.printStackTrace();
					return Constant.SERVER_UNAVAILABLE;
				}
			} else {
				// 未知错误
				return Constant.CONNECT_ERROR;
			}
		} catch (Exception e) {
			return Constant.CONNECT_ERROR;
		}
		
	}

}
