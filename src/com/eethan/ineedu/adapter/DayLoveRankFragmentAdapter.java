package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.DayLoveRankFragmentModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class DayLoveRankFragmentAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<DayLoveRankFragmentModel> list = new ArrayList<DayLoveRankFragmentModel>();
	

	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	public DayLoveRankFragmentAdapter(Context context,ArrayList<DayLoveRankFragmentModel> arrayList){
		this.context = context;
		this.list = arrayList;

		options = new DisplayImageOptions.Builder()  
        .showImageForEmptyUri(R.drawable.logo)  // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.logo)       // 设置图片加载或解码过程中发生错误显示的图片      
        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
        .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中  
       .displayer(new CircleBitmapDisplayer())  // 设置成圆角图片  
        .imageScaleType(ImageScaleType. EXACTLY_STRETCHED)
        .build(); 
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
		DayLoveRankFragmentModel NearContact = list.get(position);
		Item item = null;
		if(view==null){
			item = new Item();
			view = LayoutInflater.from(context).inflate(R.layout.nearby_dayrank_page_item, parent, false);
			//获取头像
			item.headPic = (ImageView)view.findViewById(R.id.nearby_page_headpic_button);
			//获取姓名
			item.name = (TextView)view.findViewById(R.id.nearby_page_name);
			//item.distant=(TextView)view.findViewById(R.id.nearby_page_distance);
			//item.loveNum=(TextView)view.findViewById(R.id.nearby_page_loveNum);
			item.detail=(TextView)view.findViewById(R.id.nearby_page_detail);
			item.love_addNum=(TextView)view.findViewById(R.id.nearby_page_love_addnum);
			item.sexImgVu=(ImageView)view.findViewById(R.id.iv_sex);
			view.setTag(item);
		}else{
			item = (Item)view.getTag();
		}
		
		//item.headPic.setImageResource(Integer.parseInt(NearContact.getHeadPic()));
		//item.love_addNum.setText(NearContact.getLove_addnum());
		item.name.setText(NearContact.getName());
		//item.distant.setText(NearContact.getDistant()+"米");
		//item.loveNum.setText("帮过"+NearContact.getLoveNum()+"个人");
	
		item.love_addNum.setText("+"+NearContact.getLove_addnum());
		if(NearContact.getSex()!=null && NearContact.getSex().equals("男")){
			item.sexImgVu.setImageResource(R.drawable.sex_boy_press);
			item.detail.setText(DataTraslator.DistanceToString(NearContact.getDistant())
					+"|"+Constant.GOD_VALUE_STRING+NearContact.getLoveNum());
		}else {
			item.sexImgVu.setImageResource(R.drawable.sex_girl_press);
			item.detail.setText(DataTraslator.DistanceToString(NearContact.getDistant())
					+"|"+Constant.GODNESS_VALUE_STRING+NearContact.getLoveNum());
		}
		//头像
    	imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+NearContact.getId()+".png", item.headPic, options);
		final String userid=""+NearContact.getId();
		item.headPic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new HeadClickEvent(context, Integer.parseInt(userid)).click();
			}
		});
		return view;
	}
	
	class Item{
		 public TextView detail;
		TextView name;
		 TextView distant;
		 TextView loveNum;
		 ImageView headPic;
		 TextView ranknum;
		 TextView love_addNum;
		 ImageView sexImgVu;
	}

}
