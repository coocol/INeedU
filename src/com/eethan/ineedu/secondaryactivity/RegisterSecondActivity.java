package com.eethan.ineedu.secondaryactivity;


import java.io.File;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.util.BitmapSaver;
import com.eethan.ineedu.util.WebImageSaver;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RegisterSecondActivity extends BaseActivity{

	private Button nextButton;
	private ImageButton backButton;
	private ImageButton headPicButton;
	private TextView defaultHeadTextView;
	
	private EditText realname;
	private EditText nickname;
	
	private String realnameString;
	private String nicknameString;
	private String sexString="女";
	
	private RadioGroup sexRadioGroup;
	private RadioButton sex_boy;
	private RadioButton sex_girl;
	
	private View focusView = null;
	private boolean hasDefineHead=false;
	private static final String TAG = "UploadActivity";
	File file;
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int RESULT_REQUEST_CODE = 2;
	private static final int USE_DEFAULT_HEAD = 3;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register2);
		
		
		sexRadioGroup=(RadioGroup)findViewById(R.id.register2__rg_sex);
		sex_boy = (RadioButton) findViewById(R.id.register2_rb_boy);
	    	sex_girl = (RadioButton) findViewById(R.id.register2_rb_girl);
	    	sex_girl.setChecked(true);
		
		findView();
	}
	
	private void findView() {
		// TODO Auto-generated method stub
		realname = (EditText) findViewById(R.id.register2_et_realname);
		nickname = (EditText) findViewById(R.id.register2_et_nickname);
		headPicButton=(ImageButton)findViewById(R.id.register2_bt_head);
		
		defaultHeadTextView=(TextView)findViewById(R.id.default_head);
		defaultHeadTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//startActivityForResult(new Intent(getContext(),DefaultHeadChooseActivity.class),USE_DEFAULT_HEAD);
				Intent intentFromGallery = new Intent();
				intentFromGallery.setType("image/*"); // 设置文件类型
				intentFromGallery
						.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intentFromGallery,
						0);
			}
		});
		headPicButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intentFromGallery = new Intent();
				intentFromGallery.setType("image/*"); // 设置文件类型
				intentFromGallery
						.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intentFromGallery,
						0);
			}
		});
		sexRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				// TODO Auto-generated method stub
				if(R.id.register2_rb_boy==checkedId)
					sexString="男";
				if(R.id.register2_rb_girl==checkedId)
					sexString="女";
			}
			
		});
		
		backButton = (ImageButton)findViewById(R.id.register2_bt_back);
		backButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RegisterSecondActivity.this.finish();
			}
			
		});
		
		nextButton = (Button)findViewById(R.id.register2_bt_next);
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!hasDefineHead)
				{
//					MyToast.showToast(RegisterSecondActivity.this, "请设置您的头像!");
//					return;
				}
				if(this.checkInfo()) {
					Intent intent = getIntent();
					//使用Intent对象得到FirstActivity传递来的参数
					String phoneString = intent.getStringExtra("phoneString");
					String pwdString = intent.getStringExtra("pwdString");
					String emailString = intent.getStringExtra("emailString");
					
					Intent intent2 = new Intent(RegisterSecondActivity.this,RegisterThirdActivity.class); 
			    	//Intent传递参数
					intent2.putExtra("phoneString", phoneString);
					intent2.putExtra("pwdString", pwdString);
					intent2.putExtra("emailString", emailString);
					intent2.putExtra("nicknameString", nicknameString);
					intent2.putExtra("realnameString", realnameString);
					intent2.putExtra("sexString", sexString);
					
					startActivity(intent2);
				}
				if(!this.checkInfo()) {
		    		focusView.requestFocus();
		    		return;
		    	}
			}
			/**
		     * 如果所有的信息填写正确,返回true,否则false
		     * @return
		     */
			private boolean checkInfo() {

				realnameString = realname.getText().toString().trim();
				nicknameString = nickname.getText().toString().trim();
				
				if(realnameString.length()>5||realnameString.length()<2) {
					realname.setError(Html.fromHtml("<font color=#E10979>请输入真实姓名</font>"));
					focusView = realname;
					return false;
				}
				if(nicknameString.length()<1) {
					nickname.setError(Html.fromHtml("<font color=#E10979>昵称不能为空</font>"));
					focusView = realname;
					return false;
				}
				if(nicknameString.length()>5) {
					nickname.setError(Html.fromHtml("<font color=#E10979>昵称不能超过五个字</font>"));
					focusView = realname;
					return false;
				}
				return true;
			}
		});
	}

	
	
	/**
	 * obtain locate face image
	 */
	protected void obtainHead(){
		Intent intent = getIntent();
		final int id=intent.getIntExtra("id",-1);
		String lfm="sdcard/DCIM/"+id+".png";
		File myfacefile=new File(lfm);
		if (myfacefile.exists()) {
			Bitmap maybit=BitmapSaver.getImageFromSDCard(lfm);
			Drawable drawa=new BitmapDrawable(maybit);
			headPicButton.setBackgroundDrawable(drawa);
		}
	}
	/**
	 * onActvityResult method
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					try {
						getImageToView(data);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			case USE_DEFAULT_HEAD:
				Bundle bundle = data.getExtras();
				final String imageUrl = bundle.getString("ImageURL");
				final String pathAndName = Constant.HEAD_PATH+"big_-1.png";
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							WebImageSaver.save(imageUrl, pathAndName);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				
				imageLoader.displayImage(imageUrl, headPicButton, getHeadDisplayOption());
				hasDefineHead=true;
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}
	/**
     * translate the picture to a round one
     */
	 public Bitmap toRoundBitmap(Bitmap bitmap) {
         int width = bitmap.getWidth();
         int height = bitmap.getHeight();
         float roundPx;
         float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
         if (width <= height) {
                 roundPx = width / 2;
                 top = 0;
                 bottom = width;
                 left = 0;
                 right = width;
                 height = width;
                 dst_left = 0;
                 dst_top = 0;
                 dst_right = width;
                 dst_bottom = width;
         } else {
                 roundPx = height / 2;
                 float clip = (width - height) / 2;
                 left = clip;
                 right = width - clip;
                 top = 0;
                 bottom = height;
                 width = height;
                 dst_left = 0;
                 dst_top = 0;
                 dst_right = height;
                 dst_bottom = height;
         }
          
         Bitmap output = Bitmap.createBitmap(width,
                         height, Config.ARGB_8888);
         Canvas canvas = new Canvas(output);
          
         final int color = 0xff424242;
         final Paint paint = new Paint();
         final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
         final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
         final RectF rectF = new RectF(dst);

         paint.setAntiAlias(true);
          
         canvas.drawARGB(0, 0, 0, 0);
         paint.setColor(color);
         canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

         paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
         canvas.drawBitmap(bitmap, src, dst, paint);
         return output;
 }
	 /**
		 * 保存裁剪之后的图片数据为文件并上传、更改
		 * 
		 * @param picdata
		 * @throws Exception 
		 */
	@SuppressLint("NewApi")
	private void getImageToView(Intent data) throws Exception {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Intent intent = getIntent();
			Bitmap photo = extras.getParcelable("data");
			
			Drawable drawable = new BitmapDrawable(toRoundBitmap(photo));
			headPicButton.setBackgroundDrawable(drawable);
			//uploadFile(file);暂时先不上传
			String bigHeadName="big_-1.png";
			BitmapSaver.saveImage(photo, bigHeadName);//保存大头像

			hasDefineHead=true;
		}
	}
		
	
}
