package com.eethan.ineedu.adapter;

import java.util.ArrayList;
import java.util.List;

import com.eethan.ineedu.CommonUse.GiveHeartEvent;
import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.MoodLikeEvent;
import com.eethan.ineedu.CommonUse.NewPhotoNewsEvent;
import com.eethan.ineedu.CommonUse.PhotoLikeEvent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.PhotoNewsAdapter.PhotoNewsItem;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.databasebeans.TakePhotos;
import com.eethan.ineedu.databasebeans.TakePhotosPraise;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.model.MoodModel;
import com.eethan.ineedu.model.PhotoLike;
import com.eethan.ineedu.mycontrol.AdapterOnClickListener;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.CommentUtil;
import com.eethan.ineedu.util.DataTraslator;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MoodAdapter extends BaseAdapter {

	private ImageLoader imageLoader;
	private ArrayList<MoodModel> list;

	private PhotoItem photoItem;
	private MoodItem moodItem;
	private PourItem pourItem;
	private DisplayImageOptions options;
	private DisplayImageOptions options_head;
	private Context context;
	private int myUserId;
	
	private Handler handler;

	public MoodAdapter(Context context, List<MoodModel> list,
			ImageLoader imageLoader) {
		this.list = (ArrayList<MoodModel>) list;
		this.imageLoader = imageLoader;
		this.context = context;

		myUserId = new SPHelper(context).GetUserId();

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.gray_background)
				.showImageForEmptyUri(R.drawable.gray_background)
				.showImageOnFail(R.drawable.gray_background)
				.cacheInMemory(true).cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		options_head = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.logo) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.logo) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
			}
		};

	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 3;
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if(list.get(position).getFlag()==Constant.MOOD_FLAG_NOTE)
			return Constant.MOOD_FLAG_NOTE;
		else if(list.get(position).getFlag()==Constant.MOOD_FLAG_PHOTO)
			return Constant.MOOD_FLAG_PHOTO;
		else if(list.get(position).getFlag()==Constant.MOOD_FLAG_POUR)
			return Constant.MOOD_FLAG_POUR;
		else
			return 100;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		int type = getItemViewType(position);
		switch (type) {
		case Constant.MOOD_FLAG_PHOTO:
			if (view == null) {
				photoItem = new PhotoItem();
				view = LayoutInflater.from(context).inflate(
						R.layout.takephotos_main_item, parent, false);
				findPhotoView(view);
				view.setTag(photoItem);
			} else {
				photoItem = (PhotoItem) view.getTag();
			}
			setPhotoViewContent(position);
			break;
		case Constant.MOOD_FLAG_NOTE:
			if (view == null) {
				moodItem = new MoodItem();
				view = LayoutInflater.from(context).inflate(
						R.layout.mood_item, parent, false);
				findMoodView(view);
				view.setTag(moodItem);
			} else {
				moodItem = (MoodItem) view.getTag();
			}
			setMoodViewContent(position);
			break;
		case Constant.MOOD_FLAG_POUR:
			if (view == null) {
				pourItem = new PourItem();
				view = LayoutInflater.from(context).inflate(
						R.layout.pourlisten_page_item, parent, false);
				findPourView(view);
				view.setTag(pourItem);
			} else {
				pourItem = (PourItem) view.getTag();
			}
			setPourViewContent(position);
			break;
		default:
			break;
		}
		
		return view;
	}
		
	
	private void setPourViewContent(final int position){
		imageLoader.displayImage(list.get(position).getMood().getImageUrl(), pourItem.bgPic,options);//通过url display image
		pourItem.content.setText(list.get(position).getMood().getContent());
		pourItem.numOfCommit.setText(list.get(position).getMood().getCommentNum()+"");
		if(list.get(position).getOwnerInfo().getSex()!=null && list.get(position).getOwnerInfo().getSex().equals("男")){
			pourItem.sexImgVu.setImageResource(R.drawable.sex_boy_press);
		}else {
			pourItem.sexImgVu.setImageResource(R.drawable.sex_girl_press);
		}
		pourItem.distanceTxtVu.setText(DataTraslator.DistanceToString(DataTraslator.GetDistanceToMe(context, list.get(position).getMood().getLat(), list.get(position).getMood().getLng())));
		pourItem.timeTxtVu.setText(DataTraslator.LongToTimePastGeneral(Long.parseLong(list.get(position).getMood().getTime())));
	}
	
	private void setMoodViewContent(final int position){
		moodItem.nickNameTxtVw.setText(list.get(position).getOwnerInfo().getNickName() + "  "
				+ list.get(position).getOwnerDetailInfo().getAcademy());
		String readable_time = DataTraslator.LongToTimePastGeneral(Long
				.parseLong(list.get(position).getMood().getTime()));
		moodItem.timeTxtVw.setText(readable_time);
		
		if (list.get(position).getOwnerInfo().getSex().equals("女"))
			moodItem.sexImgVu.setImageResource(R.drawable.sex_girl_press);
		else {
			moodItem.sexImgVu.setImageResource(R.drawable.sex_boy_press);
		}
		String contentString = list.get(position).getMood().getContent();
		moodItem.contentTxtVw.setText(contentString);
		moodItem.commentTxtVw.setText("");
		moodItem.likeTxtVw.setText("");
		moodItem.flowerTxtVu.setText(String.valueOf(list.get(position).getOwnerInfo().getPopularityNum()));
		moodItem.commentTxtVw.setText(String.valueOf(list.get(position).getMood()
				.getCommentNum()));
		moodItem.likeTxtVw.setText(String.valueOf(list.get(position).getMood().getPraiseNum()));
		moodItem.distanceTxtVu.setText(DataTraslator
				.DistanceToString(DataTraslator.GetDistanceToMe(context, list
						.get(position).getMood().getLat(),
						list.get(position).getMood().getLng())));
		imageLoader.displayImage(
				URLConstant.BIG_HEAD_PIC_URL + list.get(position).getOwnerInfo().getUserId() + ".png",
				moodItem.headImgBtn, options_head);
		moodItem.headImgBtn.setOnClickListener(new AdapterOnClickListener(
				position) {
			@Override
			public void onClick(View arg0) {
				UserInfo owneruserInfos = list.get(getPosition())
						.getOwnerInfo();
				if (owneruserInfos != null && owneruserInfos.getUserId() > 0) {
					new HeadClickEvent(context, owneruserInfos.getUserId())
							.click();
				}
			}
		});
		
		moodItem.likeRelativeLayout
				.setOnClickListener(new AdapterOnClickListener(position) {
					@Override
					public void onClick(View arg0) {
						new MoodLikeEvent(context, list.get(position).getMood().getUserId(), list.get(position).getMood().getId(), MoodAdapter.this, list, position).start();
					}
				});
		
		moodItem.commRelativeLayout
				.setOnClickListener(new AdapterOnClickListener(position) {
					@Override
					public void onClick(View arg0) {
						new CommentUtil(context, getPosition(), -1, list,
								MoodAdapter.this,
								Constant.COMMENT_MOOD);
					}
				});
		moodItem.flowerRelativeLayout.setOnClickListener(new AdapterOnClickListener(position){
			@Override
			public void onClick(View arg0) {
				new GiveHeartEvent(context, list.get(position).getOwnerInfo().getUserId(), list.get(position).getMood().getId(), list, MoodAdapter.this, position).start();
			}
		});

	}

	public void setPhotoViewContent(final int position) {
		photoItem.nickNameTxtVw.setText(list.get(position).getOwnerInfo().getNickName() + "  "
				+ list.get(position).getOwnerDetailInfo().getAcademy());
		String readable_time = DataTraslator.LongToTimePastGeneral(Long
				.parseLong(list.get(position).getMood().getTime()));
		photoItem.timeTxtVw.setText(readable_time);
		photoItem.atNameTxtVw.setText("");
		if (list.get(position).getAtInfo().getUserId() > 0) {
			photoItem.atNameTxtVw.setText("   转自@" + list.get(position).getAtInfo().getNickName());
		}
		if (list.get(position).getOwnerInfo().getSex().equals("女"))
			photoItem.sexImgVu.setImageResource(R.drawable.sex_girl_press);
		else {
			photoItem.sexImgVu.setImageResource(R.drawable.sex_boy_press);
		}
		String contentString = list.get(position).getMood().getContent();
		photoItem.contentTxtVw.setText(contentString);
		photoItem.commentTxtVw.setText("");
		photoItem.likeTxtVw.setText("");
		photoItem.retweetTxtVw.setText("");
		photoItem.commentTxtVw.setText(String.valueOf(list.get(position).getMood()
				.getCommentNum()));
		photoItem.likeTxtVw.setText(String.valueOf(list.get(position).getMood().getPraiseNum()));
		photoItem.retweetTxtVw.setText(String.valueOf(list.get(position).getMood()
				.getTransmitNum()));
		photoItem.distanceTxtVu.setText(DataTraslator
				.DistanceToString(DataTraslator.GetDistanceToMe(context, list
						.get(position).getMood().getLat(),
						list.get(position).getMood().getLng())));
		imageLoader.displayImage(list.get(position).getMood().imageUrl,
				photoItem.photoImgVw, options);
		imageLoader.displayImage(
				URLConstant.BIG_HEAD_PIC_URL + list.get(position).getOwnerInfo().getUserId() + ".png",
				photoItem.headImgBtn, options_head);
		photoItem.headImgBtn.setOnClickListener(new AdapterOnClickListener(
				position) {
			@Override
			public void onClick(View arg0) {
				UserInfo owneruserInfos = list.get(getPosition())
						.getOwnerInfo();
				if (owneruserInfos != null && owneruserInfos.getUserId() > 0) {
					new HeadClickEvent(context, owneruserInfos.getUserId())
							.click();
				}
			}
		});
		photoItem.atNameTxtVw.setOnClickListener(new AdapterOnClickListener(
				position) {
			@Override
			public void onClick(View arg0) {
				UserInfo atuserInfos = list.get(getPosition()).getAtInfo();
				if (atuserInfos != null && atuserInfos.getUserId() > 0) {
					new HeadClickEvent(context, atuserInfos.getUserId())
							.click();
				}
			}
		});
		photoItem.likeRelativeLayout
				.setOnClickListener(new AdapterOnClickListener(position) {
					@Override
					public void onClick(View arg0) {
						new NewPhotoNewsEvent(context, list.get(position)
								.getOwnerInfo().getUserId(), list
								.get(position).getMood().getId(),
								MoodAdapter.this, list, position)
								.start();
					}
				});
		photoItem.retweetRelativeLayout
				.setOnClickListener(new AdapterOnClickListener(position) {
					@Override
					public void onClick(View arg0) {
						new CommentUtil(context, getPosition(), -1, list,
								MoodAdapter.this,
								Constant.TRANSMIT_PHOTONEWS_MOOD);

					}
				});
		photoItem.commRelativeLayout
				.setOnClickListener(new AdapterOnClickListener(position) {
					@Override
					public void onClick(View arg0) {
						new CommentUtil(context, getPosition(), -1, list,
								MoodAdapter.this,
								Constant.COMMENT_MOOD_PHOTO);
					}
				});

	}
	private void findPourView(View view){
		pourItem.bgPic=(ImageView)view.findViewById(R.id.pourlisten_page_item_background);
		pourItem.content=(TextView)view.findViewById(R.id.pourlisten_page_item_text);
		pourItem.numOfCommit=(TextView)view.findViewById(R.id.pourlisten_page_item_numofcommit);
		pourItem.sexImgVu = (ImageView)view.findViewById(R.id.iv_sex);
		pourItem.distanceTxtVu = (TextView)view.findViewById(R.id.tv_distance);
		pourItem.timeTxtVu = (TextView)view.findViewById(R.id.tv_time);
	}
	private void findPhotoView(View view) {

		photoItem.atNameTxtVw = (TextView) view.findViewById(R.id.at_nickname);
		photoItem.commentTxtVw = (TextView) view.findViewById(R.id.commentNum);
		photoItem.contentTxtVw = (TextView) view.findViewById(R.id.content);
		photoItem.headImgBtn = (ImageButton) view.findViewById(R.id.head);
		photoItem.sexImgVu = (ImageView) view.findViewById(R.id.sex);
		photoItem.likeTxtVw = (TextView) view.findViewById(R.id.likeNum);
		photoItem.nickNameTxtVw = (TextView) view.findViewById(R.id.nickname);
		photoItem.photoImgVw = (ImageView) view.findViewById(R.id.photo);
		photoItem.retweetTxtVw = (TextView) view.findViewById(R.id.transmitNum);
		photoItem.timeTxtVw = (TextView) view.findViewById(R.id.time);
		photoItem.distanceTxtVu = (TextView) view
				.findViewById(R.id.tv_distance);
		photoItem.likeRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.praise);
		photoItem.retweetRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.transmit);
		photoItem.commRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.comment);

	}
	private void findMoodView(View view) {

		moodItem.commentTxtVw = (TextView) view.findViewById(R.id.commentNum);
		moodItem.contentTxtVw = (TextView) view.findViewById(R.id.content);
		moodItem.headImgBtn = (ImageButton) view.findViewById(R.id.head);
		moodItem.sexImgVu = (ImageView) view.findViewById(R.id.sex);
		moodItem.likeTxtVw = (TextView) view.findViewById(R.id.likeNum);
		moodItem.nickNameTxtVw = (TextView) view.findViewById(R.id.nickname);
		moodItem.timeTxtVw = (TextView) view.findViewById(R.id.time);
		moodItem.distanceTxtVu = (TextView) view
				.findViewById(R.id.tv_distance);
		moodItem.likeRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.praise);
		moodItem.commRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.comment);
		moodItem.flowerRelativeLayout = (RelativeLayout) view.findViewById(R.id.flower);
		moodItem.flowerTxtVu = (TextView) view.findViewById(R.id.flowernum);
	}

	class PhotoLikeTask {

		private int beSendManId;
		private int myUserId;
		private int photoId;

		public PhotoLikeTask(int beSendManId, int photoId) {
			this.beSendManId = beSendManId;
			this.photoId = photoId;
		}

		public void start() {
			exeute();
		}

		public void exeute() {
			myUserId = new SPHelper(context).GetUserId();

			new Thread() {
				public void run() {
					Message message = handler.obtainMessage();
					if (myUserId == beSendManId) {
						message.what = Constant.PHOTO_LIKE_MYSELF;
						message.sendToTarget();
						return;
					}
					PhotoLike photoLike = null;
					try {
						photoLike = DbUtil.getDbUtils(context)
								.findFirst(
										Selector.from(PhotoLike.class)
												.where(PhotoLike.PHOTOID, "=",
														photoId)
												.where(PhotoLike.USERID, "=",
														myUserId));
					} catch (DbException e1) {
						e1.printStackTrace();
					}
					if (photoLike != null) {
						message.what = Constant.PHOTO_LIKE_ALREADY;
						message.sendToTarget();
						return;
					}

					TakePhotosPraise takePhotosPraise = new TakePhotosPraise();
					takePhotosPraise.setPraisedUserId(beSendManId);
					takePhotosPraise.setUserId(myUserId);
					takePhotosPraise.setPhotoId(photoId);
					try {
						ServerCommunication.request(takePhotosPraise,
								URLConstant.LIKE_PHOTOS_URL);
					} catch (PostException e) {
						e.printStackTrace();
						message.what = Constant.CONNECT_FAILED;
						message.sendToTarget();
						return;
					}

					PhotoLike photoLike2 = new PhotoLike();
					photoLike2.setPhotoId(photoId);
					photoLike2.setUserId(myUserId);
					try {
						DbUtil.getDbUtils(context).save(photoLike2);
					} catch (DbException e) {
						e.printStackTrace();
					}

					message.what = Constant.PHOTO_LIKE_SUCCESS;
					message.sendToTarget();

				};
			}.start();

		}

	}

	class PhotoItem {
		public ImageButton headImgBtn;
		public ImageView sexImgVu;
		public TextView nickNameTxtVw;
		public ImageView photoImgVw;
		public TextView contentTxtVw;
		public TextView likeTxtVw;
		public TextView retweetTxtVw;
		public TextView commentTxtVw;
		public TextView timeTxtVw;
		public TextView atNameTxtVw;
		public RelativeLayout likeRelativeLayout;
		public RelativeLayout retweetRelativeLayout;
		public RelativeLayout commRelativeLayout;
		public TextView distanceTxtVu;
	}

	class MoodItem {
		public ImageButton headImgBtn;
		public ImageView sexImgVu;
		public TextView nickNameTxtVw;
		public TextView contentTxtVw;
		public TextView likeTxtVw;
		public TextView commentTxtVw;
		public TextView timeTxtVw;
		public RelativeLayout likeRelativeLayout;
		public RelativeLayout commRelativeLayout;
		public TextView distanceTxtVu;
		public TextView flowerTxtVu;
		public RelativeLayout flowerRelativeLayout;
	}
	
	class PourItem {
		ImageView bgPic;
		TextView content;
		TextView numOfCommit;
		ImageView sexImgVu;
		TextView distanceTxtVu;
		TextView timeTxtVu;
	}

}
