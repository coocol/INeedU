package com.eethan.ineedu.adapter;

import com.eethan.ineedu.fragment.DayLoveRankFragment;
import com.eethan.ineedu.fragment.SumLoveRankFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class LoveRankActivityAdapter extends FragmentPagerAdapter{

	//两个fragment的适配器，爱心排行榜
	private DayLoveRankFragment dayLoveRankFragment = new DayLoveRankFragment();
	private SumLoveRankFragment sumLoveRankFragment = new SumLoveRankFragment();
		
	public LoveRankActivityAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		switch (arg0) {
		case 0:
			return dayLoveRankFragment;
		case 1:
			return sumLoveRankFragment;
		default:
			return dayLoveRankFragment;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
