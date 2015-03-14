package com.eethan.ineedu.adapter;

import com.eethan.ineedu.fragment.FindFragment;
import com.eethan.ineedu.fragment.NeedFragment;
import com.eethan.ineedu.fragment.NearFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class MainFragmentpagerAdapter extends FragmentStatePagerAdapter{

	//三个fragment的适配器，need,find,near
	private NeedFragment needFragment = new NeedFragment();
	private FindFragment findFragment = new FindFragment();
	private NearFragment nearFragment = new NearFragment();
	
	public MainFragmentpagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		switch (arg0) {
		case 0:
			Log.i("NET","0");
			return needFragment;
		case 1:
			Log.i("NET","1");
			return findFragment;
		case 2:
			Log.i("NET","2");
			return nearFragment;
		default:
			Log.i("NET","0");
			return needFragment;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

}
