package com.eethan.ineedu.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.eethan.ineedu.adapter.NeedFragmentAdapter;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Need;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.RefreshNeedJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.NeedFragmentModel;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.NeedDetailActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataCache;
import com.eethan.ineedu.util.MyTimer;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

public class NeedFragment extends Fragment {
	public static final int REFRESH_OVER = 1;
	public static final int GET_MORE = 2;
	public static final int FAILED = 3;

	public static NeedFragment context;
	public static int lastNum = 0;
	public static boolean REFRESH_TASK = true;
	public static boolean isRefresh = false;

	ArrayList<Need> ns = new ArrayList<Need>();
	ArrayList<UserInfo> userinfos = new ArrayList<UserInfo>();
	ArrayList<UserDetailInfo> userDetailinfos = new ArrayList<UserDetailInfo>();
	ArrayList<UserInfo> solveUserinfos = new ArrayList<UserInfo>();

	private PullToRefreshListView mPullRefreshListView;
	public NeedFragmentAdapter needAdapter;

	View needFragmentView;
	private LoadingDialog loadingDialog;
	private static Fragment fragment;

	public static Fragment GetFragment() {
		return fragment;
	}

	private final String TAG = "NeedFragment";

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "onResume");
		if (isRefresh) {
			loadingDialog.show();
			REFRESH_TASK = true;
			new GetDataTask().execute();
			isRefresh = false;
		}
		MobclickAgent.onPageStart("NeedFragment"); // 统计页面
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("NeedFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		needFragmentView = inflater.inflate(R.layout.home_page, null);
		loadingDialog = new LoadingDialog(getActivity());
		fragment = NeedFragment.this;
		Init();
		MyPullRefreshListViewListener();
		context = NeedFragment.this;
		new GetDataTask().execute();
		return needFragmentView;
	}

	private void MyPullRefreshListViewListener() {
		// 拉至最底部监听
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {
						REFRESH_TASK = false;
						new GetDataTask().execute();
					}
				});

		// item单击监听
		mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				if (id < -1) {
					// 点击的是headerView或者footerView
					return;
				}
				isRefresh = false;
				int realPosition = (int) id;
				Intent intent = new Intent(getActivity(),
						NeedDetailActivity.class);
				intent.putExtra("needId", DataCache.needs.get(realPosition)
						.getId());
				intent.putExtra("userId", DataCache.needs.get(realPosition)
						.getUserId());
				startActivity(intent);
			}
		});

		// 刷新监听
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity()
								.getApplicationContext(), System
								.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						// Do work to refresh the list here.
						REFRESH_TASK = true;
						new GetDataTask().execute();

					}
				});
	}

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		@Override
		protected Object doInBackground(Void... params) {
			if (REFRESH_TASK)
				MyTimer.Start();
			// 服务器需要的数据
			JsonObject DataWhatServerNeed = new JsonObject();
			double lat, lng;
			lat = LocateManager.getInstance().getLatitude();
			lng = LocateManager.getInstance().getLontitude();

			DataWhatServerNeed.setDouble1(lng);
			DataWhatServerNeed.setDouble2(lat);
			DataWhatServerNeed.setInt1(MainFragment.TYPE);// 求助类型
			if (!REFRESH_TASK)
				DataWhatServerNeed.setInt2(lastNum);
			DataWhatServerNeed.setString1(MainFragment.SEX);// 性别筛选
			DataWhatServerNeed.setString2(MainFragment.DISTANCE + "");// 距离筛选

			String response = null, URL = URLConstant.REFRESH_NEED_URL;
			if (!REFRESH_TASK)
				URL = URLConstant.NEED_GET_MORE;
			try {
				response = ServerCommunication.requestWithoutEncrypt(
						DataWhatServerNeed, URL);// 发送请求，获得数据
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}
			RefreshNeedJsonObject refreshNeed = null;
			try {
				refreshNeed = JacksonUtil.json().fromJsonToObject(response,
						RefreshNeedJsonObject.class);
			} catch (Exception e) {
				refreshNeed = null;
			}
			if (REFRESH_TASK)
				MyTimer.RefreshDelay();
			return refreshNeed;
		}

		@Override
		protected void onPostExecute(Object result) {

			if (result == null) {
				loadingDialog.dismiss();
				MyToast.showToast(getActivity(), "加载失败");
				mPullRefreshListView.onRefreshComplete();
				super.onPostExecute(result);
				return;
			}
			if (!ServerCommunication.checkResult(getActivity(), result))// 各种网络异常的处理部分
			{
				MyToast.showToast(getActivity(), (String) result);
				loadingDialog.dismiss();
				mPullRefreshListView.onRefreshComplete();
				return;
			}
			if (REFRESH_TASK) {
				// doInBackground返回数据成功后进行数据操作处理

				RefreshNeedJsonObject refreshNeedJsonObject = (RefreshNeedJsonObject) result;
				ns = (ArrayList<Need>) refreshNeedJsonObject.getNeeds();
				userinfos = (ArrayList<UserInfo>) refreshNeedJsonObject
						.getUserInfos();
				userDetailinfos = (ArrayList<UserDetailInfo>) refreshNeedJsonObject
						.getUserDetailInfos();
				solveUserinfos = (ArrayList<UserInfo>) refreshNeedJsonObject
						.getSolveUserInfos();

				if (ns.size() == 0) {

					MyToast.showToast(getActivity(), "没有该类信息~", false);
					DataCache.needs.clear();
					loadingDialog.dismiss();
					mPullRefreshListView.onRefreshComplete();
					super.onPostExecute(result);
					return;
				}
				lastNum = ns.get(ns.size() - 1).getId();

			} else {
				RefreshNeedJsonObject getMoreData = (RefreshNeedJsonObject) result;

				ArrayList<Need> tmpNeeds = (ArrayList<Need>) getMoreData
						.getNeeds();
				ArrayList<UserInfo> tmpUserinfos = (ArrayList<UserInfo>) getMoreData
						.getUserInfos();
				ArrayList<UserDetailInfo> tmpUserDetailinfos = (ArrayList<UserDetailInfo>) getMoreData
						.getUserDetailInfos();
				ArrayList<UserInfo> tmpSolveUserinfos = (ArrayList<UserInfo>) getMoreData
						.getSolveUserInfos();
				if (tmpNeeds.size() == 0) {

					MyToast.showToast(getActivity(), "没有更多了~", false);
					loadingDialog.dismiss();
					mPullRefreshListView.onRefreshComplete();
					super.onPostExecute(result);
					return;
				}

				ns.clear();
				userinfos.clear();
				userDetailinfos.clear();
				solveUserinfos.clear();

				ns = tmpNeeds;
				userinfos = tmpUserinfos;
				solveUserinfos = tmpSolveUserinfos;
				userDetailinfos = tmpUserDetailinfos;
				lastNum = ns.get(ns.size() - 1).getId();
				Log.i("DB", "lastNum:" + lastNum);

			}
			// 数据处理
			if (REFRESH_TASK)
				DataCache.needs.clear();
			// 数据放入适配器绑定的Need数组中
			for (int i = 0; i < ns.size(); i++) {
				String nickname = userinfos.get(i).getNickName();
				String sex = userinfos.get(i).getSex();
				String content = ns.get(i).getContent();
				String reward = ns.get(i).getReward();
				String helperName = solveUserinfos.get(i).getNickName();
				int loveNum = userinfos.get(i).getLoveNum();
				String cademyString = userDetailinfos.get(i).getAcademy();
				int popularityNum = userinfos.get(i).getPopularityNum();
				int solveId = ns.get(i).getSolveId();
				int type = ns.get(i).getType();
				int commentNum = ns.get(i).getCommentNum();
				long time = ns.get(i).getTime();
				long timeLimit = ns.get(i).getTimeLimit();
				int id = ns.get(i).getId();
				int userId = ns.get(i).getUserId();

				NeedFragmentModel n = new NeedFragmentModel(nickname, sex,
						content, reward, helperName, cademyString, id, userId,
						loveNum, popularityNum, solveId, type, commentNum,
						time, timeLimit);
				n.setLat(ns.get(i).getLat());
				n.setLng(ns.get(i).getLng());
				DataCache.needs.add(n);
			}
			needAdapter.notifyDataSetChanged();

			// 插入本地库
			// DatabaseToModel.databaseToModel.ClearTable(DBHelper.TABLE_NEED);
			// DatabaseToModel.databaseToModel.ClearTable(DBHelper.TABLE_SOLVEUSERINFO);
			// DatabaseToModel.databaseToModel.ClearTable(DBHelper.TABLE_USERINFO);
			// for(int i=ns.size()-1;i>=0;i--)
			// DatabaseToModel.databaseToModel.InsertObject(ns.get(i),
			// DBHelper.TABLE_NEED);
			// for(int i=userinfos.size()-1;i>=0;i--)
			// DatabaseToModel.databaseToModel.InsertObject(userinfos.get(i),
			// DBHelper.TABLE_USERINFO);
			// for(int i=solveUserinfos.size()-1;i>=0;i--)
			// if(solveUserinfos.get(i).getId()!=0)
			// DatabaseToModel.databaseToModel.InsertObject(solveUserinfos.get(i),DBHelper.TABLE_SOLVEUSERINFO);
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			loadingDialog.dismiss();
			super.onPostExecute(result);
		}
	}

	// private ArrayList<NeedFragmentModel> getLocalData(){
	// ArrayList<NeedFragmentModel> needList = new
	// ArrayList<NeedFragmentModel>();
	// //创建实体，在这里从数据库取数据
	// needList=DatabaseToModel.databaseToModel.getNeedFragmentData();
	// return needList;
	// }
	public void Init() {
		ns = new ArrayList<Need>();
		userinfos = new ArrayList<UserInfo>();
		userDetailinfos = new ArrayList<UserDetailInfo>();
		solveUserinfos = new ArrayList<UserInfo>();
		DataCache.needs = new ArrayList<NeedFragmentModel>();

		mPullRefreshListView = (PullToRefreshListView) needFragmentView
				.findViewById(R.id.home_page_listview);

		needAdapter = new NeedFragmentAdapter(getActivity(), DataCache.needs);
		mPullRefreshListView.setAdapter(needAdapter);

	}

}
