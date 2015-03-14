package com.eethan.ineedu.primaryactivity;

import java.util.Date;

import u.aly.ac;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.databasebeans.Wish;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.LastPick;
import com.eethan.ineedu.model.LastWrite;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.secondaryactivity.BgImageSelectActivity;
import com.eethan.ineedu.util.DataTraslator;
import com.eethan.ineedu.util.SysApplication;
import com.eethan.ineedu.util.WebTime;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class WishReleaseActivity extends BaseActivity {

	private ImageButton backImgBtn;
	private TextView finishTxtVu;
	private EditText contentEditTxText;
	private ToggleButton nonameTogBtn;
	private ImageView bgImgVu;

	private String bgImgUrl = "";
	private boolean isNoname = false;
	private ImageLoader imageLoader;
	private String contentString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.wish_issue);
		bgImgUrl = getIntent().getStringExtra("imageUrl");
		imageLoader = ImageLoader.getInstance();

		bgImgVu = (ImageView) findViewById(R.id.pour_listen_issue_page_background);
		imageLoader.displayImage(bgImgUrl, bgImgVu);
		nonameTogBtn = (ToggleButton) findViewById(R.id.tgbt_anonymity);
		contentEditTxText = (EditText) findViewById(R.id.pour_listen_issue_page_content);
		backImgBtn = (ImageButton) findViewById(R.id.imgbt_back);
		backImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				// Intent intent = new Intent();
				// intent.setClass(WishReleaseActivity.this,
				// GirlsWishesActivity.class);
				// startActivity(intent);
				finish();
			}
		});
		finishTxtVu = (TextView) findViewById(R.id.tv_finish);
		finishTxtVu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				contentString = contentEditTxText.getText().toString();
				if (contentString == null || contentString.equals("")) {
					MyToast.showToast(WishReleaseActivity.this, "请输入内容");
				} else {
					isNoname = nonameTogBtn.isChecked();
					loadingDialogShow();
					new ReleaseAWish().execute();
				}
			}
		});
	}

	private class ReleaseAWish extends AsyncTask<Void, Void, Object> {
		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {

			String response = null, URL = URLConstant.WISH_COMMIT_WISH;
			Wish wish = new Wish();
			wish.setImageUrl(bgImgUrl);

			try {
				long time = WebTime.getTime();
				wish.setTime(String.valueOf(time));
			} catch (Exception e) {
				wish.setTime(DataTraslator.getDateNow());
			}

			wish.setLat(LocateManager.getInstance().getLatitude());
			wish.setLng(LocateManager.getInstance().getLontitude());
			wish.setUserId(new SPHelper(getApplicationContext()).GetUserId());
			wish.setContent(contentString);

			if (isNoname) {
				wish.setAuth(0);
			} else {
				wish.setAuth(1);
			}
			try {
				response = ServerCommunication.request(wish, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			boolean refreshPR;
			try {
				refreshPR = JacksonUtil.json().fromJsonToObject(response,
						boolean.class);
			} catch (Exception e) {
				refreshPR = false;
			}
			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {

			loadingDialogDismiss();
			super.onPostExecute(result);

			// Call onRefreshComplete when the list has been refreshed.
			if (result == null
					|| !ServerCommunication.checkResult(
							WishReleaseActivity.this, result)) {
				MyToast.showToast(WishReleaseActivity.this, "发布失败~");
				return;
			}

			boolean res = (Boolean) result;
			if (!res) {
				MyToast.showToast(WishReleaseActivity.this, "发布失败~");
			} else {
				MyToast.showToast(getApplicationContext(), "发布成功~");
				new Thread() {
					public void run() {
						LastWrite lastWrite;
						int userid = new SPHelper(WishReleaseActivity.this)
								.GetUserId();
						try {
							lastWrite = DbUtil.getDbUtils(
									WishReleaseActivity.this).findFirst(
									Selector.from(LastWrite.class)
											.where(LastWrite.USERID, "=",
													userid)
											.and(LastWrite.TYPE, "=", "hope"));
							String hh = DataTraslator.longToFormatString(
									new Date().getTime(), "MM-dd");
							if (lastWrite == null) {
								lastWrite = new LastWrite();
								lastWrite.setUserId(userid);
								lastWrite.setHope("hope");
								lastWrite.setNum(0);
								lastWrite.setTimeStr(hh);
								DbUtil.getDbUtils(WishReleaseActivity.this)
										.save(lastWrite);
							}else{
								int n = lastWrite.getNum();
								String lastTime = lastWrite.getTimeStr();
								String[] lasttimeStrings = lastTime.split("-");
								String nowtimeDString = DataTraslator.longToFormatString(new Date().getTime(), "dd");
								if(Integer.parseInt(nowtimeDString)-Integer.parseInt(lasttimeStrings[1])==0){
									lastWrite.setNum(n+1);
								}else{
									lastWrite.setNum(0);
								}
								lastWrite.setTimeStr(hh);
								DbUtil.getDbUtils(WishReleaseActivity.this).update(lastWrite, LastWrite.NUM,LastWrite.TIMESTR);
							}
						} catch (DbException e) {
						}
					};
				}.start();
				// Intent intent = new Intent();
				// intent.setClass(WishReleaseActivity.this,
				// GirlsWishesActivity.class);
				// startActivity(intent);
				finish();
			}
		}
	}

}
