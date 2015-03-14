package com.eethan.ineedu.primaryactivity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.PhotoLikeEvent;
import com.eethan.ineedu.adapter.PhotoNewsDetailsAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.TakePhotos;
import com.eethan.ineedu.databasebeans.TakePhotosComment;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.jackson.DetailPhotosJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.model.PhotoNewsDetailsModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.mycontrol.ScrollListView;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.secondaryactivity.BigPhotoActivity;
import com.eethan.ineedu.util.CommentUtil;
import com.eethan.ineedu.util.DataTraslator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class PhotoNewsDetailActivity extends BaseActivity {

	private ImageButton backButton;
	private ImageButton commButton;
	private ImageButton ownerHeadImgBtn;
	private ImageView ownerSexImfVu;
	private TextView atNameTxtVu;
	private TextView timetxtVu;
	private TextView ownerNameTxtVu;
	private ImageView photoImgVu;
	private TextView contentTxtVu;
	private ImageButton likeImgBtn;
	private ImageButton transmitImgBtn;
	private TextView distanceTxtVu;

	public static List<PhotoNewsDetailsModel> commList;
	private ScrollListView commListView;
	private PhotoNewsDetailsAdapter adapter;

	public static boolean isScrollToBottom = false;

	private UserInfo ownerUserInfo;
	private UserInfo atUserInfo;
	private TakePhotos takePhotos;
	private ArrayList<UserInfo> commUserLists;
	private ArrayList<UserLocation> commUserLocationLists;
	private ArrayList<TakePhotosComment> commLists;

	private int userId;
	private int photoId;

	private boolean isFirstJumpToHere = true;

	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private DisplayImageOptions options_head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.takephotos_detail);

		userId = getIntent().getIntExtra("userId", -1);
		photoId = getIntent().getIntExtra("photoId", -1);

		options = new DisplayImageOptions.Builder()
				// .showStubImage(R.drawable.ic_stub_horizon)
				// .showImageForEmptyUri(R.drawable.ic_error_horizon)
				// .showImageOnFail(R.drawable.ic_error_horizon)
				.cacheInMemory(true).cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		options_head = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.logo) // 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.logo) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new CircleBitmapDisplayer()) // 设置成圆角图片
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

		ownerHeadImgBtn = (ImageButton) findViewById(R.id.head);
		ownerSexImfVu = (ImageView) findViewById(R.id.sex);
		ownerNameTxtVu = (TextView) findViewById(R.id.nickname);
		atNameTxtVu = (TextView) findViewById(R.id.at_nickname);
		timetxtVu = (TextView) findViewById(R.id.time);
		photoImgVu = (ImageView) findViewById(R.id.photo);
		contentTxtVu = (TextView) findViewById(R.id.content);
		transmitImgBtn = (ImageButton) findViewById(R.id.bt_transmit);
		likeImgBtn = (ImageButton) findViewById(R.id.bt_praise);
		commListView = (ScrollListView) findViewById(R.id.lv_comment);
		backButton = (ImageButton) findViewById(R.id.photonews_imgbt_back);
		commButton = (ImageButton) findViewById(R.id.bt_comment);
		distanceTxtVu = (TextView)findViewById(R.id.tv_distance);
		commList = new ArrayList<PhotoNewsDetailsModel>();
		adapter = new PhotoNewsDetailsAdapter(getApplicationContext(), commList,userId);
		commListView.setAdapter(adapter);

		ownerHeadImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (ownerUserInfo != null && ownerUserInfo.getUserId() > 0) {
					new HeadClickEvent(getApplicationContext(), ownerUserInfo
							.getUserId()).click();
				}
			}
		});
		atNameTxtVu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (atUserInfo != null && atUserInfo.getUserId() > 0) {
					new HeadClickEvent(getApplicationContext(), atUserInfo
							.getUserId()).click();
				}
			}
		});
		ownerNameTxtVu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(),
						PersonInformationActivity.class);
				intent.putExtra("userId", ownerUserInfo.getUserId());
				startActivity(intent);
			}
		});
		photoImgVu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(PhotoNewsDetailActivity.this,
						BigPhotoActivity.class);
				intent.putExtra("imgUrl", takePhotos.imageUrl);
				startActivity(intent);
			}
		});
		commButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new CommentUtil(PhotoNewsDetailActivity.this, takePhotos
						.getId(), -1, commLists, adapter,
						Constant.COMMENT_PHOTONEWS_DETAIL);
			}
		});
		likeImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new PhotoLikeEvent(PhotoNewsDetailActivity.this, ownerUserInfo
						.getUserId(), takePhotos.getId()).start();
			}
		});
		transmitImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new CommentUtil(PhotoNewsDetailActivity.this, takePhotos
						.getId(), -1, null, Constant.TRANSMIT_PHOTONEWS_DETAIL,takePhotos.getContent());
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		commListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int tarUserId = commList.get((int) arg3).getUserInfo()
						.getUserId();
					new CommentUtil(PhotoNewsDetailActivity.this, takePhotos
							.getId(), tarUserId, commLists, adapter,
							Constant.COMMENT_PHOTONEWS_DETAIL);
//				}

			}
		});

		if (isFirstJumpToHere) {
			loadingDialogShow();
		}

		new GetDataTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		isFirstJumpToHere = true;
	};

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {

			String response = null, URL = URLConstant.DETAIL_PHOTOS_URL;
			try {
				response = ServerCommunication.request(photoId, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			DetailPhotosJsonObject refreshPR;
			try {
				refreshPR = JacksonUtil.json().fromJsonToObject(response,
						DetailPhotosJsonObject.class);
			} catch (Exception e) {
				return "fail";
			}

			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {

			if (result==null || result.equals("fail")) {
				MyToast.showToast(PhotoNewsDetailActivity.this, "加载失败");
				loadingDialogDismiss();
				return;
			}

			if (!ServerCommunication.checkResult(PhotoNewsDetailActivity.this,
					result)) {
				MyToast.showToast(PhotoNewsDetailActivity.this, (String) result);
				loadingDialogDismiss();
				return;
			}

			DetailPhotosJsonObject res = (DetailPhotosJsonObject) result;

			ownerUserInfo = res.getMyUserInfo();
			atUserInfo = res.getAtWho();
			takePhotos = res.getTakePhotos();
			commLists = (ArrayList<TakePhotosComment>) res.getPhotosComments();
			commUserLists = (ArrayList<UserInfo>) res.getCommentUserInfos();
			commUserLocationLists = (ArrayList<UserLocation>) res.getCommentUserLocations();
			if (ownerUserInfo != null) {
				imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL
						+ ownerUserInfo.getUserId() + ".png", ownerHeadImgBtn,
						options_head);
				ownerNameTxtVu.setText(ownerUserInfo.getNickName()+"  "+res.getUserDetailInfo().getAcademy());
				if (ownerUserInfo.getSex().equals("女"))
					ownerSexImfVu.setImageResource(R.drawable.sex_girl_press);
				else {
					ownerSexImfVu.setImageResource(R.drawable.sex_boy_press);
				}
			}
			String readable_time = DataTraslator.LongToTimePastGeneral(Long
					.parseLong(takePhotos.getTime()));
			timetxtVu.setText(readable_time);
			atNameTxtVu.setText("");
			distanceTxtVu.setText(DataTraslator.DistanceToString(DataTraslator.GetDistanceToMe(PhotoNewsDetailActivity.this, takePhotos.getLat(), takePhotos.getLng())));
			if (atUserInfo.getUserId() > 0) {
				atNameTxtVu.setText("   转自@" + atUserInfo.getNickName());
			}
			if (takePhotos != null) {
				contentTxtVu.setText(takePhotos.getContent());
				imageLoader.displayImage(takePhotos.imageUrl, photoImgVu,
						options);
			}
			commList.clear();
			int num = commLists.size();
			PhotoNewsDetailsModel model = null;
			int curUserId;
			for (int i = 0; i < num; i++) {

				model = new PhotoNewsDetailsModel();
				model.setTakePhotosComment(commLists.get(i));
				model.setUserInfo(commUserLists.get(i));
				model.setUserLocation(commUserLocationLists.get(i));
				commList.add(model);

			}

			if (isFirstJumpToHere) {
				loadingDialogDismiss();
			}
			isFirstJumpToHere = false;

			adapter.notifyDataSetChanged();
			final ScrollView scrollView = (ScrollView) findViewById(R.id.photonews_details_scroll);
			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					scrollView.fullScroll(ScrollView.FOCUS_UP);
				}
			});
			super.onPostExecute(result);
		}
	}

}
