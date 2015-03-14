package com.eethan.ineedu.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.eethan.ineedu.database.DbUtil;
import com.eethan.ineedu.im.Notice;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;


/**
 * 通知,消息管理类NoticeManager,实际上消息同时也会被作为通知传送，消息本身不带有未读已读状态，是通知带有
 * 这里有点冗余
 * @author Shaw
 *
 */
public class NoticeManager {
	private static NoticeManager noticeManager = null;
//	private static DBManager dbManager = null;
	private static DbUtils db = null;
	/**
	 * 私有构造函数
	 * @param context
	 */
	private NoticeManager(Context context) {
//		dbManager = DBManager.getDBManager(context);
		db = DbUtil.getDbUtils(context);
	}

	/**
	 * 单例模式,获取一个NoticeManager实例
	 * @param context
	 * @return NoticeManager
	 */
	public static NoticeManager getInstance(Context context) {

		if (noticeManager == null) {
			noticeManager = new NoticeManager(context);
		}
		return noticeManager;
	}

	/**
	 * 保存Notice
	 * @param notice Notice对象
	 * @return 保存的Notice的ID
	 */
	public void saveNotice(Notice notice) {
		 saveNotice(notice.getType(), notice.getTitle(), notice.getContent(),
				notice.getFrom(), notice.getTime(), notice.getStatus());
	}
	
	/**
	 * 保存Notice
	 * @param type
	 * @param title
	 * @param content
	 * @param from
	 * @param time
	 * @param status
	 * @return 保存的Notice的ID
	 */
	public void saveNotice(int type, String title, String content, String from,long time,
			int status) {
//		ContentValues newValues = new ContentValues();
//		newValues.put(DBHelper.NOTICE_TYPE, type);
//		newValues.put(DBHelper.NOTICE_TITLE, title);
//		newValues.put(DBHelper.NOTICE_CONTENT, content);
//		newValues.put(DBHelper.NOTICE_FROM, from);
//		newValues.put(DBHelper.NOTICE_TIME, time);
//		newValues.put(DBHelper.NOTICE_STATUS, status);
//		return this.saveNotice(newValues);
		Notice notice = new Notice(type, title, content, from, time, status);
		try {
			db.save(notice);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	/**
//	 * 保存Notice通知
//	 * @param cv一个保存了Notice结构的ContentValues对象
//	 * @return
//	 */
//	public long saveNotice(ContentValues cv) {
//		return dbManager.save(DBHelper.NOTICE_TABLE_NAME, cv);
//	}
//	
	/**
	 * 
	 * 获取所有未读消息.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-5-16 下午3:22:53
	 */
	public List<Notice> getUnReadNoticeList() {
//		String RAWSQL = "select * from " + DBHelper.NOTICE_TABLE_NAME + " where " +
//				DBHelper.NOTICE_STATUS + "=" + Notice.UNREAD;
//		return this.getNoticeList(dbManager.rawQuery(RAWSQL));
		List<Notice> notices = new ArrayList<Notice>();
		try {
			notices = db.findAll(Selector.from(Notice.class)
					.where(Notice.STATUS,"=",Notice.UNREAD));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notices;
	}

	/**
	 * 根据ID更新通知的状态
	 * @param noticeid
	 * @param status
	 */
	public void updateStatusById(long noticeid, int status) {
//		ContentValues cv = new ContentValues();
//		cv.put(DBHelper.NOTICE_STATUS, status);
//		return dbManager.update(DBHelper.NOTICE_TABLE_NAME, cv, DBHelper.NOTICE_ID + "=?",
//				new String[] {String.valueOf(noticeid)});
		Notice notice = new Notice();
		try {
			notice = db.findById(Notice.class, noticeid);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		notice.setStatus(status);
		try {
			db.update(notice);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 根据ID更新添加好友请求的状态
	 * @param noticeid
	 * @param status
	 * @param content
	 */
	public void updateAddFriendStatus(long noticeid, int status, String content) {
//		ContentValues cv = new ContentValues();
//		cv.put(DBHelper.NOTICE_STATUS, status);
//		cv.put(DBHelper.NOTICE_CONTENT, content);
//		return dbManager.update(DBHelper.NOTICE_TABLE_NAME, cv, DBHelper.NOTICE_ID + "=? and " +
//				DBHelper.NOTICE_TYPE + "=?",
//				new String[] {String.valueOf(noticeid), String.valueOf(Notice.FRIEND_REQUEST)});
		Notice notice = new Notice();
		try {
			notice = db.findById(Notice.class, noticeid);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		notice.setStatus(status);
		notice.setContent(content);
		try {
			db.update(notice);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取未读消息数目
	 * @return
	 */
	public long getUnReadNoticeCount() {
//		return dbManager.getDataCount(DBHelper.NOTICE_TABLE_NAME,
//				DBHelper.NOTICE_STATUS + "=" + Notice.UNREAD);
		List<Notice> notices = new ArrayList<Notice>();
		try {
			notices = db.findAll(Selector.from(Notice.class)
					.where(Notice.STATUS,"=",Notice.UNREAD));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notices.size();
	}

//	/**
//	 * 根据ID获取Notice
//	 * @param noticeid
//	 * @return
//	 */
//	public Notice getNoticeById(long noticeid) {
//		Cursor cursor = dbManager.rawQuery("select * from " + DBHelper.NOTICE_TABLE_NAME +
//				" where " + DBHelper.NOTICE_ID + "=" + noticeid);
//		return this.getNoticeList(cursor).get(0);
//	}

	/**
	 * 
	 * 获取未读消息的条数（参数为分类）.type 1 好友添加 2系统 消息 3 聊天
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-5-16 下午6:22:03
	 */
	public long getUnReadNoticeCountByType(int type) {
//		return dbManager.getDataCount(DBHelper.NOTICE_TABLE_NAME,
//				DBHelper.NOTICE_TYPE + "=" + type);
		List<Notice> notices = new ArrayList<Notice>();
		try {
			notices = db.findAll(Selector.from(Notice.class)
					.where(Notice.STATUS,"=",Notice.UNREAD)
					.where(Notice.TYPE,"=",type));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notices.size();
	}

	/**
	 * 
	 * 获取来自某人未读消息的条数（分类）.1 好友添加 2系统 消息 3 聊天
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-5 下午1:59:53
	 */
	public long getUnReadNoticeCountByTypeAndFrom(int type, String from) {
//		return dbManager.getDataCount(DBHelper.NOTICE_TABLE_NAME,
//				DBHelper.NOTICE_TYPE + "=" + type + " and " + DBHelper.NOTICE_FROM +
//				"=" + from + " and " + DBHelper.NOTICE_STATUS + "=" + Notice.UNREAD);
		List<Notice> notices = new ArrayList<Notice>();
		try {
			notices = db.findAll(Selector.from(Notice.class)
					.where(Notice.STATUS,"=",Notice.UNREAD)
					.where(Notice.TYPE,"=",type)
					.where(Notice.FROM,"=",from));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notices.size();
	}

	/**
	 * 更新某人的全部通知/消息为某个状态
	 * @param from 对象
	 * @param status 状态
	 */
	public void updateStatusByFrom(String from, int status) {
//		ContentValues cv = new ContentValues();
//		cv.put(DBHelper.NOTICE_STATUS, status);
//		return dbManager.update(DBHelper.NOTICE_TABLE_NAME, cv, DBHelper.NOTICE_FROM + "=?",
//				new String[] {from});
		List<Notice> notices = new ArrayList<Notice>();
		try {
			notices = db.findAll(Selector.from(Notice.class)
					.where(Notice.FROM,"=",from));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(notices==null)
			return;
		for(Notice notice : notices)
			notice.setStatus(status);
		try {
			db.updateAll(notices);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 分页分类获取所有通知,降序排列
	 * @param type
	 * @param isRead 0已读 1未读
	 * @param pageNum
	 * @param pageSize
	 * @return List<Notice>
	 */
	public List<Notice> getNoticeListByTypeAndPage(int type, int isRead,
			int pageNum, int pageSize) {
//		int fromIndex = (pageNum - 1) * pageSize;
//		String RAWSQL = "select * from" + DBHelper.NOTICE_TABLE_NAME + " where " +
//				DBHelper.NOTICE_STATUS + "=" + isRead + " and " + DBHelper.NOTICE_TYPE +
//				"=" + type + " order by " + DBHelper.NOTICE_TIME + " desc limit " + fromIndex +
//				"," + pageSize;
//		return this.getNoticeList(dbManager.rawQuery(RAWSQL));
		int fromIndex = (pageNum - 1) * pageSize;
		List<Notice> notices = new ArrayList<Notice>();
		try {
			notices = db.findAll(Selector.from(Notice.class)
					.where(Notice.STATUS,"=",isRead)
					.where(Notice.TYPE,"=",type)
					.orderBy(Notice.TIME,true)
					.limit(pageSize)
					.offset(fromIndex));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notices;
	}

	/**
	 * 
	 * 获取所有1 好友添加 和 2系统 消息 
	 * 不获取3聊天消息
	 * @param isRead
	 *            0 已读 1 未读 2 全部
	 * @return
	 * @author shimiso
	 * @update 2012-7-6 下午3:22:53
	 */
	public List<Notice> getNoticeListByTypeAndPage(int isRead) {
//		String RAWSQL = "select * from " + DBHelper.NOTICE_TABLE_NAME + " where " +
//				DBHelper.NOTICE_STATUS + "=" + isRead + " and " + DBHelper.NOTICE_TYPE +
//				"<>" + Notice.AllSTATUS + " order by " + DBHelper.NOTICE_TIME + " desc";
//		return this.getNoticeList(dbManager.rawQuery(RAWSQL));
		List<Notice> notices = new ArrayList<Notice>();
		try {
			notices = db.findAll(Selector.from(Notice.class)
					.where(Notice.STATUS,"=",isRead)
					.where(Notice.TYPE,"<>",Notice.AllSTATUS)
					.orderBy(Notice.TIME,true));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notices;
	}

	/**
	 * 根据ID删除消息
	 * @param noticeId
	 * @return
	 */
	public void delById(long noticeId) {
//		return dbManager.deleteRow(DBHelper.NOTICE_TABLE_NAME, DBHelper.NOTICE_ID + "=?",
//				new String[] {String.valueOf(noticeId)});
		try {
			db.deleteById(Notice.class, noticeId);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 删除全部Notice
	 */
	public void delAllNotice() {
//		return dbManager.deleteRow(DBHelper.NOTICE_TABLE_NAME, null, null);
		try {
			db.deleteAll(Notice.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 删除与某人的通知的历史记录,貌似是用在删除用户的时候
	 * 
	 * @param fromUser
	 */
	public void delNoticeHisWithSb(String from) {
//		return dbManager.deleteRow(DBHelper.NOTICE_TABLE_NAME, DBHelper.NOTICE_FROM + "=?",
//				new String[] {from});
		List<Notice> notices = new ArrayList<Notice>();
		try {
			notices = db.findAll(Selector.from(Notice.class)
					.where(Notice.FROM,"=",from));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			db.deleteAll(notices);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过Cursor获取List<Notice>,实际上引用的是ArrayList<Notice>
	 * @param cursor
	 * @return List<Notice>,实际上引用的是ArrayList<Notice>
	 */
//	private List<Notice> getNoticeList(Cursor cursor) {
//		List<Notice> noticelist = new ArrayList<Notice>();
//		while(cursor.moveToNext()) {
//			Notice notice = new Notice(cursor.getLong(cursor.getColumnIndex(DBHelper.NOTICE_ID)),
//					cursor.getInt(cursor.getColumnIndex(DBHelper.NOTICE_TYPE)),
//					cursor.getString(cursor.getColumnIndex(DBHelper.NOTICE_TITLE)),
//					cursor.getString(cursor.getColumnIndex(DBHelper.NOTICE_CONTENT)),
//					cursor.getString(cursor.getColumnIndex(DBHelper.NOTICE_FROM)),
//					cursor.getLong(cursor.getColumnIndex(DBHelper.NOTICE_TIME)),
//					cursor.getInt(cursor.getColumnIndex(DBHelper.NOTICE_STATUS)));
//			noticelist.add(notice);
//		}
//		return noticelist;
		
//	}
}
