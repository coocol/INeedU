package com.eethan.ineedu.secondaryactivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Album;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.primaryactivity.BaseActivity;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.primaryactivity.TakeOnePhotoActivity;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.UploadHelper;
import com.eethan.ineedu.util.WebTime;

public class UploadMyPhotoActivity extends BaseActivity {
	
	private ImageView photoImgVu;
	private ImageButton backBtn;
	private Button uploadButton;
	private Bitmap theBitmap;
	
	public final int ISSUE_OK = 1;
	public final int ISSUE_FAILED = 0;

	
	@SuppressLint("HandlerLeak")
	private Handler resultHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			loadingDialogDismiss();
			switch (msg.what) {
			case ISSUE_OK:
				MyToast.showToast(UploadMyPhotoActivity.this, "上传成功~", false);
				Intent intent = new Intent(UploadMyPhotoActivity.this,EditMyInformationActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
				startActivity(intent);
				break;
			case ISSUE_FAILED:
				MyToast.showToast(UploadMyPhotoActivity.this, "上传失败~", true);
				break;
			case Constant.CONNECT_ERROR:
				MyToast.showToast(UploadMyPhotoActivity.this, (String) msg.obj,
						true);
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_uploadphoto);
		photoImgVu = (ImageView) findViewById(R.id.iv_photo);
		backBtn = (ImageButton) findViewById(R.id.ib_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		uploadButton = (Button) findViewById(R.id.btn_upload);
		uploadButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (photoImgVu.getDrawable() == null || theBitmap==null) {
					MyToast.showToast(getApplicationContext(), "请添加图片", true);
					return;
				}
				loadingDialogShow();
				new Thread(){
					@Override
					public void run() {
						long time;
						try {
							time = WebTime.getTime();
						} catch (Exception e) {
							time =  DateUtil.getMSTime();
						}
						int userId = getSharedPreferences(Constant.INEEDUSPR, 0)
								.getInt(Constant.ID, 0);
						
						UploadHelper.uploadAlbumPhoto(theBitmap,userId+"_"+time);
						Album album = new Album();
						album.setPhotoUrl(URLConstant.ALBUM_PHOTO_URL+userId+"_"+time+".jpg");
						album.setUserId(userId);
						
						String result;
						try {
							result = ServerCommunication.request(album,
									URLConstant.COMMIT_ALBUM_URL);
						} catch (PostException e) {
							e.printStackTrace();
							Message msg = resultHandler.obtainMessage();
							msg.what = Constant.CONNECT_ERROR;
							msg.obj = e.getMessage();
							msg.sendToTarget();
							return;
						}
						Message msg = resultHandler.obtainMessage();
						if (result!=null && result.equals("true"))
							msg.what = ISSUE_OK;
						else
							msg.what = ISSUE_FAILED;
						msg.sendToTarget();
					}
				}.start();
			}
		});
		Intent openCameraIntent = new Intent(
				Intent.ACTION_GET_CONTENT);
		Uri imageUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), "myphoto.jpg"));
		openCameraIntent.setType("image/*");
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent,
				0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if(requestCode==0){
			 ContentResolver resolver = getContentResolver();  
             //照片的原始资源地址  
             Uri originalUri = data.getData();
             try {  
                 //使用ContentProvider通过URI获取原始图片  
                 Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);  
                 if (photo != null) {  
                	 int oldHeight1 = photo.getHeight();
     				int oldWidth1 = photo.getWidth();
     				float scale = (float) 800 / oldHeight1;
                     //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存  
                     Bitmap smallBitmap = zoomBitmap(photo, (int) (oldWidth1 * scale),
     						(int) (oldHeight1 * scale));
                     //释放原始图片占用的内存，防止out of memory异常发生  
                     photo.recycle();  
                     FileOutputStream out = new FileOutputStream(new File(
 							Environment.getExternalStorageDirectory()
							+ "/myphoto.jpg"));
                     smallBitmap.compress(CompressFormat.JPEG, 90, out);
                     theBitmap = BitmapFactory.decodeFile(Environment
         					.getExternalStorageDirectory() + "/myphoto.jpg");
         			photoImgVu.setImageBitmap(theBitmap);
                 }  
             } catch (FileNotFoundException e) {  
                 e.printStackTrace();  
                 MyToast.showToast(UploadMyPhotoActivity.this, "获取图片出错了");
             } catch (IOException e) {  
                 e.printStackTrace();  
                 MyToast.showToast(UploadMyPhotoActivity.this, "获取图片出错了");
             }    
		}else if(requestCode==1){
			Bitmap cameraBitmap = BitmapFactory.decodeFile(Environment
					.getExternalStorageDirectory() + "/myphoto.jpg");
			if (null != cameraBitmap) {
				int oldHeight = cameraBitmap.getHeight();
				int oldWidth = cameraBitmap.getWidth();
				float scale = (float) 800 / oldHeight;
				Bitmap bitmap = zoomBitmap(cameraBitmap, (int) (oldWidth * scale),
						(int) (oldHeight * scale));
				FileOutputStream out;
				try {
					out = new FileOutputStream(new File(
							Environment.getExternalStorageDirectory()
									+ "/myphoto.jpg"));
					bitmap.compress(CompressFormat.JPEG, 90, out);
					 theBitmap = BitmapFactory.decodeFile(Environment
	         					.getExternalStorageDirectory() + "/myphoto.jpg");
	         			photoImgVu.setImageBitmap(theBitmap);
				} catch (Exception e) {
					MyToast.showToast(UploadMyPhotoActivity.this, "拍照出错了");
				}
			}
		}
		
	}

	public static Bitmap zoomBitmap(Bitmap oldBitmap, int newWidth,
			int newHeight) {
		// 获得图片的宽高
		int width = oldBitmap.getWidth();
		int height = oldBitmap.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(oldBitmap, 0, 0, width, height,
				matrix, true);
		oldBitmap.recycle();
		return newbm;
	}
}
