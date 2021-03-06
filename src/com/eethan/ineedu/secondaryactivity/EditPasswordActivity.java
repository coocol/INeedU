package com.eethan.ineedu.secondaryactivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.im.XmppConnection;
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
import android.os.Looper;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditPasswordActivity extends BaseActivity{

	private EditText editPassword;
	private EditText confirmPassword;
	private Button submit;
	private ImageButton back;
	
	boolean editcomplete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("EditPasswordActivity");
		setContentView(R.layout.update_password_page);
		
		
		editPassword=(EditText)findViewById(R.id.editPassword);
		confirmPassword=(EditText)findViewById(R.id.confirmPassword);
		back=(ImageButton)findViewById(R.id.register_page_back_button);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditPasswordActivity.this.finish();
			}
		});
		submit=(Button)findViewById(R.id.update_nickname_page_finish_button);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(editPassword.getText().toString().trim().equals(""))
				{
					editPassword.setError(Html.fromHtml("<font color=#E10979>内容不能为空</font>"));
					return;
				}
				loadingDialogShow();
				new Thread(){
			          public void run() {
			        	Intent intent = getIntent();
			      		int id=intent.getIntExtra("id",-1);
			      		String password=editPassword.getText().toString();
			      		String confirm=confirmPassword.getText().toString();
			      		
			      		if(!password.equals(confirm)){
			      			Message msg = mHandler.obtainMessage();
							msg.sendToTarget();
							msg.obj="两次输入不一致";
			      			return;
			      		}
			      			
			      		JsonObject jsonObject=new JsonObject();
			      		jsonObject.setInt1(id);
			      		jsonObject.setString1(password);
			      		
			      		String response;
						try {
							response = ServerCommunication.request(jsonObject, URLConstant.UPDATE_PASSWORD_URL);
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
			
			if(msg.obj!=null)
				MyToast.showToast(EditPasswordActivity.this, msg.obj.toString());
			if(editcomplete){
				//startActivity(new Intent(getApplicationContext(), MainActivity.class));
				//登陆IneedU服务器成功以后登陆openfire
				
				MyToast.showToast(EditPasswordActivity.this, "修改成功~", false);
				loadingDialogDismiss();
				new SPHelper(EditPasswordActivity.this).
					put(Constant.PASSWORD, confirmPassword.getText().toString());
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							XmppConnection.getConnection().getAccountManager()
								.changePassword(confirmPassword.getText().toString());
							
						} catch (XMPPException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Looper.prepare();
							MyToast.showToast(EditPasswordActivity.this, "修改失败!");
							Looper.loop();
						}
					}
				}).start();
				
				EditPasswordActivity.this.finish();
			}else {
				MyToast.showToast(EditPasswordActivity.this, "修改失败~", true);
				loadingDialogDismiss();
	    		return;
			}
		}
	};
}