package com.eethan.ineedu.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.IMMessage;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.ExpressionUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


public class MessageListAdapter extends BaseAdapter {

	private List<IMMessage> items;
	private Context mContext;
	private ListView adapterList;
	private LayoutInflater inflater;
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private boolean isHidden = false;
	
	public MessageListAdapter(Context mContext, List<IMMessage> items,
			ListView adapterList, boolean isHidden) {
		
		this.mContext = mContext;
		this.items = items;
		if(this.items == null) this.items = new ArrayList<IMMessage>();
		this.adapterList = adapterList;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		options = new DisplayImageOptions.Builder()  
        .showImageForEmptyUri(R.drawable.logo)  // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.logo)       // 设置图片加载或解码过程中发生错误显示的图片      
        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
        .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中  
        .displayer(new CircleBitmapDisplayer())  // 设置成圆角图片  
        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .build(); 
		
		this.isHidden = isHidden;
	}

	public void refreshList(List<IMMessage> items) {
		this.refresh(items);
		adapterList.setSelection(items.size() - 1);
	}
	
	public void refreshHistory(List<IMMessage> items, int temp) {
		Log.d("TATATATAT", ""+temp);
		this.refresh(items);
		adapterList.setSelection(temp);
	}

	private void refresh(List<IMMessage> items) {
		this.items = items;
		if(this.items == null) items = new ArrayList<IMMessage>();
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		IMMessage message = items.get(position);
		if (message.getType().equals(IMMessage.MSG_TYPE[0])) {
			convertView = this.inflater.inflate(
					R.layout.chat_page_item_left_wenzi, null);
		} else {
			convertView = this.inflater.inflate(
					R.layout.chat_page_item_right_wenzi, null);
		}
		ImageButton headPic = (ImageButton) convertView
				.findViewById(R.id.chat_page_headpic_button);
		TextView msgView = (TextView) convertView
				.findViewById(R.id.chat_page_item_text);
		//headPic.setBackgroundResource(R.drawable.gediao_24);
		if(!isHidden){
			imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+items.get(position).getUserid()+".png", headPic, options);      

		}
		
		msgView.setText(items.get(position).getContent());
		
		TextView dateView = (TextView)convertView.findViewById(R.id.chat_page_date_text);
		dateView.setText(DateUtil.getDateByStringTime(items.get(position).getTime()));

		//点击头像跳转到个人资料
		//得到userId
		final String userId = items.get(position).getUserid();
		headPic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isHidden)//不匿名才能点
					new HeadClickEvent(mContext, Integer.parseInt(userId)).click();
			}
		});
		//将文字变成带表情的文字,注意必须是TextView里面有文字
		ExpressionUtil.changeToTextWithFace(mContext, msgView);
		//msgView.setText(message.getContent());
		
		return convertView;
	}

}