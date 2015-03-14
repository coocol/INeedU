package com.eethan.ineedu.adapter;


import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.PlayCommentModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.ExpressionUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;



public class PlaysDetailsAdapter extends BaseAdapter {
	

	
	private ArrayList<PlayCommentModel> playsComments;
	private Context context;
	private ImageLoader imageLoader;
	private PlaysCommentItem item;
	private DisplayImageOptions options_head;
	
	public PlaysDetailsAdapter(Context context,ArrayList<PlayCommentModel> list,ImageLoader imageLoader){
		this.context = context;
		this.playsComments = list;
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
		return playsComments.size();
	}

	@Override
	public Object getItem(int arg0) {
		return playsComments.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final int pos = position;
		if(view==null){
			item = new PlaysCommentItem();
			view = LayoutInflater.from(context).inflate(R.layout.plays_detail_item, parent,false);
			item.headImgBtn = (ImageButton)view.findViewById(R.id.comment_head);
			item.nickNameTxtVu = (TextView)view.findViewById(R.id.comment_nickname);
			item.timeTxtVu = (TextView)view.findViewById(R.id.comment_time);
			item.contentTxtVu = (TextView)view.findViewById(R.id.comment_content);
			view.setTag(item);
		}else {
			item = (PlaysCommentItem) view.getTag();
		}

		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+playsComments.get(pos).getPlaysComment().getUserId()+".png",item.headImgBtn,options_head);
		item.headImgBtn.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				int userId = playsComments.get(pos).getPlaysComment().getUserId();
				new HeadClickEvent(context, userId).click();
			}
		});
		item.nickNameTxtVu.setText(playsComments.get(pos).getCommUserInfo().getNickName());
		
		try {
			String distanceString = DataTraslator.DistanceToString(DataTraslator.GetDistanceToMe(context, playsComments.get(pos).getCommUserLocation().getLat(), playsComments.get(pos).getCommUserLocation().getLng()));
			String logtimeString = DataTraslator.LongToTimePastGeneral(playsComments.get(pos).getCommUserLocation().getTime());
			
			String timesString = DataTraslator.LongToTimePastGeneral(Long.parseLong(playsComments.get(pos).getPlaysComment().getTime()));
			item.timeTxtVu.setText(timesString+" "+playsComments.get(pos).getCommUserInfo().getSex()+" "+distanceString+" "+logtimeString);
		} catch (Exception e) {
			item.timeTxtVu.setText(playsComments.get(pos).getPlaysComment().getTime());
		}
		
		int tarId = playsComments.get(pos).getPlaysComment().getCommentedUserId();
		if(tarId!=-1){
			int num = playsComments.size();
			String namesString = "";
			for (int i = 0; i < num; i++) {
				if(playsComments.get(i).getCommUserInfo().getUserId()==tarId){
					namesString = playsComments.get(i).getCommUserInfo().getNickName();
					item.contentTxtVu.setText("回复 "+namesString+" :  "+playsComments.get(pos).getPlaysComment().getContent());
					break;
				}
			}
		}else {
			item.contentTxtVu.setText(playsComments.get(pos).getPlaysComment().getContent());
		}
		ExpressionUtil.changeToTextWithFace(context, item.contentTxtVu);
		return view;
	}
	
	class PlaysCommentItem{
		public ImageButton headImgBtn;
		public TextView nickNameTxtVu;
		public TextView contentTxtVu;
		public TextView timeTxtVu;
	}

}
