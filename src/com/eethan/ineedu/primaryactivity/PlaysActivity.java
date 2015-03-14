package com.eethan.ineedu.primaryactivity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.eethan.ineedu.adapter.PlaysAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.RefreshPlaysJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.PlaysModel;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.MyTimer;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PlaysActivity extends BaseActivity {
	

	public static boolean REFRESH_TASK = true;
	public static boolean GET_MORE_TASK = false;
	
	private int lastNum = 0;
	private boolean isFirstIn = true;
	
	private ImageButton writePlayImgBtn;
	private ImageButton backImgBtn;
	
	private PullToRefreshListView pullToRefreshListView;
	private PlaysAdapter playsAdapter;
	private ArrayList<PlaysModel> playsList;
	private ImageLoader imageLoader;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plays_main);
		
		writePlayImgBtn = (ImageButton)findViewById(R.id.photonews_imgbt_take_photo);
		writePlayImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), PlaysReleaseActivity.class);
				startActivity(intent);
			}
		});
		backImgBtn = (ImageButton)findViewById(R.id.back);
		backImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		

		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.lv_plays);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				int playId = playsList.get((int)id).getPlays().getId();
				Intent intent = new Intent();
				intent.setClass(PlaysActivity.this, PlaysDetailsActivity.class);
				intent.putExtra("playId", playId);
				startActivity(intent);				
			}
			
		});
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(
						PlaysActivity.this,
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel(label);
				REFRESH_TASK = true;
				GET_MORE_TASK = false;
				// Do work to refresh the list here.
				new GetDataTask().execute((Void) null);
			}
		});
		pullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				REFRESH_TASK = false;
				GET_MORE_TASK = true;
				// Do work to refresh the list here.
				new GetDataTask().execute((Void) null);
			}
			
		});
		
		playsList = new ArrayList<PlaysModel>();
		imageLoader = ImageLoader.getInstance();
		playsAdapter = new PlaysAdapter(this, playsList,imageLoader);
		pullToRefreshListView.setAdapter(playsAdapter);
	
		if(isFirstIn)
			loadingDialogShow();
		new GetDataTask().execute();
	}
	
	
	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {
			
			if (REFRESH_TASK)
				MyTimer.Start();
			
			JsonObject jsonObject = new JsonObject();
			jsonObject.setDouble1(LocateManager.getInstance().getLontitude());
			jsonObject.setDouble2(LocateManager.getInstance().getLatitude());
			jsonObject.setDouble3(Constant.DISTANCE_NATIONWIDE);
			
			String response = null, URL = URLConstant.PLAYS_REFREH_PLAYS;
			if(!REFRESH_TASK){
				URL = URLConstant.PLAYS_GETMORE_PLAYS;
				jsonObject.setInt1(lastNum);
			}
			
			try {
				response = ServerCommunication.request(jsonObject, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			RefreshPlaysJsonObject refreshPR = null;
			try {
				refreshPR = JacksonUtil.json()
						.fromJsonToObject(response, RefreshPlaysJsonObject.class);
			} catch (Exception e) {
				return null;
			}
			
			if (REFRESH_TASK)
				MyTimer.RefreshDelay();
			
			return refreshPR;
		}
		

		@Override
		protected void onPostExecute(Object result) {
			
			super.onPostExecute(result);

			if(isFirstIn)
				loadingDialogDismiss();
			pullToRefreshListView.onRefreshComplete();
			isFirstIn = false;
			if(result==null){
				MyToast.showToast(PlaysActivity.this, "加载失败");
				return;
			}
			if(result.equals("服务器请求超时!")){
				MyToast.showToast(PlaysActivity.this, (String) result);
				return;
			}
			

			RefreshPlaysJsonObject res = (RefreshPlaysJsonObject) result;
			lastNum = res.getLastNum();
			
			// Call onRefreshComplete when the list has been refreshed.
			if (!ServerCommunication
					.checkResult(PlaysActivity.this, result)) {
				MyToast.showToast(PlaysActivity.this, (String) result);
				return;
			}
			
			int num = res.getPlays().size();
			PlaysModel model;
			if(REFRESH_TASK){
				if(num==0){
					MyToast.showToast(getApplicationContext(), "没有更新的活动了~");
					return;
				}else {
					playsList.clear();
				}
			}else {
				if(num == 0) {
					MyToast.showToast(getApplicationContext(), "没有更多了~");
					return;
				}
			}

			for (int i = 0; i < num; i++) {
				model = new PlaysModel();
				model.setPlays(res.getPlays().get(i));
				model.setOwnerUserInfo(res.getOwnerUserInfos().get(i));
				model.setLastNum(res.getLastNum());
				playsList.add(model);
			}
			
			playsAdapter.notifyDataSetChanged();	

		}
	}

}
