package com.eethan.ineedu.adapter;

import java.util.ArrayList;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.GiveHeartEvent;
import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.NeedFragmentModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.CommentUtil;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class NeedFragmentAdapter extends BaseAdapter{

	private Context context;
	private Item item = null;
	private ArrayList<NeedFragmentModel> list = new ArrayList<NeedFragmentModel>();
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	
	public NeedFragmentAdapter(Context context,ArrayList<NeedFragmentModel> list){
		this.context = context;
		this.list = list;
		
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
		
		if(view==null){
			item = new Item();
			view = LayoutInflater.from(context).inflate(R.layout.home_page_item, parent, false);
			//获取信息
			item.nickname = (TextView)view.findViewById(R.id.home_page_name);
			item.loveNum = (TextView)view.findViewById(R.id.home_page_loveNum);
			item.content = (TextView)view.findViewById(R.id.home_page_content);
			item.reward = (TextView)view.findViewById(R.id.home_page_reward);
			item.time = (TextView)view.findViewById(R.id.home_page_time);
			item.headPic=(ImageView)view.findViewById(R.id.home_page_user_headpic_button);
			item.sexPic=(ImageView)view.findViewById(R.id.home_page_sex);
			item.helperHeadPic=(ImageButton)view.findViewById(R.id.home_page_helper_headpic_button);
			item.helperLinearLayout=(LinearLayout)view.findViewById(R.id.home_page_helper_linearlayout);
			item.commentNum=(TextView)view.findViewById(R.id.home_page_item_numofcomment);
			item.helperName=(TextView)view.findViewById(R.id.home_page_helperName);
			item.popularityNum=(TextView)view.findViewById(R.id.home_page_item_numofheart);
			item.typeImageView=(ImageView)view.findViewById(R.id.homepage_state_ask_icon);
			item.timeRemainTextView=(TextView)view.findViewById(R.id.home_page_time_remain_text);
			item.distanceTxtVu = (TextView)view.findViewById(R.id.tv_distance);
			item.sendTimeTxtVu = (TextView)view.findViewById(R.id.tv_time);
			item.needNumTxtVu = (TextView) view.findViewById(R.id.tv_num);
			view.setTag(item);
		}else{
			item = (Item)view.getTag();
		}
		setButtonListener(view, position);

		//将值赋给item类中对应的控件
		item.nickname.setText(list.get(position).getNickname()+"  "+list.get(position).getCademy());
		
		int dis = DataTraslator.GetDistanceToMe(context,list.get(position).getLat(),list.get(position).getLng());
		
		item.distanceTxtVu.setText(DataTraslator.DistanceToString(dis));

		item.sendTimeTxtVu.setText(DataTraslator.LongToTimePastGeneral(list.get(position).getTime()));
		item.content.setText(list.get(position).getContent());
		item.reward.setText(list.get(position).getReward());
		item.commentNum.setText(list.get(position).getCommentNum()+"");
		item.popularityNum.setText(list.get(position).getPopularityNum()+"");
		item.needNumTxtVu.setText("#"+list.get(position).getId());
		Log.i("DB","ADAPTER:"+list.get(position).getContent());
		//发布类型的图标
		switch (list.get(position).getType()) {
		case Constant.TYPE_ASK:
			item.typeImageView.setBackgroundResource(R.drawable.common_ask_press);
			break;
		case Constant.TYPE_BORROW:
			item.typeImageView.setBackgroundResource(R.drawable.common_borrow_press);
			break;
		case Constant.TYPE_INVITE:
			item.typeImageView.setBackgroundResource(R.drawable.common_invite_press);
			break;
		case Constant.TYPE_BRING:
			item.typeImageView.setBackgroundResource(R.drawable.common_bring_press);
			break;
		case Constant.TYPE_BUY:
			item.typeImageView.setBackgroundResource(R.drawable.common_buy_press);
			break;
		default:
			break;
		}
		//头像 
//		if(list.get(position).getUserId()==context.getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, -1))
//		{
//			item.headPic.setBackgroundDrawable(Header.getHead(list.get(position).getUserId()));
//		}
//		else
		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+list.get(position).getUserId()+".png", item.headPic, options); 
		//没有人帮助时不显示帮助者一栏
		if(list.get(position).getSolveId()==-1)
			item.helperLinearLayout.setVisibility(View.GONE);
		else
		{
			item.helperLinearLayout.setVisibility(View.VISIBLE);
			item.helperName.setText(list.get(position).getHelperName());
			imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+list.get(position).getSolveId()+".png", item.helperHeadPic, options);
		}
		
		//性别图片判断
		String sex=list.get(position).getSex();
		if(sex.equals("男")){
			item.sexPic.setImageResource(R.drawable.sex_boy_press);
			item.loveNum.setText(Constant.GOD_VALUE_STRING+list.get(position).getLoveNum());
		}else if(sex.equals("女")){
			item.sexPic.setImageResource(R.drawable.sex_girl_press);
			item.loveNum.setText(Constant.GODNESS_VALUE_STRING+list.get(position).getLoveNum());
		}
			
		Log.i("TIME",list.get(position).getContent()+"");
		//将Long型的数据处理为还剩X天X小时X分钟形式
		long dueTime=list.get(position).getTime()+list.get(position).getTimeLimit();//截止时间点
		long timeNow=DateUtil.getMSTime();//现在时间
		long timeRemain=dueTime-timeNow;
		item.timeRemainTextView.setText("剩余");
		if(timeRemain>0)
		{
			item.time.setText(DataTraslator.LongToTimeRemain(timeRemain));
		}
		else {
			item.time.setText("已截止");
		}
		
		
		
		return view;
	}
	class Item{
		TextView nickname;
		TextView signature;
		TextView content;
		TextView reward;
		TextView time;
		TextView loveNum;
		TextView helperName;
		ImageView sexPic;
		ImageView headPic;
		ImageButton helperHeadPic;//帮助者头像
		LinearLayout helperLinearLayout;//帮助者一栏，没有人时不显示它
		TextView commentNum;//评论的数量
		TextView popularityNum;//人气，即爱心图片处的数字
		ImageView typeImageView;//发布类型
		TextView timeRemainTextView;//“剩余”这两个字所在的TextView
		TextView distanceTxtVu;
		TextView sendTimeTxtVu;
		
		TextView needNumTxtVu;
	}
	
	private void setButtonListener(View view,int position)
	{
		//主角头像
			item.headPic.setOnClickListener(new BtnOnClickListener(position) {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int userId=list.get(getPosition()).getUserId();
					new HeadClickEvent(context, userId).click();
				}
			});
			
			//帮助者头像
			ImageButton helperHeadPic=(ImageButton)view.findViewById(R.id.home_page_helper_headpic_button);
			helperHeadPic.setOnClickListener(new BtnOnClickListener(position) {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int userId=list.get(getPosition()).getSolveId();
					new HeadClickEvent(context, userId).click();
				}
			});
			//评论
			ImageButton commentButton=(ImageButton)view.findViewById(R.id.home_page_item_comment_button);
			commentButton.setOnClickListener(new BtnOnClickListener(position) {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//-1表示评论的是事件而不是人
					new CommentUtil(context,getPosition(), -1, list, NeedFragmentAdapter.this, Constant.COMMENT_NEED);
				}
			});
			
			//点赞
			ImageButton giveHeartButton=(ImageButton)view.findViewById(R.id.home_page_giveheart_button);
			giveHeartButton.setOnClickListener(new BtnOnClickListener(position){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					new GiveHeartEvent(context, list.get(getPosition()).getUserId(),list.get(getPosition()).getId(),
							list,NeedFragmentAdapter.this,getPosition()).start();
				}
			});
	}
	class BtnOnClickListener implements android.view.View.OnClickListener{
        private int position;
        public BtnOnClickListener(int p) {
                // TODO Auto-generated constructor stub
                position=p;
        }
        public int getPosition()
        {
        	return position;
        }
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
	}
}
