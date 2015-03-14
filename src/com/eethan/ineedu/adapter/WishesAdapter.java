package com.eethan.ineedu.adapter;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.GiveHeartEvent;
import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.MyIntent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.model.LastPick;
import com.eethan.ineedu.model.WishModel;
import com.eethan.ineedu.mycontrol.AdapterOnClickListener;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.CommentUtil;
import com.eethan.ineedu.util.DataTraslator;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


public class WishesAdapter extends BaseAdapter {
	
	private ArrayList<WishModel> wishesList;
	private Context context;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private DisplayImageOptions options_head;
	private WishItem item;
	private MyTakeDialog dialog2 = null;
	
	private int position;
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public WishesAdapter(List<WishModel> list,Context context,ImageLoader imageLoader){
		this.wishesList = (ArrayList<WishModel>) list;
		this.context = context;
		this.imageLoader = imageLoader;
		//将值赋给item类中对应的控件
		 options= new DisplayImageOptions.Builder()
		//.showStubImage(R.drawable.ic_stub_horizon)
		//.showImageForEmptyUri(R.drawable.ic_error_horizon)
		//.showImageOnFail(R.drawable.ic_error_horizon)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		 
		 options_head = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.logo) // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.logo) // 设置图片加载或解码过程中发生错误显示的图片
			.showImageOnLoading(R.drawable.logo)
			.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
			.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
			.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
		 
		 dialog2 = new MyTakeDialog(context,R.style.MyDialog,"不","是"){
			@Override
			public void onYesButtonClick() {
				// TODO Auto-generated method stub
				super.onYesButtonClick();
				dialog2.dismiss();
				int pp = getPosition();
				new PickTheWish(getPosition()).execute();
			} 
		 };
			dialog2.setText("你想帮她完成心愿吗？");
		
	}

	@Override
	public int getCount() {
		return wishesList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return wishesList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final int pos = position;
		if(view==null){
			item = new WishItem();
			view = LayoutInflater.from(context).inflate(R.layout.wish_main_item, parent,false);
			item.headImgVu = (ImageView)view.findViewById(R.id.iv_head);
			item.nameTxtVu = (TextView)view.findViewById(R.id.tv_nickname);
			item.distanceTxtVu = (TextView)view.findViewById(R.id.tv_distance);
			item.timeTxtVu = (TextView)view.findViewById(R.id.tv_time);
			item.contentTxtVu = (TextView)view.findViewById(R.id.tv_content);
			item.flowerNumTxtVu = (TextView)view.findViewById(R.id.transmitNum);
			item.commNumTxtVu = (TextView)view.findViewById(R.id.commentNum);
			item.commLayout = (RelativeLayout)view.findViewById(R.id.comment);
			item.flowerLayout = (RelativeLayout)view.findViewById(R.id.flower);
			item.pickLayout = (RelativeLayout)view.findViewById(R.id.pickoff);
			item.backImgVu = (ImageView)view.findViewById(R.id.iv_background);
			item.wishNumTxtVu = (TextView) view.findViewById(R.id.tv_num);
			item.pickerNumTxtVu = (TextView)view.findViewById(R.id.wannapickNum);
			view.setTag(item);
		}else {
			item = (WishItem) view.getTag();
		}
		
		imageLoader.displayImage(wishesList.get(pos).getWish().getImageUrl(), item.backImgVu,options);
		if(wishesList.get(pos).getWish().getAuth()==0){
			item.nameTxtVu.setText("匿名"+"  "+wishesList.get(pos).getAcademy());
			imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+"logo.png", item.headImgVu,options_head);
			item.headImgVu.setOnClickListener(new AdapterOnClickListener(position){
				@Override
				public void onClick(View arg0) {
					SharedPreferences sharedPre = context
							.getSharedPreferences(Constant.INEEDUSPR,
									Context.MODE_PRIVATE);
					int userId = sharedPre.getInt(Constant.ID, -1);
					MyIntent.toChatActivity(context, userId, wishesList.get(pos).getOwnerInfo().getUserId(),
							"匿名聊天", true);
				}
			});
		}else if(wishesList.get(pos).getWish().getAuth()==1) {
			imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+wishesList.get(pos).getOwnerInfo().getUserId()+".png", item.headImgVu,options_head);
			item.nameTxtVu.setText(wishesList.get(pos).getOwnerInfo().getNickName()+"  "+wishesList.get(pos).getAcademy());
			item.headImgVu.setOnClickListener(new AdapterOnClickListener(position) {
				@Override
				public void onClick(View arg0) {
					new HeadClickEvent(context, wishesList.get(getPosition()).getOwnerInfo().getUserId()).click();
				}
			});
		}
		
		try {
			int distance = DataTraslator.GetDistanceToMe(context, wishesList.get(pos).getWish().getLat(),wishesList.get(pos).getWish().getLng());
			item.distanceTxtVu.setText(DataTraslator.DistanceToString(distance));
		} catch (Exception e) {
			item.distanceTxtVu.setText("似乎有什么问题 :(");
		}
		
		try {
			String timeString = DataTraslator.LongToTimePastGeneral(Long.parseLong(wishesList.get(pos).getWish().getTime()));
			item.timeTxtVu.setText(timeString);
		} catch (Exception e) {
			item.timeTxtVu.setText("貌似出错了 :(");
		}
		
		item.pickerNumTxtVu.setText(wishesList.get(pos).getWish().getPickerNum()+"人");
		item.wishNumTxtVu.setText("#"+wishesList.get(pos).getWish().getId());
		
		item.contentTxtVu.setText(wishesList.get(pos).getWish().getContent());
		item.commNumTxtVu.setText(String.valueOf(wishesList.get(pos).getWish().getCommentNum()));
		item.flowerNumTxtVu.setText(String.valueOf(wishesList.get(pos).getOwnerInfo().getPopularityNum()));
		
		item.commLayout.setOnClickListener(new AdapterOnClickListener(position){	
			@Override
			public void onClick(View arg0) {
				new CommentUtil(context, getPosition(), -1, wishesList, WishesAdapter.this, Constant.COMMENT_WISH);
			}
		});
		item.flowerLayout.setOnClickListener(new AdapterOnClickListener(position) {		
			@Override
			public void onClick(View arg0) {
//				int iii =  wishesList.get(pos).getWish().getId();
//				new WishFlowerEvent(context, wishesList.get(pos).getWish().getId(), wishesList.get(pos).getOwnerInfo().getUserId(),wishesList,WishesAdapter.this,pos).start();
				new GiveHeartEvent(context, wishesList.get(getPosition()).getOwnerInfo().getUserId(),wishesList.get(getPosition()).getWish().getId(), wishesList, WishesAdapter.this, pos).exeute();
			}
		});
		final android.content.DialogInterface.OnClickListener positiveClickListener = new android.content.DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				new PickTheWish(pos).execute();
			}
		};
		if(wishesList.get(pos).getWish().getSolveId()>0){
			item.pickLayout.setVisibility(View.GONE);
		}else {
			item.pickLayout.setVisibility(View.VISIBLE);
			item.pickLayout.setOnClickListener(new AdapterOnClickListener(position) {
				@SuppressLint("NewApi")
				@Override
				public void onClick(View arg0) {
					setPosition(pos);
					try {
						LastPick lastPick = DbUtil.getDbUtils(context).findFirst(Selector.from(LastPick.class)
								.where(LastPick.USERID,"=",new SPHelper(context).GetUserId()));
						if(lastPick!=null){
							String lastTime = lastPick.getTimeStr();
							String[] lasttimeStrings = lastTime.split("-");
							String nowtimeMString = DataTraslator.longToFormatString(new Date().getTime(), "MM");
							String nowtimeDString = DataTraslator.longToFormatString(new Date().getTime(), "dd");
							if(Integer.parseInt(nowtimeMString)-Integer.parseInt(lasttimeStrings[0])==0){
								if(Integer.parseInt(nowtimeDString)-Integer.parseInt(lasttimeStrings[1])<=0){
									MyToast.showToast(context, "每天只能摘下一个心愿哦");
									return;
								}
							}
						}
					} catch (Exception e) {
						
					}
					int myUserId = new SPHelper(context).GetUserId();
					if(myUserId == wishesList.get(getPosition()).getOwnerInfo().getUserId()){
						MyToast.showToast(context, "这是自己的心愿哦");
						return;
					}
					
					String userSex = new SPHelper(context).get("sex").toString();
					if(userSex!=null && userSex.equals("女")){
						MyToast.showToast(context, "只有男生才能实现女生的心愿哦");
						return;
					}
					
					dialog2.show();

				}
			});
		}
		
		return view;
	}
	
	class WishItem{
		public ImageView headImgVu;
		public TextView nameTxtVu;
		public TextView distanceTxtVu;
		public TextView timeTxtVu;
		public TextView contentTxtVu;
		public TextView flowerNumTxtVu;
		public TextView commNumTxtVu;
		public RelativeLayout pickLayout;
		public RelativeLayout flowerLayout;
		public RelativeLayout commLayout;
		public ImageView backImgVu;
		public TextView wishNumTxtVu;
		public TextView pickerNumTxtVu;
		
	}
	
	private class PickTheWish extends AsyncTask<Void, Void, Object> {
		// 后台线程操作
		
		private int position;
		public PickTheWish(int Pos){
			this.position = Pos;
		}
		
		@Override
		protected Object doInBackground(Void... params) {

			String response = null, URL = URLConstant.WISH_JOIN;
			JsonObject jObject = new JsonObject();
			jObject.setInt1(wishesList.get(position).getWish().getId());
			int boy = new SPHelper(context).GetUserId();
			jObject.setInt2(wishesList.get(position).getOwnerInfo().getUserId());
			jObject.setInt3(boy);
			
			try {
				response = ServerCommunication.request(jObject, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			int refreshPR ;
			try {
				 refreshPR = JacksonUtil.json()
							.fromJsonToObject(response,Integer.class);
			} catch (Exception e) {
				refreshPR = -1;
			}
			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {
			
			super.onPostExecute(result);

			// Call onRefreshComplete when the list has been refreshed.
			if (!ServerCommunication.checkResult(context,
					result)) {
				MyToast.showToast(context, "好像有问题:(~");
				return;
			}

			int res = (Integer)result;
			if (res==-1) {
				MyToast.showToast(context, "似乎有问题:(");
			}else if(res==0){
				MyToast.showToast(context, "你已经加入了");
			}else {
				MyToast.showToast(context, "Ok，已通知对方");
				int n = wishesList.get(position).getWish().getPickerNum()+1;
				wishesList.get(position).getWish().setPickerNum(n);
				notifyDataSetChanged();
				new Thread(){
					@Override
					public void run() {
						LastPick lastPick = new LastPick();
						String hh = DataTraslator.longToFormatString(new Date().getTime(), "MM-dd");
						lastPick.setTimeStr(hh);
						lastPick.setUserId(new SPHelper(context).GetUserId());
						try {
							DbUtil.getDbUtils(context).save(lastPick);
						} catch (DbException e) {
							SharedPreferences sharedPre = context.getSharedPreferences(
									Constant.INEEDUSPR, 0);
							Editor editor = sharedPre.edit();
							editor.putString(SPHelper.PICK_LAST_TIME,hh);
							editor.commit();
						}
					};
				}.start();
			
			}
		}
	}

}
