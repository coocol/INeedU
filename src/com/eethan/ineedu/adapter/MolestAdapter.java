package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.constant.UserFlag;
import com.eethan.ineedu.model.MolestModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.FragmentOnClickListener;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.ExpressionUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class MolestAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<MolestModel> list = new ArrayList<MolestModel>();
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	public MolestAdapter(Context context,ArrayList<MolestModel> list){
		this.context = context;
		this.list = list;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
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
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
			return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		try {
			MolestModel model = list.get(position);
			if (model.getFlag() == UserFlag.NORMAL) {
				view = this.inflater.inflate(
						R.layout.chat_left_text, null);
			} else {
				view = this.inflater.inflate(
						R.layout.chat_right_text, null);
			}
			
			ImageButton headPic = (ImageButton) view
					.findViewById(R.id.ib_head);
			TextView nickName = (TextView) view
					.findViewById(R.id.tv_nickname);
			TextView msgView = (TextView) view
					.findViewById(R.id.tv_content);
			//headPic.setBackgroundResource(R.drawable.gediao_24);
			
				imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+list.get(position).getUserId()+".png", headPic, options);      
				String commentedName = list.get(position).getCommentedUserName();
				if(commentedName.equals(""))
					msgView.setText(list.get(position).getContent());
				else
					msgView.setText("回复 "+commentedName+": "+list.get(position).getContent());
				
				nickName.setText(list.get(position).getName());
				TextView dateView = (TextView)view.findViewById(R.id.tv_time);
				dateView.setText(DateUtil.getDateByStringTime(list.get(position).getTime()));

				//点击头像跳转到个人资料
				//得到userId
				headPic.setOnClickListener(new FragmentOnClickListener(position) {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new HeadClickEvent(context, list.get(getPosition()).getUserId()).click();
					}
				});
				//将文字变成带表情的文字,注意必须是TextView里面有文字
				ExpressionUtil.changeToTextWithFace(context, msgView);
				//msgView.setText(message.getContent());
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return view;
	}
	
}
