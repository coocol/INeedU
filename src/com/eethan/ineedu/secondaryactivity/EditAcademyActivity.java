package com.eethan.ineedu.secondaryactivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.CustomerHttpClient;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class EditAcademyActivity extends BaseActivity{
	private EditText editAcademy;
	private EditText editGrade;
	private Button submit;
	private ImageButton back;
	
	boolean editcomplete1;
	boolean editcomplete2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("EditAcademyActivity");
		setContentView(R.layout.update_major_page);
		
		
		editAcademy=(EditText)findViewById(R.id.edit_academy);
		editGrade=(EditText)findViewById(R.id.edit_grade);
		back=(ImageButton)findViewById(R.id.register_page_back_button);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditAcademyActivity.this.finish();
			}
		});
		submit=(Button)findViewById(R.id.update_nickname_page_finish_button);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(editAcademy.getText().toString().trim().equals(""))
				{
					editAcademy.setError(Html.fromHtml("<font color=#E10979>内容不能为空</font>"));
					return;
				}
				if(editGrade.getText().toString().trim().equals(""))
				{
					editGrade.setError(Html.fromHtml("<font color=#E10979>内容不能为空</font>"));
					return;
				}
				loadingDialogShow();
				new Thread(){
			          public void run() {
			        	Intent intent = getIntent();
			      		int id=intent.getIntExtra("id",-1);
			      		String academy=editAcademy.getText().toString();
			      		
			      		JsonObject jsonObject=new JsonObject();
			      		jsonObject.setInt1(id);
			      		jsonObject.setString1(academy);
			      		
			      		String response;
						try {
							response = ServerCommunication.request(jsonObject, URLConstant.UPDATE_ACADEMY_URL);
							editcomplete1 = JacksonUtil.json().fromJsonToObject(response,Boolean.class);
							
							String grade=editGrade.getText().toString();
				      		
				      		JsonObject jsonObject2=new JsonObject();
				      		jsonObject2.setInt1(id);
				      		jsonObject2.setString1(grade);
				      		
				      		String editJson2 = JacksonUtil.json().fromObjectToJson(jsonObject2);
				      		NameValuePair editNameValuePair2 = new BasicNameValuePair("data", editJson2);
				      		String response2 = CustomerHttpClient.post(URLConstant.UPDATE_GRADE_URL, editNameValuePair2);
				      		
							editcomplete2 = JacksonUtil.json().fromJsonToObject(response2,Boolean.class);
							
							Message msg = mHandler.obtainMessage();
							msg.sendToTarget();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Message msg = mHandler.obtainMessage();
							msg.what=Constant.CONNECT_ERROR;
							msg.obj=e.getMessage();
							msg.sendToTarget();
							return;
						}
			      		
						
			        }
			    }.start();
			}
		});
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			if(msg.what==Constant.CONNECT_ERROR)
			{
				MyToast.showToast(getContext(), (String)msg.obj );
				loadingDialogDismiss();
				return;
			}
			
			if(editcomplete1&&editcomplete2){
				//startActivity(new Intent(getApplicationContext(), MainActivity.class));
				//登陆IneedU服务器成功以后登陆openfire
				
				MyToast.showToast(EditAcademyActivity.this, "修改成功~", false);
				loadingDialogDismiss();
				EditAcademyActivity.this.finish();
			}else {
				MyToast.showToast(EditAcademyActivity.this, "修改失败~", true);
				loadingDialogDismiss();
	    		return;
			}
		}
	};
}
