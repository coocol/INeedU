package com.eethan.ineedu.CommonUse;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.im.XmppConnection;
import com.eethan.ineedu.mycontrol.MyToast;

public class LoginToOpenfire extends AsyncTask<String, Integer, Integer> {
	private Context context;
	//

	private static final String TAG = "UploadActivity";
	
	private static int loginNum = 0;

	private String accounts;
	private String password;

	public LoginToOpenfire(Context context) {
		this.context = context;
		SharedPreferences sharedPre = context.getSharedPreferences(
				Constant.INEEDUSPR, Context.MODE_PRIVATE);
		accounts = String.valueOf(sharedPre.getInt(Constant.ID, -1));
		password = sharedPre.getString(Constant.PASSWORD, "");
	}

	@Override
	protected Integer doInBackground(String... params) {

		if (!Constant.IS_USE_OPENFIRE)
			return Constant.LOGIN_SUCCESS;
		else {
			return loginOpenfire();
		}
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		switch (result) {
		case Constant.LOGIN_SUCCESS: // 登录成功
			//MyToast.showToast(context, "登陆openfire成功,"+loginNum);
			break;
		case Constant.LOGIN_ERROR_ACCOUNT_PASS:// 账户或者密码错误
			MyToast.showToast(context, "聊天可能不稳定");
			break;
		case Constant.SERVER_UNAVAILABLE:// 服务器连接失败
			//MyToast.showToast(context, "无法连接到服务器"+loginNum);
			if(loginNum<100)
			{
				new LoginToOpenfire(context).execute();
				loginNum++;
			}else {
				MyToast.showToast(context, "聊天可能不稳定");
			}
			break;
		case Constant.CONNECT_ERROR:// 未知异常
		case Constant.NOT_CONNECTED_TO_SERVER:
			//MyToast.showToast(context, "连接错误"+loginNum);
			if(loginNum<100)
			{
				new LoginToOpenfire(context).execute();
				loginNum++;
			}else {
				MyToast.showToast(context, "聊天可能不稳定");
			}
			break;
		case Constant.ALREADY_LOGGED:
			//MyToast.showToast(context, "已经登陆了"+loginNum);
			break;

		default:
			MyToast.showToast(context, "恭喜你遇到一个我们从来都没有遇到过的问题");
			break;
		}

	}

	
	// 登陆openfire
	private int loginOpenfire() {
		// SmackAndroid.init(context);
		try {
			XmppConnection.getConnection().login(accounts, password);
			// 连接服务器成功，更改在线状态
			Presence presence = new Presence(Presence.Type.available);
			XmppConnection.getConnection().sendPacket(presence);

			return Constant.LOGIN_SUCCESS;
		} catch (XMPPException e) {
			if (XmppConnection.getConnection().isConnected())
				XmppConnection.closeConnection();
			e.printStackTrace();
			return Constant.CONNECT_ERROR;
		} catch (IllegalStateException e) {
			// TODO: handle exception
			e.printStackTrace();
			if (XmppConnection.getConnection().isConnected())
				XmppConnection.closeConnection();
			if (e.getMessage().equals("Not connected to server."))
				return Constant.NOT_CONNECTED_TO_SERVER;
			else
				// Already logged in to server.
				return Constant.ALREADY_LOGGED;
		}

	}

}
