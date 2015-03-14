package com.eethan.ineedu.secondaryactivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.AlbumGridAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Album;
import com.eethan.ineedu.databasebeans.City;
import com.eethan.ineedu.databasebeans.College;
import com.eethan.ineedu.databasebeans.Province;
import com.eethan.ineedu.databasebeans.School;
import com.eethan.ineedu.databasebeans.User;
import com.eethan.ineedu.databasebeans.UserDescInfo;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.ProCityColSchJsonObject;
import com.eethan.ineedu.jackson.UserDetailJsonObject;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.MyInformationActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.BitmapSaver;
import com.eethan.ineedu.util.DataCleanUtil;
import com.eethan.ineedu.util.UploadHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EditMyInformationActivity extends BaseActivity implements
		OnClickListener {
	ImageButton headPic;
	ImageButton pageBack;
	EditText nickname;
	TextView password;
	TextView tele;
	EditText email;
	TextView signature;
	TextView school;
	TextView major;

	RelativeLayout head;
	RelativeLayout nick;
	RelativeLayout psd;
	RelativeLayout uni;
	RelativeLayout gra;

	private boolean issetCity = false;
	private boolean issetProvince = false;

	private static final int SET_HOMETOWN = 0;
	private static final int SET_COLLEGE = 1;
	private static final int SET_SCHOOL = 2;
	private static final int SET_PROVINCE = 3;
	private static final int SET_CITY = 4;
	private static final int SET_MARRIAGE = 5;
	private static final int SET_BIRTH = 6;
	private static final int SET_DIPLOMA = 7;
	private static final int SET_SEX = 8;
	private static final int SET_GRADE = 9;
	private static final int SET_PROVINCE_COLLEGE = 10;

	private ArrayList<Province> provinces;
	private List<String> provincesStrings;
	private ArrayList<City> cities;
	private List<String> citiesStrings;
	private ArrayList<College> colleges;
	private List<String> collegesStrings;
	private ArrayList<School> schools;
	private List<String> schoolsStrings;

	private TextView marriageTxtVu;
	private TextView birthTxtVu;
	private TextView hometownTxtVu;
	private TextView collegeCityTxtVu;
	private TextView schoolTxtVu;
	private EditText likeplaceTxtVu;
	private EditText likefoodTxtVu;
	private EditText likegiftTxtVu;
	private EditText likefilmTxtVu;
	private EditText likebookTxtVu;
	private EditText likemusicTxtVu;
	private EditText signatureEditTxt;
	private EditText describeEditTxt;
	private TextView diplomaTxtVu;
	private EditText realnameEditTxt;
	private TextView sexTxtVu;
	private TextView gradeTxtVu;
	private TextView collegeTxtVu;
	private RelativeLayout diplomaLayout;
	private RelativeLayout marriageLayout;
	private RelativeLayout hometownLayout;
	private RelativeLayout gradeLayout;
	private RelativeLayout sexLayout;
	private RelativeLayout birthLayout;
	private RelativeLayout collegeCityLayout;
	private RelativeLayout collegeLayout;
	private RelativeLayout schoolLayout;

	private TextView finishTxtVu;

	ImageView faceImage;
	File file;
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int RESULT_REQUEST_CODE = 2;
	private static final String TAG = "UploadActivity";
	private int id;

	private GridView gridView;
	private AlbumGridAdapter adapter;
	private ArrayList<Album> albums;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private UserDescInfo usdeDescInfo_commit = new UserDescInfo();
	private UserDetailInfo userDetailInfo_commit = new UserDetailInfo();
	private UserInfo userInfo_commit = new UserInfo();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("EditMyinfomationActivity");
		setContentView(R.layout.update_information_page);

		findView();

		Intent intent = getIntent();
		id = intent.getIntExtra("id", -1);
		obtainHead();

		gridView = (GridView) findViewById(R.id.gv_add_photo);
		RelativeLayout.LayoutParams linearParams = (android.widget.RelativeLayout.LayoutParams) gridView.getLayoutParams(); // 取控件mGrid当前的布局参数
		 linearParams.height = 0;// 当控件的高强制设成75象素
		 gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2 
		albums = new ArrayList<Album>();
		albums.add(new Album());
		adapter = new AlbumGridAdapter(EditMyInformationActivity.this, albums,
				imageLoader, true);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg3 != albums.size() - 1) {
					Intent intent = new Intent(EditMyInformationActivity.this,
							BigPhotoActivity.class);
					intent.putExtra("imgUrl", albums.get((int) arg3)
							.getPhotoUrl());
					startActivity(intent);
				}else if(arg3 == albums.size() - 1){
					Intent intent = new Intent(EditMyInformationActivity.this,UploadMyPhotoActivity.class);
					startActivity(intent);
				}
			}
		});
		
		loadingDialogShow();
		new GetDataTask().execute();

		/*
		 * nickname.setText(intent.getStringExtra("nickname"));
		 * password.setText(intent.getStringExtra("password"));
		 * tele.setText(intent.getStringExtra("tele"));
		 * email.setText(intent.getStringExtra("email"));
		 * signature.setText(intent.getStringExtra("signature"));
		 * school.setText(intent.getStringExtra("school"));
		 * major.setText(intent.
		 * getStringExtra("academy")+","+intent.getStringExtra("grade"));
		 */

		pageBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(EditMyInformationActivity.this,MyInformationActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
				startActivity(intent);
			}
		});
		finishTxtVu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loadingDialogShow();
				new CommitInfoTask().execute();
			}
		});
		/**
		 *  在这里写修改头像的方法入口
		 * 
		 */
		// headPic.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// DataCleanUtil.cleanInternalCache(getApplicationContext());
		// DataCleanUtil.cleanExternalCache(getApplicationContext());
		// Intent intentFromGallery = new Intent();
		// intentFromGallery.setType("image/*"); // 设置文件类型
		// intentFromGallery
		// .setAction(Intent.ACTION_GET_CONTENT);
		// startActivityForResult(intentFromGallery,
		// IMAGE_REQUEST_CODE);
		// }
		// });

		// nickname.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // // TODO Auto-generated method stub
		// // Intent intent = new Intent(getApplicationContext(),
		// // EditNickNameActivity.class);
		// // intent.putExtra("id", id);
		// // startActivity(intent);
		// }
		// });
		// password.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(getApplicationContext(),
		// EditPasswordActivity.class);
		// intent.putExtra("id", id);
		// startActivity(intent);
		// }
		// });
		
		marriageLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] itemsStrings = new String[] { "保密","单身", "求勾搭", "恋爱中","失恋了","已婚" };
				int pos = Arrays.asList(itemsStrings).indexOf(
						marriageTxtVu.getText().toString());
				if (pos < 0) {
					pos = 0;
				}
				showSingleSelectDialog("情感状况", itemsStrings, pos,
						marriageTxtVu, SET_MARRIAGE);
			}
		});
		diplomaLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] itemsStrings = new String[] { "本科在读", "硕士在读", "博士在读" };
				int pos = Arrays.asList(itemsStrings).indexOf(
						diplomaTxtVu.getText().toString());
				if (pos < 0) {
					pos = 0;
				}
				showSingleSelectDialog("选择学历", itemsStrings, pos, diplomaTxtVu,
						SET_DIPLOMA);
			}
		});
		sexLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] itemsStrings = new String[] { "男", "女" };
				int pos = Arrays.asList(itemsStrings).indexOf(
						sexTxtVu.getText().toString());
				if (pos < 0) {
					pos = 0;
				}
				showSingleSelectDialog("选择性别", itemsStrings, pos, sexTxtVu,
						SET_SEX);
			}
		});
		gradeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String[] itemsStrings = new String[] { "2014", "2013", "2012",
						"2011", "2010", "2009", "2008", "2007", "2006" };
				int pos = Arrays.asList(itemsStrings).indexOf(
						gradeTxtVu.getText().toString());
				if (pos < 0) {
					pos = 0;
				}
				showSingleSelectDialog("入学年份", itemsStrings, pos, gradeTxtVu,
						SET_GRADE);
			}
		});
		collegeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(provinces == null || provinces.size() == 0){
					MyToast.showToast(EditMyInformationActivity.this, "请先选择所在省份");
				}else{// if(colleges ==null || colleges.size()==0){
					int poscc = provincesStrings.indexOf(collegeCityTxtVu.getText().toString());
					if (poscc < 0) {
						poscc = 0;
					}
					loadingDialogShow();
					new GetProCityTask(Constant.GET_COLLEGE,provinces.get(poscc).getProvinceID() , SET_COLLEGE,
							collegeTxtVu).execute();
				}
//				}else {
//					int poss = collegesStrings.indexOf(collegeTxtVu.getText().toString());
//					if (poss < 0) {
//						poss = 0;
//					}
//					setCollegeString(collegeTxtVu.getText().toString());
//					showSingleSelectDialog(
//							"选择大学",
//							collegesStrings.toArray(new String[collegesStrings.size()]),
//							poss, collegeTxtVu, SET_COLLEGE);
//				}
			}
		});
		schoolLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(provinces == null || provinces.size() == 0){
					MyToast.showToast(EditMyInformationActivity.this, "请先选择所在省份");
				}else if(colleges ==null || colleges.size()==0){
					MyToast.showToast(EditMyInformationActivity.this, "请先选择所在大学");
				}else{/// if(schools ==null || schools.size()==0){
					int poss = collegesStrings.indexOf(collegeTxtVu.getText().toString());
					if (poss < 0) {
						poss = 0;
					}
					loadingDialogShow();
					new GetProCityTask(Constant.GET_SCHOOL, colleges.get(poss).getCoid(), SET_SCHOOL,
							schoolTxtVu).execute();
				}
//				}else{
//					int poss = schoolsStrings.indexOf(schoolTxtVu.getText().toString());
//					if (poss < 0) {
//						poss = 0;
//					}
//					setSchoolString(schoolTxtVu.getText().toString());
//					showSingleSelectDialog(
//							"选择院系",
//							schoolsStrings.toArray(new String[schoolsStrings.size()]),
//							poss, schoolTxtVu, SET_SCHOOL);
//				}
			}
		});
		collegeCityLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(provinces == null || provinces.size() == 0 || getCollprovinceString()==null){
					loadingDialogShow();
					new GetProCityTask(Constant.GET_PROVINCE, 0, SET_PROVINCE_COLLEGE,
							collegeCityTxtVu).execute();
				}else{
					int pos = provincesStrings.indexOf(collegeCityTxtVu.getText().toString());
					if (pos < 0) {
						pos = 0;
					}
					setCollprovinceString(collegeCityTxtVu.getText().toString());
					showSingleSelectDialog(
							"大学所在地",
							provincesStrings.toArray(new String[provincesStrings.size()]),
							pos, collegeCityTxtVu, SET_PROVINCE_COLLEGE);
				}
			}
		});
		birthLayout.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				Calendar calendar = Calendar.getInstance();
				DatePickerDialog datePickerDialog = new DatePickerDialog(
						EditMyInformationActivity.this,
						DatePickerDialog.THEME_HOLO_LIGHT,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker arg0, int arg1,
									int arg2, int arg3) {
								usdeDescInfo_commit.setBirthday(arg1 + "-"
										+ (arg2 + 1) + "-" + arg3);
								birthTxtVu.setText(usdeDescInfo_commit
										.getBirthday());
							}

						}, calendar.get(Calendar.YEAR), calendar
								.get(Calendar.MONTH), calendar
								.get(Calendar.DAY_OF_MONTH));
				DatePicker dp = datePickerDialog.getDatePicker();
				dp.setMaxDate(new Date().getTime());
				datePickerDialog.show();
			}
		});
		hometownLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(provinces == null || provinces.size() == 0 || cities==null || cities.size()==0){
					loadingDialogShow();
					new GetProCityTask(Constant.GET_PROVINCE, 0, SET_PROVINCE,
							hometownTxtVu).execute();
				}else{
					int pos = provincesStrings.indexOf(hometownTxtVu.getText().toString());
					if (pos < 0) {
						pos = 0;
					}
					setProvinceString(hometownTxtVu.getText().toString());
					showSingleSelectDialog(
							"选择省份",
							provincesStrings.toArray(new String[provincesStrings.size()]),
							pos, hometownTxtVu, SET_PROVINCE);
				}
			}
		});
		// tele.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// MyToast.showToast(EditMyInformationActivity.this,
		// "电话和邮箱暂时不能更改~");
		// }
		// });
		//
		// email.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// MyToast.showToast(EditMyInformationActivity.this,
		// "电话和邮箱暂时不能更改~");
		// }
		// });
		//
		// signature.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(getApplicationContext(),
		// EditSignatureActivity.class);
		// intent.putExtra("id", id);
		// startActivity(intent);
		// }
		// });

		// school.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(getApplicationContext(),
		// EditUniversityActivity.class);
		// intent.putExtra("id", id);
		// startActivity(intent);
		// }
		// });
		//
		// major.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// Intent intent = new Intent(getApplicationContext(),
		// EditAcademyActivity.class);
		// intent.putExtra("id", id);
		// startActivity(intent);
		// }
		// });

		

	}

	private void findView() {
		// TODO Auto-generated method stub
		pageBack = (ImageButton) findViewById(R.id.ib_back);
		headPic = (ImageButton) findViewById(R.id.ib_head);
		finishTxtVu = (TextView) findViewById(R.id.tv_finish);

		marriageTxtVu = (TextView) findViewById(R.id.tv_love);
		birthTxtVu = (TextView) findViewById(R.id.tv_birthday);
		hometownTxtVu = (TextView) findViewById(R.id.tv_hometown);
		likebookTxtVu = (EditText) findViewById(R.id.et_book);
		likefilmTxtVu = (EditText) findViewById(R.id.et_film);
		likefoodTxtVu = (EditText) findViewById(R.id.et_food);
		likegiftTxtVu = (EditText) findViewById(R.id.et_gift);
		likemusicTxtVu = (EditText) findViewById(R.id.et_music);
		likeplaceTxtVu = (EditText) findViewById(R.id.et_place);
		collegeCityTxtVu = (TextView) findViewById(R.id.tv_city);
		schoolTxtVu = (TextView) findViewById(R.id.tv_academy);
		diplomaTxtVu = (TextView) findViewById(R.id.tv_diploma);
		realnameEditTxt = (EditText) findViewById(R.id.et_realname);
		sexTxtVu = (TextView) findViewById(R.id.tv_sex);
		gradeTxtVu = (TextView) findViewById(R.id.tv_year);
		collegeTxtVu = (TextView) findViewById(R.id.tv_college);

		signatureEditTxt = (EditText) findViewById(R.id.et_signature);
		describeEditTxt = (EditText) findViewById(R.id.et_describe);

		nickname = (EditText) findViewById(R.id.et_nickname);
		password = (TextView) findViewById(R.id.tv_password);
		tele = (TextView) findViewById(R.id.tv_telephone);
		email = (EditText) findViewById(R.id.et_email);
		signature = (TextView) findViewById(R.id.tv_signature);
		school = (TextView) findViewById(R.id.tv_academy);
		major = (TextView) findViewById(R.id.tv_diploma);

		head = (RelativeLayout) findViewById(R.id.rl_head);
		nick = (RelativeLayout) findViewById(R.id.rl_nickname);
		psd = (RelativeLayout) findViewById(R.id.rl_password);
		uni = (RelativeLayout) findViewById(R.id.rl_college);
		gra = (RelativeLayout) findViewById(R.id.rl_academy);

		diplomaLayout = (RelativeLayout) findViewById(R.id.rl_diploma);
		marriageLayout = (RelativeLayout) findViewById(R.id.rl_love);
		hometownLayout = (RelativeLayout) findViewById(R.id.rl_hometown);
		gradeLayout = (RelativeLayout) findViewById(R.id.rl_year);
		sexLayout = (RelativeLayout) findViewById(R.id.rl_sex);
		birthLayout = (RelativeLayout) findViewById(R.id.rl_birthday);
		collegeCityLayout = (RelativeLayout) findViewById(R.id.rl_city);
		collegeLayout = (RelativeLayout) findViewById(R.id.rl_college);
		schoolLayout = (RelativeLayout) findViewById(R.id.rl_academy);
		//
		 head.setOnClickListener(this);
		// nick.setOnClickListener(this);
		// psd.setOnClickListener(this);
		// uni.setOnClickListener(this);
		// gra.setOnClickListener(this);

	}

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {
			// 后台获取数据

			SharedPreferences lightDB = getSharedPreferences(
					Constant.INEEDUSPR, 0);

			int id = lightDB.getInt(Constant.ID, -1);

			if (id == -1) {
				EditMyInformationActivity.this.finish();
			}

			String response;
			UserDetailJsonObject AllMyInfo = null;
			try {
				response = ServerCommunication.request(id,
						URLConstant.GET_USER_DETAIL_INFO_NEW_URL);
				AllMyInfo = JacksonUtil.json().fromJsonToObject(response,
						UserDetailJsonObject.class);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			return AllMyInfo;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result == null) {
				MyToast.showToast(getContext(), "加载失败");
				loadingDialogDismiss();
				super.onPostExecute(result);
				return;
			}
			if (!ServerCommunication.checkResult(getContext(), result)) {
				MyToast.showToast(getContext(), (String) result);
				loadingDialogDismiss();
				super.onPostExecute(result);
				return;
			}
			loadingDialogDismiss();
			UserDetailJsonObject myDatilInfoJsonObject = (UserDetailJsonObject) result;
			User me = myDatilInfoJsonObject.getUser();
			UserInfo myInfo = myDatilInfoJsonObject.getUserInfo();
			UserDetailInfo myDetailInfo = myDatilInfoJsonObject
					.getUserDetailInfo();
			UserDescInfo myuserDescInfo = myDatilInfoJsonObject
					.getUserDescInfo();
			if (myuserDescInfo != null) {
				likebookTxtVu.setText(myuserDescInfo.getLikebook());
				likefoodTxtVu.setText(myuserDescInfo.getLikefood());
				likegiftTxtVu.setText(myuserDescInfo.getLikegift());
				likeplaceTxtVu.setText(myuserDescInfo.getLikeplace());
				likefilmTxtVu.setText(myuserDescInfo.getLikefilm());
				likemusicTxtVu.setText(myuserDescInfo.getLikemusic());
				marriageTxtVu.setText(myuserDescInfo.getEmotion());
				hometownTxtVu.setText(myuserDescInfo.getHometown());
				describeEditTxt.setText(myuserDescInfo.getDescription());
				birthTxtVu.setText(myuserDescInfo.getBirthday());
				diplomaTxtVu.setText(myuserDescInfo.getDiploma());
			}
			// UserLocation myLocation=myDatilInfoJsonObject.getUserLocation();
			albums.clear();
			if (myDatilInfoJsonObject.getAlbums() == null) {
				albums = new ArrayList<Album>();
			} else {
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
			albums.add(new Album());
			int height = 0;
			if (albums.size() == 0) {
				height = 0;
			} else if (albums.size() <= 4) {
				height = 200;
			} else if (albums.size() > 4) {
				height = 400;
			}
			RelativeLayout.LayoutParams linearParams = (android.widget.RelativeLayout.LayoutParams) gridView.getLayoutParams(); // 取控件mGrid当前的布局参数
			 linearParams.height = height;// 当控件的高强制设成75象素
			 gridView.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件mGrid2 
			adapter.notifyDataSetChanged();

			final int myid = me.getId();
			final String mytele = me.getTele();
			final String mypassword = me.getPassword();
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
				mysignature = "未填写";
			} else if (myDetailInfo.getSignature().equals("")) {
				mysignature = myDetailInfo.getSignature();// maybe it is null
			} else {
				mysignature = myDetailInfo.getSignature();// maybe it is null
			}
			if (myDetailInfo.getSchool().equals("")) {
				myschool = "未填写";
			} else {
				myschool = myDetailInfo.getSchool();
			}
			if (myDetailInfo.getAcademy().equals("")) {
				myacademy = "未填写";
			} else {
				myacademy = myDetailInfo.getAcademy();
			}
			if (myDetailInfo.getGrade().equals("")) {
				mygrade = "未填写";
			} else {
				mygrade = myDetailInfo.getGrade();
			}

			nickname.setText(mynickName);
			password.setText(mypassword);
			tele.setText(mytele);
			signatureEditTxt.setText(mysignature);
			collegeTxtVu.setText(myschool);
			school.setText(myacademy);
			gradeTxtVu.setText(mygrade);
			email.setText(myemail);
			realnameEditTxt.setText(myrealName);
			sexTxtVu.setText(mysex);
			super.onPostExecute(result);

		}
	}

	/**
	 * obtain locate face image
	 */
	protected void obtainHead() {

		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL
				+ new SPHelper(getContext()).GetUserId() + ".png", headPic,
				getHeadDisplayOption());
	}

	/**
	 * onActvityResult method
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					try {
						getImageToView(data);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据为文件并上传、更改
	 * 
	 * @param picdata
	 * @throws Exception
	 */
	private void getImageToView(Intent data) throws Exception {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Intent intent = getIntent();
			final int id = intent.getIntExtra("id", -1);
			Bitmap photo = extras.getParcelable("data");
			String bigHeadName = "big_" + id + ".png";

			BitmapSaver.saveImage(photo, bigHeadName);
			file = new File("sdcard/DCIM/" + bigHeadName);
			new UploadHelper(
					new SPHelper(EditMyInformationActivity.this).GetUserId())
					.uploadBigHead(file);// 上传大头像

		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		obtainHead();
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
		new GetDataTask().execute();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.rl_head:

			Intent intentFromGallery = new Intent();
			intentFromGallery.setType("image/*"); // 设置文件类型
			intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
			DataCleanUtil.cleanInternalCache(getApplicationContext());
			DataCleanUtil.cleanExternalCache(getApplicationContext());
			break;
		// case R.id.re_layout_nick:
		// intent = new Intent(getApplicationContext(),
		// EditNickNameActivity.class);
		// intent.putExtra("id", id);
		// startActivity(intent);
		// break;
		// case R.id.re_layout_psd:
		// intent = new Intent(getApplicationContext(),
		// EditPasswordActivity.class);
		// intent.putExtra("id", id);
		// startActivity(intent);
		// break;
		// case R.id.re_layout_sig:
		// intent = new Intent(getApplicationContext(),
		// EditSignatureActivity.class);
		// intent.putExtra("id", id);
		// startActivity(intent);
		// break;
		// case R.id.re_layout_uni:
		// intent = new Intent(getApplicationContext(),
		// EditUniversityActivity.class);
		// intent.putExtra("id", id);
		// startActivity(intent);
		// break;
		// case R.id.re_layout_gra:
		// intent = new Intent(getApplicationContext(),
		// EditAcademyActivity.class);
		// intent.putExtra("id", id);
		// startActivity(intent);
		// break;

		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	private void showSingleSelectDialog(String title, final String[] items,
			final int pos, final TextView tarTxtVu, final int flag) {
		Builder dialog = new AlertDialog.Builder(
				EditMyInformationActivity.this, AlertDialog.THEME_HOLO_LIGHT);
		dialog.setTitle(title);
		switch (flag) {
		case SET_PROVINCE:
			setProvinceString(tarTxtVu.getText().toString());
			break;
		case SET_PROVINCE_COLLEGE:
			setCollprovinceString(tarTxtVu.getText().toString());
			break;
		case SET_CITY:
			setCityString(tarTxtVu.getText().toString());
			break;
		case SET_COLLEGE:
			setCollegeString(tarTxtVu.getText().toString());
			break;
		case SET_SCHOOL:
			setSchoolString(tarTxtVu.getText().toString());
			break;
		case SET_SEX:
			setSexString(tarTxtVu.getText().toString());
			break;
		case SET_DIPLOMA:
			setDiplomaString(tarTxtVu.getText().toString());
			break;
		case SET_GRADE:
			setGradeString(tarTxtVu.getText().toString());
			break;
		case SET_MARRIAGE:
			setMarriageString(tarTxtVu.getText().toString());
			break;
		default:
			break;
		}
		dialog.setSingleChoiceItems(items, pos,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						switch (flag) {
						case SET_PROVINCE:
							setProvinceString(items[arg1]);
							break;
						case SET_PROVINCE_COLLEGE:
							setCollprovinceString(items[arg1]);
							break;
						case SET_CITY:
							setCityString(items[arg1]);
							break;
						case SET_COLLEGE:
							setCollegeString(items[arg1]);
							break;
						case SET_SCHOOL:
							setSchoolString(items[arg1]);
							break;
						case SET_SEX:
							setSexString(items[arg1]);
							break;
						case SET_DIPLOMA:
							setDiplomaString(items[arg1]);
							break;
						case SET_GRADE:
							setGradeString(items[arg1]);
							break;
						case SET_MARRIAGE:
							setMarriageString(items[arg1]);
							break;
						default:
							break;
						}
					}
				});
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch (flag) {
				case SET_CITY:
					tarTxtVu.setText(tarTxtVu.getText() + " " + getCityString());
					break;
				case SET_COLLEGE:
					tarTxtVu.setText(getCollegeString());
					break;
				case SET_PROVINCE:
					tarTxtVu.setText(getProvinceString());
					int poss = provincesStrings.indexOf(hometownTxtVu.getText().toString());
					if (poss < 0) {
						poss = 0;
					}
					new GetProCityTask(Constant.GET_CITY, provinces.get(poss).getProvinceID(), SET_CITY, tarTxtVu).execute();
					break;
				case SET_PROVINCE_COLLEGE:
					tarTxtVu.setText(getCollprovinceString());
					break;
				case SET_DIPLOMA:
					tarTxtVu.setText(getDiplomaString());
					break;
				case SET_SEX:
					tarTxtVu.setText(getSexString());
					break;
				case SET_GRADE:
					tarTxtVu.setText(getGradeString());
					break;
				case SET_SCHOOL:
					tarTxtVu.setText(getSchoolString());
					break;
				case SET_MARRIAGE:
					tarTxtVu.setText(getMarriageString());
					break;
				default:
					break;
				}
			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				issetCity = false;
			}
		});
		dialog.show();

	}

	private void setNewInfo() {

		int myid = new SPHelper(getApplicationContext()).GetUserId();

		userInfo_commit.setNickName(nickname.getText().toString());
		userInfo_commit.setRealName(realnameEditTxt.getText().toString());
		userInfo_commit.setSex(sexTxtVu.getText().toString());
		userInfo_commit.setUserId(myid);

		userDetailInfo_commit.setAcademy(school.getText().toString());
		userDetailInfo_commit.setGrade(gradeTxtVu.getText().toString());
		userDetailInfo_commit.setSchool(collegeTxtVu.getText().toString());
		userDetailInfo_commit.setSignature(signatureEditTxt.getText()
				.toString());
		userDetailInfo_commit.setUserId(myid);

		usdeDescInfo_commit.setBirthday(birthTxtVu.getText().toString());
		usdeDescInfo_commit
				.setDescription(describeEditTxt.getText().toString());
		usdeDescInfo_commit.setEmotion(marriageTxtVu.getText().toString());
		usdeDescInfo_commit.setHometown(hometownTxtVu.getText().toString());
		usdeDescInfo_commit.setLikebook(likebookTxtVu.getText().toString());
		usdeDescInfo_commit.setLikefilm(likefilmTxtVu.getText().toString());
		usdeDescInfo_commit.setLikefood(likefoodTxtVu.getText().toString());
		usdeDescInfo_commit.setLikegift(likegiftTxtVu.getText().toString());
		usdeDescInfo_commit.setLikemusic(likemusicTxtVu.getText().toString());
		usdeDescInfo_commit.setLikeplace(likeplaceTxtVu.getText().toString());
		usdeDescInfo_commit.setDiploma(diplomaTxtVu.getText().toString());
		usdeDescInfo_commit.setUserId(myid);
	}

	class CommitInfoTask extends AsyncTask<Void, Void, Object> {

		@Override
		protected Object doInBackground(Void... arg0) {

			String response;
			setNewInfo();
			UserDetailJsonObject userDetailJsonObject = new UserDetailJsonObject(
					null, userInfo_commit, userDetailInfo_commit, null,
					usdeDescInfo_commit, null);

			try {
				response = ServerCommunication.requestWithoutEncrypt(
						userDetailJsonObject,
						URLConstant.UPDATE_USER_ALLINFO_URL);
			} catch (Exception e) {
				return null;
			}

			return response;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			loadingDialogDismiss();
			if (result == null) {
				MyToast.showToast(EditMyInformationActivity.this, "修改失败");
				return;
			}
			try {
				boolean isok = Boolean.parseBoolean((String) result);
				if (isok) {
					MyToast.showToast(EditMyInformationActivity.this, "修改成功");
				} else {
					MyToast.showToast(EditMyInformationActivity.this, "修改失败");
				}
			} catch (Exception e) {
				MyToast.showToast(EditMyInformationActivity.this, "修改失败");
			}
		}
	}

	class GetProCityTask extends AsyncTask<Void, Void, Object> {

		public int flag;
		public int id;
		public int category;
		public TextView tarTextView;

		public GetProCityTask(int f, int id, int cat, TextView tar) {
			this.flag = f;
			this.id = id;
			this.category = cat;
			this.tarTextView = tar;
		}

		@Override
		protected Object doInBackground(Void... arg0) {
			String response;
			JsonObject jsonObject = new JsonObject();
			jsonObject.setInt1(flag);
			jsonObject.setInt2(id);
			try {
				response = ServerCommunication.requestWithoutEncrypt(
						jsonObject, URLConstant.GET_PRO_CITY_COL_SCH);
				ProCityColSchJsonObject proCityColSchJsonObject = JacksonUtil
						.json().fromJsonToObject(response,
								ProCityColSchJsonObject.class);
				return proCityColSchJsonObject;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);

			if (result == null) {
				loadingDialogDismiss();
				MyToast.showToast(EditMyInformationActivity.this, "拉取数据失败");
				return;
			}

			try {
				ProCityColSchJsonObject proCityColSchJsonObject = (ProCityColSchJsonObject) result;
				switch (category) {
				case SET_PROVINCE:
					
					provinces = (ArrayList<Province>) proCityColSchJsonObject
							.getProvinces();
					if (provinces == null || provinces.size() == 0) {
						MyToast.showToast(EditMyInformationActivity.this,
								"拉取数据失败");
						return;
					}
					provincesStrings = new ArrayList<String>();
					for (int i = 0; i < provinces.size(); i++) {
						provincesStrings.add(provinces.get(i).getPname());
					}
					int pos = provincesStrings.indexOf(tarTextView.getText()
							.toString());
					if (pos < 0) {
						pos = 0;
					}
					loadingDialogDismiss();
					setProvinceString(tarTextView.getText().toString());
					showSingleSelectDialog(
							"选择省市",
							provincesStrings
									.toArray(new String[provincesStrings.size()]),
							pos, tarTextView, SET_PROVINCE);
					break;
				case SET_PROVINCE_COLLEGE:
					
					provinces = (ArrayList<Province>) proCityColSchJsonObject
							.getProvinces();
					if (provinces == null || provinces.size() == 0) {
						MyToast.showToast(EditMyInformationActivity.this,
								"拉取数据失败");
						return;
					}
					provincesStrings = new ArrayList<String>();
					for (int i = 0; i < provinces.size(); i++) {
						provincesStrings.add(provinces.get(i).getPname());
					}
					int pos1 = provincesStrings.indexOf(tarTextView.getText()
							.toString());
					if (pos1 < 0) {
						pos1 = 0;
					}
					loadingDialogDismiss();
					setProvinceString(tarTextView.getText().toString());
					showSingleSelectDialog(
							"选择省市",
							provincesStrings
									.toArray(new String[provincesStrings.size()]),
							pos1, tarTextView, SET_PROVINCE_COLLEGE);
					break;
				case SET_CITY:
					cities = (ArrayList<City>) proCityColSchJsonObject
							.getCities();
					if (cities == null || cities.size() == 0) {
						MyToast.showToast(EditMyInformationActivity.this,
								"拉取数据失败");
						return;
					}
					citiesStrings = new ArrayList<String>();
					for (int i = 0; i < cities.size(); i++) {
						citiesStrings.add(cities.get(i).getCity());
					}
					int post = citiesStrings.indexOf(tarTextView.getText()
							.toString());
					if (post < 0) {
						post = 0;
					}
					loadingDialogDismiss();
					issetCity = true;
					setCityString(tarTextView.getText().toString());
					showSingleSelectDialog("选择省市",
							citiesStrings.toArray(new String[citiesStrings
									.size()]), post, tarTextView, SET_CITY);
					break;
				case SET_COLLEGE:
					colleges = (ArrayList<College>) proCityColSchJsonObject
							.getColleges();
					if (colleges == null || colleges.size() == 0) {
						MyToast.showToast(EditMyInformationActivity.this,
								"拉取数据失败");
						return;
					}
					collegesStrings = new ArrayList<String>();
					for (int i = 0; i < colleges.size(); i++) {
						collegesStrings.add(colleges.get(i).getName());
					}
					int postt = collegesStrings.indexOf(tarTextView.getText()
							.toString());
					if (postt < 0) {
						postt = 0;
					}
					loadingDialogDismiss();
					setCollegeString(tarTextView.getText().toString());
					showSingleSelectDialog("选择大学",
							collegesStrings.toArray(new String[collegesStrings
									.size()]), postt, tarTextView, SET_COLLEGE);
					break;
				case SET_SCHOOL:
					schools = (ArrayList<School>) proCityColSchJsonObject
							.getSchools();
					if (schools == null || schools.size() == 0) {
						MyToast.showToast(EditMyInformationActivity.this,
								"拉取数据失败");
						return;
					}
					schoolsStrings = new ArrayList<String>();
					for (int i = 0; i < schools.size(); i++) {
						schoolsStrings.add(schools.get(i).getName());
					}
					int posttt = schoolsStrings.indexOf(tarTextView.getText()
							.toString());
					if (posttt < 0) {
						posttt = 0;
					}
					loadingDialogDismiss();
					setCollegeString(tarTextView.getText().toString());
					showSingleSelectDialog("选择院系",
							schoolsStrings.toArray(new String[schoolsStrings
									.size()]), posttt, tarTextView, SET_SCHOOL);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				loadingDialogDismiss();
				MyToast.showToast(EditMyInformationActivity.this, "拉取数据失败");
			}
		}
	}

	private String provinceString;
	private String collprovinceString;
	public String getCollprovinceString() {
		return collprovinceString;
	}

	public void setCollprovinceString(String collprovinceString) {
		this.collprovinceString = collprovinceString;
	}

	private String cityString;
	private String collegeString;
	private String schoolString;
	private String gradeString;
	private String diplomaString;
	private String sexString;
	private String marriageString;
	public String getMarriageString() {
		return marriageString;
	}

	public void setMarriageString(String marriageString) {
		this.marriageString = marriageString;
	}

	public String getGradeString() {
		return gradeString;
	}

	public void setGradeString(String gradeString) {
		this.gradeString = gradeString;
	}

	public String getDiplomaString() {
		return diplomaString;
	}

	public void setDiplomaString(String diplomaString) {
		this.diplomaString = diplomaString;
	}

	public String getProvinceString() {
		return provinceString;
	}

	public void setProvinceString(String provinceString) {
		this.provinceString = provinceString;
	}

	public String getCityString() {
		return cityString;
	}

	public void setCityString(String cityString) {
		this.cityString = cityString;
	}

	public String getCollegeString() {
		return collegeString;
	}

	public void setCollegeString(String collegeString) {
		this.collegeString = collegeString;
	}

	public String getSchoolString() {
		return schoolString;
	}

	public void setSchoolString(String schoolString) {
		this.schoolString = schoolString;
	}

	public String getSexString() {
		return sexString;
	}

	public void setSexString(String sexString) {
		this.sexString = sexString;
	}

}
