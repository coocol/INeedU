package com.eethan.ineedu.im;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.network.CustomerHttpClient;

public class XmppUtil {
	/**
	 * 根据jid获取用户id
	 */
	public static String getJidToUserId(String jid){
		return jid.split("@")[0];
	}
	
	public static String getUserIdToJid(String userId){
		return userId + "@" + Constant.XMPPNAME;
	}
	
	//根据IneedU的userId获取userInfo用于聊天消息来了以后的消息通知
	public static String getUserNameFromUserId(final int userId){
		// TODO Auto-generated method stub
		String jsonString = JacksonUtil.json().fromObjectToJson(new Integer(userId));
		NameValuePair getUserNameValuePair = new BasicNameValuePair("data", jsonString);
		String response = CustomerHttpClient.post(URLConstant.GET_USERINFO_FROM_USERID, getUserNameValuePair);
		String s = JacksonUtil.json().fromJsonToObject(response, UserInfo.class).getRealName();
		Log.d("聊天", s);
		return s;
	}
}
