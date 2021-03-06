package com.eethan.ineedu.primaryactivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.eethan.ineedu.adapter.PhotoNewsAdapter;
import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.jackson.JacksonUtil;
import com.eethan.ineedu.jackson.JsonObject;
import com.eethan.ineedu.jackson.RefreshPhotosJsonObject;
import com.eethan.ineedu.manager.LocateManager;
import com.eethan.ineedu.model.PhotoNewsModel;
import com.eethan.ineedu.mycontrol.MyTakeDialog;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.network.PostException;
import com.eethan.ineedu.network.ServerCommunication;
import com.eethan.ineedu.util.DataCache;
import com.eethan.ineedu.util.MyTimer;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoNewsActivity extends BaseActivity {

	public static boolean REFRESH_TASK = true;
	public static boolean GET_MORE_TASK = false;

	public int lastNum = 0;

	private ImageButton takephotoBtn;
	private ImageButton backBtn;

	private ImageLoader imageLoader = ImageLoader.getInstance();

	private PullToRefreshListView pullToRefreshListView;
	private PhotoNewsAdapter photoNewsAdapter;
	private List<PhotoNewsModel> newslists = new ArrayList<PhotoNewsModel>();
	private MyTakeDialog dialog2 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.takephotos_main);
		
		dialog2 = new MyTakeDialog(PhotoNewsActivity.this,R.style.MyDialog,"拍照","相册"){
			@Override
			public void onYesButtonClick() {
				Intent openCameraIntent = new Intent(
						Intent.ACTION_GET_CONTENT);
				Uri imageUri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), "inupai.jpg"));
				openCameraIntent.setType("image/*");
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent,
						0);
			};
			@Override
			public void onNoButtonClick() {
				Intent openCameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				Uri imageUri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), "inupai.jpg"));
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent,
						1);
			};
		};
		dialog2.setText("从以下方式选择一张图片");

		takephotoBtn = (ImageButton) findViewById(R.id.photonews_imgbt_take_photo);
		takephotoBtn.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				dialog2.show();
			}
		});
		backBtn = (ImageButton) findViewById(R.id.photonews_imgbt_back);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(dialog2!=null && dialog2.isShowing()){
					dialog2.dismiss();
				}
				finish();
			}
		});
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_takephotos);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				Intent intent = new Intent();
				int itemUserId = newslists.get((int) id).getOwnerInfo()
						.getUserId();
				int itemPhotoId = newslists.get((int) id).getTakePhotos()
						.getId();
				intent.putExtra("userId", itemUserId);
				intent.putExtra("photoId", itemPhotoId);
				intent.setClass(getApplicationContext(),
						PhotoNewsDetailActivity.class);
				startActivity(intent);
			}
		});
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								PhotoNewsActivity.this,
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						REFRESH_TASK = true;
						GET_MORE_TASK = false;
						// Do work to refresh the list here.
						new GetDataTask().execute((Void) null);

					}
				});
		pullToRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// 加载更多数据
						REFRESH_TASK = false;
						GET_MORE_TASK = true;
						new GetDataTask().execute();
					}
				});

		photoNewsAdapter = new PhotoNewsAdapter(PhotoNewsActivity.this,
				newslists, imageLoader);
		pullToRefreshListView.setAdapter(photoNewsAdapter);
		REFRESH_TASK = true;
		GET_MORE_TASK = false;

		loadingDialogShow();
		new GetDataTask().execute();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(dialog2!=null && dialog2.isShowing()){
			dialog2.dismiss();
		}
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
							+ "/inupai.jpg"));
                     smallBitmap.compress(CompressFormat.JPEG, 90, out);
 					Intent intent = new Intent();
 					intent.setClass(PhotoNewsActivity.this,
 							TakeOnePhotoActivity.class);
 					startActivity(intent);
                 }  
             } catch (FileNotFoundException e) {  
                 e.printStackTrace();  
                 MyToast.showToast(PhotoNewsActivity.this, "获取图片出错了");
             } catch (IOException e) {  
                 e.printStackTrace();  
                 MyToast.showToast(PhotoNewsActivity.this, "获取图片出错了");
             }    
		}else if(requestCode==1){
			Bitmap cameraBitmap = BitmapFactory.decodeFile(Environment
					.getExternalStorageDirectory() + "/inupai.jpg");
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
									+ "/inupai.jpg"));
					bitmap.compress(CompressFormat.JPEG, 90, out);
					Intent intent = new Intent();
					intent.setClass(PhotoNewsActivity.this,
							TakeOnePhotoActivity.class);
					startActivity(intent);
				} catch (Exception e) {
					MyToast.showToast(PhotoNewsActivity.this, "拍照出错了");
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

	@Override
	protected void onResume() {
		super.onResume();
		REFRESH_TASK = true;
		GET_MORE_TASK = false;

		// loadingDialog.show();
		// new GetDataTask().execute((Void) null);
	};

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 后台线程操作
		@Override
		protected Object doInBackground(Void... params) {
			if (REFRESH_TASK)
				MyTimer.Start();
			// 后台获取数据
			JsonObject jsonObject = new JsonObject();
			double lat = LocateManager.getInstance().getLatitude();
			double lng = LocateManager.getInstance().getLontitude();
			jsonObject.setDouble1(lng);
			jsonObject.setDouble2(lat);
			jsonObject.setDouble3(Constant.DISTANCE_NATIONWIDE);
			jsonObject.setString1(Constant.SEX_ALL);
			String response = null, URL = URLConstant.REFRESH_PHOTOS_URL;
			if (!REFRESH_TASK) {
				URL = URLConstant.GET_MORE_PHOTOS_URL;
				jsonObject.setInt1(lastNum);
			}
			try {
				response = ServerCommunication.request(jsonObject, URL);// 发送请求，获得数据
			} catch (PostException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			RefreshPhotosJsonObject refreshPR = null;
			try {
				refreshPR = JacksonUtil.json()
						.fromJsonToObject(response, RefreshPhotosJsonObject.class);
			} catch (Exception e) {
				return null;
			}
			
			if (REFRESH_TASK)
				MyTimer.RefreshDelay();
			return refreshPR;
		}

		@Override
		protected void onPostExecute(Object result) {

			super.onPostExecute(result);
			loadingDialogDismiss();
			pullToRefreshListView.onRefreshComplete();
			
			if(result==null){
				MyToast.showToast(PhotoNewsActivity.this, "加载失败");
				return;
			}
			if(result.equals("服务器请求超时!")){
				MyToast.showToast(PhotoNewsActivity.this, (String) result);
				return;
			}

			// Call onRefreshComplete when the list has been refreshed.
			if (!ServerCommunication
					.checkResult(PhotoNewsActivity.this, result)) {
				MyToast.showToast(PhotoNewsActivity.this, (String) result);
				return;
			}

			RefreshPhotosJsonObject res = (RefreshPhotosJsonObject) result;
			int num = res.getTakePhotos().size();
			PhotoNewsModel model;
			if (REFRESH_TASK) {
				if (num == 0) {
					MyToast.showToast(getApplicationContext(), "没有更新的动态了~");
					pullToRefreshListView.onRefreshComplete();
					super.onPostExecute(result);
					return;
				} else {
					newslists.clear();
				}
			} else if (GET_MORE_TASK) {
				if (num == 0) {
					MyToast.showToast(getApplicationContext(), "没有更多了~");
					pullToRefreshListView.onRefreshComplete();
					super.onPostExecute(result);
					return;
				}
			}
			for (int i = 0; i < num; i++) {
				model = new PhotoNewsModel();
				model.setTakePhotos(res.getTakePhotos().get(i));
				model.setOwnerInfo(res.getOwnerUserInfos().get(i));
				model.setAtInfo(res.getBeAtManUserInfos().get(i));
				model.setOwnerDetailInfo(res.getUserDetailInfo().get(i));
				newslists.add(model);
			}
			lastNum = res.getLastNum();
			DataCache.photoNews = (ArrayList<PhotoNewsModel>) newslists;
			photoNewsAdapter.notifyDataSetChanged();

		}
	}

}
