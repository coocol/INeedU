package com.eethan.ineedu.mycontrol;
import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.eethan.ineedu.primaryactivity.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class LoadingDialog extends Dialog{
		GifView gf1;
		public LoadingDialog(Context context) {
			super(context,R.style.MyDialog);
		// TODO Auto-generated constructor stub
	}
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				// TODO Auto-generated method stub
				super.onCreate(savedInstanceState);
				setContentView(R.layout.loading_page_black);
//				WindowManager.LayoutParams lp=this.getWindow().getAttributes();
//				lp.dimAmount=0.0f;
//				this.getWindow().setAttributes(lp);//设置黑暗度为0
				setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失 
				
				
				gf1 = (GifView) findViewById(R.id.gif1);  
			    // 设置Gif图片源  
			    gf1.setGifImage(R.drawable.loading_black);   
			    // 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示  
			    gf1.setGifImageType(GifImageType.COVER);
		}
}