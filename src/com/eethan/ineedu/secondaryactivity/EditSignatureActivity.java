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

public class EditSignatureActivity extends BaseActivity {
	private EditText editSignature;
	private Button submit;
	private ImageButton back;
	
	boolean editcomplete;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("EditSignatureActivity");
		setContentView(R.layout.update_signature_page);
		
		
		editSignature=(EditText)findViewById(R.id.edit_sign);
		back=(ImageButton)findViewById(R.id.register_page_back_button);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditSignatureActivity.this.finish();
			}
		});
		submit=(Button)findViewById(R.id.update_nickname_page_finish_button);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(editSignature.getText().toString().trim().equals(""))
				{
					editSignature.setError(Html.fromHtml("<font color=#E10979>内容不能为空</font>"));
					return;
				}
				loadingDialogShow();
				new Thread(){
			          public void run() {
			        	Intent intent = getIntent();
			      		int id=intent.getIntExtra("id",-1);
			      		String nickname=editSignature.getText().toString();
			      		
			      		JsonObject jsonObject=new JsonObject();
			      		jsonObject.setInt1(id);
			      		jsonObject.setString1(nickname);
			      		
			      		String response;
						try {
							response = ServerCommunication.request(jsonObject, URLConstant.UPDATE_SIGNATURE_URL);
							editcomplete = JacksonUtil.json().fromJsonToObject(response,Boolean.class);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Message msg = mHandler.obtainMessage();
							msg.what=Constant.CONNECT_ERROR;
							msg.obj=e.getMessage();
							msg.sendToTarget();
							return;
						}
						
						
						
						Message msg = mHandler.obtainMessage();
						msg.obj = nickname;
						msg.sendToTarget();
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
			
			
			if(editcomplete){
				//startActivity(new Intent(getApplicationContext(), MainActivity.class));
				//登陆IneedU服务器成功以后登陆openfire;
				MyToast.showToast(EditSignatureActivity.this, "修改成功~", false);
				loadingDialogDismiss();
				EditSignatureActivity.this.finish();
			}else {
				MyToast.showToast(EditSignatureActivity.this, "修改失败~", true);
				loadingDialogDismiss();
	    		return;
			}
		}
	};
}
