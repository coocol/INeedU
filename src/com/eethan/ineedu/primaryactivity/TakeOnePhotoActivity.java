package com.eethan.ineedu.primaryactivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.eethan.ineedu.CommonUse.AddLoveNum;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.databasebeans.Mood;
import com.eethan.ineedu.databasebeans.TakePhotos;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DateUtil;
import com.eethan.ineedu.util.UploadHelper;
import com.eethan.ineedu.util.WebTime;

public class TakeOnePhotoActivity extends BaseActivity {

	private ImageView imageView;
	private Button finishBtn;
	private ImageButton backBtn;
	private EditText contentEditTxt;
	private Bitmap thePucBitmap;

	public final int ISSUE_OK = 1;
	public final int ISSUE_FAILED = 0;

	@SuppressLint("HandlerLeak")
	private Handler resultHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			loadingDialogDismiss();
			switch (msg.what) {
			case ISSUE_OK:
				MyToast.showToast(TakeOnePhotoActivity.this, "发布成功~", false);
				int userid = getSharedPreferences(Constant.INEEDUSPR, 0).getInt(Constant.ID, 0);
				new AddLoveNum(TakeOnePhotoActivity.this, 2, userid).execute();
				finish();
				break;
			case ISSUE_FAILED:
				MyToast.showToast(TakeOnePhotoActivity.this, "发布失败~", true);
				break;
			case Constant.CONNECT_ERROR:
				MyToast.showToast(TakeOnePhotoActivity.this, (String) msg.obj,
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
		setContentView(R.layout.takephotos_issue);

		imageView = (ImageView) findViewById(R.id.take_photo_photo);
		contentEditTxt = (EditText) findViewById(R.id.take_photo_content);
		finishBtn = (Button) findViewById(R.id.take_photo_finish);

		finishBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (imageView.getDrawable() == null || thePucBitmap==null) {
					MyToast.showToast(getApplicationContext(), "请添加图片", true);
					return;
				}
				if(contentEditTxt.getText().toString()==null || contentEditTxt.getText().toString().equals("")) {
					MyToast.showToast(getApplicationContext(), "请添加文字", true);
					return;
				}
				
//				Bitmap bmp = BitmapFactory.decodeFile(Environment
//						.getExternalStorageDirectory() + "/inupai.jpg");

				loadingDialogShow();
				
				
				new Thread(new Runnable() {
					public void run() {
						long time;
						try {
							time = WebTime.getTime();
						} catch (Exception e) {
							time =  DateUtil.getMSTime();
						}
						UploadHelper.uploadPhotoNewsPhoto(thePucBitmap, String.valueOf(time));
						String contentTextString = contentEditTxt.getText()
								.toString();
						int userId = getSharedPreferences(Constant.INEEDUSPR, 0)
								.getInt(Constant.ID, 0);
						double lat = LocateManager.getInstance().getLatitude();
						double lng = LocateManager.getInstance().getLontitude();
					
						Mood mood = new Mood();
						mood.setContent(contentTextString);
						mood.setLat(lat);
						mood.setLng(lng);
						mood.setUserId(userId);
						mood.setTime(String.valueOf(time));
						mood.setTransmitFrom(-1);
						mood.setImageUrl(URLConstant.PHOTO_NEWS_PHOTO_URL+String.valueOf(time)+".jpg");
						mood.setFlag(Constant.MOOD_FLAG_PHOTO);
						
						TakePhotos takePhotos2 = new TakePhotos();
						takePhotos2.setContent(contentTextString);
						takePhotos2.setLat(lat);
						takePhotos2.setLng(lng);
						takePhotos2.setUserId(userId);
						takePhotos2.setTime(String.valueOf(time));
						takePhotos2.setTransmitFrom(-1);
						takePhotos2.setImageUrl(URLConstant.PHOTO_NEWS_PHOTO_URL+String.valueOf(time)+".jpg");
						String result;
						try {
							result = ServerCommunication.request(mood,
									URLConstant.COMMIT_MOOD_URL);
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
				}).start();
			}
		});
		backBtn = (ImageButton) findViewById(R.id.take_photo_imgbt_back);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		try {
			thePucBitmap = BitmapFactory.decodeFile(Environment
					.getExternalStorageDirectory() + "/inupai.jpg");
			imageView.setImageBitmap(thePucBitmap);
		} catch (Exception e) {
			finish();
		}
		
		//startTakePhoto();
	}
//
//	void startTakePhoto() {
//		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		Uri imageUri = Uri.fromFile(new File(Environment
//				.getExternalStorageDirectory(), "inupai.jpg"));
//		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//		startActivityForResult(openCameraIntent, Activity.DEFAULT_KEYS_DIALER);
//	}

//	@Override
//	protected void onActivityResult(int requestCode, int resultCode,
//			android.content.Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode != Activity.RESULT_OK) {
//			return;
//		}
//		Bitmap cameraBitmap = BitmapFactory.decodeFile(Environment
//				.getExternalStorageDirectory() + "/inupai.jpg");
//		if (null != cameraBitmap) {
//			int oldHeight = cameraBitmap.getHeight();
//			int oldWidth = cameraBitmap.getWidth();
//			float scale = (float) 800 / oldHeight;
//			Bitmap bitmap = zoomBitmap(cameraBitmap, (int) (oldWidth * scale),
//					(int) (oldHeight * scale));
//			FileOutputStream out;
//			try {
//				out = new FileOutputStream(new File(
//						Environment.getExternalStorageDirectory()
//								+ "/inupai.jpg"));
//				bitmap.compress(CompressFormat.JPEG, 90, out);
//				imageView.setImageBitmap(bitmap);
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//
//		}
//	};
//
//	public static Bitmap zoomBitmap(Bitmap oldBitmap, int newWidth,
//			int newHeight) {
//		// 获得图片的宽高
//		int width = oldBitmap.getWidth();
//		int height = oldBitmap.getHeight();
//		// 计算缩放比例
//		float scaleWidth = ((float) newWidth) / width;
//		float scaleHeight = ((float) newHeight) / height;
//		// 取得想要缩放的matrix参数
//		Matrix matrix = new Matrix();
//		matrix.postScale(scaleWidth, scaleHeight);
//		// 得到新的图片
//		Bitmap newbm = Bitmap.createBitmap(oldBitmap, 0, 0, width, height,
//				matrix, true);
//		oldBitmap.recycle();
//		return newbm;
//	}

}
