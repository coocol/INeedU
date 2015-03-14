package com.eethan.ineedu.fragment;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.eethan.ineedu.CommonUse.HeadClickEvent;
import com.eethan.ineedu.CommonUse.SPHelper;
import com.eethan.ineedu.adapter.SlidingMenuFragmentAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.model.SlidingMenuFragmentModel;
import com.eethan.ineedu.mycontrol.CircleBitmapDisplayer;
import com.eethan.ineedu.primaryactivity.MainActivity;
import com.eethan.ineedu.primaryactivity.NotificationActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.primaryactivity.SettingsActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

public class SlidingMenuFragment extends Fragment{

	///成员变量
	private ImageButton headPic;
	private ImageButton noticeButton;
	private ImageButton settingImageButton;
	private TextView realName;
	private ListView lv;
	private ArrayList<SlidingMenuFragmentModel> smfms;
	private SlidingMenuFragmentModel smfm;
	
	private SharedPreferences myPreferences;
	
	private int selected = -1;
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View slidingMenuFragmentView = inflater.inflate(R.layout.sidebar_page,null);
		realName=(TextView)slidingMenuFragmentView.findViewById(R.id.sidebar_page_userName);
		headPic=(ImageButton)slidingMenuFragmentView.findViewById(R.id.sidebar_page_headpic_button);
		noticeButton=(ImageButton)slidingMenuFragmentView.findViewById(R.id.sidebar_page_notice_button);
		settingImageButton = (ImageButton)slidingMenuFragmentView.findViewById(R.id.sidebar_page_setting_button);
		myPreferences = getActivity().getSharedPreferences(Constant.INEEDUSPR, getActivity().MODE_PRIVATE);
		realName.setText(myPreferences.getString(Constant.REALNAME, "获取姓名失败"));
		
		options = new DisplayImageOptions.Builder()  
//        .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中  
//        .cacheOnDisc(true)    // 设置下载的图片是否缓存在SD卡中
//        .showImageForEmptyUri(R.drawable.logo)  // 设置图片Uri为空或是错误的时候显示的图片  
//        .showImageOnFail(R.drawable.logo)       // 设置图片加载或解码过程中发生错误显示的图片      
        .displayer(new CircleBitmapDisplayer())  // 设置成圆角图片  
        .imageScaleType(ImageScaleType.EXACTLY)
        .build(); 
		
		//headPic.setBackgroundDrawable(Header.getHead(myPreferences.getInt(Constant.ID, -1)));
//		String lfm="sdcard/DCIM/"+myPreferences.getInt(Constant.ID, -1)+".png";
//		File myfacefile=new File(lfm);
//		if (myfacefile.exists()) {
//			Bitmap maybit=BitmapSaver.getImageFromSDCard(lfm);
//			Drawable drawa=new BitmapDrawable(maybit);
//			headPic.setBackgroundDrawable(drawa);
//		}
//		else {
//			headPic.setBackgroundResource(R.drawable.logo);
//		}
		
		imageLoader.displayImage(URLConstant.BIG_HEAD_PIC_URL+myPreferences.getInt(Constant.ID, -1)+".png",headPic, options);
		
		lv=(ListView)slidingMenuFragmentView.findViewById(R.id.sidebar_page_listview);
		SlidingMenuFragmentAdapter SMFA = new SlidingMenuFragmentAdapter(getActivity().getApplicationContext(),getSideList());
		lv.setCacheColorHint(0);
        lv.setAdapter(SMFA);
        
        if(selected!=-1){
        	lv.setItemChecked(selected, true);  
        	lv.setSelection(selected);  
        }else{
        	lv.setItemChecked(0, true);  
        	lv.setSelection(0);  
        }
        
        
//        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        lv.setItemChecked(1, true);
//        lv.setSelection(0);
        findView(slidingMenuFragmentView);
        
		return slidingMenuFragmentView;
	}
	
	
	private void findView(View slidingMenuFragmentView) {
		// TODO Auto-generated method stub
		//设置
		settingImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().startActivity(new Intent(getActivity().getApplicationContext(), SettingsActivity.class));
			}
		});
		//头像
		headPic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new HeadClickEvent(getActivity(), new SPHelper(getActivity()).GetUserId()).click();
			}
		});
		//通知
		noticeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getActivity().startActivity(new Intent(getActivity(),NotificationActivity. class));
			}
		});
		 lv.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
						long id) {
					// TODO Auto-generated method stub
					if(id<-1) { 
				        // 点击的是headerView或者footerView 
				        return; 
				    } 
					int realPosition=(int)id;
					switch (realPosition) {
					case 0:
						MainFragment.TYPE=Constant.TYPE_ALL;
						break;
					case 1:
						MainFragment.TYPE=Constant.TYPE_ASK;
						break;
					case 2:
						MainFragment.TYPE=Constant.TYPE_BORROW;
						break;
					case 3:
						MainFragment.TYPE=Constant.TYPE_INVITE;
						break;
					case 4:
						MainFragment.TYPE=Constant.TYPE_BRING;
						break;
					case 5:
						MainFragment.TYPE=Constant.TYPE_BUY;
						break;
					default:
						break;
					}
					
					lv.setItemChecked(realPosition, true);  
					lv.setSelection(realPosition);  
			        
			       
			        selected = realPosition;
				        
					MainActivity.GetSlidingMenu().toggle();
					NeedFragment.isRefresh=true;
					NeedFragment.context.onResume();
				}
			});
	}
	
	
	private ArrayList<SlidingMenuFragmentModel> getSideList(){
		smfms = new ArrayList<SlidingMenuFragmentModel>();
		//创建实体
		smfm=new SlidingMenuFragmentModel();
		smfm.setOption("Let's meet~");
		smfm.setPicLocation(R.drawable.common_home_normal+"");
		smfms.add(smfm);
		
		smfm=new SlidingMenuFragmentModel();
		smfm.setOption("Ask");
		smfm.setPicLocation(R.drawable.common_ask_normal+"");
		smfms.add(smfm);
		
		smfm=new SlidingMenuFragmentModel();
		smfm.setOption("Borrow");
		smfm.setPicLocation(R.drawable.common_borrow_normal+"");
		smfms.add(smfm);
		
		smfm=new SlidingMenuFragmentModel();
		smfm.setOption("Date");
		smfm.setPicLocation(R.drawable.common_invite_normal+"");
		smfms.add(smfm);
		
		smfm=new SlidingMenuFragmentModel();
		smfm.setOption("Bring");
		smfm.setPicLocation(R.drawable.common_bring_normal+"");
		smfms.add(smfm);
		
		smfm=new SlidingMenuFragmentModel();
		smfm.setOption("Buy");
		smfm.setPicLocation(R.drawable.common_buy_normal+"");
		smfms.add(smfm);
		return smfms;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("SlidingMenuFragment"); //统计页面
		
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("SlidingMenuFragment"); 
	}
}
