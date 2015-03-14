package com.eethan.ineedu.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MyViewpagerFragment extends Fragment{
	//三个标题
	protected TextView firstTextView,secondTextView,thirdTextView;
	protected int currentIndex = 0;//当前页卡编号
		
	//管理滑动页面的viewpager
	protected ViewPager viewPager;	
	//放字的linearlayout
	protected LinearLayout titleliLinearLayout;
		
	
	
	/////
	/* 页卡切换监听 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			
			currentIndex = arg0;
			
			setTextTitleSelectedColor(arg0);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/* 设置标题文本的颜色 */
	protected void setTextTitleSelectedColor(int arg0) {
		int count = viewPager.getChildCount();
		for (int i = 0; i < count; i++) {
			TextView mTextView = (TextView) titleliLinearLayout.getChildAt(i);
			if (arg0 == i) {
				mTextView.setTextColor(0xff03b6f9);
			} else {
				mTextView.setTextColor(0xff000000);
			}
		}
	}


	/* 标题点击监听 */
	protected class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	}

	
}
