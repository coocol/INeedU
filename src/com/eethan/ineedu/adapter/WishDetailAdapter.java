package com.eethan.ineedu.adapter;

import java.util.ArrayList;
import java.util.List;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.WishDetailModel;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.ExpressionUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;


public class WishDetailAdapter extends BaseAdapter {

	private Context context;
	private ImageLoader imageLoader;
	private ArrayList<WishDetailModel> commList;
	private WishDetailItem item;
	private int ownerid;
	
	public WishDetailAdapter(Context context,List<WishDetailModel> list,ImageLoader imageLoader,int ownerid){
		this.context = context;
		this.commList = (ArrayList<WishDetailModel>)list;
		this.imageLoader = imageLoader;
		this.ownerid = ownerid;
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return commList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return commList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int postion, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int pos = postion;
		if(view==null){
			item = new WishDetailItem();
			view = LayoutInflater.from(context).inflate(R.layout.wish_detail_item, parent,false);
			item.headImgBtn = (ImageButton)view.findViewById(R.id.imgbt_head);
			item.timeTxtVu = (TextView)view.findViewById(R.id.tv_commtime);
			item.contentTxtVu = (TextView)view.findViewById(R.id.tv_commcontent);
			item.floorTxtVu = (TextView) view.findViewById(R.id.tv_floor);
			item.sexTxtVu = (TextView) view.findViewById(R.id.tv_sex);
			view.setTag(item);
		}else {
			item = (WishDetailItem) view.getTag();
		}
		//imageLoader.displayImage(URLConstant.HEAD_PIC_URL+commList.get(pos).getCommUserInfo().getUserId()+".png", item.headImgBtn);
		try {
			String timeString = DataTraslator.LongToTimePastGeneral(Long.parseLong(commList.get(pos).getWishComment().getTime()));
			String distanceString = DataTraslator.DistanceToString(DataTraslator.GetDistanceToMe(context, commList.get(pos).getCommUserLocation().getLat(), commList.get(pos).getCommUserLocation().getLng()));
			item.sexTxtVu.setText(commList.get(pos).getCommUserInfo().getSex());
			item.timeTxtVu.setText(timeString+"|"+distanceString);
		} catch (Exception e) {
			item.timeTxtVu.setText("parse error");
		}
		if(ownerid==commList.get(pos).getCommUserInfo().getUserId()){
			item.floorTxtVu.setText("楼主");
		}else{
			item.floorTxtVu.setText(pos+1+"楼");
		}
		item.contentTxtVu.setText(commList.get(pos).getWishComment().getContent());
		ExpressionUtil.changeToTextWithFace(context, item.contentTxtVu);
		item.headImgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new HeadClickEvent(context, commList.get(pos).getCommUserInfo().getUserId());
			}
		});
		return view;
	}
	
	class WishDetailItem{
		public ImageButton headImgBtn;
		public TextView nameTxtVu;
		public TextView timeTxtVu;
		public TextView contentTxtVu;
		public TextView floorTxtVu;
		public TextView sexTxtVu;
	}

}
