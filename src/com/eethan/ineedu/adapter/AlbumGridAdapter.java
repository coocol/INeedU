package com.eethan.ineedu.adapter;

import java.util.List;

import com.eethan.ineedu.databasebeans.Album;
import com.eethan.ineedu.primaryactivity.R;
import com.eethan.ineedu.secondaryactivity.UploadMyPhotoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class AlbumGridAdapter extends BaseAdapter{
	
	private Context context;
	private ImageLoader imageLoader;
	private List<Album> albums;
	private DisplayImageOptions options;
	
	private boolean isMyInfo = false;
	
	private PlaceHolder placeHolder;
	
	public AlbumGridAdapter(Context context,List<Album> albums,ImageLoader imageLoader,boolean ismyInfo){
		this.context = context;
		this.albums = albums;
		this.imageLoader = imageLoader;
		this.isMyInfo = ismyInfo;
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.gray_background)
		.showImageForEmptyUri(R.drawable.gray_background)
		.showImageOnFail(R.drawable.gray_background)
		.cacheInMemory(true).cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		return albums.size();
	}

	@Override
	public Object getItem(int arg0) {
		return albums.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		final int pos = position;
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(R.layout.album_item, group,false);
			placeHolder = new PlaceHolder();
			placeHolder.photoImgVu = (ImageView) convertView.findViewById(R.id.iv_photo);
			if(isMyInfo && position==albums.size()-1){
				placeHolder.photoImgVu.setImageResource(R.drawable.addphoto);
			}else{
				imageLoader.displayImage(albums.get(position).getPhotoUrl(), placeHolder.photoImgVu,options);
			}
			convertView.setTag(placeHolder);
		}else{
			placeHolder = (PlaceHolder) convertView.getTag();
		}
//		placeHolder.photoImgVu.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if(isMyInfo && pos==albums.size()-1){
//					Intent intent = new Intent(context,UploadMyPhotoActivity.class);
//					context.startActivity(intent);
//				}else{
//					
//				}
//			}
//		});
		return convertView;
	}
	
	class PlaceHolder{
		public ImageView photoImgVu;
	}

}
