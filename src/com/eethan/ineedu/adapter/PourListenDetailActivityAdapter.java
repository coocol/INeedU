package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.MyIntent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.databasebeans.PourlistenComment;
import com.eethan.ineedu.model.PourlistenDetailModel;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.ExpressionUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class PourListenDetailActivityAdapter extends BaseAdapter{
	
	
	private Context context;
	private ArrayList<PourlistenDetailModel> list = new ArrayList<PourlistenDetailModel>();
	private ArrayList<PourlistenComment> comments;
	private Item item;
	DisplayImageOptions options;
	
	public PourListenDetailActivityAdapter(Context context,ArrayList<PourlistenDetailModel> list,
			ArrayList<PourlistenComment> comments){
		this.context = context;
		this.list = list;
		this.comments=comments;
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
		
		PourlistenDetailModel pdm = list.get(position);
		if(view==null){
			item = new Item();
			view = LayoutInflater.from(context).inflate(R.layout.pour_listen_detail_page_item, parent, false);
			//获取信息
			item.commentTextView=(TextView)view.findViewById(R.id.pour_listen_detail_page_item_comment);
			item.floorTextView=(TextView)view.findViewById(R.id.pour_listen_detail_floor);
			item.timeTextView=(TextView)view.findViewById(R.id.pour_listen_page_item_time);
			item.head=(RelativeLayout)view.findViewById(R.id.pour_listen_detail_rela);
			item.sexTxtVu = (TextView) view.findViewById(R.id.tv_sex);
			view.setTag(item);
		}else{
			item = (Item)view.getTag();
		}
		//将值赋给item类中对应的控件
		if(list.get(position).getCommentedUserId()==-1)
		{
			item.commentTextView.setText(pdm.getComment());
			for(int i=0;i<comments.size();i++)
			{
				if(comments.get(i).getId()==list.get(position).getCommentId())
				{
					item.head.setVisibility(View.VISIBLE);
					item.floorTextView.setText((i+1)+"楼");
				}
			}
		}
		else
		{
			item.commentTextView.setText("回复: "+pdm.getComment());
			item.head.setVisibility(View.INVISIBLE);
		}
		//将文字变成带表情的文字,注意必须是TextView里面有文字
		ExpressionUtil.changeToTextWithFace(context, item.commentTextView);
		long time=pdm.getTime();
		item.sexTxtVu.setText(list.get(position).getSex());
		item.timeTextView.setText(
				DataTraslator.LongToTimePastGeneral(time)+"|"+
				list.get(position).getDistance());

		return view;
	}
	

	class Item{
		TextView commentTextView;
		TextView floorTextView;
		TextView timeTextView;
		RelativeLayout head;
		TextView sexTxtVu;
	}
	
	
}
