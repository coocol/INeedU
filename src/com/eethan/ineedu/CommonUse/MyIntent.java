package com.eethan.ineedu.CommonUse;

import com.eethan.ineedu.primaryactivity.ChatActivity;

import android.content.Context;
import android.content.Intent;

public class MyIntent {
	public static void toChatActivity(Context context,int userId,int friendId,String friendName,boolean isHidden)
	{
		Intent intent = new Intent(context,ChatActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("USERID", userId+"");
		intent.putExtra("FRIENDID", friendId+"");
		intent.putExtra("FRIEND_NAME", friendName);
		intent.putExtra("isHidden", isHidden);
		context.startActivity(intent);
	}
	public static void toChatActivityWithFlag(Context context,String userId,String friendId,String friendName,boolean isHidden)
	{
		Intent intent = new Intent(context,ChatActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("USERID", userId);
		intent.putExtra("FRIENDID", friendId);
		intent.putExtra("FRIEND_NAME", friendName);
		intent.putExtra("isHidden", isHidden);
		context.startActivity(intent);
	}
	//老写法
	public static void toChatActivity(Context context,String userId,String friendId,String friendName,boolean isHidden)
	{
		Intent intent = new Intent(context,ChatActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("USERID", userId);
		intent.putExtra("FRIENDID", friendId);
		intent.putExtra("FRIEND_NAME", friendName);
		intent.putExtra("isHidden", isHidden);
		context.startActivity(intent);
	}
	
}
