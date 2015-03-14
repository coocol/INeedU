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
import com.eethan.ineedu.constant.NotificationType;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.NotificationActivityModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.ExpressionUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class NotificationActivityAdapter extends BaseAdapter {
	private Context context;
	private Item item = null;
	private ArrayList<NotificationActivityModel> list = new ArrayList<NotificationActivityModel>();
	
	private boolean isISay = false;

	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public NotificationActivityAdapter(Context context,
			ArrayList<NotificationActivityModel> list) {
		this.context = context;
		this.list = list;

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.logo) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.logo) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
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

		if (view == null) {
			item = new Item();
			view = LayoutInflater.from(context).inflate(
					R.layout.notification_page_item, parent, false);
			// 获取信息
			item.replyContent = (TextView) view
					.findViewById(R.id.home_page_content);
			item.headPic = (ImageButton) view
					.findViewById(R.id.home_page_user_headpic_button);
			item.sexPic = (ImageView) view.findViewById(R.id.home_page_sex);
			item.name = (TextView) view.findViewById(R.id.home_page_name);

			view.setTag(item);
		} else {
			item = (Item) view.getTag();
		}
		int type = list.get(position).getTYPE();
		type = Math.abs(type);

//		if (type != 2)// 倾诉倾听是 匿名不能查看
//			setButtonListener(view, position);
		
		//isISay = false;
		
		item.headPic.setOnClickListener(new BtnOnClickListener(position) {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new HeadClickEvent(context, list.get(getPosition())
						.getReplyManId()).click();
			}
		});
		
		switch (type) {
		case NotificationType.NEED_COMMENT_WAIT:
			item.name.setText(list.get(position).getReplyName() + " 回复了你:");
			break;
		case NotificationType.POURLISTEN_COMMENT_WAIT:
			item.name.setText("有人在我想说中回复你:");
			//isISay = true;
			item.headPic.setOnClickListener(new BtnOnClickListener(position) {
				@Override
				public void onClick(View arg0) {
				}
			});
			break;
		case NotificationType.FLOWER_WAIT:
			item.name.setText(list.get(position).getReplyName()
					+ "给你送了一朵花~");
			break;
		case NotificationType.PHOTO_COMMENT_WAIT:
			item.name.setText(list.get(position).getReplyName()
					+ " 评论了你的照片:");

			break;
		case NotificationType.PHOTO_PRAISE_WAIT:
			item.name.setText(list.get(position).getReplyName()
					+ " 赞了你的照片~");
			break;
		case NotificationType.PHOTO_TRANSMIT_WAIT:
			item.name.setText(list.get(position).getReplyName()
					+ " 转发了你的照片~");
			break;
		case NotificationType.WISH_PRAISE_WAIT:
			item.name.setText(list.get(position).getReplyName()
					+ " 赞了你的心愿~");
			break;
//		case NotificationType.WISH_ACCEPT_WAIT:
//			// item.name.setText(list.get(position).getReplyName()
//			// +" 赞了你的心愿~");
//			break;
		case NotificationType.PLAY_COMMENT_WAIT:
			item.name.setText(list.get(position).getReplyName()
					+ " 在活动中回复了你:");
			break;
		case NotificationType.PARTICIPATE_WAIT:
			item.name
					.setText(list.get(position).getReplyName() + " 加入了活动~");
			break;
		case NotificationType.NEED_NOTIFY_WAIT:
			item.name
					.setText("你附近的 "+list.get(position).getReplyName() + " 发布了见面~");
			break;
		case NotificationType.WISH_NOTIFY_WAIT:
			item.name
					.setText("你附近有妹子发布了星愿~");
			item.headPic.setOnClickListener(new BtnOnClickListener(position) {
				@Override
				public void onClick(View arg0) {
				}
			});
			break;
		case NotificationType.WISH_ACCEPT_WAIT:
			item.name
					.setText("你的星愿被摘走");
			
			break;
		case NotificationType.WISH_COMMENT_WAIT:
			item.name
					.setText("有人回复了你的星愿");
			item.headPic.setOnClickListener(new BtnOnClickListener(position) {
				@Override
				public void onClick(View arg0) {
				}
			});
			break;
		case NotificationType.WISH_TO_BOY_WAIT:
			item.name
					.setText("你摘下了女孩的星愿");
			item.headPic.setOnClickListener(new BtnOnClickListener(position) {
				@Override
				public void onClick(View arg0) {
				}
			});
			break;
		default:
			break;
		}
		
		//setButtonListener(view, position);
		
		if (type == NotificationType.POURLISTEN_COMMENT_WAIT)
			item.headPic.setBackgroundResource(R.drawable.logo);
		else
			imageLoader.displayImage(
					URLConstant.BIG_HEAD_PIC_URL
							+ list.get(position).getReplyManId() + ".png",
					item.headPic, options);

		// 性别图片判断
		String sex = list.get(position).getReplySex();
		if (sex.equals("男"))
			item.sexPic.setImageResource(R.drawable.sex_boy_press);
		else if (sex.equals("女"))
			item.sexPic.setImageResource(R.drawable.sex_girl_press);
		item.replyContent.setText(list.get(position).getReplyContent());
		// 将文字变成带表情的文字,注意必须是TextView里面有文字
		ExpressionUtil.changeToTextWithFace(context, item.replyContent);
		return view;
	}

	class Item {
		TextView name;
		ImageButton headPic;
		ImageView sexPic;
		TextView replyContent;
	}

	private void setButtonListener(View view, int position) {
		
		if(isISay){
			item.headPic.setOnClickListener(new BtnOnClickListener(position) {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					new HeadClickEvent(context, list.get(getPosition())
							.getReplyManId()).click();
				}
			});
			
		}
		isISay = false;
		
	}

	class BtnOnClickListener implements android.view.View.OnClickListener {
		private int position;

		public BtnOnClickListener(int p) {
			// TODO Auto-generated constructor stub
			position = p;
		}

		public int getPosition() {
			return position;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub

		}
	}
}
