package com.eethan.ineedu.primaryactivity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.eethan.ineedu.adapter.NeedFragmentAdapter;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Need;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.fragment.NeedFragment;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.RefreshNeedJsonObject;
import com.eethan.ineedu.model.NeedFragmentModel;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.MyTimer;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class AllNeedsActivity extends BaseActivity {
	public static final int REFRESH_OVER = 1;
	public static final int GET_MORE = 2;
	public static final int FAILED = 3;

	public static NeedFragment context;
	public static int lastNum = 0;
	public static boolean REFRESH_TASK = true;

	private ImageButton backButton;

	public int userId;
	ArrayList<Need> ns = new ArrayList<Need>();
	ArrayList<UserInfo> userinfos = new ArrayList<UserInfo>();
	ArrayList<UserDetailInfo> userDetailInfos = new ArrayList<UserDetailInfo>();
	ArrayList<UserInfo> solveUserinfos = new ArrayList<UserInfo>();

	private PullToRefreshListView mPullRefreshListView;
	public NeedFragmentAdapter needAdapter;
	ArrayList<NeedFragmentModel> needs = new ArrayList<NeedFragmentModel>();

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		REFRESH_TASK = true;
		new GetDataTask().execute();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_needs_page);
		setPageName("AllNeedsActivity");
		loadingDialogShow();
		userId = getIntent().getExtras().getInt("userId");
		new GetDataTask().execute();
		Init();
		MyPullRefreshListViewListener();
	}

	private void Init() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.home_page_listview);
		needAdapter = new NeedFragmentAdapter(AllNeedsActivity.this, needs);
		mPullRefreshListView.setAdapter(needAdapter);
		backButton = (ImageButton) findViewById(R.id.allneeds_imgbt_back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AllNeedsActivity.this.finish();
			}
		});
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
				int realPosition = (int) id;
				Intent intent = new Intent(AllNeedsActivity.this,
						NeedDetailActivity.class);
				intent.putExtra("needId", needs.get(realPosition).getId());
				intent.putExtra("userId", needs.get(realPosition).getUserId());
				startActivity(intent);

			}
		});

		// 刷新监听
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								AllNeedsActivity.this,
								System.currentTimeMillis(),
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

	// 用于异步获取数据后刷新UI
	@SuppressLint("HandlerLeak")
	private Handler dataHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (ns.size() == 0) {
				if (msg.what == REFRESH_OVER) {
					MyToast.showToast(AllNeedsActivity.this, "没有这样的信息了");
					return;
				}
				MyToast.showToast(AllNeedsActivity.this, "没有更多了");
				return;
			}
			if (msg.what == REFRESH_OVER)
				needs.clear();
			// 数据放入适配器绑定的Need数组中
			for (int i = 0; i < ns.size(); i++) {
				String nickname = userinfos.get(i).getNickName();
				String sex = userinfos.get(i).getSex();
				String content = ns.get(i).getContent();
				String reward = ns.get(i).getReward();
				String helperName = solveUserinfos.get(i).getNickName();
				String codemyString = userDetailInfos.get(i).getAcademy();
				int loveNum = userinfos.get(i).getLoveNum();
				int popularityNum = userinfos.get(i).getPopularityNum();
				int solveId = ns.get(i).getSolveId();
				int type = ns.get(i).getType();
				int commentNum = ns.get(i).getCommentNum();
				long time = ns.get(i).getTime();
				long timeLimit = ns.get(i).getTimeLimit();
				int id = ns.get(i).getId();
				int userId = userinfos.get(i).getUserId();

				NeedFragmentModel n = new NeedFragmentModel(nickname, sex,
						content, reward, helperName,codemyString, id, userId, loveNum,
						popularityNum, solveId, type, commentNum, time,
						timeLimit);

				needs.add(n);
			}
			needAdapter.notifyDataSetChanged();

		}
	};

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		@Override
		protected Object doInBackground(Void... params) {

			if (REFRESH_TASK)
				MyTimer.Start();
			Object object = (Integer) userId;
			String URL = URLConstant.GET_ALL_NEEDS_URL;
			if (!REFRESH_TASK) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.setInt1(userId);
				jsonObject.setInt2(lastNum);
				object = jsonObject;

				URL = URLConstant.GET_MORE_ALL_NEEDS_URL;
			}

			String response;
			RefreshNeedJsonObject result = null;
			try {
				response = ServerCommunication.requestWithoutEncrypt(object, URL);
				result = JacksonUtil.json().fromJsonToObject(response,
						RefreshNeedJsonObject.class);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			if (REFRESH_TASK)
				MyTimer.RefreshDelay();
			return result;

		}

		@Override
		protected void onPostExecute(Object result) {
			if (result == null) {
				MyToast.showToast(getContext(), "加载数据失败");
				mPullRefreshListView.onRefreshComplete();
				loadingDialogDismiss();
				super.onPostExecute(result);
				return;
			}
			if (!ServerCommunication.checkResult(getContext(), result)) {
				MyToast.showToast(getContext(), (String) result);
				mPullRefreshListView.onRefreshComplete();
				loadingDialogDismiss();
				super.onPostExecute(result);
				return;
			}
			if (REFRESH_TASK) {
				// doInBackground返回数据成功后进行数据操作处理

				RefreshNeedJsonObject refreshNeedJsonObject = (RefreshNeedJsonObject) result;
				ns = (ArrayList<Need>) refreshNeedJsonObject.getNeeds();
				userinfos = (ArrayList<UserInfo>) refreshNeedJsonObject
						.getUserInfos();
				userDetailInfos = (ArrayList<UserDetailInfo>) refreshNeedJsonObject.getUserDetailInfos();
				solveUserinfos = (ArrayList<UserInfo>) refreshNeedJsonObject
						.getSolveUserInfos();
				if (ns.size() == 0) {
					MyToast.showToast(AllNeedsActivity.this, "没有该类信息~");
					mPullRefreshListView.onRefreshComplete();
					loadingDialogDismiss();
					super.onPostExecute(result);
					return;
				}
				lastNum = ns.get(ns.size() - 1).getId();

				Message msg = dataHandler.obtainMessage();
				msg.what = REFRESH_OVER;
				msg.sendToTarget();
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

				ns = tmpNeeds;
				userinfos = tmpUserinfos;
				solveUserinfos = tmpSolveUserinfos;
				userDetailInfos = tmpUserDetailinfos;
				if (ns.size() != 0)
					lastNum = ns.get(ns.size() - 1).getId();

				Message msg = dataHandler.obtainMessage();
				msg.what = GET_MORE;
				msg.sendToTarget();

			}

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			loadingDialogDismiss();
			super.onPostExecute(result);
		}
	}
}
