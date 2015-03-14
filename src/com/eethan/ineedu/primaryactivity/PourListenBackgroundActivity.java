package com.eethan.ineedu.primaryactivity;


import java.util.ArrayList;


import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.StringArrayJsonObject;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.secondaryactivity.CustomSearchActivity;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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

public class PourListenBackgroundActivity extends BaseActivity{
	
	public static ArrayList<String> imageUrls=new ArrayList<String>();
	DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private int pos;//选中图片的位置
	
	TextView custom;
	ImageView back;
	StringArrayJsonObject jsonObject;
	String[] bgs;
	PullToRefreshGridView gridView;
	ImageAdapter adapter;
	boolean isCustom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setPageName("PourlistenBackgroundActivity");
		setContentView(R.layout.pour_listen_bg_choose_page);
		findView();
			
		if(!isCustom)
			options = new DisplayImageOptions.Builder()  
		      .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
		      .cacheOnDisc(true)    // 设置下载的图片是否缓存在SD卡中
	//      .showImageForEmptyUri(R.drawable.logo)  // 设置图片Uri为空或是错误的时候显示的图片  
	//      .showImageOnFail(R.drawable.logo)       // 设置图片加载或解码过程中发生错误显示的图片      
	      //.displayer(new RoundedBitmapDisplayer(100))  // 设置成圆角图片  
	//      .imageScaleType(ImageScaleType.EXACTLY)
	      .build(); 
		else
			options = new DisplayImageOptions.Builder()  
	    .build(); 
		
		}
	
	private void findView() {
		// TODO Auto-generated method stub
		isCustom=getIntent().getExtras().getBoolean("isCustom");
		if(!isCustom)//非自定义
		{
			imageUrls.clear();
			new GetDataTask().execute();
		}
		
		back=(ImageView)findViewById(R.id.pour_listen_bg_choose_page_back_button);
		custom=(TextView)findViewById(R.id.pour_listen_bg_choose_page_define_button);
		if(isCustom)//隐藏
			custom.setVisibility(View.GONE);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});		
//		custom.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Intent intent=new Intent();
//				intent.setClass(PourListenBackgroundActivity.this, CustomSearchActivity.class);
//				startActivity(intent);
//			}
//		});	
		
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
				pos=realPosition;
				Intent intent=new Intent();
				intent.putExtra("imageUrl", imageUrls.get(pos));
				intent.putExtra("isLocal", false);
				int num=2;
				if(isCustom)
					num=4;
				intent.putExtra("exitPageNum",num);
				intent.setClass(PourListenBackgroundActivity.this, IssuePourListenActivity.class);
				startActivity(intent);
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
			imageLoader.displayImage(imageUrls.get(position), imageView, options);

			return imageView;
		}
	}
	private class GetDataTask extends AsyncTask<Void, Void, Object> {
		@Override
		protected Object doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String response;
			try {
				response = ServerCommunication.request("new version", URLConstant.GET_BACKGROUND_URL);
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
				MyToast.showToast(getContext(),"加载失败");
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
				MyToast.showToast(PourListenBackgroundActivity.this, "暂时没有图片");
				return;
			}
			imageUrls.clear();
			for(int i=0;i<bgs.length;i++)
				imageUrls.add(URLConstant.BG_URL+bgs[i]);
			
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}
	
	
}