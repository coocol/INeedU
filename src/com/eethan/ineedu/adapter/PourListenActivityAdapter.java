package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.model.PourListenActivityModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PourListenActivityAdapter extends BaseAdapter{
	
	
	private Context context;
	private ArrayList<PourListenActivityModel> list = new ArrayList<PourListenActivityModel>();
	private ImageLoader imageLoader;
	private Item item;
	DisplayImageOptions options;
	private ArrayList<Boolean> canRefresh;
	
	public PourListenActivityAdapter(Context context,ArrayList<PourListenActivityModel> list,ImageLoader imageLoader){
		this.context = context;
		this.list = list;
		this.imageLoader=imageLoader;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		
		
		if(view==null){
			item = new Item();
			view = LayoutInflater.from(context).inflate(R.layout.pourlisten_page_item, parent, false);
			//获取信息
			item.bgPic=(ImageView)view.findViewById(R.id.pourlisten_page_item_background);
			item.content=(TextView)view.findViewById(R.id.pourlisten_page_item_text);
			item.numOfCommit=(TextView)view.findViewById(R.id.pourlisten_page_item_numofcommit);
			item.sexImgVu = (ImageView)view.findViewById(R.id.iv_sex);
			item.distanceTxtVu = (TextView)view.findViewById(R.id.tv_distance);
			item.timeTxtVu = (TextView)view.findViewById(R.id.tv_time);
			view.setTag(item);
		}else{
			item = (Item)view.getTag();
		}
		//将值赋给item类中对应的控件
		 options= new DisplayImageOptions.Builder()
		//.showStubImage(R.drawable.ic_stub_horizon)
		//.showImageForEmptyUri(R.drawable.ic_error_horizon)
		//.showImageOnFail(R.drawable.ic_error_horizon)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
		 //if(canRefresh.get(position))
		imageLoader.displayImage(list.get(position).getImageUrl(), item.bgPic,options);//通过url display image
		item.content.setText(list.get(position).getContent());
		item.numOfCommit.setText(list.get(position).getNumOfComment()+"");
		if(list.get(position).getSex()!=null && list.get(position).getSex().equals("男")){
			item.sexImgVu.setImageResource(R.drawable.sex_boy_press);
		}else {
			item.sexImgVu.setImageResource(R.drawable.sex_girl_press);
		}
		item.distanceTxtVu.setText(DataTraslator.DistanceToString(DataTraslator.GetDistanceToMe(context, list.get(position).getLat(), list.get(position).getLng())));
		item.timeTxtVu.setText(DataTraslator.LongToTimePastGeneral(list.get(position).getTime()));
		//if(list.get(position).get)
		Log.i("DB","GetView");
		 //if(canRefresh.get(position))
			 //canRefresh.set(position, false);
		return view;
	}
	

	public ArrayList<Boolean> getIsRefresh() {
		return canRefresh;
	}

	public void setIsRefresh(ArrayList<Boolean> isRefresh) {
		this.canRefresh = isRefresh;
	}


	class Item{
		ImageView bgPic;
		TextView content;
		TextView numOfCommit;
		ImageView sexImgVu;
		TextView distanceTxtVu;
		TextView timeTxtVu;
	}
	
	
}
