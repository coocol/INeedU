package com.eethan.ineedu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.NetCondition;
import com.eethan.ineedu.primaryactivity.GirlsWishesActivity;
import com.eethan.ineedu.primaryactivity.LoveRankActivity;
import com.eethan.ineedu.primaryactivity.MolestActivity;
import com.eethan.ineedu.primaryactivity.MoodActivity;
import com.eethan.ineedu.primaryactivity.PhotoNewsActivity;
import com.eethan.ineedu.primaryactivity.PlaysActivity;
import com.eethan.ineedu.primaryactivity.PopularityRankActivity;
import com.eethan.ineedu.primaryactivity.PourListenActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.primaryactivity.WakeUpActivity;
import com.umeng.analytics.MobclickAgent;


public class FindFragment extends Fragment{
	//发现页面的三个按钮，用layout充当按钮
	private RelativeLayout pourlistenRelativeLayout;
	private RelativeLayout photoewsRelativeLayout;
	private RelativeLayout playsRelativeLayout;
	private RelativeLayout wakeUpLayout;
	private RelativeLayout molestLayout;
	private RelativeLayout nightLayout;

	private LinearLayout wishLinearLayout;
	private LinearLayout loveRelativeLayout;
	private LinearLayout popularityRelativeLayout;
	private LinearLayout moodLinearLayout;
	private static Fragment fragment;
	private MyTakeDialog myTakeDialog;
	public static Fragment GetFragment()
	{
		return fragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fragment=this;
		View findFragmentView = inflater.inflate(R.layout.find_page,null);
		
		findView(findFragmentView);
		return findFragmentView;
	}
	
	//找到资源,设置监听器
	void findView(View findFragmentView){
		pourlistenRelativeLayout = (RelativeLayout)findFragmentView.findViewById(R.id.say);
		pourlistenRelativeLayout.setOnClickListener(new MyOnViewClickListener(1));
		photoewsRelativeLayout = (RelativeLayout)findFragmentView.findViewById(R.id.takephoto);
		photoewsRelativeLayout.setOnClickListener(new MyOnViewClickListener(2));
		wakeUpLayout = (RelativeLayout)findFragmentView.findViewById(R.id.morning);
		wakeUpLayout.setOnClickListener(new MyOnViewClickListener(5));
		molestLayout = (RelativeLayout)findFragmentView.findViewById(R.id.molest);
		molestLayout.setOnClickListener(new MyOnViewClickListener(6));
		nightLayout = (RelativeLayout)findFragmentView.findViewById(R.id.night);
		nightLayout.setOnClickListener(new MyOnViewClickListener(7));
		moodLinearLayout = (LinearLayout) findFragmentView.findViewById(R.id.ll_mood);
		loveRelativeLayout = (LinearLayout)findFragmentView.findViewById(R.id.love);
		loveRelativeLayout.setOnClickListener(new MyOnViewClickListener(3));
		popularityRelativeLayout = (LinearLayout)findFragmentView.findViewById(R.id.popularity);
		popularityRelativeLayout.setOnClickListener(new MyOnViewClickListener(4));
		
		moodLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), MoodActivity.class);
				startActivity(intent);
			}
		});
		
		playsRelativeLayout = (RelativeLayout)findFragmentView.findViewById(R.id.play);
		playsRelativeLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), PlaysActivity.class);
				startActivity(intent);
			}
		});
		wishLinearLayout = (LinearLayout)findFragmentView.findViewById(R.id.wish);
		wishLinearLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
	//		MyToast.showToast(getActivity(), "敬请期待光棍节活动~");
				Intent intent = new Intent();
				intent.setClass(getActivity(), GirlsWishesActivity.class);
				startActivity(intent);
			}
		});
	}
	
	//点击的监听器
	private class MyOnViewClickListener implements OnClickListener{

		private int position;
		private Intent intent;
		public MyOnViewClickListener(int position) {
			// TODO Auto-generated constructor stub
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (position) {
			case 1://我想说
				intent = new Intent(getActivity().getApplicationContext(), PourListenActivity.class);
				if(NetCondition.isNetworkConnected(getActivity())&&!NetCondition.isWifiConnected(getActivity()))//有网并且不是WIFI
					{
						myTakeDialog=new MyTakeDialog(getActivity()){
						@Override
						public void onYesButtonClick() {
							// TODO Auto-generated method stub
							getActivity().startActivity(intent);
							myTakeDialog.dismiss();
						}
					};
					myTakeDialog.setText("我想说页面图片较多,确认继续?");
					myTakeDialog.show();
				}else
					getActivity().startActivity(intent);
				break;
			case 2://我想拍
				intent = new Intent(getActivity().getApplicationContext(), PhotoNewsActivity.class);
				getActivity().startActivity(intent);
				break;
			case 3://附近活雷锋
				intent = new Intent(getActivity().getApplicationContext(), LoveRankActivity.class);
				getActivity().startActivity(intent);
				break;
			case 4://附近男女神
				intent = new Intent(getActivity().getApplicationContext(), PopularityRankActivity.class);
				getActivity().startActivity(intent);
				break;
			case 5://起床困难户
				if(true)
				{
					MyToast.showToast(getActivity(), "正在内测,敬请期待~");
					break;
				}
				intent = new Intent(getActivity().getApplicationContext(), WakeUpActivity.class);
				getActivity().startActivity(intent);
				break;
			case 6://调戏ee团队
				intent = new Intent(getActivity().getApplicationContext(), MolestActivity.class);
				getActivity().startActivity(intent);
				break;
			case 7://深夜话疗
				if(true)
				{
					MyToast.showToast(getActivity(), "正在内测,敬请期待~");
					break;
				}
				break;
			default:
				break;
			}
		}
		
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("FindFragment"); //统计页面
		
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("FindFragment"); 
	}
	
}
