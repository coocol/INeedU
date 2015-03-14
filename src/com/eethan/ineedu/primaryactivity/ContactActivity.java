package com.eethan.ineedu.primaryactivity;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.ContactAdapter;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Contact;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.jackson.ContactsJsonObject;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.model.ContactModel;
import com.eethan.ineedu.mycontrol.LoadingDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.ServerCommunication;

public class ContactActivity extends MyViewpagerActivity{

	///viewpager的适配器
	private ContactAdapter contactAdapter;
	//存放服务器获得的数据
	public static ArrayList<ContactModel> recents=new ArrayList<ContactModel>();
	public static ArrayList<ContactModel> generals=new ArrayList<ContactModel>();
	
	private ImageButton back;
	private LoadingDialog loadingDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loverank);
		loadingDialog=new LoadingDialog(this);
		loadingDialog.show();
		
		titleliLinearLayout = (LinearLayout)findViewById(R.id.title_linearlayout);
		viewPager = (ViewPager) findViewById(R.id.loverank_viewpager);
		new GetDataTask().execute();
		TextView title = (TextView)findViewById(R.id.tv_title);
		TextView firstTextView = (TextView)findViewById(R.id.weekrank_text);
		TextView secondTextView = (TextView)findViewById(R.id.sumrank_text);
		title.setText("我的联系人");
		firstTextView.setText("最近");
		secondTextView.setText("常用");

		
		back=(ImageButton) findViewById(R.id.love_rank_page_back_button);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		//后台线程操作
		@Override
		protected Object doInBackground(Void... params) {
			
			int userId = new SPHelper(getApplicationContext()).GetUserId();
			
			String response;
			ContactsJsonObject object = null;
			try {
				response = ServerCommunication.request(userId, URLConstant.REFRESH_CONTACT);
				object = JacksonUtil.json().fromJsonToObject(response,ContactsJsonObject.class);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			return object;
		}

		@Override
		protected void onPostExecute(Object result) {
			loadingDialog.dismiss();
			if(result==null){
				MyToast.showToast(ContactActivity.this, "加载失败");
				return;
			}
			if(!ServerCommunication.checkResult(ContactActivity.this, result))//各种网络异常的处理部分
			{
				MyToast.showToast(ContactActivity.this, (String)result);
				return;
			}
		
			ContactsJsonObject object = (ContactsJsonObject) result;
			List<Contact> recentContacts = object.getRecentContacts();
			List<Contact> generalContacts = object.getGeneralContacts();
			List<UserInfo> recentUserInfos = object.getRecentUserInfos();
			List<UserInfo> generalUserInfos = object.getGeneralUserInfos();
			
			if(recentContacts.size() == 0){
				MyToast.showToast(ContactActivity.this, "暂时没有联系人~");
				return;
			}
			
			recents.clear();
			generals.clear();
			for(int i=0;i<recentUserInfos.size();i++){
				int contactId;
				String name;
				String sex;
				
				contactId = recentContacts.get(i).contactId;
				name = recentUserInfos.get(i).getNickName();
				sex  = recentUserInfos.get(i).getSex();
				ContactModel model = new ContactModel(contactId,name,sex);
				recents.add(model);
				
				contactId = generalContacts.get(i).contactId;
				name = generalUserInfos.get(i).getNickName();
				sex  = generalUserInfos.get(i).getSex();
				model = new ContactModel(contactId,name,sex);
				generals.add(model);
			}

//			generals.clear();
//			for(int i=0;i<sumUserInfos.size();i++){
//				SumLoveRankFragmentModel sumContact=new SumLoveRankFragmentModel();
//				int userid = sumUserInfos.get(i).getUserId();
//				String realname=sumUserInfos.get(i).getRealName();
//				Integer loveNum=sumUserInfos.get(i).getLoveNum();
//				sumContact.setId(userid);
//				sumContact.setRankNum(i+1);
//				sumContact.setName(realname);
//				sumContact.setLoveNum(loveNum);
//				
//				double lat_b,lng_b;
//				//好友的经纬度
//				lat_b=sumUserLocations.get(i).getLat();
//				lng_b=sumUserLocations.get(i).getLng();
//				//距离
//				int s=DataTraslator.GetDistanceToMe(ContactActivity.this,lat_b,lng_b);
//				sumContact.setDistant(s);
//				
//				generals.add(sumContact);
//			}
			
			viewPager.setOffscreenPageLimit(2);/* 预加载页面 */
			contactAdapter = new ContactAdapter(getSupportFragmentManager());
			viewPager.setAdapter(contactAdapter);
			viewPager.setCurrentItem(0);
			viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
			//初始化页卡标题
			firstTextView = (TextView) findViewById(R.id.weekrank_text);
			secondTextView = (TextView) findViewById(R.id.sumrank_text);

			firstTextView.setOnClickListener(new MyOnClickListener(0));
			secondTextView.setOnClickListener(new MyOnClickListener(1));

			
			super.onPostExecute(result);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if(contactAdapter!=null){
			contactAdapter.notifyDataSetChanged();
		}

		super.onStart();
	}

}
