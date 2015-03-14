package com.eethan.ineedu.primaryactivity;


import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.MyIntent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.CommonUse.URLHelper;
import com.eethan.ineedu.Enum.Scope;
import com.eethan.ineedu.constant.MatchCondition;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.WakeUp;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.WakeUpJsonObject;
import com.eethan.ineedu.jackson.WakeUpMatchJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DataTraslator;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class WakeUpActivity extends BaseActivity{
	ImageButton back;
	ImageButton require;
	ImageButton matchButton;
	ToggleButton hideName;
	
	TextView boyNum;
	TextView girlNum;
	TextView totalNum;
	TextView rule;
	TextView matchTextView;
	
	RelativeLayout pairLayout;
	ImageView pairHead;
	ImageView pairSex;
	TextView pairTele;
	TextView pairRequire;
	Button removePair;
	
	WakeUpJsonObject dataSource = new WakeUpJsonObject();
	int pairId = 0;
	boolean isHidden = false;
	int matchCondition = MatchCondition.NOT_MATCH;
	ImageLoader imageLoader = ImageLoader.getInstance();
	

	protected static Scope scope = Scope.NEAR;
	protected static String requireString = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wakeup_main);
		loadingDialogShow();
		findView();
		new GetDataTask().execute();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		new SPHelper(getContext()).put(SPHelper.REQUIRE, requireString);
	}
	private void findView() {
		// TODO Auto-generated method stub
		back = (ImageButton) findViewById(R.id.imgbt_back);
		require = (ImageButton) findViewById(R.id.imgbt_requirement);
		matchButton = (ImageButton) findViewById(R.id.imgbt_match);
		hideName = (ToggleButton) findViewById(R.id.tgbt_anonymity);
		
		boyNum = (TextView) findViewById(R.id.tv_boynum);
		girlNum = (TextView) findViewById(R.id.tv_girlnum);
		totalNum = (TextView) findViewById(R.id.tv_total);
		rule = (TextView) findViewById(R.id.tv_rule);
		matchTextView = (TextView) findViewById(R.id.tv_match);
		
		pairLayout = (RelativeLayout) findViewById(R.id.re_partner);
		pairHead = (ImageView) findViewById(R.id.img_head);
		pairSex = (ImageView) findViewById(R.id.img_sex);
		pairTele = (TextView) findViewById(R.id.tv_telephone);
		pairRequire = (TextView) findViewById(R.id.tv_requirement);
		removePair = (Button) findViewById(R.id.bt_remove);
		
		requireString = (String) new SPHelper(getContext()).get(SPHelper.REQUIRE);
		
		removePair.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyTakeDialog myTakeDialog = new MyTakeDialog(getContext()){
					@Override
					public void onYesButtonClick() {
						if(matchCondition == MatchCondition.MATCH_CHOOSE)
						{
							SPHelper spHelper = new SPHelper(getContext());
							int matchTime = (Integer) spHelper.get(SPHelper.MATCH_TIME);
							if(matchTime < MatchCondition.MAX_MATCH_TIME)
							{
								confirmMatch();
								spHelper.put(SPHelper.MATCH_TIME, matchTime+1);
							}
							else{
								MyToast.showToast(getContext(), "今日匹配次数已用完...");
							}
							
						}
						else
							removePair();
						dismiss();
					};
					
				};
				switch (matchCondition) {
				case MatchCondition.MATCH_CHOOSE:
					myTakeDialog.setText("每日只有一次机会,确认建立关系?");
					break;
				case MatchCondition.MATCH_OK:
					myTakeDialog.setText("确认要解除关系?");
					break;
				default:
					break;
				}
				
				
				
				myTakeDialog.show();
				
			}
		});
		matchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(requireString.equals(""))
				{
					MyToast.showToast(getContext(), "请点击右上角设置要求内容~");
					return;
				}
				if((Integer)new SPHelper(getContext()).get(SPHelper.MATCH_TIME) >= MatchCondition.MAX_MATCH_TIME)
				{
					MyToast.showToast(getContext(), "今日匹配次数已用完~");
					return;
				}
				switch (matchCondition) {
				case MatchCondition.NOT_MATCH:
					match();
					break;
				case MatchCondition.MATCHING:
					MyToast.showToast(getContext(), "正在匹配中,请耐心等待~");
					break;
				case MatchCondition.MATCH_OK:
					reMatch();
					break;
				case MatchCondition.MATCH_CHOOSE:
					match();
					break;
				default:
					break;
				}
					
				
				
			}

			
		});
		pairHead.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(dataSource.getAuth() == 1)//不匿名允许看到信息
					new HeadClickEvent(getContext(), pairId).click();
				else
					MyIntent.toChatActivity(getContext(),
							new SPHelper(getContext()).GetUserId(),pairId,"", true);
			}
		});
		require.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(WakeUpActivity.this, WakeUpRequireActivity.class);
				startActivity(intent);
			}
		});
		hideName.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
            				isHidden = !isHidden;
                 }

        });
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	//确认关系
	private void confirmMatch(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int userId = new SPHelper(getContext()).GetUserId();
				JsonObject jsonObject = new JsonObject();
				jsonObject.setInt1(userId);
				jsonObject.setInt2(pairId);
				
				
				String response;
				int result;
				try {
					response = ServerCommunication.request(jsonObject, URLConstant.CONFIRM_WAKE_UP_URL);
					result = JacksonUtil.json().fromJsonToObject(response, int.class);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Looper.prepare();
					MyToast.showToast(getContext(), e.getMessage());
					Looper.loop();
					return;
				}
				matchCondition = result;
				Message message = myHandler.obtainMessage();
				message.what = matchCondition;
				message.sendToTarget();
			}
		}).start();
	}
	//移除关系
	private void removePair(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int userId = new SPHelper(getContext()).GetUserId();
				String response;
				Boolean result = false;
				try {
					response = ServerCommunication.request(userId, URLConstant.REMOVE_WAKE_UP_URL);
					result = JacksonUtil.json().fromJsonToObject(response, Boolean.class);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Looper.prepare();
					MyToast.showToast(getContext(), e.getMessage());
					Looper.loop();
					return;
				}
				Message message = myHandler.obtainMessage();
				if(result)
					message.what = MatchCondition.REMOVE_SUCCESS;
				else
					message.what = MatchCondition.REMOVE_FAILED;
				message.sendToTarget();
			}
		}).start();
	}
	private void reMatch() {
		// TODO Auto-generated method stub
		
	}
	//开始匹配
	private void match() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int userId = new SPHelper(getContext()).GetUserId();
				double lng = LocateManager.getInstance().getLontitude();
				double lat = LocateManager.getInstance().getLatitude();
				int auth;//1代表其他人有允许查看资料的权限，0为匿名
				if(isHidden)
					auth = 0;
				else
					auth = 1;
				String aquirement = requireString;
				String date = DataTraslator.getDateNow();
				
				String scopeString = scope.getType();
				String sex = (String) new SPHelper(getContext()).get(SPHelper.SEX);
				
				
				WakeUp wakeUp = new WakeUp(userId, sex, auth, aquirement, scopeString, date, 0);
				WakeUpMatchJsonObject jsonObject = 
						new WakeUpMatchJsonObject(wakeUp,"", 0, lng, lat);
				
				String response;
				WakeUpMatchJsonObject result = null;
				String URL = "";
				if(matchCondition == MatchCondition.NOT_MATCH)
					URL = URLConstant.MATCH_WAKE_UP_URL;
				if(matchCondition == MatchCondition.MATCH_CHOOSE)
					URL = URLConstant.CHOOSE_MATCH_URL;
				try {
					response = ServerCommunication.request(jsonObject, URL);
					result = JacksonUtil.json().fromJsonToObject(response, WakeUpMatchJsonObject.class);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Looper.prepare();
					MyToast.showToast(getContext(), e.getMessage());
					Looper.loop();
					return;
				}
				
				matchCondition = result.matchCondition;
				pairId = result.wakeUp.getUserId();
				dataSource.setPairTele(result.tele);
				dataSource.setSex(result.wakeUp.getSex());
				dataSource.setRequire(result.wakeUp.getAquirement());
				dataSource.setAuth(result.wakeUp.getAuth());
				
				Message message = myHandler.obtainMessage();
				message.what = matchCondition;
				message.sendToTarget();
			}
		}).start();
	}
	//统一信息处理
	@SuppressLint("HandlerLeak")
	private Handler myHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg) {
			String sex;
			switch (msg.what) {
			case MatchCondition.REMOVE_SUCCESS:
				MyToast.showToast(getContext(), "解除关系成功~");
				break;
			case MatchCondition.REMOVE_FAILED:
				MyToast.showToast(getContext(), "解除失败!请检查网络后再试");
				break;
			case MatchCondition.NOT_MATCH:
				removePair.setVisibility(View.VISIBLE);
				matchTextView.setText("加入匹配");
				matchButton.setClickable(true);
				pairLayout.setVisibility(View.INVISIBLE);
				rule.setVisibility(View.GONE);
				break;
			case MatchCondition.MATCH_OK:
				matchTextView.setText("匹配成功~");
				matchButton.setClickable(false);
				removePair.setText("解除关系");
				//匹配成功显示对象信息
				removePair.setVisibility(View.VISIBLE);
				pairLayout.setVisibility(View.VISIBLE);
				rule.setVisibility(View.GONE);
				
				imageLoader.displayImage(URLHelper.getHeadUrl(pairId), pairHead,getHeadDisplayOption());
				sex = dataSource.getSex();
				if(sex.equals("男"))
					pairSex.setImageResource(R.drawable.sex_boy_normal);
				else
					pairSex.setImageResource(R.drawable.sex_girl_normal);
				pairTele.setText(dataSource.getPairTele());
				pairRequire.setText("对方要求:"+dataSource.getRequire());
				MyToast.showToast(getContext(), "匹配成功~");
				break;
			case MatchCondition.MATCHING:
				matchTextView.setText("匹配中...");
				matchButton.setClickable(false);
				removePair.setVisibility(View.INVISIBLE);
				pairLayout.setVisibility(View.INVISIBLE);
				rule.setVisibility(View.GONE);
				MyToast.showToast(getContext(), "暂未匹配到合适对象,请耐心等候~");
				break;
			case MatchCondition.MATCH_CHOOSE:
				if(dataSource.getPairTele().equals(""))
					pairLayout.setVisibility(View.INVISIBLE);
				else
					pairLayout.setVisibility(View.VISIBLE);
				rule.setVisibility(View.GONE);
				removePair.setVisibility(View.VISIBLE);
				matchButton.setClickable(true);
				matchTextView.setText("重新匹配");
				String mySex = (String) new SPHelper(getContext()).get(SPHelper.SEX);
				if(mySex.equals("男"))
					removePair.setText("就是她了!");
				else
					removePair.setText("就是他了!");
				
				imageLoader.displayImage(URLHelper.getHeadUrl(pairId), pairHead,getHeadDisplayOption());
				sex = dataSource.getSex();
				if(sex.equals("男"))
					pairSex.setImageResource(R.drawable.sex_boy_normal);
				else
					pairSex.setImageResource(R.drawable.sex_girl_normal);
				pairTele.setText(dataSource.getPairTele());
				pairRequire.setText("对方要求:"+dataSource.getRequire());
				break;
			default:
				break;
			}
		};
	};
	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		
		@Override
		protected Object doInBackground(Void... params) {
			
				//后台获取数据
				int userId = new SPHelper(getContext()).GetUserId();
				
				String response=null,URL=URLConstant.SHOW_WAKE_UP_URL;
				WakeUpJsonObject wakeUpJsonObject = null;
				try {
					response = ServerCommunication.request(userId, URL);//发送请求，获得数据
					wakeUpJsonObject = 
							JacksonUtil.json().fromJsonToObject(response, WakeUpJsonObject.class);
				} catch (PostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return e.getMessage();
				}
				return wakeUpJsonObject;
			
		}

		
		@Override
		protected void onPostExecute(Object result) {
			if(result==null){
				MyToast.showToast(getContext(), "加载失败");
				loadingDialogDismiss();
				return;
			}
			if(!ServerCommunication.checkResult(getContext(), result))
			{
				MyToast.showToast(getContext(), (String)result);
				loadingDialogDismiss();
				return;
			}
			dataSource = (WakeUpJsonObject) result;
			
			matchCondition = dataSource.getMatchCondition();
			boyNum.setText(dataSource.getBoyNum()+"");
			girlNum.setText(dataSource.getGirlNum()+"");
			totalNum.setText("累计:"+dataSource.getTotalNum()+"人");
			pairId = dataSource.getPairId();
			
			Message message = myHandler.obtainMessage();
			message.what = matchCondition;
			message.sendToTarget();
			
			loadingDialogDismiss();
			super.onPostExecute(result);
		}
	}
	
}
