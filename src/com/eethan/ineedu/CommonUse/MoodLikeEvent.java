package com.eethan.ineedu.CommonUse;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.eethan.ineedu.adapter.MoodAdapter;
import com.eethan.ineedu.adapter.PhotoNewsAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.databasebeans.MoodLikeBean;
import com.eethan.ineedu.databasebeans.TakePhotosPraise;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.model.MoodLike;
import com.eethan.ineedu.model.MoodModel;
import com.eethan.ineedu.model.PhotoLike;
import com.eethan.ineedu.model.PhotoNewsModel;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.ToastHandler;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class MoodLikeEvent {

	private Context context;
	private int beSendManId;
	private int myUserId;
	private int moodId;
	private Handler handler;
	
	private ArrayList<MoodModel> list;
	private Handler numHandler;
	private MoodAdapter adapter;
	private int pos;


	public MoodLikeEvent(Context context, int beSendManId, int moodId) {
		this.context = context;
		this.beSendManId = beSendManId;
		this.moodId = moodId;

		handler = new ToastHandler(context);
	}
	
	public void setLikeNum(){
		if(adapter!=null && list!=null){
			int num = list.get(pos).getMood().getPraiseNum();
			list.get(pos).getMood().setPraiseNum(num+1);
			adapter.notifyDataSetChanged();
		}
	}
	
	public MoodLikeEvent(Context context, int beSendManId, int moodId,MoodAdapter adapter,List<MoodModel> list,int pos) {
		this.context = context;
		this.beSendManId = beSendManId;
		this.moodId = moodId;
		this.adapter = adapter;
		this.pos = pos;
		this.numHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				setLikeNum();
			}
		};
		this.list = (ArrayList<MoodModel>) list;
		
		handler = new ToastHandler(context);
	}

	public void start() {
		exeute();
	}

	public void exeute() {
		myUserId = new SPHelper(context).GetUserId();

		new Thread() {
			public void run() {
				Message message = handler.obtainMessage();
				if (myUserId == beSendManId) {
					message.what = Constant.PHOTO_LIKE_MYSELF;
					message.sendToTarget();
					return;
				}
				MoodLike moodLike = null;
				try {
					moodLike = DbUtil.getDbUtils(context).findFirst(
							Selector.from(MoodLike.class)
									.where(MoodLike.MOODID, "=", moodId)
									.and(MoodLike.USERID, "=", myUserId));
				} catch (DbException e1) {
					e1.printStackTrace();
				}
				if (moodLike != null) {
					message.what = Constant.PHOTO_LIKE_ALREADY;
					message.sendToTarget();
					return;
				}
				
	
				JsonObject jsonObject = new JsonObject();
				jsonObject.setInt1(moodId);
				jsonObject.setInt2(myUserId);
				jsonObject.setInt3(beSendManId);
				try {
					ServerCommunication.request(
							jsonObject, URLConstant.LIKE_MOOD_URL);
				} catch (PostException e) {
					e.printStackTrace();
					message.what = Constant.CONNECT_FAILED;
					message.sendToTarget();
					return;
				}
				
				MoodLike photoLike2 = new MoodLike();
				photoLike2.setMoodId(moodId);
				photoLike2.setUserId(myUserId);
				//photoLike2.setUserId(myUserId);
				try {
					DbUtil.getDbUtils(context).save(photoLike2);
				} catch (DbException e) {
					e.printStackTrace();
				}
				if(numHandler!=null){
					Message msg = numHandler.obtainMessage();
					msg.sendToTarget();
				}
				

				message.what = Constant.PHOTO_LIKE_SUCCESS;
				message.sendToTarget();
				
				
			};
		}.start();

	}

}
