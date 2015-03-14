package com.eethan.ineedu.util;

import java.util.LinkedList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.Application;

public class SysApplication extends Application {
private List<Activity> mList = new LinkedList<Activity>();
private static SysApplication instance;

private SysApplication() {
}

public synchronized static SysApplication getInstance() {
   if (null == instance) {
         instance = new SysApplication();
      }
         return instance;
      }

// add Activity
public void addActivity(Activity activity) {
      mList.add(activity);
}
//退出最后的一个Activity
public void exitActivity() {
	if(mList.get(mList.size()-1)!=null)
	{
		mList.get(mList.size()-1).finish();       
		mList.remove(mList.size()-1);
	}
}
public Activity getActivity()
{
	if(mList.get(mList.size()-1)!=null)
	{
		return mList.get(mList.size()-1);    
	}
	return null;
}
public void removeActivity(Activity activity)
{
	for(int i=0;i<mList.size();i++)
		if(mList.get(i)==activity)
			mList.remove(i);
}
//一次退出多个Activity
public void exitAll() {
	for(int i=0;i<mList.size();i++)
	{
		if(mList.get(i)!=null)
	    {
			mList.get(i).finish();
	    }
	}
	mList.clear();
}
public void empty()
{
	mList.clear();
}

@Override
public void onLowMemory() {
    super.onLowMemory();
      System.gc();
}

}
