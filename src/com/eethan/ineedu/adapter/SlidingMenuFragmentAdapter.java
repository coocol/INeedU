package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.model.SlidingMenuFragmentModel;

public class SlidingMenuFragmentAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<SlidingMenuFragmentModel> list = new ArrayList<SlidingMenuFragmentModel>();
	
	public SlidingMenuFragmentAdapter(Context context,ArrayList<SlidingMenuFragmentModel> list){
		this.context = context;
		this.list = list;
		
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
		SlidingMenuFragmentModel SlidingMenu = list.get(position);
		Item item = null;
		if(view==null){
			item = new Item();
			view = LayoutInflater.from(context).inflate(R.layout.sidebar_page_item, parent, false);
			//获取信息
			item.option = (TextView)view.findViewById(R.id.sidebarpage_ineedu_button);
			item.picLocation=(ImageView)view.findViewById(R.id.sidebarpage_ineedu_pic);
			view.setTag(item);
		}else{
			item = (Item)view.getTag();
		}
		//将值赋给item类中对应的控件
		item.option.setText(SlidingMenu.getOption());
		item.picLocation.setImageResource(Integer.parseInt(SlidingMenu.getPicLocation()));
		return view;
	}
	
	class Item{
		TextView option;
		ImageView picLocation;
	}
	
	
}
