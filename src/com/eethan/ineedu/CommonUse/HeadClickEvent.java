package com.eethan.ineedu.CommonUse;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.NetCondition;
import com.eethan.ineedu.primaryactivity.MyInformationActivity;
import com.eethan.ineedu.primaryactivity.PersonInformationActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class HeadClickEvent {
	private Context context;
	private int userId;
	public HeadClickEvent(Context context,int userId)
	{
		this.context=context;
		this.userId=userId;
	}
	public void click()
	{
		if(!NetCondition.isNetworkConnected(context))
		{
			MyToast.showToast(context, "无网络连接!");
			return;
		}
		SharedPreferences setting = context.getSharedPreferences(Constant.INEEDUSPR, 0);
		int myUserId=setting.getInt(Constant.ID, -1);
		
		Intent intent;
		if(myUserId==userId)
			intent = new Intent(context,MyInformationActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		else 
			intent = new Intent(context,PersonInformationActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("userId",String.valueOf(userId));
		context.startActivity(intent);
	}
}
