package com.eethan.ineedu.CommonUse;

import java.util.ArrayList;
import java.util.Date;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.eethan.ineedu.adapter.MoodAdapter;
import com.eethan.ineedu.adapter.NeedFragmentAdapter;
import com.eethan.ineedu.adapter.WishesAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.model.MoodModel;
import com.eethan.ineedu.model.NeedFragmentModel;
import com.eethan.ineedu.model.NeedPraise;
import com.eethan.ineedu.model.WishFlower;
import com.eethan.ineedu.model.WishModel;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DataCache;
import com.eethan.ineedu.util.ToastHandler;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class GiveHeartEvent {

	public static final int HEART_WISH = 0;
	public static final int HEART_MEET = 1;

	private Context context;
	private int beSendManId;
	private int myUserId;
	private int needId;
	private Handler handler;
	private boolean isDaily = false;

	private Handler numHandler;

	private ArrayList<NeedFragmentModel> list;
	private ArrayList<MoodModel> moodList;
	private NeedFragmentAdapter adapter;
	private MoodAdapter moodAdapter;
	private ArrayList<WishModel> wishlist;
	private WishesAdapter wishadapter;
	private int pos;

	public GiveHeartEvent(Context context, int beSendManId, int needId) {
		this.context = context;
		this.beSendManId = beSendManId;
		this.needId = needId;
		handler = new ToastHandler(context);
		this.numHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				setNumChange();
			};
		};
	}

	public GiveHeartEvent(Context context, int beSendManId, int needId,
			ArrayList<NeedFragmentModel> list, NeedFragmentAdapter adapter,
			int p) {
		this.context = context;
		this.beSendManId = beSendManId;
		this.needId = needId;
		handler = new ToastHandler(context);
		this.list = list;
		this.adapter = adapter;
		this.pos = p;
		this.numHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				setNumChange();
			};
		};
	}

	public GiveHeartEvent(Context context, int beSendManId,int needId,
			ArrayList<WishModel> list, WishesAdapter adapter, int p) {
		this.context = context;
		this.beSendManId = beSendManId;
		this.needId = needId;
		handler = new ToastHandler(context);
		this.wishlist = list;
		this.wishadapter = adapter;
		this.pos = p;
		this.numHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				setNumChange();
			};
		};
	}

	public GiveHeartEvent(Context context, int beSendManId,int needId,
			ArrayList<MoodModel> list, MoodAdapter adapter, int p) {
		this.context = context;
		this.beSendManId = beSendManId;
		this.needId = needId;
		handler = new ToastHandler(context);
		this.moodList = list;
		this.moodAdapter = adapter;
		this.pos = p;
		this.numHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				setNumChange();
			};
		};
	}
	
	public void setNumChange() {
		if (list != null && adapter != null) {
			int num = list.get(pos).getPopularityNum();
			list.get(pos).setPopularityNum(num + 1);
			DataCache.needs.get(pos).setPopularityNum(num + 1);
			adapter.notifyDataSetChanged();
		}
		if (wishlist != null && wishadapter != null) {
			int num = wishlist.get(pos).getOwnerInfo().getPopularityNum();
			wishlist.get(pos).getOwnerInfo().setPopularityNum(num + 1);
			wishadapter.notifyDataSetChanged();
		}
		if (moodList != null && moodAdapter != null) {
			int num = moodList.get(pos).getOwnerInfo().getPopularityNum();
			moodList.get(pos).getOwnerInfo().setPopularityNum(num + 1);
			moodAdapter.notifyDataSetChanged();
		}
	}

	public void IsDaily() {
		isDaily = true;
	}

	public void start() {
		exeute();
	}

	public void exeute() {
		myUserId = new SPHelper(context).GetUserId();

		IsDaily();

		new Thread() {
			public void run() {
				Message message = handler.obtainMessage();
				if (myUserId == beSendManId) {
					message.what = Constant.GIVEHEART_MYSELF;
					message.sendToTarget();
					return;
				}
				NeedPraise needPraise = null;
				try {
					int b = beSendManId;
					 int m = myUserId;
					 needPraise = DbUtil.getDbUtils(context)
								.findFirst(Selector.from(NeedPraise.class)
										.where(NeedPraise.NEEDID,"=",myUserId)
										.and(NeedPraise.USERID,"=",beSendManId));
				} catch (DbException e1) {
					e1.printStackTrace();
				}
				if (needPraise != null) {
					message.what = Constant.HAS_GIVEHEART;
					message.sendToTarget();
					return;
				}

				JsonObject jsonObject = new JsonObject();
				jsonObject.setInt1(new SPHelper(context).GetUserId());// 我的id
				jsonObject.setInt2(beSendManId);
				if (isDaily)
					jsonObject.setInt3(-1);
				else
					jsonObject.setInt3(needId);
				jsonObject.setLong1(new Date().getTime());
				boolean renbool;
				try {
					String ss = ServerCommunication.requestWithoutEncrypt(jsonObject,
							URLConstant.GIVE_POPULARITY_URL);
					renbool = JacksonUtil.json().fromJsonToObject(ss,
							boolean.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					message.what = Constant.CONNECT_FAILED;
					message.sendToTarget();
					return;
				}

				if (isDaily)
					message.what = Constant.DAILY_GIVEHEART_SUCCESS;
				else
					message.what = Constant.GIVEHEART_SUCCESS;

					message.sendToTarget();

					NeedPraise needPraise2 = new NeedPraise();
					needPraise2.setNeedId(myUserId);
					needPraise2.setUserId(beSendManId);
					try {
						DbUtil.getDbUtils(context).save(needPraise2);
					} catch (DbException e) {
						e.printStackTrace();
					}
				

				if (numHandler != null) {
					Message msgMessage = numHandler.obtainMessage();
					numHandler.sendMessage(msgMessage);
				}

			};
		}.start();

	}
}
