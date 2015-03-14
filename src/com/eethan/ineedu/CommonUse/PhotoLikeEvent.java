package com.eethan.ineedu.CommonUse;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.eethan.ineedu.adapter.PhotoNewsAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.databasebeans.TakePhotosPraise;
import com.eethan.ineedu.model.PhotoLike;
import com.eethan.ineedu.model.PhotoNewsModel;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.ToastHandler;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class PhotoLikeEvent {

	private Context context;
	private int beSendManId;
	private int myUserId;
	private int photoId;
	private Handler handler;
	
	private ArrayList<PhotoNewsModel> list;
	private Handler numHandler;
	private PhotoNewsAdapter adapter;
	private int pos;


	public PhotoLikeEvent(Context context, int beSendManId, int photoId) {
		this.context = context;
		this.beSendManId = beSendManId;
		this.photoId = photoId;

		handler = new ToastHandler(context);
	}
	
	public void setLikeNum(){
		if(adapter!=null && list!=null){
			int num = list.get(pos).getTakePhotos().getPraiseNum();
			list.get(pos).getTakePhotos().setPraiseNum(num+1);
			adapter.notifyDataSetChanged();
		}
	}
	
	public PhotoLikeEvent(Context context, int beSendManId, int photoId,PhotoNewsAdapter adapter,List<PhotoNewsModel> list,int pos) {
		this.context = context;
		this.beSendManId = beSendManId;
		this.photoId = photoId;
		this.adapter = adapter;
		this.pos = pos;
		this.numHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				setLikeNum();
			}
		};
		this.list = (ArrayList<PhotoNewsModel>) list;
		
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
				PhotoLike photoLike = null;
				try {
					photoLike = DbUtil.getDbUtils(context).findFirst(
							Selector.from(PhotoLike.class)
									.where(PhotoLike.PHOTOID, "=", photoId)
									.and(PhotoLike.USERID, "=", myUserId));
				} catch (DbException e1) {
					e1.printStackTrace();
				}
				if (photoLike != null) {
					message.what = Constant.PHOTO_LIKE_ALREADY;
					message.sendToTarget();
					return;
				}

				TakePhotosPraise takePhotosPraise = new TakePhotosPraise();
				takePhotosPraise.setPraisedUserId(beSendManId);
				takePhotosPraise.setUserId(myUserId);
				takePhotosPraise.setPhotoId(photoId);
				try {
					ServerCommunication.request(
							takePhotosPraise, URLConstant.LIKE_PHOTOS_URL);
				} catch (PostException e) {
					e.printStackTrace();
					message.what = Constant.CONNECT_FAILED;
					message.sendToTarget();
					return;
				}
				
				PhotoLike photoLike2 = new PhotoLike();
				photoLike2.setPhotoId(photoId);
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
