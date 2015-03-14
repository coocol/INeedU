package com.eethan.ineedu.mycontrol;

import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.MyTimer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 自定义toast
 */
public class MyToast {
	
private static Toast toast;

	private static View toastView;
	private static TextView tv;
	
	public static void showToast(Context context,String msg,boolean ifLong){
		
		if(toast == null){
			LayoutInflater inflater = LayoutInflater.from(context);
			toastView = inflater.inflate(R.layout.toast_view, null);
			toast = new Toast(context);
			toast.setView(toastView);  
			tv =(TextView)toastView.findViewById(R.id.toast_text);  
		}
		
		tv.setText(msg);  
			
		if (ifLong) {
			toast.setDuration(Toast.LENGTH_LONG);
		}else {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		if(MyTimer.ForbidRepeat(2000))
			toast.show();
	}
	public static void showToast(Context context,String msg){
		
		if(toast == null){
			LayoutInflater inflater = LayoutInflater.from(context);
			toastView = inflater.inflate(R.layout.toast_view, null);
			toast = new Toast(context);
			toast.setView(toastView);  
			tv =(TextView)toastView.findViewById(R.id.toast_text);  
		}
		
		tv.setText(msg);  
		toast.setDuration(Toast.LENGTH_SHORT);
		
		if(MyTimer.ForbidRepeat(2000))
			toast.show();
	}
}
