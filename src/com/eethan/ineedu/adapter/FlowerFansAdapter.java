package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.primaryactivity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FlowerFansAdapter extends BaseAdapter{
	
	private ArrayList<UserInfo> fansList;
	private Context context;
	private FansItem item;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public FlowerFansAdapter(Context context,ArrayList<UserInfo> list,ImageLoader imageLoader,DisplayImageOptions options){
		this.context = context;
		this.fansList = list;
		this.imageLoader = imageLoader;
		this.options = options;
	}

	@Override
	public int getCount() {
		return fansList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return fansList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			item = new FansItem();
			convertView = LayoutInflater.from(context).inflate(R.layout.flower_fans_item, parent, false);
			item.headImgBtn = (ImageButton)convertView.findViewById(R.id.nearby_page_headpic_button);
			item.nameTxtVu = (TextView)convertView.findViewById(R.id.nearby_page_name);
			convertView.setTag(item);
		}else {
			item = (FansItem) convertView.getTag();
		}
		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+fansList.get(position).getUserId()+".png", item.headImgBtn,options);
		item.nameTxtVu.setText(fansList.get(position).getNickName());
		return convertView;
	}
	
	class FansItem{
		public ImageButton headImgBtn;
		public TextView nameTxtVu;
		public TextView sexTxtVu;
		public ImageView sexImgVu;
	}

}
