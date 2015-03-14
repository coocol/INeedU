package com.eethan.ineedu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.User;
import com.eethan.ineedu.model.PhotoNewsDetailsModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.ExpressionUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class PhotoNewsDetailsAdapter extends BaseAdapter {

	private CommentItem item;
	private Context context;
	private List<PhotoNewsDetailsModel> commList = new ArrayList<PhotoNewsDetailsModel>();
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options_head;
	private int ownerid;

	public PhotoNewsDetailsAdapter(Context cnt, List<PhotoNewsDetailsModel> list,int ownerId) {
		context = cnt;
		commList = list;
		this.ownerid = ownerId;

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
		return commList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return commList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final int pos = position;
		if (view == null) {
			item = new CommentItem();
			view = LayoutInflater.from(context).inflate(
					R.layout.takephotos_detail_item, parent, false);
			item.headImgBtn = (ImageButton) view
					.findViewById(R.id.imageButton1);
			item.nickTxtVw = (TextView) view.findViewById(R.id.nickname);
			item.timeTxtVw = (TextView) view.findViewById(R.id.tpd_tv_time);
			item.contentTxtVw = (TextView) view.findViewById(R.id.content);
			item.floorTxtVu = (TextView) view.findViewById(R.id.tv_floor);
			item.sexImgVu = (ImageView) view.findViewById(R.id.iv_sex);
			view.setTag(item);
		} else {
			item = (CommentItem) view.getTag();
		}

		String headURL = URLConstant.BIG_HEAD_PIC_URL
				+ commList.get(pos).getUserInfo().getUserId() + ".png";
		String nickName = commList.get(pos).getUserInfo().getNickName();
		String readableTime = DataTraslator.LongToTimePastGeneral(Long
				.parseLong(commList.get(pos).getTakePhotosComment().getTime()));
		imageLoader.displayImage(headURL, item.headImgBtn, options_head);
		if(commList.get(pos).getUserInfo().getSex().equals("男")){
			item.sexImgVu.setImageResource(R.drawable.sex_boy_press);
		}else{
			item.sexImgVu.setImageResource(R.drawable.sex_girl_press);
		}
		try {
			String distanceString = DataTraslator
					.DistanceToString(DataTraslator.GetDistanceToMe(context,
							commList.get(pos).getUserLocation().getLat(),
							commList.get(pos).getUserLocation().getLng()));
			item.timeTxtVw.setText(distanceString+" "+	readableTime);
		} catch (Exception e) {
			item.timeTxtVw.setText(readableTime);
		}

			
		
			item.nickTxtVw.setText(nickName);
		
		if(commList.get(pos).getUserInfo().getUserId()==ownerid){
			item.floorTxtVu.setText("楼主");
		}else{
			item.floorTxtVu.setText(pos+1+"楼");
		}

		

		int tarId = commList.get(pos).getTakePhotosComment()
				.getCommentedUserId();
		if (tarId != -1) {
			int num = commList.size();
			String namesString = "";
			for (int i = 0; i < num; i++) {
				if (commList.get(i).getUserInfo().getUserId() == tarId) {
					namesString = commList.get(i).getUserInfo().getNickName();
					item.contentTxtVw.setText("回复 "
							+ namesString
							+ " :  "
							+ commList.get(pos).getTakePhotosComment()
									.getContent());
					break;
				}
			}
		} else {
			item.contentTxtVw.setText(commList.get(pos).getTakePhotosComment()
					.getContent());
		}
		ExpressionUtil.changeToTextWithFace(context, item.contentTxtVw);
		item.headImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int userId = commList.get(pos).getUserInfo().getUserId();
				new HeadClickEvent(context, userId).click();
			}
		});
		return view;
	}

	class CommentItem {
		public ImageButton headImgBtn;
		public TextView nickTxtVw;
		public TextView timeTxtVw;
		public TextView contentTxtVw;
		public TextView floorTxtVu;
		public ImageView sexImgVu; 
	}

}
