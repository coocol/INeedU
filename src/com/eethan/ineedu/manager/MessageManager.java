package com.eethan.ineedu.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.model.IMMessage;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.content.Context;

/**
 * 本地聊天消息管理类
 *
 */
public class MessageManager {
	private static MessageManager messageManager = null;
	private static DbUtils db = null;

	/**
	 * 构造函数,单例模式,初始化dbManager
	 * @param context
	 */
	private MessageManager(Context context) {
		
//		dbManager = DBManager.getDBManager(context);
		db = DbUtil.getDbUtils(context);
	}

	/**
	 * 获取messageManager
	 * @param context
	 * @return MessageManager
	 */
	public static MessageManager getInstance(Context context) {

		if (messageManager == null) {
			messageManager = new MessageManager(context);
		}
		return messageManager;
	}

	/**
	 * 保存Message
	 * @param msg Message对象
	 * @return
	 */
	public void saveMsg(IMMessage msg) {
//		 this.saveMsg(msg.getUserid(),msg.getType(), msg.getWith(), msg.getContent(),
//				msg.getTime());
		try {
			db.save(msg);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取某人的聊天记录
	 * @param with消息来源,必须是phone
	 * @param pageNum页号
	 * @param pageSize页面大小
	 * @return List<IMMessage>
	 */
	public List<IMMessage> getMessageList(String with, int pageNum, int pageSize) {
		int fromIndex = (pageNum - 1) * pageSize;

		List<IMMessage> messages = null;
		try {
			messages = db.findAll(Selector.from(IMMessage.class)
					.where(IMMessage.WITH,"=",with)
					.orderBy(IMMessage.Time,true)
					.limit(pageSize)
					.offset(fromIndex));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
//		String RAWSQL = "select * from " + DBHelper.IMMESSAGE_TABLE_NAME + " where " +
//				DBHelper.MSG_WITH + "=\"" + with + "\" order by " + DBHelper.MSG_TIME + 
//				" desc limit " + fromIndex + "," + pageSize;
//		Cursor cursor = dbManager.rawQuery(RAWSQL);
		//此处List<IMMessage>的msglist实际上是ArrayList<IMMessage>对象
//		List<IMMessage> list = this.getIMMessageList(cursor);
//		Collections.sort(list);
//		return list;
		if(messages==null)
			return null;
		Collections.sort(messages);
		return messages;
	}

	/**
	 * 获取与某人聊天记录总数,返回数据库操作
	 * @param fromUser
	 * @return int
	 */
	public long getChatCountWith(String with) {
//		return dbManager.getDataCount(DBHelper.IMMESSAGE_TABLE_NAME, DBHelper.MSG_WITH + "=\"" + with +"\"");
		List<IMMessage> messages = new ArrayList<IMMessage>();
		try {
			messages = db.findAll(Selector.from(IMMessage.class)
					.where(IMMessage.WITH,"=",with));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		return messages.size();
	}

	/**
	 * 删除与某人的聊天记录,返回数据库操作结果
	 * @param with 要删除聊天记录的对象
	 * @return int
	 */
	public void delChatHisWithSb(String with) {
//		return dbManager.deleteRow(DBHelper.IMMESSAGE_TABLE_NAME,
//				DBHelper.MSG_WITH + "=?", new String[] {with});
		List<IMMessage> messages = new ArrayList<IMMessage>();
		try {
			messages = db.findAll(Selector.from(IMMessage.class)
					.where(IMMessage.WITH,"=",with));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			db.delete(messages);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过Cursor获取List<IMMessage>,实际上引用的是ArrayList<IMMessage>
	 * @param cursor
	 * @return List<IMMessage>,实际上引用的是ArrayList<IMMessage>
	 */
//	private List<IMMessage> getIMMessageList(Cursor cursor) {
//		List<IMMessage> msglist = new ArrayList<IMMessage>();
//		while(cursor.moveToNext()) {
//			IMMessage msg = new IMMessage();
//			msg.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.MSG_ID)));
//			msg.setUserid(cursor.getString(cursor.getColumnIndex(DBHelper.MSG_USERID)));
//			msg.setType(cursor.getString(cursor.getColumnIndex(DBHelper.MSG_TYPE)));
//			msg.setWith(cursor.getString(cursor.getColumnIndex(DBHelper.MSG_WITH)));
//			msg.setContent(cursor.getString(cursor.getColumnIndex(DBHelper.MSG_CONTENT)));
//			msg.setTime(cursor.getString(cursor.getColumnIndex(DBHelper.MSG_TIME)));
//			msglist.add(msg);
//		}
//		return msglist;
//	}
//	

	
}
