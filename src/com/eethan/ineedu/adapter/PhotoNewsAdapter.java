package com.eethan.ineedu.adapter;

import java.util.ArrayList;
import java.util.List;

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

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.PhotoLikeEvent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.databasebeans.TakePhotos;
import com.eethan.ineedu.databasebeans.TakePhotosPraise;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.model.PhotoLike;
import com.eethan.ineedu.model.PhotoNewsModel;
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

public class PhotoNewsAdapter extends BaseAdapter {

	private Context context;
	private List<PhotoNewsModel> newsList = new ArrayList<PhotoNewsModel>();
	// private ImageLoader imageLoader = ImageLoader.getInstance();
	// private ImageLoader imageLoader_head = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayImageOptions options_head;
	private PhotoNewsItem item;

	private ImageLoader imageLoader;

	private Handler handler;

	private int gloablePhotoId;

	private int myUserId;

	public PhotoNewsAdapter(Context con, List<PhotoNewsModel> list,
			ImageLoader imageLoadert) {
		this.context = con;
		this.newsList = list;
		this.imageLoader = imageLoadert;

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
				switch (msg.what) {
				case Constant.PHOTO_LIKE_MYSELF:
					MyToast.showToast(context, "不能赞自己哦~");
					break;
				case Constant.PHOTO_LIKE_ALREADY:
					MyToast.showToast(context, "已经赞过了~");
					break;
				case Constant.PHOTO_LIKE_SUCCESS:
					if (item != null && item.likeTxtVw != null) {
						try {
							int num = Integer.parseInt(item.likeTxtVw.getText()
									.toString()) + 1;
							item.likeTxtVw.setText(String.valueOf(num + 1));
						} catch (Exception e) {
						}
					}
					MyToast.showToast(context, "赞 +1~");
					break;
				case Constant.CONNECT_FAILED:
					MyToast.showToast(context, "抱歉，出了点 错:(");
					break;
				default:
					break;
				}
			}
		};

	}

	@Override
	public int getCount() {
		return newsList.size();
	}

	@Override
	public Object getItem(int position) {
		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		TakePhotos takePhotos = newsList.get(position).getTakePhotos();
		UserInfo ownerInfo = newsList.get(position).getOwnerInfo();
		UserInfo atInfo = newsList.get(position).getAtInfo();
		
		if (view == null) {
			item = new PhotoNewsItem();
			view = LayoutInflater.from(context).inflate(
					R.layout.takephotos_main_item, parent, false);
			item.atNameTxtVw = (TextView) view.findViewById(R.id.at_nickname);
			item.commentTxtVw = (TextView) view.findViewById(R.id.commentNum);
			item.contentTxtVw = (TextView) view.findViewById(R.id.content);
			item.headImgBtn = (ImageButton) view.findViewById(R.id.head);
			item.sexImgVu = (ImageView) view.findViewById(R.id.sex);
			item.likeTxtVw = (TextView) view.findViewById(R.id.likeNum);
			item.nickNameTxtVw = (TextView) view.findViewById(R.id.nickname);
			item.photoImgVw = (ImageView) view.findViewById(R.id.photo);
			item.retweetTxtVw = (TextView) view.findViewById(R.id.transmitNum);
			item.timeTxtVw = (TextView) view.findViewById(R.id.time);
			item.distanceTxtVu = (TextView) view.findViewById(R.id.tv_distance);
			item.likeRelativeLayout = (RelativeLayout) view
					.findViewById(R.id.praise);
			item.retweetRelativeLayout = (RelativeLayout) view
					.findViewById(R.id.transmit);
			item.commRelativeLayout = (RelativeLayout) view
					.findViewById(R.id.comment);

			view.setTag(item);

		} else {
			item = (PhotoNewsItem) view.getTag();
		}

		item.nickNameTxtVw.setText(ownerInfo.getNickName()+"  "+newsList.get(position).getOwnerDetailInfo().getAcademy());
		String readable_time = DataTraslator.LongToTimePastGeneral(Long
				.parseLong(takePhotos.getTime()));
		item.timeTxtVw.setText(readable_time);
		item.atNameTxtVw.setText("");
		if (atInfo.getUserId() > 0) {
			item.atNameTxtVw.setText("   转自@" + atInfo.getNickName());
		}
		if (ownerInfo.getSex().equals("女"))
			item.sexImgVu.setImageResource(R.drawable.sex_girl_press);
		else {
			item.sexImgVu.setImageResource(R.drawable.sex_boy_press);
		}
		String contentString = takePhotos.getContent();
		item.contentTxtVw.setText(contentString);
		item.commentTxtVw.setText("");
		item.likeTxtVw.setText("");
		item.retweetTxtVw.setText("");
		item.commentTxtVw.setText(String.valueOf(takePhotos.getCommentNum()));
		item.likeTxtVw.setText(String.valueOf(takePhotos.getPraiseNum()));
		item.retweetTxtVw.setText(String.valueOf(takePhotos.getTransmitNum()));
		item.distanceTxtVu.setText(DataTraslator.DistanceToString(DataTraslator
				.GetDistanceToMe(context, newsList.get(position)
						.getTakePhotos().getLat(), newsList.get(position)
						.getTakePhotos().getLng())));
		imageLoader.displayImage(
				newsList.get(position).getTakePhotos().imageUrl,
				item.photoImgVw, options);
		imageLoader.displayImage(
				URLConstant.BIG_HEAD_PIC_URL + ownerInfo.getUserId() + ".png",
				item.headImgBtn, options_head);
		item.headImgBtn
				.setOnClickListener(new AdapterOnClickListener(position) {
					@Override
					public void onClick(View arg0) {
						UserInfo owneruserInfos = newsList.get(getPosition())
								.getOwnerInfo();
						if (owneruserInfos != null
								&& owneruserInfos.getUserId() > 0) {
							new HeadClickEvent(context, owneruserInfos
									.getUserId()).click();
						}
					}
				});
		item.atNameTxtVw
				.setOnClickListener(new AdapterOnClickListener(position) {
					@Override
					public void onClick(View arg0) {
						UserInfo atuserInfos = newsList.get(getPosition())
								.getAtInfo();
						if (atuserInfos != null && atuserInfos.getUserId() > 0) {
							new HeadClickEvent(context, atuserInfos.getUserId())
									.click();
						}
					}
				});
		item.likeRelativeLayout.setOnClickListener(new AdapterOnClickListener(
				position) {
			@Override
			public void onClick(View arg0) {
				new PhotoLikeEvent(context, newsList.get(getPosition())
						.getOwnerInfo().getUserId(), newsList
						.get(getPosition()).getTakePhotos().getId(),
						PhotoNewsAdapter.this, newsList, getPosition()).start();
			}
		});
		item.retweetRelativeLayout
				.setOnClickListener(new AdapterOnClickListener(position) {
					@Override
					public void onClick(View arg0) {
						new CommentUtil(context, getPosition(), -1, newsList,
								PhotoNewsAdapter.this,
								Constant.TRANSMIT_PHOTONEWS);

					}
				});
		item.commRelativeLayout.setOnClickListener(new AdapterOnClickListener(
				position) {
			@Override
			public void onClick(View arg0) {
				new CommentUtil(context, getPosition(), -1, newsList,
						PhotoNewsAdapter.this, Constant.COMMENT_PHOTONEWS);
			}
		});

		return view;

	}

	class PhotoNewsItem {
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

}
