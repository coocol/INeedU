package com.eethan.ineedu.primaryactivity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.baidu.location.al;
import com.eethan.ineedu.CommonUse.GiveHeartEvent;
import com.eethan.ineedu.CommonUse.MyIntent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.AlbumGridAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Album;
import com.eethan.ineedu.databasebeans.User;
import com.eethan.ineedu.databasebeans.UserDescInfo;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.RegisterJsonObject;
import com.eethan.ineedu.jackson.UserDetailJsonObject;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.secondaryactivity.BigHeadActivity;
import com.eethan.ineedu.secondaryactivity.BigPhotoActivity;
import com.eethan.ineedu.secondaryactivity.EditMyInformationActivity;
import com.eethan.ineedu.secondaryactivity.FlowerFansActivity;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.ToastHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonInformationActivity extends BaseActivity {
	ImageButton back;
	ImageButton giveHeart;
	ImageButton privateLetter;

	private TextView usernameonhead;
	private ImageView headPic;
	private TextView username;
	private TextView loveNUM;
	private TextView popularityNUM;
	private TextView realname;
	private ImageView sex;
	private TextView signature;
	private TextView school;
	private TextView major;
	private TextView email;
	private TextView allNeedsTextView;
	private ImageButton allNeedImageButton;
	private TextView fansTextView;
	private TextView timedisTxtVu;
	
	private TextView likefoodTxtVu;
	private TextView likefilmTxtVu;
	private TextView likemusicTxtVu;
	private TextView likebookTxtVu;
	private TextView likeplaceTxtVu;
	private TextView hometownTxtVu;
	private TextView birthTxtVu;
	private TextView nowatTxtVu;
	private TextView marriageTxtVu;
	private TextView likegiftTxtVu;
	private TextView describeTxtVu;
	private TextView diplomaTxtVu;

	private GridView gridView;
	private AlbumGridAdapter adapter;
	private ArrayList<Album> albums;

	private int userId;// this is myself id

	private int ownerid;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private ToastHandler toastHandler = new ToastHandler(
			PersonInformationActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("PersonInfomationActivity");
		setContentView(R.layout.information_for_others_page);
		loadingDialogShow();
		userId = Integer.parseInt(getIntent().getStringExtra("userId"));

		back = (ImageButton) findViewById(R.id.pour_listen_issue_page_back_button);
		giveHeart = (ImageButton) findViewById(R.id.information_for_others_page_giveheart_button);
		privateLetter = (ImageButton) findViewById(R.id.information_for_others_page_private_letter_button);

		gridView = (GridView) findViewById(R.id.gv_photo);
		RelativeLayout.LayoutParams linearParams = (android.widget.RelativeLayout.LayoutParams) gridView
				.getLayoutParams(); // 取控件mGrid当前的布局参数
		linearParams.height = 0;// 当控件的高强制设成75象素
		gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
		albums = new ArrayList<Album>();
		adapter = new AlbumGridAdapter(PersonInformationActivity.this, albums,
				imageLoader, false);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(PersonInformationActivity.this,
						BigPhotoActivity.class);
				intent.putExtra("imgUrl", albums.get((int) arg3).getPhotoUrl());
				startActivity(intent);

			}
		});

		usernameonhead = (TextView) findViewById(R.id.information_for_others_page_username);
		headPic = (ImageView) findViewById(R.id.information_for_me_page_headpic);
		timedisTxtVu = (TextView) findViewById(R.id.tv_time_dis);
		username = (TextView) findViewById(R.id.information_for_me_page_username);
		loveNUM = (TextView) findViewById(R.id.information_for_me_page_numofaid);
		popularityNUM = (TextView) findViewById(R.id.information_for_me_page_value);
		realname = (TextView) findViewById(R.id.information_for_me_page_realname);
		sex = (ImageView) findViewById(R.id.information_for_me_page_sex);
		signature = (TextView) findViewById(R.id.tv_signature);
		school = (TextView) findViewById(R.id.information_for_me_page_school);
		major = (TextView) findViewById(R.id.information_for_me_page_major);
		email = (TextView) findViewById(R.id.information_for_me_page_mail);
		
		likefoodTxtVu = (TextView) findViewById(R.id.tv_food);
		likebookTxtVu = (TextView) findViewById(R.id.tv_book);
		likegiftTxtVu = (TextView) findViewById(R.id.tv_gift);
		likeplaceTxtVu = (TextView) findViewById(R.id.tv_place);
		likemusicTxtVu = (TextView) findViewById(R.id.tv_music);
		birthTxtVu = (TextView) findViewById(R.id.tv_birthday);
		likefilmTxtVu = (TextView) findViewById(R.id.tv_film);
		describeTxtVu = (TextView) findViewById(R.id.tv_describe);
		hometownTxtVu = (TextView) findViewById(R.id.tv_hometown);
		nowatTxtVu = (TextView) findViewById(R.id.tv_city);
		marriageTxtVu = (TextView) findViewById(R.id.tv_love);

		allNeedImageButton = (ImageButton) findViewById(R.id.information_for_me_page_allneeds_button);
		allNeedsTextView = (TextView) findViewById(R.id.information_for_me_page_allneeds_textview);
		fansTextView = (TextView) findViewById(R.id.information_for_me_page_fans_textview);
		fansTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("ownerid", userId);
				intent.setClass(PersonInformationActivity.this,
						FlowerFansActivity.class);
				startActivity(intent);
			}
		});
		new GetDataTask().execute();

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				PersonInformationActivity.this.finish();
			}
		});
		headPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(PersonInformationActivity.this,
						BigHeadActivity.class);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});
		giveHeart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int thisManUserId = userId;
				GiveHeartEvent giveHeartEvent = new GiveHeartEvent(
						getContext(), thisManUserId, -1);
				giveHeartEvent.IsDaily();
				giveHeartEvent.start();
			}
		});

		allNeedImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PersonInformationActivity.this,
						AllNeedsActivity.class);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});
		allNeedsTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PersonInformationActivity.this,
						AllNeedsActivity.class);
				intent.putExtra("userId", userId);
				startActivity(intent);
			}
		});

	}

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {

			// 后台获取数据
			if (userId == -1) {
				PersonInformationActivity.this.finish();
			}

			String response;
			try {
				response = ServerCommunication.requestWithoutEncrypt(userId,
						URLConstant.GET_USER_DETAIL_INFO_NEW_URL);
				UserDetailJsonObject AllMyInfo = JacksonUtil.json()
						.fromJsonToObject(response, UserDetailJsonObject.class);
				return AllMyInfo;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object result) {
			if (result == null) {
				MyToast.showToast(getContext(), "加载数据失败");
				loadingDialogDismiss();
				return;
			}
			if (result.toString().equals("")
					|| result.toString().startsWith("解析")) {
				MyToast.showToast(getContext(), "该用户尚未完善资料");
				loadingDialogDismiss();
				// finish();
				return;
			}
			if (!ServerCommunication.checkResult(getContext(), result))// 各种网络异常的处理部分
			{
				MyToast.showToast(getContext(), (String) result);
				loadingDialogDismiss();
				return;
			}
			UserDetailJsonObject myDatilInfoJsonObject = (UserDetailJsonObject) result;
			if (myDatilInfoJsonObject == null) {
				MyToast.showToast(PersonInformationActivity.this,
						"连接服务器失败,请稍后再试...");
				PersonInformationActivity.this.finish();
				return;
			}

			User me = myDatilInfoJsonObject.getUser();
			final UserInfo myInfo = myDatilInfoJsonObject.getUserInfo();
			UserDetailInfo myDetailInfo = myDatilInfoJsonObject
					.getUserDetailInfo();
			UserDescInfo myUserDescInfo = myDatilInfoJsonObject.getUserDescInfo();
			
			if (myDatilInfoJsonObject.getAlbums() == null) {
				albums = new ArrayList<Album>();
			} else {
				albums.clear();
				for (int i = 0; i < myDatilInfoJsonObject.getAlbums().size(); i++) {
					Album thealbum = new Album();
					thealbum.setUserId(myDatilInfoJsonObject.getAlbums().get(i)
							.getUserId());
					thealbum.setId(myDatilInfoJsonObject.getAlbums().get(i)
							.getId());
					thealbum.setPhotoUrl(myDatilInfoJsonObject.getAlbums()
							.get(i).getPhotoUrl());
					albums.add(thealbum);
				}
			}
			int height = 0;
			if (albums.size() == 0) {
				height = 0;
			} else if (albums.size() <= 4) {
				height = 200;
			} else if (albums.size() > 4) {
				height = 400;
			}

			RelativeLayout.LayoutParams linearParams = (android.widget.RelativeLayout.LayoutParams) gridView
					.getLayoutParams(); // 取控件mGrid当前的布局参数
			linearParams.height = height;// 当控件的高强制设成75象素
			gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2
			adapter.notifyDataSetChanged();
			// UserLocation myLocation=myDatilInfoJsonObject.getUserLocation();

			final String myemail = me.getEmail();

			final String mynickName = myInfo.getNickName();
			final String myrealName = myInfo.getRealName();
			final String mysex = myInfo.getSex();
			final int myloveNum = myInfo.getLoveNum();
			final int mypopularityNum = myInfo.getPopularityNum();

			final String mysignature;
			final String myschool;
			final String myacademy;
			final String mygrade;
			if (myDetailInfo.getSignature() == null) {
				mysignature = "还未填写";
			} else if (myDetailInfo.getSignature().equals("")) {
				mysignature = myDetailInfo.getSignature();// maybe it is null
			} else {
				mysignature = myDetailInfo.getSignature();// maybe it is null
			}
			if (myDetailInfo.getSchool().equals("")) {
				myschool = "还未填写";
			} else {
				myschool = myDetailInfo.getSchool();
			}
			if (myDetailInfo.getAcademy().equals("")) {
				myacademy = "还未填写";
			} else {
				myacademy = myDetailInfo.getAcademy();
			}
			if (myDetailInfo.getGrade().equals("")) {
				mygrade = "还未填写";
			} else {
				mygrade = myDetailInfo.getGrade();
			}
			if(myUserDescInfo!=null){
				likebookTxtVu.setText(myUserDescInfo.getLikebook());
				likefilmTxtVu.setText(myUserDescInfo.getLikefilm());
				likegiftTxtVu.setText(myUserDescInfo.getLikegift());
				likemusicTxtVu.setText(myUserDescInfo.getLikemusic());
				likeplaceTxtVu.setText(myUserDescInfo.getLikeplace());
				birthTxtVu.setText(myUserDescInfo.getBirthday());
				hometownTxtVu.setText(myUserDescInfo.getHometown());
				describeTxtVu.setText(myUserDescInfo.getDescription());
				marriageTxtVu.setText(myUserDescInfo.getEmotion());
				likefoodTxtVu.setText(myUserDescInfo.getLikefood());
			}
			imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL + me.getId()
					+ ".png", headPic, getHeadDisplayOption());

			usernameonhead.setText(mynickName + "的资料");
			String distanceString = DataTraslator
					.DistanceToString(DataTraslator.GetDistanceToMe(
							PersonInformationActivity.this,
							myDatilInfoJsonObject.getUserLocation().getLat(),
							myDatilInfoJsonObject.getUserLocation().getLng()));
			String lastLogString = DataTraslator
					.LongToTimePastGeneral(myDatilInfoJsonObject
							.getUserLocation().getTime());
			username.setText(mynickName);

			timedisTxtVu.setText(lastLogString + "  " + distanceString);

			realname.setText(myrealName);
			if (mysex.equals("女")) {
				loveNUM.setText(Constant.GODNESS_VALUE_STRING + myloveNum);
				sex.setImageResource(R.drawable.sex_girl_press);
				popularityNUM.setText(+mypopularityNum + "人给她送花");
			} else {
				sex.setImageResource(R.drawable.sex_boy_press);
				loveNUM.setText(Constant.GOD_VALUE_STRING + myloveNum);
				popularityNUM.setText(+mypopularityNum + "人给他送花");
			}
			signature.setText(mysignature);
			school.setText(myschool);
			if (myDetailInfo.getAcademy().equals("")
					&& myDetailInfo.getGrade().equals(""))
				major.setText("还未填写");
			else
				major.setText(myacademy + "," + mygrade);
			// email.setText(myemail);

			privateLetter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					int userId = new SPHelper(getContext()).GetUserId();
					int friendId = myInfo.getUserId();
					String friendName = myrealName;
					MyIntent.toChatActivity(getContext(), userId, friendId,
							friendName, false);
				}
			});

			loadingDialogDismiss();
			super.onPostExecute(result);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
//		new GetDataTask().execute();

	}

}
