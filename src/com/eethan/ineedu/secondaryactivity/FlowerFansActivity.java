package com.eethan.ineedu.secondaryactivity;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.adapter.FlowerFansAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.jackson.FlowerFansJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class FlowerFansActivity extends BaseActivity {

	private ImageButton backBtn;
	private PullToRefreshListView pullToRefreshListView;
	private int userId = 0;
	private ArrayList<UserInfo> fanslist;
	private int lastNum = 0;
	private FlowerFansAdapter adapter;
	private ImageLoader imageLoader;
	private int ownerId;
	private DisplayImageOptions options_head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_flower_fans);
		
		ownerId = getIntent().getIntExtra("ownerid", 3);
		options_head = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.logo) // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.logo) // 设置图片加载或解码过程中发生错误显示的图片
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
		.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

		backBtn = (ImageButton) findViewById(R.id.fans_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.fans_listview);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int pos = (int)arg3;
				new HeadClickEvent(FlowerFansActivity.this, fanslist.get(pos).getUserId()).click();
			}
		});
		pullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
				//new GetDataTask().execute();
			}
		});
		fanslist = new ArrayList<UserInfo>();
		imageLoader = imageLoader.getInstance();
		adapter = new FlowerFansAdapter(FlowerFansActivity.this, fanslist,imageLoader,options_head);
		pullToRefreshListView.setAdapter(adapter);

		SharedPreferences sharedPre = FlowerFansActivity.this
				.getSharedPreferences(Constant.INEEDUSPR, Context.MODE_PRIVATE);
		userId = sharedPre.getInt(Constant.ID, -1);
		if (userId > 0) {
			loadingDialogShow();
			new GetDataTask().execute();
		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {

			String response;
			try {
				JsonObject jsonObject = new JsonObject();
				jsonObject.setInt1(ownerId);
				jsonObject.setInt2(lastNum);
				response = ServerCommunication.request(jsonObject,
						URLConstant.GET_FNAS);
			} catch (PostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return e.getMessage();
			}
			FlowerFansJsonObject jsonObject = null;
			try {
				jsonObject = JacksonUtil.json().fromJsonToObject(
						response, FlowerFansJsonObject.class);
				return jsonObject;
			} catch (Exception e) {
				return null;
			}
			
		}

		@Override
		protected void onPostExecute(Object result) {
			if(result==null){
				MyToast.showToast(getContext(),"没有更多数据");
				loadingDialogDismiss();
				return;
			}
			if (!ServerCommunication.checkResult(getContext(), result))// 各种网络异常的处理部分
			{
				MyToast.showToast(getContext(), (String) result);
				loadingDialogDismiss();
				return;
			}
			
			FlowerFansJsonObject res = (FlowerFansJsonObject) result;
			
			int num = res.getFanslist().size();
			lastNum = res.getLastNum();
			
			UserInfo fan = null;
			if(lastNum==0){
				fanslist.clear();
			}
			for (int i = 0; i < num; i++) {
				fan = new UserInfo();
				fan = res.getFanslist().get(i);
				fanslist.add(fan);
			}
			loadingDialogDismiss();
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

}
