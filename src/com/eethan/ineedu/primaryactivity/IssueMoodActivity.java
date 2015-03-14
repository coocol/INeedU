package com.eethan.ineedu.primaryactivity;

import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Mood;
import com.eethan.ineedu.databasebeans.TakePhotos;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.UploadHelper;
import com.eethan.ineedu.util.WebTime;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class IssueMoodActivity extends BaseActivity {

	private Button finishBtn;
	private ImageButton backImgBtn;
	private EditText contentEditText;
	private String contentString;
	
	public final int ISSUE_OK = 1;
	public final int ISSUE_FAILED = 0;

	
	@SuppressLint("HandlerLeak")
	private Handler resultHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			loadingDialogDismiss();
			switch (msg.what) {
			case ISSUE_OK:
				MyToast.showToast(IssueMoodActivity.this, "发布成功~", false);
				int userid = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
				new AddLoveNum(IssueMoodActivity.this, 2, userid).execute();
				finish();
				break;
			case ISSUE_FAILED:
				MyToast.showToast(IssueMoodActivity.this, "发布失败~", true);
				break;
			case Constant.CONNECT_ERROR:
				MyToast.showToast(IssueMoodActivity.this, (String) msg.obj,
						true);
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mood_edit);
		
		finishBtn = (Button) findViewById(R.id.bt_finish);
		contentEditText = (EditText) findViewById(R.id.et_content);
		backImgBtn = (ImageButton) findViewById(R.id.bt_back);
		backImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		finishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				contentString = contentEditText.getText().toString();
				if(contentString==null || contentString.equals("")){
					contentEditText.setError("请输入内容");
				}else {
					loadingDialogShow();
					new Thread(new Runnable() {
						public void run() {
							long time;
							try {
								time = WebTime.getTime();
							} catch (Exception e) {
								time =  DateUtil.getMSTime();
							}
							
							int userId = getSharedPreferences(Constant.INEEDUSPR, 0)
									.getInt(Constant.ID, 0);
							double lat = LocateManager.getInstance().getLatitude();
							double lng = LocateManager.getInstance().getLontitude();
						
							Mood mood = new Mood();
							mood.setContent(contentString);
							mood.setLat(lat);
							mood.setLng(lng);
							mood.setUserId(userId);
							mood.setTime(String.valueOf(time));
							mood.setTransmitFrom(-1);
							mood.setFlag(Constant.MOOD_FLAG_NOTE);
							
							String result;
							try {
								result = ServerCommunication.request(mood,
										URLConstant.COMMIT_MOOD_URL);
							} catch (PostException e) {
								e.printStackTrace();
								Message msg = resultHandler.obtainMessage();
								msg.what = Constant.CONNECT_ERROR;
								msg.obj = e.getMessage();
								msg.sendToTarget();
								return;
							}
							Message msg = resultHandler.obtainMessage();
							if (result!=null && result.equals("true"))
								msg.what = ISSUE_OK;
							else
								msg.what = ISSUE_FAILED;
							msg.sendToTarget();
						}
					}).start();
				}
			}
		});
	}
}
