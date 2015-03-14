package com.eethan.ineedu.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.mycontrol.MyToast;
//一个专门用来UI显示各种TOAST的HANDLER
public class ToastHandler extends Handler{
	private Context context;
	public ToastHandler(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	public void handleMessage(android.os.Message msg) {
		switch (msg.what) {
		case Constant.THANK_SUCCESS:
			MyToast.showToast(context, "感谢成功~");
			break;
		case Constant.THANK_FAILED:
			MyToast.showToast(context, "感谢失败~");
			break;
		case Constant.GIVEHEART_SUCCESS:
			MyToast.showToast(context, "送花成功~");
			break;
		case Constant.HAS_GIVEHEART:
			MyToast.showToast(context, "已经送过花了~");
			break;
		case Constant.GIVEHEART_MYSELF:
			MyToast.showToast(context, "不能给自己送花哦~");
			break;
		case Constant.DAILY_GIVEHEART_SUCCESS:
			MyToast.showToast(context, "每日送花成功~");
			break;
		case Constant.DELETE_FAILED:
			MyToast.showToast(context, "删除失败!");
			break;
		case Constant.DELETE_SUCCESS:
			MyToast.showToast(context, "删除成功~");
			SysApplication.getInstance().exitActivity();
			break;
		case Constant.CONNECT_FAILED:
			MyToast.showToast(context, "服务器连接失败!");
			break;
		case Constant.PHOTO_LIKE_SUCCESS:
			MyToast.showToast(context, "赞 +1~");
			break;
		case Constant.PHOTO_LIKE_ALREADY:
			MyToast.showToast(context, "你已经点过赞了~");
			break;
		case Constant.PHOTO_LIKE_MYSELF:
			MyToast.showToast(context, "不要赞自己哦~");
			break;
		case Constant.WISH_FLOWER_SELF:
			MyToast.showToast(context, "不要给自己送花哦~");
			break;
		case Constant.WISH_FLOWER_SUCCESS:
			MyToast.showToast(context, "花 +1~");
			break;
		case Constant.WISH_FLOWER_ALREADY:
			MyToast.showToast(context, "已经送过了~");
			break;
		case Constant.PLAY_JOIN_ALREADY:
			MyToast.showToast(context, "已经参加了~");
			break;
		case Constant.PLAY_JOIN_MYSELF:
			MyToast.showToast(context, "自己发起的活动哦");
			break;
		case Constant.PLAY_JOIN_SUCCESS:
			MyToast.showToast(context, "参加成功~");
			break;
		case Constant.PLAY_JOIN_FAIL:
			MyToast.showToast(context, ":(出错了~");
			break;
		default:
			break;
		}
		
	};
}
