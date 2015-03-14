package com.eethan.ineedu.CommonUse;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.eethan.ineedu.adapter.NeedFragmentAdapter;
import com.eethan.ineedu.adapter.WishesAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.model.NeedFragmentModel;
import com.eethan.ineedu.model.WishFlower;
import com.eethan.ineedu.model.WishModel;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.ToastHandler;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class WishFlowerEvent {

	private Context context;
	private int ownerId;
	private int myUserId;
	private int wishId;
	private Handler handler;
	private ArrayList<WishModel> list;
	private WishesAdapter adapter;
	private int pos;

	public WishFlowerEvent(Context context, int wishId,int ownerId) {
		this.context = context;
		this.wishId = wishId;
		this.ownerId = ownerId;
		handler = new ToastHandler(context);
	}
	
	private Context getContext(){
		return context;
	}
	

	private void setChangedNum(){
		if(adapter!=null && list!=null){
			int num = list.get(pos).getWish().getFlowerNum();
			list.get(pos).getWish().setFlowerNum(num+1);
			adapter.notifyDataSetChanged();
		}
	}
	
	public WishFlowerEvent(Context context, int wishId,int ownerId,ArrayList<WishModel> list,WishesAdapter adapter,int p) {
		this.context = context;
		this.wishId = wishId;
		this.ownerId = ownerId;
		this.list = list;
		this.adapter = adapter;
		this.pos = p;
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case Constant.WISH_FLOWER_SELF:
					MyToast.showToast(getContext(), "自己的心愿哦");
					break;
				case Constant.WISH_FLOWER_ALREADY:
					MyToast.showToast(getContext(), "已经送过啦");
					break;
				case Constant.CONNECT_FAILED:
					MyToast.showToast(getContext(), "抱歉，出错了:(");
					break;
				case Constant.WISH_FLOWER_SUCCESS:
					MyToast.showToast(getContext(), "花+1啊~");
					setChangedNum();
					break;
				default:
					break;
				}
			}
		};
	}

	public void start() {
		exeute();
	}

	public void exeute() {
		myUserId = new SPHelper(context).GetUserId();

		new Thread() {
			public void run() {
				Message message = handler.obtainMessage();
				if (myUserId == ownerId) {
					message.what = Constant.WISH_FLOWER_SELF;
					message.sendToTarget();
					return;
				}
				WishFlower wishFlower = null;
				try {
					int ddd = wishId;
					wishFlower = DbUtil.getDbUtils(context).findFirst(
							Selector.from(WishFlower.class)
									.where(WishFlower.WISHID, "=", wishId)
									.where(WishFlower.USERID, "=", myUserId));
				} catch (DbException e1) {
					e1.printStackTrace();
				}
				if (wishFlower != null) {
					message.what = Constant.WISH_FLOWER_ALREADY;
					message.sendToTarget();
					return;
				}
				String response;
				com.eethan.ineedu.databasebeans.WishFlower wishFlower2 = new com.eethan.ineedu.databasebeans.WishFlower();
				wishFlower2.setPraisedUserId(ownerId);
				wishFlower2.setUserId(myUserId);
				wishFlower2.setWishId(wishId);
				try {
					response = ServerCommunication.request(
							wishFlower2, URLConstant.WISH_FLOWER_WISH);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					message.what = Constant.CONNECT_FAILED;
					message.sendToTarget();
					return;
				}
				boolean res = false;
				try {
					res = Boolean.parseBoolean(response);
				} catch (Exception e) {
					message.what = Constant.CONNECT_FAILED;
					message.sendToTarget();
					return;
				}
				if(res){
					WishFlower wishFlower3 = new WishFlower();
					wishFlower3.setWishId(wishId);
					wishFlower3.setUserId(myUserId);
					try {
						DbUtil.getDbUtils(context).save(wishFlower3);
					} catch (DbException e) {
						e.printStackTrace();
					}
					message.what = Constant.WISH_FLOWER_SUCCESS;
					message.sendToTarget();

					
				}
				
			};
		}.start();

	}

}
