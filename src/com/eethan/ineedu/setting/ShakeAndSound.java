package com.eethan.ineedu.setting;

import com.eethan.ineedu.CommonUse.SPHelper;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class ShakeAndSound {
	private Context context;
	boolean isShake;
	boolean isSound;
	public ShakeAndSound(Context context)
	{
		this.context=context;
	}
	public void PlayShakeAndSound()
	{
		isShake=new SPHelper(context).isShake();
		isSound=new SPHelper(context).isSound();
		
		if(isSound)
		{
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);  
	        Ringtone r = RingtoneManager.getRingtone(context, notification);  
	        r.play();
		}
		if(isShake)//震动
		{
			try {
				Thread.sleep(300);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ShakeHelper.Vibrate(context, 500);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ShakeHelper.Vibrate(context, 500);
		}
	}
}
