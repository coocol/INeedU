package com.eethan.ineedu.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.eethan.ineedu.constant.URLConstant;
import com.eethan.ineedu.network.SocketHttpRequester;

public class UploadHelper {
	private int userId;
	private static final String TAG = "UploadActivity";
	public UploadHelper(int userId)
	{
		this.userId=userId;
	}
	
	public void uploadRoundHead(final File imageFile) {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String requestUrl = URLConstant.HEAD_REQUEST_URL;
					// 请求普通信息
					Map<String, String> params = new HashMap<String, String>();
					// 上传文件
					FormFile formfile = new FormFile("head_"+userId+".png", imageFile,
							"image", "application/octet-stream");				
					SocketHttpRequester.post(requestUrl, params, formfile);		
				} catch (Exception e) {
					Log.i(TAG, "upload error");
					e.printStackTrace();
				}
				Log.i(TAG, "upload end");
			}
		}).start();
        
    }
	
	public void uploadBigHead(final File imageFile) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String requestUrl = URLConstant.HEAD_REQUEST_URL;
					// 请求普通信息
					Map<String, String> params = new HashMap<String, String>();
					// 上传文件
					FormFile formfile = new FormFile("head_big_"+userId+".png", imageFile,
							"image", "application/octet-stream");				
					SocketHttpRequester.post(requestUrl, params, formfile);		
				} catch (Exception e) {
					Log.i(TAG, "upload error");
					e.printStackTrace();
				}
				Log.i(TAG, "upload end");
			}
		}).start();
        
    }
	public void uploadScrShot(Bitmap bmp,final String imageName)
	{
//		final Bitmap bitmap=compressImage(bmp);
		final Bitmap bitmap=bmp;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String requestUrl = URLConstant.UPLOAD_SHARE_IMAGE_URL;
					// 请求普通信息
					Map<String, String> params = new HashMap<String, String>();
					
//					 int oldHeight1 = bitmap.getHeight();
//	     				int oldWidth1 = bitmap.getWidth();
//	     				float scale = (float) 800 / oldHeight1;
//	                     //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存  
//	                     Bitmap smallBitmap = zoomBitmap(bitmap, (int) (oldWidth1 * scale),
//	     						(int) (oldHeight1 * scale));
					
					ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
					bitmap.compress(CompressFormat.JPEG, 80 /*ignored for PNG*/, bos); 
					byte[] bitmapdata = bos.toByteArray(); 
					
					
					// 上传文件
					FormFile formfile = new FormFile(imageName+".jpg", bitmapdata,
							"image", "application/octet-stream");				
					SocketHttpRequester.post(requestUrl, params, formfile);		
				} catch (Exception e) {
					Log.i(TAG, "upload error");
					e.printStackTrace();
				}
				Log.i(TAG, "upload end");
			}
		}).start();
	}
	private static Bitmap compressImage(Bitmap image) {  
		   
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }
	public static void uploadCustomBg(Bitmap bmp,final String imageName)
	{
		final Bitmap bitmap= bmp;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String requestUrl = URLConstant.UPLOAD_CUSTOM_BG_URL;
					// 请求普通信息
					Map<String, String> params = new HashMap<String, String>();
					
					ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
					bitmap.compress(CompressFormat.PNG, 100 /*ignored for PNG*/, bos); 
					byte[] bitmapdata = bos.toByteArray(); 
					
					
					// 上传文件
					FormFile formfile = new FormFile(imageName+".png", bitmapdata,
							"image", "application/octet-stream");				
					SocketHttpRequester.post(requestUrl, params, formfile);		
				} catch (Exception e) {
					Log.i(TAG, "upload error");
					e.printStackTrace();
				}
				Log.i(TAG, "upload end");
			}
		}).start();
	}
	public static void uploadPhotoNewsPhoto(Bitmap bmp,final String imageName)
	{
		final Bitmap bitmap= bmp;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String requestUrl = URLConstant.UPLOAD_TAKE_PHOTOS_URL;
					// 请求普通信息
					Map<String, String> params = new HashMap<String, String>();
					
					ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
					bitmap.compress(CompressFormat.JPEG, 90 /*ignored for PNG*/, bos); 
					byte[] bitmapdata = bos.toByteArray(); 
					
					
					// 上传文件
					FormFile formfile = new FormFile(imageName+".jpg", bitmapdata,
							"image", "application/octet-stream");				
					SocketHttpRequester.post(requestUrl, params, formfile);		
				} catch (Exception e) {
					Log.i(TAG, "upload error");
					e.printStackTrace();
				}
				Log.i(TAG, "upload end");
			}
		}).start();
	}
	public static void uploadAlbumPhoto(Bitmap bmp,final String imageName)
	{
		final Bitmap bitmap= bmp;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String requestUrl = URLConstant.UPLOAD_ALBUM_PHOTO_URL;
					// 请求普通信息
					Map<String, String> params = new HashMap<String, String>();
					
					ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
					bitmap.compress(CompressFormat.JPEG, 90 /*ignored for PNG*/, bos); 
					byte[] bitmapdata = bos.toByteArray(); 
					// 上传文件
					FormFile formfile = new FormFile(imageName+".jpg", bitmapdata,
							"image", "application/octet-stream");				
					SocketHttpRequester.post(requestUrl, params, formfile);		
				} catch (Exception e) {
					Log.i(TAG, "upload error");
					e.printStackTrace();
				}
				Log.i(TAG, "upload end");
			}
		}).start();
	}
}
