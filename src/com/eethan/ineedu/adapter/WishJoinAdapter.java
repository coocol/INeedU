package com.eethan.ineedu.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.WishJoinModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


public class WishJoinAdapter extends BaseAdapter {
	
	private ImageLoader imageLoader;
	private List<WishJoinModel> list;
	private Context context;
	
	private HolderView holderView;
	private DisplayImageOptions options_head;

	public WishJoinAdapter(Context context,List<WishJoinModel> list,ImageLoader imageLoader){
		this.context = context;
		this.list = list;
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
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final int pos = position;
		if (view == null) {
			holderView = new HolderView();
			view = LayoutInflater.from(context).inflate(R.layout.wish_join_item, parent, false);
			holderView.headImgBtn = (ImageButton) view.findViewById(R.id.ib_head);
			holderView.timeTxtVu = (TextView)view.findViewById(R.id.tv_detail);
			holderView.nametxtVu = (TextView) view.findViewById(R.id.tv_name);
			holderView.sexImgVu = (ImageView) view.findViewById(R.id.iv_sex);
			view.setTag(holderView);
		} else {
			holderView = (HolderView) view.getTag();
		}
		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+list.get(position).getUserId()+".png", holderView.headImgBtn,options_head);
		holderView.nametxtVu.setText(list.get(position).getNickName()+"  "+list.get(position).getAcademy());
		holderView.timeTxtVu.setText(list.get(position).getTime()+"|"+list.get(position).getDistance());
		holderView.sexImgVu.setImageResource(R.drawable.sex_boy_press);
		holderView.headImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new HeadClickEvent(context, list.get(pos).getUserId()).click();
			}
		});
		return view;
	}
	
	class HolderView{
		public ImageView sexImgVu;
		public ImageButton headImgBtn;
		public TextView nametxtVu;
		public TextView timeTxtVu;
	}

}
