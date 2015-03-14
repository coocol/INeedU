package com.eethan.ineedu.mycontrol;
import com.ant.liao.GifView;
import com.ant.liao.GifView.GifImageType;
import com.eethan.ineedu.primaryactivity.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class LoginLoadingDialog extends Dialog{
		GifView gf1;
		TextView text;
		String content="正在登陆...";
		public LoginLoadingDialog(Context context) {
			super(context,R.style.MyDialog);
		// TODO Auto-generated constructor stub
	}
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				// TODO Auto-generated method stub
				super.onCreate(savedInstanceState);
				setContentView(R.layout.login_ing_page);
//				WindowManager.LayoutParams lp=this.getWindow().getAttributes();
//				lp.dimAmount=0.0f;
//				this.getWindow().setAttributes(lp);//设置黑暗度为0
				setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失 
				
				text=(TextView)findViewById(R.id.default_head);
				text.setText(content);
				
				gf1 = (GifView) findViewById(R.id.gif1);  
			    // 设置Gif图片源  
			    gf1.setGifImage(R.drawable.login_loading);  
			    // 设置显示的大小，拉伸或者压缩  
			    gf1.setShowDimension(256, 68);  
			    // 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示  
			    gf1.setGifImageType(GifImageType.COVER);
		}
		public void setText(String text)
		{
			content=text;
		}
}