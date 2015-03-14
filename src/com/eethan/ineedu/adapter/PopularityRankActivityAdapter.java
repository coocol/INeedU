package com.eethan.ineedu.adapter;

import com.eethan.ineedu.fragment.SumPopularityRankFragment;
import com.eethan.ineedu.fragment.WeekPopularityRankFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PopularityRankActivityAdapter extends FragmentPagerAdapter{

	//两个fragment的适配器,人气榜
	private WeekPopularityRankFragment weekPopularityRankFragment = new WeekPopularityRankFragment();
	private SumPopularityRankFragment sumPopularityRankFragment = new SumPopularityRankFragment();
			
	public PopularityRankActivityAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		switch (arg0) {
		case 0:
			return weekPopularityRankFragment;
		case 1:
			return sumPopularityRankFragment;
		default:
			return weekPopularityRankFragment;
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
