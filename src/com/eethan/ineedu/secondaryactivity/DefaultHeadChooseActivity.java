package com.eethan.ineedu.secondaryactivity;

import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.ToastHandler;

import java.util.ArrayList;


import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.StringArrayJsonObject;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DefaultHeadChooseActivity extends BaseActivity{
		public static ArrayList<String> imageUrls=new ArrayList<String>();
		protected ImageLoader imageLoader = ImageLoader.getInstance();
		ToastHandler toastHandler = new ToastHandler(DefaultHeadChooseActivity.this);
		TextView custom;
		ImageView back;
		StringArrayJsonObject jsonObject;
		String[] bgs;
		PullToRefreshGridView gridView;
		ImageAdapter adapter;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setPageName("DefaultHeadChooseActivity");
			setContentView(R.layout.default_head_choose);
			findView();
			
			new GetDataTask().execute();
			}
		
		private void findView() {
			// TODO Auto-generated method stub
			back=(ImageView)findViewById(R.id.pour_listen_bg_choose_page_back_button);
			custom=(TextView)findViewById(R.id.pour_listen_bg_choose_page_define_button);
			
			back.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					finish();
				}
			});		
			PullToRefreshGridView gridView=(PullToRefreshGridView) findViewById(R.id.pour_listen_bg_gridview);
			adapter=new ImageAdapter();
			gridView.setAdapter(adapter);
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(id<-1) { 
				        // 点击的是headerView或者footerView 
				        return; 
				    } 
					int realPosition=(int)id; 
					//选择图片响应
//					JsonObject jsonObject = new JsonObject();
//					jsonObject.setInt1(new SPHelper(getContext()).GetUserId());
//					jsonObject.setString1(bgs[realPosition]);
					
//					String response;
//					try {
//						response = ServerCommunication.request(jsonObject, URLConstant.USE_DEFAULT_HEAD_URL);
//					} catch (PostException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						Message message = toastHandler.obtainMessage();
//						message.what = Constant.CONNECT_FAILED;
//						message.sendToTarget();
//						return;
//					}
//					//是否成功
//					boolean result = JacksonUtil.json().fromJsonToObject(response, Boolean.class);
//					if(!result)
//					{
//						MyToast.showToast(getContext(), "连接服务器失败!");
//						return;
//					}
					//回调
					 Intent intent = new Intent(DefaultHeadChooseActivity.this,RegisterSecondActivity.class);
                     Bundle bundle = new Bundle();
                     bundle.putString("ImageURL", imageUrls.get(realPosition));
					 intent.putExtras(bundle);
                     setResult(RESULT_OK, intent);
                     finish();
					
				}
			});
		}

		public class ImageAdapter extends BaseAdapter {
			@Override
			public int getCount() {
				return imageUrls.size();
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final ImageView imageView;
				if (convertView == null) {
					imageView = (ImageView) getLayoutInflater().inflate(R.layout.pour_listen_bg_choose_page_gridview_item, parent, false);
				} else {
					imageView = (ImageView) convertView;
				}
				imageLoader.displayImage(imageUrls.get(position), imageView, getHeadDisplayOption());

				return imageView;
			}
		}
		private class GetDataTask extends AsyncTask<Void, Void, Object> {
			@Override
			protected Object doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				String response;
				try {
					response = ServerCommunication.request("new version", URLConstant.GET_DEFAULT_HEAD_URL);
					jsonObject = JacksonUtil.json().fromJsonToObject(response,StringArrayJsonObject.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				
				return jsonObject;
			}
			@Override
			protected void onPostExecute(Object result) {
				if(result==null){
					MyToast.showToast(getContext(), "加载失败");
					loadingDialogDismiss();
					super.onPostExecute(result);
					return;
				}
				// TODO Auto-generated method stub
				if(!ServerCommunication.checkResult(getContext(), result))//各种网络异常的处理部分
				{
					MyToast.showToast(getContext(), (String)result);
					loadingDialogDismiss();
					super.onPostExecute(result);
					return;
				}
				bgs=((StringArrayJsonObject)result).getStrings();
				if(bgs.length==0)
				{
					MyToast.showToast(getContext(), "暂时没有图片");
					return;
				}
				imageUrls.clear();
				for(int i=0;i<bgs.length;i++)
					imageUrls.add(URLConstant.DEFAULT_HEAD_FOLDER+bgs[i]);
				
				adapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}
		}
		
		
}
