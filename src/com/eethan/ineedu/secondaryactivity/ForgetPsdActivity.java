package com.eethan.ineedu.secondaryactivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;

public class ForgetPsdActivity extends BaseActivity{
	EditText Email;
	Button finish;
	LoadingDialog loadingDialog;
	private static final int FORGET_PSD_SUCCESS=3;
	private static final int FORGET_PSD_FAILED=4;
	private static final int FORGET_PSD_UNKNOWERROR=10;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("ForgetPasswordActivity");
		setContentView(R.layout.activity_forget_psd);
		loadingDialog=new LoadingDialog(this);
		
		Email=(EditText)findViewById(R.id.login_et_account);
		finish=(Button)findViewById(R.id.forget_bt_login);
		
		finish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!check())
					return;
				loadingDialog.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							
							String email=Email.getText().toString();
							String str1,str2;
							int index1,index2;
							index1=email.indexOf("@");
							index2=email.indexOf(".");
							str1=email.substring(0, index1);
							str2=email.substring(index1+1,index2);
							
							JsonObject jsonObject=new JsonObject();
							jsonObject.setString1(str1);
							jsonObject.setString2(str2);
							
							String response = ServerCommunication.request(jsonObject, URLConstant.FORGET_PASSWORD_URL);
							Boolean result = JacksonUtil.json().fromJsonToObject(response,Boolean.class);
							
							Message msg = mHandler.obtainMessage();
							if(result)
								msg.what=FORGET_PSD_SUCCESS;
							else
								msg.what=FORGET_PSD_FAILED;
							msg.sendToTarget();
						}  catch (PostException e) {
							// TODO: handle exception
							Message msg = mHandler.obtainMessage();
							msg.what=Constant.CONNECT_ERROR;
							msg.sendToTarget();
							return;
						}catch (Exception e) {
							Message msg = mHandler.obtainMessage();
							msg.what=FORGET_PSD_UNKNOWERROR;
							msg.sendToTarget();
						}
						
					}
				}).start();
			}

			private boolean check() {
				// TODO Auto-generated method stub
				String email=Email.getText().toString();
				if(email.indexOf("@")==-1||email.indexOf(".com")==-1)
				{
					Email.setError(Html.fromHtml("<font color=#E10979>请输入正确的邮箱!</font>"));
					return false;
				}
				return true;
			}
		});
		
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			
			case FORGET_PSD_SUCCESS:
				MyToast.showToast(ForgetPsdActivity.this, "邮件已发送至您的邮箱,请及时查收");
				loadingDialog.dismiss();
				finish();
				break;
			case FORGET_PSD_FAILED:
				MyToast.showToast(ForgetPsdActivity.this, "找回失败。。。请检查邮箱是否正确");
				loadingDialog.dismiss();
				finish();
				break;
			case Constant.CONNECT_ERROR:
				MyToast.showToast(ForgetPsdActivity.this, "服务器连接失败!");
				loadingDialog.dismiss();
				break;
			case FORGET_PSD_UNKNOWERROR:
				MyToast.showToast(ForgetPsdActivity.this, "出错了，请再试一次");
				loadingDialog.dismiss();
				break;
			default:
				break;
			}
			
		}
	};
	
	
}
