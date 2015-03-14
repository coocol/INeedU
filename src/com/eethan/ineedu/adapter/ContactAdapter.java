package com.eethan.ineedu.adapter;

import com.eethan.ineedu.fragment.GeneralContactFragment;
import com.eethan.ineedu.fragment.RecentContactFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ContactAdapter extends FragmentPagerAdapter{

	//两个fragment的适配器，爱心排行榜
	private RecentContactFragment recentFragment = new RecentContactFragment();
	private GeneralContactFragment generalFragment = new GeneralContactFragment();
		
	public ContactAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		switch (arg0) {
		case 0:
			return recentFragment;
		case 1:
			return generalFragment;
		default:
			return recentFragment;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
