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

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.SumPopularityRankFragmentModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.FragmentOnClickListener;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class SumPopularityRankFragmentAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<SumPopularityRankFragmentModel> list = new ArrayList<SumPopularityRankFragmentModel>();
	

	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	public SumPopularityRankFragmentAdapter(Context context,ArrayList<SumPopularityRankFragmentModel> arrayList){
		this.context = context;
		this.list = arrayList;

		options = new DisplayImageOptions.Builder()  
        .showImageForEmptyUri(R.drawable.logo)  // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.logo)       // 设置图片加载或解码过程中发生错误显示的图片      
        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
        .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中  
       .displayer(new CircleBitmapDisplayer())  // 设置成圆角图片  
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
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
		Item item = null;
		if(view==null){
			item = new Item();
			view = LayoutInflater.from(context).inflate(R.layout.nearby_sumrank_page_item, parent, false);
			//获取头像
			item.headPic = (ImageButton)view.findViewById(R.id.nearby_page_headpic_button);
			//获取姓名
			item.name = (TextView)view.findViewById(R.id.nearby_page_name);
			//item.distant=(TextView)view.findViewById(R.id.nearby_page_distance);
			//item.loveNum=(TextView)view.findViewById(R.id.nearby_page_loveNum);
			item.ranknum=(TextView)view.findViewById(R.id.nearby_page_ranknum);
			item.detail=(TextView)view.findViewById(R.id.nearby_page_detail);;
			item.sexImgVu = (ImageView)view.findViewById(R.id.iv_sex);
			
			view.setTag(item);
		}else{
			item = (Item)view.getTag();
		}
		
		//item.headPic.setImageResource(Integer.parseInt(NearContact.getHeadPic()));
		item.name.setText(list.get(position).getName());
		//item.distant.setText(NearContact.getDistant()+"米");
		//item.loveNum.setText("帮过"+NearContact.getLoveNum()+"个人");
		
		item.ranknum.setText(""+list.get(position).getRankNum());
		if(list.get(position).getSex()!=null && list.get(position).getSex().equals("男")){
			item.sexImgVu.setImageResource(R.drawable.sex_boy_press);
			item.detail.setText(DataTraslator.DistanceToString(list.get(position).getDistant())
					+" | "+list.get(position).getPopularityNum()+"个人给他送过花");
		}else {
			item.sexImgVu.setImageResource(R.drawable.sex_girl_press);
			item.detail.setText(DataTraslator.DistanceToString(list.get(position).getDistant())
					+" | "+list.get(position).getPopularityNum()+"个人给她送过花");
		}
		//头像
		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+list.get(position).getId()+".png", item.headPic, options);
		item.headPic.setOnClickListener(new FragmentOnClickListener(position) {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new HeadClickEvent(context, list.get(getPosition()).getId()).click();
			}
		});
		return view;
	}
	
	class Item{
		 public TextView detail;
		TextView name;
		 TextView distant;
		 TextView loveNum;
		 ImageButton headPic;
		 TextView ranknum;
		 ImageView sexImgVu;
	}
	
}
