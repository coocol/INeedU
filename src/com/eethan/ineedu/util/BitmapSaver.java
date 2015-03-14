package com.eethan.ineedu.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
/**
 * A class saving bitmaps
 * @author MaGiga
 */
public class BitmapSaver{
	private final static String CACHE="/DCIM";
	/**
	 * A method saving bitmap to SDcard
	 * 
	 * @throws Exceptions
	 */
	public static void saveImage(Bitmap bitmap,String imageName)throws Exception{
		String filePath=isExistsFilePath();
		FileOutputStream fos=null;
		filePath="sdcard/DCIM";
		File file=new File(filePath,imageName);
		try{
			fos=new FileOutputStream(file);
			if(fos!=null){
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void saveImage(Bitmap bitmap,String path,String imageName)throws Exception{
		String filePath;
		FileOutputStream fos=null;
		filePath=path;
		File file=new File(filePath,imageName);
		try{
			fos=new FileOutputStream(file);
			if(fos!=null){
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	/**
	 * obtain the path where the bitmaps are saved in SDcard, usually being this path.
	 * 
	 * @return SDPath
	 */
	public static String getSDPath(){
		String sdDir=null;
		boolean sdCardExists=Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		//judge the existence of SDCard.
		if(sdCardExists){
			sdDir=Environment.getExternalStorageState();
			//get root dictionary
		}else{
			Log.e("ERROR","NO SDCard");
		}
		return sdDir;
	}
	/**
	 * obtain the folder where images are saved and if not exists, create it.
	 * 
	 * @return filePath
	 */
	public static String isExistsFilePath(){
		String filePath=getSDPath()+CACHE;
		File file=new File(filePath);
		if(!file.exists()){
			file.mkdirs();
		}
		return filePath;
	}
	/**
	 * obtain file from SDCard.
	 * 
	 * @return Bitmap
	 */
	public static Bitmap getImageFromSDCard(String imageName){
		String filepath=imageName;
		File file=new File(filepath);
		if(file.exists()){
			Bitmap bm=BitmapFactory.decodeFile(filepath);
			return bm;
		}
		return null;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,  
	        int reqWidth) {  
	    // 源图片的宽度  
	    final int width = options.outWidth;  
	    int inSampleSize = 1;  
	    if (width > reqWidth) {  
	        // 计算出实际宽度和目标宽度的比率  
	        final int widthRatio = Math.round((float) width / (float) reqWidth);  
	        inSampleSize = widthRatio;  
	    }  
	    return inSampleSize;  
	}  
	
	public static Bitmap imageZoom(Bitmap bitMap,double MaxSize) { 
        //图片允许最大空间   单位：KB 
        double maxSize =MaxSize; 
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）   
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        bitMap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
        byte[] b = baos.toByteArray(); 
        //将字节换成KB 
        double mid = b.length/1024; 
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩 
//        if (mid > maxSize) 
        { 
	        //获取bitmap大小 是允许最大大小的多少倍 
	        double i = mid / maxSize; 
	        //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小） 
	        bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i), 
	                        bitMap.getHeight() / Math.sqrt(i)); 
	        return bitMap;
        } 
//        else
//        		return bitMap;
} 
		public static Bitmap zoomImage(Bitmap bgimage, double newWidth, 
	            double newHeight) { 
	    // 获取这个图片的宽和高 
	    float width = bgimage.getWidth(); 
	    float height = bgimage.getHeight(); 
	    // 创建操作图片用的matrix对象 
	    Matrix matrix = new Matrix(); 
	    // 计算宽高缩放率 
	    float scaleWidth = ((float) newWidth) / width; 
	    float scaleHeight = ((float) newHeight) / height; 
	    // 缩放图片动作 
	    matrix.postScale(scaleWidth, scaleHeight); 
	    Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, 
	                    (int) height, matrix, true); 
	    return bitmap; 
	} 
	
}




