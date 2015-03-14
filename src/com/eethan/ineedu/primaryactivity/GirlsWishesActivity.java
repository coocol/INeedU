package com.eethan.ineedu.primaryactivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.WishesAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.RefreshWishJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.LastPick;
import com.eethan.ineedu.model.LastWrite;
import com.eethan.ineedu.model.NeedPraise;
import com.eethan.ineedu.model.WishModel;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.secondaryactivity.BgImageSelectActivity;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.MyTimer;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.db.sqlite.Selector;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class GirlsWishesActivity extends BaseActivity {

	public static boolean REFRESH_TASK = true;
	public static boolean GET_MORE_TASK = false;

	private boolean isFirstIn = true;

	private PullToRefreshListView pullToRefreshListView;
	private ImageButton writeImgBtn;
	private ImageButton backImgBtn;

	private List<WishModel> wishesList;
	private int lastNum = 0;
	private WishesAdapter adapter;

	private ImageLoader imageLoader;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wish_main);
		backImgBtn = (ImageButton) findViewById(R.id.imgbt_back);
		backImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		writeImgBtn = (ImageButton) findViewById(R.id.imgbt_write);
		writeImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String userSex = new SPHelper(GirlsWishesActivity.this).get(
						"sex").toString();
				if (userSex != null && userSex.equals("男")) {
					MyToast.showToast(GirlsWishesActivity.this, "女生才可以写心愿哦");
					return;
				}
				
				try {
					
					LastWrite lastWrite = DbUtil.getDbUtils(GirlsWishesActivity.this)
								.findFirst(Selector.from(LastWrite.class)
										.where(LastWrite.USERID,"=",new SPHelper(GirlsWishesActivity.this).GetUserId())
										.and(LastWrite.TYPE,"=","hope"));
						
						if(lastWrite!=null){
							String lastTime = lastWrite.getTimeStr();
							String[] lasttimeStrings = lastTime.split("-");
							String nowtimeMString = DataTraslator.longToFormatString(new Date().getTime(), "MM");
							String nowtimeDString = DataTraslator.longToFormatString(new Date().getTime(), "dd");
							if(Integer.parseInt(nowtimeMString)-Integer.parseInt(lasttimeStrings[0])==0){
								if(Integer.parseInt(nowtimeDString)-Integer.parseInt(lasttimeStrings[1])<=0){
									if(lastWrite.getNum()>=4){
										MyToast.showToast(GirlsWishesActivity.this, "每天最多发5条哦");
										return;
									}else{
										
									}
									
								}
							}
						
					} 
					
				} catch (Exception e) {
					
				}
				
				Intent intent = new Intent();
				intent.setClass(GirlsWishesActivity.this,
						BgImageSelectActivity.class);
				intent.putExtra("isCustom", false);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}
		});
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pour_listen_listview);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int wishId = wishesList.get((int) arg3).getWish().getId();
				Intent intent = new Intent();
				intent.setClass(GirlsWishesActivity.this,
						WishDetailsActivity.class);
				intent.putExtra("wishId", wishId);
				startActivity(intent);
			}
		});
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								GirlsWishesActivity.this,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						REFRESH_TASK = true;
						GET_MORE_TASK = false;
						new GetDataTask().execute();
					}
				});
		pullToRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
					@Override
					public void onLastItemVisible() {
						REFRESH_TASK = false;
						GET_MORE_TASK = true;
						new GetDataTask().execute();
					}
				});

		imageLoader = ImageLoader.getInstance();
		wishesList = new ArrayList<WishModel>();
		adapter = new WishesAdapter(wishesList, GirlsWishesActivity.this,
				imageLoader);
		pullToRefreshListView.setAdapter(adapter);

		if (isFirstIn)
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

			String response = null, URL = URLConstant.WISH_REFRESH_NEW;
			RefreshWishJsonObject refreshPR = null;
			if (!REFRESH_TASK) {
				URL = URLConstant.WISH_GETMORE_NEW;
				jsonObject.setInt1(lastNum);
			}
			try {
				response = ServerCommunication.requestWithoutEncrypt(jsonObject, URL);// 发送请求，获得数据
				refreshPR = JacksonUtil.json().fromJsonToObject(response,
						RefreshWishJsonObject.class);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			if (REFRESH_TASK)
				MyTimer.RefreshDelay();

			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {

			super.onPostExecute(result);
			loadingDialogDismiss();
			pullToRefreshListView.onRefreshComplete();

			if (result == null) {
				MyToast.showToast(GirlsWishesActivity.this, "加载失败");
				return;
			}
			// Call onRefreshComplete when the list has been refreshed.
			if (!ServerCommunication.checkResult(GirlsWishesActivity.this,
					result)) {
				MyToast.showToast(GirlsWishesActivity.this, (String) result);
				return;
			}

			RefreshWishJsonObject res = (RefreshWishJsonObject) result;
			lastNum = res.getLastNum();

			isFirstIn = false;
			
			int num = res.getWishWithWannaNums().size();
			WishModel model;
			if (REFRESH_TASK) {
				if (num == 0) {
					MyToast.showToast(getApplicationContext(), "没有更多啦:)");
					return;
				} else {
					wishesList.clear();
				}
			} else {
				if (num == 0) {
					MyToast.showToast(getApplicationContext(), "已经到底啦:)");
					return;
				}
			}

			for (int i = 0; i < num; i++) {
				model = new WishModel();
				model.setOwnerInfo(res.getOwnerUserInfos().get(i));
				model.setWish(res.getWishWithWannaNums().get(i));
				model.setAcademy(res.getUserDetailInfos().get(i).getAcademy());
				wishesList.add(model);
			}

			List<WishModel> tmp_wishsList_0 = new ArrayList<WishModel>();
			List<WishModel> tmp_wishsList_1 = new ArrayList<WishModel>();
			for (WishModel wishModel : wishesList) {
				if(wishModel.getWish().getSolveId()<=0)
					tmp_wishsList_0.add(wishModel);
				else
					tmp_wishsList_1.add(wishModel);
			}
			
			tmp_wishsList_0.addAll(tmp_wishsList_1);
			wishesList.clear();
			for (WishModel wishModel : tmp_wishsList_0) {
				wishesList.add(wishModel);
			}
			
			adapter.notifyDataSetChanged();

		}
	}

}
