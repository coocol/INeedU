package com.eethan.ineedu.primaryactivity;

import android.R.integer;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Need;
import com.eethan.ineedu.fragment.NeedFragment;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.WebTime;
public class IssueNeedActivity extends BaseActivity{
	Button issue;
	ImageButton back;
	EditText rewardEditText;
	viewHolderToDatabase viewHolder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.need_issue_page);
		setPageName("IssueNeedActivity");
		findView();
		viewHolder=new viewHolderToDatabase();
		viewHolder.needDescribtion=(EditText)findViewById(R.id.need_issue_page_describtion);
		viewHolder.timeLimit=(RadioGroup)findViewById(R.id.need_issue_page_limittime_radiogroup);
		viewHolder.payoff=(RadioGroup)findViewById(R.id.need_issue_page_payoff_radiogroup);
		viewHolder.type=(RadioGroup)findViewById(R.id.need_issue_page_type_radiogroup);
		issue=(Button)findViewById(R.id.need_issue_page_issue_button);
		back=(ImageButton)findViewById(R.id.pour_listen_issue_page_back_button);
		rewardEditText=(EditText)findViewById(R.id.need_issue_page_reward_describtion);
		
		viewHolder.timeLimit.check(R.id.need_issue_page_limittime_radiobutton1);
		viewHolder.payoff.check(R.id.need_issue_page_payoff_radiobutton1);
		viewHolder.type.check(R.id.need_issue_page_type_radiobutton1);
		
		
		//约束
		rewardEditText.setText("请奶茶");
		
		
		//当报酬RadioButton更改时在EditText里显示对应的文本
		viewHolder.payoff.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				switch (arg1) 
				{
					case R.id.need_issue_page_payoff_radiobutton1:
						rewardEditText.setText("请奶茶");
						break;
					case R.id.need_issue_page_payoff_radiobutton2:
						rewardEditText.setText("请吃饭");
						break;
					case R.id.need_issue_page_payoff_radiobutton3:
						rewardEditText.setText("请电影");
						break;
					case R.id.need_issue_page_payoff_radiobutton4:
						rewardEditText.setText("神秘礼物");
						break;
					case R.id.need_issue_page_payoff_radiobutton5:
						rewardEditText.setText("丰厚红包");
						break;
					case R.id.need_issue_page_payoff_radiobutton6:
						rewardEditText.setText("2枚大洋");
						break;
					case R.id.need_issue_page_payoff_radiobutton7:
						rewardEditText.setText("5枚大洋");
						break;
					case R.id.need_issue_page_payoff_radiobutton8:
						rewardEditText.setText("10枚大洋");
						break;
					case R.id.need_issue_page_payoff_radiobutton9:
						rewardEditText.setText("20枚大洋");
						break;
				}
			}
		});
		viewHolder.needDescribtion.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				 //TODO Auto-generated method stub
				if(s.toString().length()==50)
				{
					viewHolder.needDescribtion.setError(Html.fromHtml("<font color=#E10979>Need内容不能超过50个字!</font>"));
					return ;
				}
			}
		});
		issue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//发布Need
				
				//不能为空
				if(viewHolder.needDescribtion.getText().toString().trim().equals(""))
					{
						viewHolder.needDescribtion.setError(Html.fromHtml("<font color=#E10979>Need内容不能为空!</font>"));
						return ;
					}
				loadingDialogShow();
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							long timeLimit = 0;
							String reward = null;
							int type = 0;
							//Long time=DateUtil.getMSTime();
							
							Long time = WebTime.getTime();
							int userId = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
							int solveId=Constant.NOT_SOLVED;//默认-1
							double lat=LocateManager.getInstance().getLatitude();
							double lng=LocateManager.getInstance().getLontitude();
							
							
							String content=viewHolder.needDescribtion.getText().toString();
							
							switch(viewHolder.timeLimit.getCheckedRadioButtonId())
							{
							case R.id.need_issue_page_limittime_radiobutton1:
								timeLimit=10*60*1000;//10分钟
								break;
							case R.id.need_issue_page_limittime_radiobutton2:
								timeLimit=30*60*1000;
								break;
							case R.id.need_issue_page_limittime_radiobutton3:
								timeLimit=1*60*60*1000;//一小时
								break;
							case R.id.need_issue_page_limittime_radiobutton4:
								timeLimit=6*60*60*1000;
								break;
							case R.id.need_issue_page_limittime_radiobutton5:
								timeLimit=1*24*60*60*1000;//一天
								break;
							case R.id.need_issue_page_limittime_radiobutton6:
								timeLimit=3*24*60*60*1000;
								break;
							case R.id.need_issue_page_limittime_radiobutton7:
								timeLimit=7*24*60*60*1000;
								break;
							}
							
							if(rewardEditText.getText().toString().trim().equals(""))
								switch (viewHolder.payoff.getCheckedRadioButtonId()) 
								{
									case R.id.need_issue_page_payoff_radiobutton1:
										reward="请奶茶";
										break;
									case R.id.need_issue_page_payoff_radiobutton2:
										reward="请吃饭";
										break;
									case R.id.need_issue_page_payoff_radiobutton3:
										reward="请电影";
										break;
									case R.id.need_issue_page_payoff_radiobutton4:
										reward="神秘礼物";
										break;
									case R.id.need_issue_page_payoff_radiobutton5:
										reward="丰厚红包";
										break;
									case R.id.need_issue_page_payoff_radiobutton6:
										reward="2枚大洋";
										break;
									case R.id.need_issue_page_payoff_radiobutton7:
										reward="5枚大洋";
										break;
									case R.id.need_issue_page_payoff_radiobutton8:
										reward="10枚大洋";
										break;
									case R.id.need_issue_page_payoff_radiobutton9:
										reward="20枚大洋";
										break;
								}
							else
							{
								reward=rewardEditText.getText().toString();
							}
							
							
							switch (viewHolder.type.getCheckedRadioButtonId()) 
							{
								case R.id.need_issue_page_type_radiobutton1:
									type=Constant.TYPE_ASK;
									break;
								case R.id.need_issue_page_type_radiobutton2:
									type=Constant.TYPE_BORROW;
									break;
								case R.id.need_issue_page_type_radiobutton3:
									type=Constant.TYPE_INVITE;
									break;
								case R.id.need_issue_page_type_radiobutton4:
									type=Constant.TYPE_BRING;
									break;
								case R.id.need_issue_page_type_radiobutton5:
									type=Constant.TYPE_BUY;
									break;
							}
							//-1服务器不处理这个数字
							Need need=new Need(-1, userId, content, time, lng, lat, timeLimit, reward, type, 0, 0, Constant.NOT_SOLVED);
							
							String result = ServerCommunication.requestWithoutEncrypt(need, URLConstant.ISSUE_NEED_URL);
							
							Message msg = mHandler.obtainMessage();
							if(result!=null && result.equals("true"))
								msg.what = 1;
							else
								msg.what = 0;
							msg.sendToTarget();
							
						} catch (PostException e) {
							Message msg = mHandler.obtainMessage();
							msg.what = Constant.CONNECT_ERROR;
							msg.obj=e.getMessage();
							msg.sendToTarget();
							return;
							
						}catch (Exception e) {
							// TODO: handle exception
						}
						
					}
					
				}).start();
				
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				IssueNeedActivity.this.finish();
			}
		});
		
	}
	
	private void findView() {
		// TODO Auto-generated method stub
		
	}

	@SuppressLint({ "ShowToast", "HandlerLeak" }) 
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				MyToast.showToast(IssueNeedActivity.this, "发布失败~", true);
				break;
			case 1:
				String userSex = new SPHelper(IssueNeedActivity.this).get(
						"sex").toString();
				String toastString = "发布成功,";
				if(userSex.equals("男")){
					toastString+="附近30位妹纸已收到通知";
				}else{
					toastString+="附近30位帅哥已收到通知";
				}
				MyToast.showToast(IssueNeedActivity.this, toastString, true);
				NeedFragment.isRefresh=true;
				NeedFragment.context.onResume();
				int userid = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
				new AddLoveNum(IssueNeedActivity.this, 2, userid).execute();
				IssueNeedActivity.this.finish();
				break;
			case Constant.CONNECT_ERROR:
				MyToast.showToast(getContext(),(String)msg.obj);
				break;
			default:
				break;
			}
			loadingDialogDismiss();
		}
	};
	public class viewHolderToDatabase
	{
		EditText needDescribtion;
		RadioGroup timeLimit;
		RadioGroup payoff;
		RadioGroup type;
	}
}
