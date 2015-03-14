package com.eethan.ineedu.util;

import java.util.Timer;
import java.util.TimerTask;

import com.eethan.ineedu.constant.Constant;


public class MyTimer {
	private static int time=0;
	private static int GET_MORE_TOAST_TIME_LEFT=0;
	private static boolean cancel=false;
	public static void Start()
	{
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(cancel)
					this.cancel();
				time+=10;
			}
		}, 0,10);
	}
	
	public static void RefreshDelay()
	{
		if(time<Constant.REFRESH_DELAY)
		{
			cancel=true;
			try {
				Thread.sleep((Constant.REFRESH_DELAY-time));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time=0;
			cancel=false;
		}
		else
		{
			time=0;
			cancel=false;
		}
	}
	/**
	 * if(ForbidRepeat(time)
	 * {
	 * 		//Todo
	 * }
	 * 在time时间内，括号内的事件不会重复发生
	 */
	public static boolean ForbidRepeat(int time)
	{
		if(GET_MORE_TOAST_TIME_LEFT==0)
		{
			GET_MORE_TOAST_TIME_LEFT=time;
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(GET_MORE_TOAST_TIME_LEFT!=0)
						GET_MORE_TOAST_TIME_LEFT-=10;
					else
					{
						this.cancel();
						GET_MORE_TOAST_TIME_LEFT=0;
					}
				}
			}, 0,10);
			return true;
		}
		return false;
	}
	
}
