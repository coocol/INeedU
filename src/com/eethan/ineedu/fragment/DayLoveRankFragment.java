package com.eethan.ineedu.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import com.eethan.ineedu.primaryactivity.ChatActivity;
import com.eethan.ineedu.primaryactivity.LoveRankActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.CommonUse.MyIntent;
import com.eethan.ineedu.adapter.DayLoveRankFragmentAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.model.DayLoveRankFragmentModel;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DayLoveRankFragment extends Fragment{
	private ListView mPullRefreshListView;
	ArrayList<DayLoveRankFragmentModel> ncList = new ArrayList<DayLoveRankFragmentModel>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View SumLoveRankFragmentView = inflater.inflate(R.layout.nearby_dayrank_page, container, false);
		mPullRefreshListView = (ListView) SumLoveRankFragmentView.findViewById(R.id.nearby_dayrank_page_listview);
		DayLoveRankFragmentAdapter NearContacts = new DayLoveRankFragmentAdapter(getActivity().getApplicationContext(),getNearContact());
		mPullRefreshListView.setAdapter(NearContacts);
        
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				String friendId = ""+ncList.get(pos).getId();
				String friend_Name = ncList.get(pos).getName();
				SharedPreferences lightDB = getActivity().getSharedPreferences(Constant.INEEDUSPR,0);
				String userId = ""+lightDB.getInt(Constant.ID,-1);
				
				MyIntent.toChatActivity(getActivity().getApplicationContext(), userId, friendId, friend_Name, false);
				
			}
		});
		/*mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new GetDataTask().execute();
			}
		});*/

		// 拉至最底部
		/*mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				PULL_STATE=State.PULL_BOTTOM;
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//加载更多数据
				MyToast.showToast(getActivity().getApplicationContext(), "End of List!", Toast.LENGTH_SHORT).show();
			}
		});*/
		return SumLoveRankFragmentView;
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		//后台线程操作
		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			//刷新后执行的操作
			
			// Call onRefreshComplete when the list has been refreshed.
			//mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	private ArrayList<DayLoveRankFragmentModel> getNearContact(){
		//创建实体，在这里从数据库取数据
		ncList=LoveRankActivity.nContacts;
		return ncList;
	}
	@Override
	public void onDetach() {
	    super.onDetach();

	    try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("DayLoveRankFragment"); //统计页面
		
		
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("DayLoveRankFragment"); 
	}
}
