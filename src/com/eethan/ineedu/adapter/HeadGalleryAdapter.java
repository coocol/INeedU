package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class HeadGalleryAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<Integer> headImgUserIds;
	ImageLoader imageLoader;
	private TheItem item;
	private DisplayImageOptions options;
	
	public HeadGalleryAdapter(Context context,ArrayList<Integer> imgUrls,ImageLoader imageLoader){
		this.context = context;
		this.headImgUserIds = imgUrls;
		this.imageLoader = imageLoader;
		options = new DisplayImageOptions.Builder()  
		.showImageForEmptyUri(R.drawable.logo)  // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.logo)       // 设置图片加载或解码过程中发生错误显示的图片       
        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
        .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中  
        .displayer(new CircleBitmapDisplayer())  // 设置成圆角图片  
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .build(); 
		//
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return headImgUserIds.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return headImgUserIds.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(view==null){
			item = new TheItem();
			view = LayoutInflater.from(context).inflate(R.layout.gallery_head_item, arg2,false);
			item.headImgVu = (ImageView)view.findViewById(R.id.iv_gallery_head);
			view.setTag(item);
		}else {
			item = (TheItem) view.getTag();
		}
		imageLoader = ImageLoader.getInstance();

		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL + headImgUserIds.get(arg0)+".png",item.headImgVu,options);
		
		return view;
	}
	
	class TheItem{
		public ImageView headImgVu;
	}
}
