package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.PlayJoinEvent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Plays;
import com.eethan.ineedu.databasebeans.PlaysParticipant;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.model.PlaysModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.CommentUtil;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class PlaysAdapter extends BaseAdapter {
	
	private Context context;
	
	private ArrayList<PlaysModel> playsList;
	
	private ImageLoader imageLoader;
	private PlaysItem item;
	private Long timeDiffLong;
	private DisplayImageOptions options_head;

	
	public PlaysAdapter(Context context,ArrayList<PlaysModel> list,ImageLoader imageLoader){
		this.context = context;
		this.playsList = list;
		this.imageLoader = imageLoader;
		options_head = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.logo) // 设置图片Uri为空或是错误的时候显示的图片
		.showImageOnFail(R.drawable.logo) // 设置图片加载或解码过程中发生错误显示的图片
		.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
		.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
		
	}

	@Override
	public int getCount() {
		return playsList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return playsList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final int pos = position;
		if(view == null){
			item = new PlaysItem();
			view = LayoutInflater.from(context).inflate(R.layout.plays_main_item, parent,false);
			item.headImgBtn = (ImageButton)view.findViewById(R.id.imgbt_head);
			item.sexImgVu = (ImageView)view.findViewById(R.id.home_page_sex);
			item.ownerNameTxtVu = (TextView)view.findViewById(R.id.tv_nickname);
			item.sendTimeTxtVu = (TextView)view.findViewById(R.id.tv_init_time);
			item.themeTxtVu = (TextView)view.findViewById(R.id.tv_theme);
			item.contentTxtVu = (TextView)view.findViewById(R.id.tv_content);
			item.requireTxtVu = (TextView)view.findViewById(R.id.tv_requirement);
			item.dateTxtVu = (TextView)view.findViewById(R.id.tv_date);
			item.placetxtVu = (TextView)view.findViewById(R.id.tv_place);
			item.distanceTxtVu = (TextView)view.findViewById(R.id.tv_distance);
			item.limitTimeTxtVu = (TextView)view.findViewById(R.id.tv_time_limit);
			item.joinGuysNumTxtVu = (TextView)view.findViewById(R.id.tv_join_num);
			item.commNumTxtVu = (TextView)view.findViewById(R.id.tv_playscomm);
			item.iWannaInTxtVu = (TextView)view.findViewById(R.id.tv_iwannain);
			item.iWannInImgBtn = (ImageButton)view.findViewById(R.id.imgbt_join);
			item.commImgBtn = (ImageButton)view.findViewById(R.id.imgbt_comment);
			
			view.setTag(item);
		}else{
			item = (PlaysItem) view.getTag();
		}
		
		final UserInfo ownInfo = playsList.get(position).getOwnerUserInfo();
		final Plays aPlay= playsList.get(position).getPlays();
		
		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+ownInfo.getUserId()+".png",item.headImgBtn,options_head);
		item.ownerNameTxtVu.setText(ownInfo.getNickName());
		if(ownInfo.getSex().equals("女")){
			item.sexImgVu.setImageResource(R.drawable.sex_girl_press);
		}else {
			item.sexImgVu.setImageResource(R.drawable.sex_boy_press);
		}
		item.placetxtVu.setText(aPlay.getPlace());
		item.themeTxtVu.setText(aPlay.getTheme());
		item.contentTxtVu.setText(aPlay.getContent());
		item.requireTxtVu.setText(aPlay.getRequirement());
		try {
			item.sendTimeTxtVu.setText(DataTraslator.LongToTimePast(Long.parseLong(aPlay.getInitTime())));
		} catch (Exception e) {
			item.sendTimeTxtVu.setText("error");
		}
		try {
			Long curLong = DateUtil.getMSTime();
			Long deadLong = Long.parseLong(aPlay.getDeadLine());
			timeDiffLong = deadLong-curLong;
			if(timeDiffLong>0){
				item.limitTimeTxtVu.setText(DataTraslator.LongToTimeRemain(timeDiffLong));
			}else {
				item.limitTimeTxtVu.setText("报名已截止");
			}
			
		} catch (Exception e) {
			item.limitTimeTxtVu.setText("error");
		}
		item.dateTxtVu.setText(aPlay.getTime());
		item.commNumTxtVu.setText(String.valueOf(aPlay.getCommentNum()));
		item.joinGuysNumTxtVu.setText(String.valueOf(aPlay.getJoinNum()));
		int distanceToMe = DataTraslator.GetDistanceToMe(context, aPlay.getLat(),aPlay.getLng());
		item.distanceTxtVu.setText(DataTraslator.DistanceToString(distanceToMe));
		
		final android.content.DialogInterface.OnClickListener positiveClickListener = new android.content.DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				PlaysParticipant playsParticipant = new PlaysParticipant();
				playsParticipant.setOwner(ownInfo.getUserId());
				playsParticipant.setUserId(new SPHelper(context).GetUserId());
				playsParticipant.setPlaysId(aPlay.getId());
				new PlayJoinEvent(playsParticipant, context).start();
			}
		};
		
		item.headImgBtn.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				new HeadClickEvent(context, playsList.get(pos).getOwnerUserInfo().getUserId()).click();
			}
		});
		item.commImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new CommentUtil(context, pos, -1, playsList, PlaysAdapter.this, Constant.COMMENT_PLAYS);
			}
		});
		
		item.iWannaInTxtVu.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				Long curLong = DateUtil.getMSTime();
				Long deadLong = Long.parseLong(aPlay.getDeadLine());
				timeDiffLong = deadLong-curLong;
				if(timeDiffLong>0){
					MyTakeDialog dialog2 = new MyTakeDialog(context,R.style.MyDialog){
						@Override
						public void onYesButtonClick() {
							PlaysParticipant playsParticipant = new PlaysParticipant();
							playsParticipant.setOwner(playsList.get(pos).getOwnerUserInfo().getUserId());
							playsParticipant.setUserId(new SPHelper(
									context).GetUserId());
							playsParticipant.setPlaysId(playsList.get(pos).getPlays().getId());
							new PlayJoinEvent(playsParticipant, context).start();
						dismiss();
						};
					};
					dialog2.setText("确定要参加该活动?");
	                	dialog2.show();
				}else {
					MyToast.showToast(context, "报名已截止");
				}
			}
		});
		item.iWannInImgBtn.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				Long curLong = DateUtil.getMSTime();
				Long deadLong = Long.parseLong(aPlay.getDeadLine());
				timeDiffLong = deadLong-curLong;
				if(timeDiffLong>0){
					MyTakeDialog dialog2 = new MyTakeDialog(context,R.style.MyDialog){
						@Override
						public void onYesButtonClick() {
							PlaysParticipant playsParticipant = new PlaysParticipant();
							playsParticipant.setOwner(playsList.get(pos).getOwnerUserInfo().getUserId());
							playsParticipant.setUserId(new SPHelper(
									context).GetUserId());
							playsParticipant.setPlaysId(playsList.get(pos).getPlays().getId());
							new PlayJoinEvent(playsParticipant, context).start();
						dismiss();
						};
					};
					dialog2.setText("确定要参加该活动?");
	                	dialog2.show();
				}else {
					MyToast.showToast(context, "报名已截止");
				}
			}
		});
		
		return view;
	}
	
	class PlaysItem{
		public ImageButton headImgBtn;
		public ImageView sexImgVu;
		public TextView ownerNameTxtVu;
		public TextView sendTimeTxtVu;
		public TextView themeTxtVu;
		public TextView contentTxtVu;
		public TextView requireTxtVu;
		public TextView dateTxtVu;
		public TextView placetxtVu;
		public TextView distanceTxtVu;
		public TextView limitTimeTxtVu;
		public TextView joinGuysNumTxtVu;
		public TextView commNumTxtVu;
		public TextView iWannaInTxtVu;
		public ImageButton iWannInImgBtn;
		public ImageButton commImgBtn;
	}
	
	
	
}
