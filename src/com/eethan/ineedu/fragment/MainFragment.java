package com.eethan.ineedu.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eethan.ineedu.adapter.MainFragmentpagerAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.floatmenu.FloatMenu;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.mycontrol.ThreeButtonDialog;
import com.eethan.ineedu.primaryactivity.ContactActivity;
import com.eethan.ineedu.primaryactivity.IssueNeedActivity;
import com.eethan.ineedu.primaryactivity.MainActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.secondaryactivity.SearchActivity;

public class MainFragment extends MyViewpagerFragment implements
		OnTouchListener {

	public static final int NEED = 0;
	public static final int FOUND = 1;
	public static final int NEAR = 2;

	public static int TYPE = Constant.TYPE_ALL;
	public static String SEX = Constant.SEX_ALL;
	public static double DISTANCE = Constant.DISTANCE_NATIONWIDE;
	private final String TAG = "MainFragment";
	// viewpager的适配器
	private MainFragmentpagerAdapter mainFragmentpagerAdapter;
	View mainFragmentView;
	// 导航栏按钮
	private ImageButton slidingMenuButton;
	private ImageButton issueNeedImageButton;
	private ImageButton searchImageButton;
	// private Button Dist;
	// private String dist="500米";
	// private myPopUp wPopWindow;
	public static boolean popupRefresh = false;
	private FloatMenu floatMenu;
	// final String[] adapterData = new String[] {
	// "500米","1000米","2000米","3000米","girls","boys"
	// };
	private Activity activity;

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (popupRefresh) {
			popupRefresh = false;
			// wPopWindow=new myPopUp(activity,mainFragmentView);
		}
		Log.i(TAG, "onResume");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		mainFragmentView = inflater.inflate(R.layout.fragment_main, container,
				false);
		// 初始化floatMenu
		InitFloatMenu();

		Log.i(TAG, "onCreate");
		activity = this.getActivity();
		// wPopWindow = new myPopUp(activity,mainFragmentView);
		// 得到导航栏
		findNavigationView(mainFragmentView);

		// 得到布局控件
		titleliLinearLayout = (LinearLayout) mainFragmentView
				.findViewById(R.id.title_linearlayout);
		viewPager = (ViewPager) mainFragmentView
				.findViewById(R.id.mainfragment_viewpager);
		viewPager.setOffscreenPageLimit(2);/* 预加载页面 */
		mainFragmentpagerAdapter = new MainFragmentpagerAdapter(
				getFragmentManager());
		viewPager.setAdapter(mainFragmentpagerAdapter);
		viewPager.setCurrentItem(NEED);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		// 初始化页卡标题
		firstTextView = (TextView) mainFragmentView
				.findViewById(R.id.need_text);
		secondTextView = (TextView) mainFragmentView
				.findViewById(R.id.find_text);
		thirdTextView = (TextView) mainFragmentView
				.findViewById(R.id.near_text);
		firstTextView.setOnClickListener(new MyOnClickListener(NEED));
		secondTextView.setOnClickListener(new MyOnClickListener(FOUND));
		thirdTextView.setOnClickListener(new MyOnClickListener(NEAR));

		return mainFragmentView;
	}

	private void InitFloatMenu() {
		// TODO Auto-generated method stub
		floatMenu = new FloatMenu() {
			@Override
			public void onClickListener(int pos) {
				// TODO Auto-generated method stub
				super.onClickListener(pos);
				if (pos == 0) {
					startActivity(new Intent(getActivity(),
							ContactActivity.class));
					floatMenu.closeFloatMenu();
				}
				if (pos == 1) {
					ThreeButtonDialog distanceDialog = new ThreeButtonDialog(
							getActivity()) {
						@Override
						public void firstOnclick() {
							// TODO Auto-generated method stub
							MainFragment.DISTANCE = Constant.DISTANCE_NEAR;
							floatMenu.closeFloatMenu();
						}

						@Override
						public void secondOnclick() {
							// TODO Auto-generated method stub
							MainFragment.DISTANCE = Constant.DISTANCE_SCHOOL;
							floatMenu.closeFloatMenu();
						}

						@Override
						public void thirdOnclick() {
							// TODO Auto-generated method stub
							MainFragment.DISTANCE = Constant.DISTANCE_NATIONWIDE;
							floatMenu.closeFloatMenu();
						}

						@Override
						public void actionAfterClick() {
							// TODO Auto-generated method stub
							super.actionAfterClick();
							switch (viewPager.getCurrentItem()) {
							case NEED:
								NeedFragment.isRefresh = true;
								NeedFragment.GetFragment().onResume();
								break;
							case NEAR:
								NearFragment.isRefresh = true;
								NearFragment.GetFragment().onResume();
								break;
							default:
								viewPager.setCurrentItem(NEED);
								NeedFragment.isRefresh = true;
								NeedFragment.GetFragment().onResume();
								break;
							}
						}
					};
					distanceDialog.setFirstBtnText("附近");
					distanceDialog.setSecondBtnText("本校");
					distanceDialog.setThirdBtnText("全国");
					distanceDialog.setText("选择距离");
					distanceDialog.show();
				}
				if (pos == 2) {
					ThreeButtonDialog distanceDialog = new ThreeButtonDialog(
							getActivity()) {
						@Override
						public void firstOnclick() {
							// TODO Auto-generated method stub
							MainFragment.SEX = Constant.SEX_GRIL;
							floatMenu.closeFloatMenu();
						}

						@Override
						public void secondOnclick() {
							// TODO Auto-generated method stub
							MainFragment.SEX = Constant.SEX_BOY;
							floatMenu.closeFloatMenu();
						}

						@Override
						public void thirdOnclick() {
							// TODO Auto-generated method stub
							MainFragment.SEX = Constant.SEX_ALL;
							floatMenu.closeFloatMenu();
						}

						@Override
						public void actionAfterClick() {
							// TODO Auto-generated method stub
							super.actionAfterClick();
							switch (viewPager.getCurrentItem()) {
							case NEED:
								NeedFragment.isRefresh = true;
								NeedFragment.GetFragment().onResume();
								break;
							case NEAR:
								NearFragment.isRefresh = true;
								NearFragment.GetFragment().onResume();
								break;
							default:
								viewPager.setCurrentItem(NEED);
								NeedFragment.isRefresh = true;
								NeedFragment.GetFragment().onResume();
								break;
							}
						}
					};

					distanceDialog.setFirstBtnText("girl");
					distanceDialog.setSecondBtnText("boy");
					distanceDialog.setThirdBtnText("all");
					distanceDialog.setText("性别筛选");
					distanceDialog.show();
				}
			}
		};
		floatMenu.findView(mainFragmentView, getActivity()
				.getApplicationContext());

	}

	private void findNavigationView(View mainFragmentView) {
		// TODO Auto-generated method stub
		slidingMenuButton = (ImageButton) mainFragmentView
				.findViewById(R.id.main_title_bar_button1);
		slidingMenuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				MainActivity.GetSlidingMenu().toggle();
			}
		});

		issueNeedImageButton = (ImageButton) mainFragmentView
				.findViewById(R.id.main_title_bar_button2);
		issueNeedImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivity(new Intent(getActivity().getApplicationContext(),
						IssueNeedActivity.class));
			}
		});
		searchImageButton = (ImageButton) mainFragmentView
				.findViewById(R.id.main_title_bar_button3);
		searchImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getActivity().getApplicationContext(),
						SearchActivity.class));
			}
		});

		// Dist = (Button)mainFragmentView.findViewById(R.id.Dist);
		// Dist.setOnClickListener(new OnClickListener(){
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// wPopWindow.showPopupWindow(Dist);
		// }
		// });
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		if (mainFragmentpagerAdapter != null) {
			mainFragmentpagerAdapter.notifyDataSetChanged();
		}
		super.onStart();
	}

	//
	// private class myPopUp extends mgpopup
	// {
	//
	// public myPopUp(Activity context, View mainFragmentView) {
	// super(context, mainFragmentView);
	// // TODO Auto-generated constructor stub
	// }
	// //点击Dist 显示弹出窗口 点击选项后执行的动作 重写以使用
	//
	// }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		MyToast.showToast(getActivity(), "onTouch");

		return false;
	}

}