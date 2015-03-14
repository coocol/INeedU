package com.eethan.ineedu.CommonUse;

import android.content.Context;
import android.os.AsyncTask;

import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.GirlsWishesActivity;

public class AddLoveNum extends AsyncTask<Void,Void, Boolean>{
	
	private Context context;
	private int num;
	private int userid;
	public AddLoveNum(Context context,int num,int userid){
		this.context = context;
		this.num = num;
		this.userid = userid;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		
		boolean resbool = false;
		try {
			JsonObject jsonObject = new JsonObject();
			jsonObject.setInt1(userid);
			jsonObject.setInt2(num);
			String responsesString = ServerCommunication.request(jsonObject, URLConstant.ADD_LOVE_NUM);
			resbool = JacksonUtil.json().fromJsonToObject(responsesString,boolean.class);
			return resbool;
		} catch (Exception e) {
			return false;
		}
	}

	
	@Override
	protected void onPostExecute(Boolean result) {
		if(result!=null && result){
			String userSex = new SPHelper(context).get(
					"sex").toString();
			if(userSex.equals("男")){
				MyToast.showToast(context, "男神值+"+num);
			}else {
				MyToast.showToast(context, "女神值+"+num);
			}
		}
		super.onPostExecute(result);
	}
}
