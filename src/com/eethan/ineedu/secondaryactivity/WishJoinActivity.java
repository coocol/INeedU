package com.eethan.ineedu.secondaryactivity;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;

import com.eethan.ineedu.adapter.WishJoinAdapter;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.NearUserJsonObject;
import com.eethan.ineedu.model.WishJoinModel;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class WishJoinActivity extends BaseActivity {

	private ImageButton backBtn;
	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<WishJoinModel> pickerslist;
	private WishJoinAdapter adapter;
	private ImageLoader imageLoader;
	private int wishId;
	private int ownerId;
	private MyTakeDialog dialog2 = null;
	
	private int pos = 0;
	
	private int solvedId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_wish_join);
		
		wishId = getIntent().getIntExtra("wishId", -1);
		ownerId = getIntent().getIntExtra("ownerId", -1);
		solvedId = getIntent().getIntExtra("solvedId", -1);
		
		backBtn = (ImageButton) findViewById(R.id.ib_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_listview);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(solvedId<0){
					return;
				}
				pos = (int) arg3;
				dialog2.show();
			}
		});
		pickerslist = new ArrayList<WishJoinModel>();
		imageLoader = imageLoader.getInstance();
		adapter = new WishJoinAdapter(WishJoinActivity.this, pickerslist,
				imageLoader);
		pullToRefreshListView.setAdapter(adapter);
		
		 dialog2 = new MyTakeDialog(WishJoinActivity.this,R.style.MyDialog,"取消","确定"){
				@Override
				public void onYesButtonClick() {
					// TODO Auto-generated method stub
					super.onYesButtonClick();
					dialog2.dismiss();
					new AcceptPickWish(pos).execute();
				} 
			 };
		dialog2.setText("确认选择他来完成自己的心愿？");
		loadingDialogShow();
		new GetDataTask().execute();
	}

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {

			String response;
			try {
				JsonObject jsonObject = new JsonObject();
				jsonObject.setInt1(wishId);
				response = ServerCommunication.requestWithoutEncrypt(jsonObject,
						URLConstant.WISH_GET_PICKERS);
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			NearUserJsonObject jsonObject = null;
			try {
				jsonObject = JacksonUtil.json().fromJsonToObject(response,
						NearUserJsonObject.class);
				return jsonObject;
			} catch (Exception e) {
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object result) {
			if (result == null) {
				MyToast.showToast(getContext(), "没有更多数据");
				loadingDialogDismiss();
				return;
			}
			if (!ServerCommunication.checkResult(getContext(), result))// 各种网络异常的处理部分
			{
				MyToast.showToast(getContext(), (String) result);
				loadingDialogDismiss();
				return;
			}

			NearUserJsonObject res = (NearUserJsonObject) result;
			int num = res.getUserInfos().size();
			for(int i=0;i<num;i++){
				WishJoinModel model = new WishJoinModel();
				model.setAcademy(res.getUserDetailInfos().get(i).getAcademy());
				try {
					model.setDistance(DataTraslator.DistanceToString(DataTraslator.GetDistanceToMe(WishJoinActivity.this, res.getUserLocations().get(i).getLat(), res.getUserLocations().get(i).getLng())));
				} catch (Exception e) {
					model.setDistance("");
				}
				try {
					model.setTime(DataTraslator.LongToTimePastGeneral(res.getUserLocations().get(i).getTime()));
				} catch (Exception e) {
					model.setTime("");
				}
				model.setNickName(res.getUserInfos().get(i).getNickName());
				model.setSex(res.getUserInfos().get(i).getSex());
				model.setUserId(res.getUserInfos().get(i).getUserId());
				pickerslist.add(model);
			}

			loadingDialogDismiss();
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}
	
	private class AcceptPickWish extends AsyncTask<Void, Void, Object> {

		public AcceptPickWish(int p){
			this.pos = p;
		}
		
		private int pos;
		
		@Override
		protected Object doInBackground(Void... params) {

			String response = null, URL = URLConstant.WISH_PICK_WISH;
			JsonObject jObject = new JsonObject();
			jObject.setInt1(ownerId);
			jObject.setInt2(pickerslist.get(pos).getUserId());
			jObject.setInt3(wishId);
			
			try {
				response = ServerCommunication.request(jObject, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			boolean refreshPR;
			try {
				 refreshPR = JacksonUtil.json()
							.fromJsonToObject(response,boolean.class);
			} catch (Exception e) {
				refreshPR = false;
			}
			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {
			
			super.onPostExecute(result);

			// Call onRefreshComplete when the list has been refreshed.
			if (result==null || !ServerCommunication.checkResult(getApplicationContext(),
					result)) {
				MyToast.showToast(getApplicationContext(), "失败了:(~");
				return;
			}

			boolean res = (Boolean)result;
			if (!res) {
				MyToast.showToast(getApplicationContext(), "失败了:(");
			}else {
				MyToast.showToast(getApplicationContext(), "已通知对方");
			}
		}
	}


}
