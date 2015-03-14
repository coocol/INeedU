package com.eethan.ineedu.primaryactivity;

import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.PourlistenComment;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.CustomerHttpClient;
import com.eethan.ineedu.util.WebTime;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class IssuePourListenCommentActivity extends BaseActivity{
	private Button issueButton;
	private EditText commentEditText;
	private int pourlistenId;
	private int plUserId;//pourlisten主人的id
	
	public final int ISSUE_OK=1;
	public final int ISSUE_FAILED=0;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.pour_listen_comment_issue);
			setPageName("IssuePourlistenCommentActivity");
			pourlistenId=getIntent().getExtras().getInt("pourlistenId");
			plUserId=getIntent().getExtras().getInt("userId");

			findView();
			SetListener();
		}
		private Handler finishHandler=new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case ISSUE_OK:
					int userid = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
					new AddLoveNum(IssuePourListenCommentActivity.this, 1, userid);
					IssuePourListenCommentActivity.this.finish();
					break;
				case ISSUE_FAILED:
					MyToast.showToast(IssuePourListenCommentActivity.this, "评论失败~", true);
					
					break;
				default:
					break;
				}
				
			};
		};
		private void SetListener() {
			// TODO Auto-generated method stub
			issueButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(commentEditText.getText().toString().trim().equals(""))
					{
						commentEditText.setError(Html.fromHtml("<font color=#E10979>倾诉内容不能为空!</font>"));
						return;
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							//发送到服务器端
							PourlistenComment pourlistenComment=new PourlistenComment();
							int plId,userId,commentedUserId;
							String comment;
							long time;
							
							Date date=new Date();
							
							try {
								time = WebTime.getTime();
							} catch (Exception e) {
								time=date.getTime();
							}
							comment=commentEditText.getText().toString();
							SharedPreferences settings=getSharedPreferences(Constant.INEEDUSPR, 0);
							userId=settings.getInt(Constant.ID, 0);
							plId=pourlistenId;
							commentedUserId=plUserId;
							
							pourlistenComment.setPourlistenId(pourlistenId);
							pourlistenComment.setUserId(userId);
							pourlistenComment.setCommentedUserId(commentedUserId);
							pourlistenComment.setComment(comment);
							pourlistenComment.setTime(time);
							
							//将jsonObject转换成String
							String jsonObjectJson = JacksonUtil.json().fromObjectToJson(pourlistenComment);
							
							//将转换后的String化成名值对形式并发送给服务器端
							NameValuePair jsonObjectNameValuePair = new BasicNameValuePair("data", jsonObjectJson);
							
							String result = CustomerHttpClient.post(URLConstant.ISSUE_POUR_LISTEN_COMMENT_URL, jsonObjectNameValuePair);
							
							Message msg=finishHandler.obtainMessage();
							if(result.equals("true"))
								msg.what=ISSUE_OK;
							else
								msg.what=ISSUE_FAILED;
							msg.sendToTarget();
							
						}
					}).start();
				}
			});
		}
		private void findView() {
			// TODO Auto-generated method stub
			issueButton=(Button)findViewById(R.id.pour_listen_comment_issue_button);
			commentEditText=(EditText)findViewById(R.id.pour_listen_comment_issue_comment);
		}
}
