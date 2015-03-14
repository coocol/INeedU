package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.NeedDetailActivityModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.ExpressionUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
public class NeedDetailActivityAdapter extends BaseAdapter{


	private Context context;
	private ArrayList<NeedDetailActivityModel> list = new ArrayList<NeedDetailActivityModel>();
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private int ownerid;
	
	public NeedDetailActivityAdapter(Context context,ArrayList<NeedDetailActivityModel> list,int ownerid){
		this.context = context;
		this.list = list;
		this.ownerid = ownerid;

		options = new DisplayImageOptions.Builder()   
		.showImageForEmptyUri(R.drawable.logo)  // 设置图片Uri为空或是错误的时候显示的图片  
        .showImageOnFail(R.drawable.logo)       // 设置图片加载或解码过程中发生错误显示的图片      
        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
        .cacheOnDisc(true)                          // 设置下载的图片是否缓存在SD卡中  
		 .displayer(new CircleBitmapDisplayer())  // 设置成圆角图片  
		 .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        .build(); 
	}
	
	public int getCount() {
		return list.size();
	}
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		NeedDetailActivityModel helperInfo =  list.get(position);
		Item item = null;
		if(view==null){
			item = new Item();
			view = LayoutInflater.from(context).inflate(R.layout.need_detail_page_item, parent, false);
			//获取控件
			item.nickname=(TextView)view.findViewById(R.id.need_detail_page_item_helpername);
			item.headPic=(ImageButton)view.findViewById(R.id.need_detail_page_item_headpic_button);
			item.content=(TextView)view.findViewById(R.id.need_detail_page_item_comment_text);
			item.time=(TextView)view.findViewById(R.id.need_detail_page_item_time);
			item.floorTxtVu = (TextView) view.findViewById(R.id.tv_floor);
			item.sexTxtVu = (TextView) view.findViewById(R.id.tv_sex);
			view.setTag(item);
		}else{
			item = (Item)view.getTag();
		}
		//将值赋给item类中对应的控件
		
		if(list.get(position).getCommentedUserId()==-1)
		{
			item.content.setText(list.get(position).getContent());
		}
		else {
			item.content.setText(Html.fromHtml("<font color=#9370db>回复  </font>")
					+list.get(position).getThatManName()
					+": "
					+list.get(position).getContent());
		}
		//将文字变成带表情的文字,注意必须是TextView里面有文字
		ExpressionUtil.changeToTextWithFace(context, item.content);
		item.nickname.setText(helperInfo.getHelperName());
		String timeString = DataTraslator.LongToTimePastGeneral(helperInfo.getTime());
		String distanceString = list.get(position).getDistance();
		item.sexTxtVu.setText(list.get(position).getSex());
		item.time.setText(timeString+"|"+distanceString);
		if(ownerid==list.get(position).getUserId()){
			item.floorTxtVu.setText("楼主");
		}else{
			item.floorTxtVu.setText(position+1+"楼");
		}
		
		//头像 暂时为静态
		//item.headPic.setBackgroundResource(R.drawable.gediao_41);
		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+helperInfo.getUserId()+".png", item.headPic, options);      
		final String userId = String.valueOf(helperInfo.getUserId());
		//头像点击监听
		item.headPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new HeadClickEvent(context, Integer.parseInt(userId)).click();
			}
		});
		
		return view;
	}
	class Item{
		TextView nickname;
		TextView content;
		TextView time;
		ImageButton headPic;
		TextView floorTxtVu;
		TextView sexTxtVu;
	}
	
}
