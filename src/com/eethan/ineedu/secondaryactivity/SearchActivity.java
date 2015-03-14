package com.eethan.ineedu.secondaryactivity;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.adapter.NearFragmentAdapter;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.UserDetailInfo;
import com.eethan.ineedu.databasebeans.UserInfo;
import com.eethan.ineedu.databasebeans.UserLocation;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.NearUserJsonObject;
import com.eethan.ineedu.jackson.SearchUserJsonObject;
import com.eethan.ineedu.model.NearFragmentModel;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.DataTraslator;

public class SearchActivity extends BaseActivity{

	private EditText searchText;
	private TextView detailTextView;
	private ImageButton back;
	private ListView resultListView;
	
	private ArrayList<UserInfo> userInfos=new ArrayList<UserInfo>();
	private ArrayList<UserLocation> userLocations=new ArrayList<UserLocation>();
	private ArrayList<UserDetailInfo> userDetailInfos=new ArrayList<UserDetailInfo>();
	private ArrayList<NearFragmentModel> userModels=new ArrayList<NearFragmentModel>();
	NearFragmentAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("SearchActivity");
		setContentView(R.layout.search_page);
		findView();
		setListener();
		adapter=new NearFragmentAdapter(SearchActivity.this, userModels);
		resultListView.setAdapter(adapter);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SearchActivity.this.finish();
			}
		});
		
		searchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			    //s:变化后的所有字符
				new GetDataTask(s.toString()).execute();
			   }

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
		resultListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				// TODO Auto-generated method stub
				if(id<-1) { 
			        // 点击的是headerView或者footerView 
			        return; 
			    } 
				int realPosition=(int)id; 
				
				new HeadClickEvent(SearchActivity.this,Integer.parseInt(userModels.get(realPosition)
						.getId())).click();
			}
		});
	}

	private void findView() {
		// TODO Auto-generated method stub
		searchText=(EditText)findViewById(R.id.search_page_search_text);
		detailTextView=(TextView)findViewById(R.id.search_page_detail);
		back=(ImageButton)findViewById(R.id.search_page_back_button);
		resultListView=(ListView)findViewById(R.id.search_page_listview);
	}

	private class GetDataTask extends AsyncTask<Void, Void, Object> {
		private String searchString;
		
		public GetDataTask(String searchString)
		{
			this.searchString=searchString;
		}
		//后台线程操作
		@Override
		protected Object doInBackground(Void... params) {
			//后台获取数据
			String response;
			try {
				response = ServerCommunication.requestWithoutEncrypt(searchString, URLConstant.SEARCH_USER_URL_NEW);
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			try {
				NearUserJsonObject result = JacksonUtil.json().fromJsonToObject(response,NearUserJsonObject.class);
				return result;
			} catch (Exception e) {
				return null;
			}
			
		}

		@Override
		protected void onPostExecute(Object result) {
			loadingDialogDismiss();
			if(result==null)
			{
				userModels.clear();
				adapter.notifyDataSetChanged();
				super.onPostExecute(result);
				return;
			}
			if(!ServerCommunication.checkResult(getContext(), result))
			{
				MyToast.showToast(getContext(), (String)result);
				loadingDialogDismiss();
				super.onPostExecute(result);
				return;
			}
			
			
			NearUserJsonObject r=(NearUserJsonObject)result;
			userInfos=(ArrayList<UserInfo>)r.getUserInfos();
			userLocations=(ArrayList<UserLocation>) r.getUserLocations();
			userDetailInfos = (ArrayList<UserDetailInfo>) r.getUserDetailInfos();
			if(userInfos==null||userInfos.size()==0)
				{
					userModels.clear();
					adapter.notifyDataSetChanged();
					super.onPostExecute(result);
					return;
				}
			
			userModels.clear();
			for(int i=0;i<userInfos.size();i++)
			{
				NearFragmentModel near=new NearFragmentModel();
				near.setName(userInfos.get(i).getNickName());
				near.setLoveNum(userInfos.get(i).getLoveNum());
				near.setId(userInfos.get(i).getUserId()+"");
				near.setRealName(userInfos.get(i).getRealName());
				near.setLoveNum(userInfos.get(i).getLoveNum());
				near.setTime(userLocations.get(i).getTime());
				near.setSex(userInfos.get(i).getSex());
				near.setAcademy(userDetailInfos.get(i).getAcademy());
				double lat,lng,distance;
				lat=userLocations.get(i).getLat();
				lng=userLocations.get(i).getLng();
				distance=DataTraslator.GetDistanceToMe(SearchActivity.this, lat, lng);
				
				near.setDistant((int)distance);
				
				userModels.add(near);
			}
			
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}
}
