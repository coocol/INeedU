package com.eethan.ineedu.primaryactivity;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;




import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Plays;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.WebTime;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

public class PlaysReleaseActivity extends BaseActivity {

	
	private ImageButton backImgBtn;
	private Button finishBtn;
	private Button dateBtn;
	private Button timeBtn;
	private EditText themeEditTxt;
	private EditText contentEditTxt;
	private EditText requireEditTxt;
	private EditText placeEditTxt;
	private EditText timeEditTxt;
	 
	private String themeString;
	private String contentString;
	private String requireString;
	private String placeString;
	private String timeString;
	
	private Dialog datePickerDialog;
	private Dialog timePickerDialog;
	
	private DeadLineTime deadLineTime = new DeadLineTime();
	

	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.plays_issue);
		
		Calendar calendar = Calendar.getInstance();

		datePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
						deadLineTime.year = arg1;
						deadLineTime.month = arg2+1;
						deadLineTime.day = arg3;
						dateBtn.setText(deadLineTime.year+"/"+deadLineTime.month+"/"+deadLineTime.day);
						
					}
					
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		timePickerDialog = new TimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT,
				new TimePickerDialog.OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker arg0, int arg1, int arg2) {
						deadLineTime.hour = arg1;
						deadLineTime.minute = arg2;
						timeBtn.setText(deadLineTime.hour+":"+deadLineTime.minute);
					}
				}, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);
	
 
		backImgBtn = (ImageButton)findViewById(R.id.imgbt_back);
		backImgBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		themeEditTxt = (EditText)findViewById(R.id.et_theme);
		contentEditTxt = (EditText)findViewById(R.id.et_content);
		placeEditTxt = (EditText)findViewById(R.id.et_place);
		requireEditTxt = (EditText)findViewById(R.id.et_requirement);
		timeEditTxt = (EditText)findViewById(R.id.et_time);
		dateBtn = (Button)findViewById(R.id.bt_date); 
		dateBtn.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				datePickerDialog.show();
			}
		});
		timeBtn = (Button)findViewById(R.id.bt_time);
		timeBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				timePickerDialog.show();
			}
		});
		finishBtn = (Button)findViewById(R.id.bt_finish);
		finishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				themeString = themeEditTxt.getText().toString();
				contentString = contentEditTxt.getText().toString();
				requireString = requireEditTxt.getText().toString();
				placeString = placeEditTxt.getText().toString();
				timeString = timeEditTxt.getText().toString();
				
				if(themeString.equals("null")||themeString.equals("")){
					themeEditTxt.setError(Html.fromHtml("<font color=#E10979>请填写主题</font>"));
					return;
				}
				if(contentString.equals("null")||contentString.equals("")){
					contentEditTxt.setError(Html.fromHtml("<font color=#E10979>请填写活动内容</font>"));
					return;
				}
				if(requireString.equals("null")||requireString.equals("")){
					requireEditTxt.setError(Html.fromHtml("<font color=#E10979>请填写要求</font>"));
					return;
				}
				if(placeString.equals("null")||placeString.equals("")){
					placeEditTxt.setError(Html.fromHtml("<font color=#E10979>请填写活动地点</font>"));
					return;
				}
				if(timeString.equals("null")||timeString.equals("")){
					timeEditTxt.setError(Html.fromHtml("<font color=#E10979>请描述时间要求</font>"));
					return;
				}
				
				if(isDateNoQOk()){
					
					loadingDialogShow();
					new ReleaseAPlay().execute();
				}

			}
		});
		
		
	}
	public boolean isTimeOk(){
		if(deadLineTime.hour==0){
			//timeBtn.setError(Html.fromHtml("<font color=#E10979>请选择截止时间</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "无效的小时"+deadLineTime.hour);
			return false;
		}
		Calendar calendar = Calendar.getInstance();
		if(deadLineTime.hour<calendar.get(Calendar.HOUR_OF_DAY)){
			//timeBtn.setError(Html.fromHtml("<font color=#E10979>无效的时间</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "无效的小时"+deadLineTime.hour);
			return false;
		}
	
		if (deadLineTime.hour==calendar.get(Calendar.HOUR_OF_DAY)
				&& deadLineTime.minute<calendar.get(Calendar.MINUTE)) {
			//timeBtn.setError(Html.fromHtml("<font color=#E10979>无效的时间</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "无效的分钟"+deadLineTime.minute);
			return false;
		}
		return true;
	}
	public boolean isDateNoQOk(){
		if(deadLineTime.year == 0){
			//dateBtn.setError(Html.fromHtml("<font color=#E10979>请选择截至日期</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "无效的年份"+deadLineTime.year);
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		if(deadLineTime.year<calendar.get(Calendar.YEAR)){
			//dateBtn.setError(Html.fromHtml("<font color=#E10979>无效的年份</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "请重新选择年份"+deadLineTime.year);
			return false;
		}
		if(deadLineTime.year==calendar.get(Calendar.YEAR)
				&&deadLineTime.month<(calendar.get(Calendar.MONTH)+1)){
			//dateBtn.setError(Html.fromHtml("<font color=#E10979>无效的月份</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "请重新选择月份"+deadLineTime.month);
			return false;
		}
		if(deadLineTime.year==calendar.get(Calendar.YEAR)
				&&deadLineTime.month==(calendar.get(Calendar.MONTH)+1)
				&&deadLineTime.day<calendar.get(Calendar.DAY_OF_MONTH)){
			//dateBtn.setError(Html.fromHtml("<font color=#E10979>无效的日期</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "请重新选择天数"+deadLineTime.day);
			return false;
		}
		if(deadLineTime.year==calendar.get(Calendar.YEAR)
				&&deadLineTime.month==(calendar.get(Calendar.MONTH)+1)
				&&deadLineTime.day==calendar.get(Calendar.DAY_OF_MONTH)){
			//dateBtn.setError(Html.fromHtml("<font color=#E10979>无效的日期</font>"));
			if(!isTimeOk()){
				MyToast.showToast(PlaysReleaseActivity.this, "请重新选择时间"+deadLineTime.hour);
				return false;
			}
			
		}
		return true;
	}
	public boolean isDateQOk(){
		if(deadLineTime.year == 0){
			//dateBtn.setError(Html.fromHtml("<font color=#E10979>请选择截至日期</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "无效的年份"+deadLineTime.year);
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		if(deadLineTime.year<calendar.get(Calendar.YEAR)){
			//dateBtn.setError(Html.fromHtml("<font color=#E10979>无效的年份</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "无效的年份"+deadLineTime.year);
			return false;
		}
		if(deadLineTime.year==calendar.get(Calendar.YEAR)
				&&deadLineTime.month<(calendar.get(Calendar.MONTH)+1)){
			//dateBtn.setError(Html.fromHtml("<font color=#E10979>无效的月份</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "无效的月份"+deadLineTime.month);
			return false;
		}
		if(deadLineTime.year==calendar.get(Calendar.YEAR)
				&&deadLineTime.month==(calendar.get(Calendar.MONTH)+1)
				&&deadLineTime.day<calendar.get(Calendar.DAY_OF_MONTH)){
			//dateBtn.setError(Html.fromHtml("<font color=#E10979>无效的日期</font>"));
			MyToast.showToast(PlaysReleaseActivity.this, "无效的天"+deadLineTime.day);
			return false;
		}
		return true;
	}
	
	public String getMilliTimeString(int year,int month,int day,int hour,int minute){
		String monthString = String.valueOf(month);
		String dayString = String.valueOf(day);
		String hoursString = String.valueOf(hour);
		String minuteString = String.valueOf(minute);
		if(monthString.length()==1){
			monthString = "0"+monthString;
		}
		if(dayString.length()==1){
			dayString = "0"+dayString;
		}
		if(hoursString.length()==1){
			hoursString = "0"+hoursString;
		}
		if(minuteString.length()==1){
			minuteString = "0"+minuteString;
		}
		return year+monthString+dayString+hoursString+minuteString;
	}
	
	private class DeadLineTime{
		public int year;
		public int month;
		public int day;
		public int hour;
		public int minute;
	}

	private class ReleaseAPlay extends AsyncTask<Void, Void, Object> {
		
		public String getMilliTimeString(int year,int month,int day,int hour,int minute){
			String monthString = String.valueOf(month);
			String dayString = String.valueOf(day);
			String hoursString = String.valueOf(hour);
			String minuteString = String.valueOf(minute);
			if(monthString.length()==1){
				monthString = "0"+monthString;
			}
			if(dayString.length()==1){
				dayString = "0"+dayString;
			}
			if(hoursString.length()==1){
				hoursString = "0"+hoursString;
			}
			if(minuteString.length()==1){
				minuteString = "0"+minuteString;
			}
			return year+monthString+dayString+hoursString+minuteString;
		}

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {
			
			
			DeadLineTime dfff = deadLineTime;
			
			Long timeLong = null;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
				String da = getMilliTimeString(deadLineTime.year, deadLineTime.month, deadLineTime.day, deadLineTime.hour, deadLineTime.minute);
				timeLong = sdf.parse(da).getTime();
			
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Plays plays = new Plays();
			plays.setDeadLine(String.valueOf(timeLong));
			plays.setUserId(new SPHelper(getApplicationContext()).GetUserId());
			try {
				plays.setInitTime(String.valueOf(WebTime.getTime()));
			} catch (Exception e) {
				plays.setInitTime(String.valueOf(DateUtil.getMSTime()));
			}
			
			plays.setTheme(themeString);
			plays.setContent(contentString);
			plays.setPlace(placeString);
			plays.setRequirement(requireString);
			plays.setLat(LocateManager.getInstance().getLatitude());
			plays.setLng(LocateManager.getInstance().getLontitude());
			plays.setTime(timeString);
			
			String response = null, URL = URLConstant.PLAYS_COMMIT_PLAY;
			
			try {
				response = ServerCommunication.request(plays, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			
			return response;
		}

		@Override
		protected void onPostExecute(Object result) {

			// Call onRefreshComplete when the list has been refreshed.
			if (result==null || !ServerCommunication
					.checkResult(PlaysReleaseActivity.this, result)) {
				MyToast.showToast(PlaysReleaseActivity.this, "发布失败~");
				return;
			}

			if(result.equals("")){
				MyToast.showToast(PlaysReleaseActivity.this, "发布失败~");
			}
			
		
			loadingDialogDismiss();
			
			MyToast.showToast(getApplicationContext(), "发布成功~");
			int userid = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
			new AddLoveNum(PlaysReleaseActivity.this, 2, userid).execute();
			finish();
			
			super.onPostExecute(result);
		}
	}


}
