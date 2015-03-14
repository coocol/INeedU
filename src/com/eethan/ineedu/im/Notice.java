package com.eethan.ineedu.im;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Id;

/**
 * 消息实体,实现Serializable,Comparable<Notice>
 * @author Shaw
 *
 */
public class Notice implements Serializable, Comparable<Notice> {
	private static final long serialVersionUID = 1L;
	public static final String NOTICE_KEY = "notice_key";
	/**
	 * 好友请求
	 */
	public static final int FRIEND_REQUEST = 1;
	/**
	 * 系统消息
	 */
	public static final int SYS_MSG = 2; 
	/**
	 * 聊天消息
	 */
	public static final int CHAT_MSG = 3;
	/**
	 * 已读状态
	 */
	public static final int READ = 0;
	/**
	 * 未读状态
	 */
	public static final int UNREAD = 1;
	/**
	 * 所有状态,即已读和未读
	 */
	public static final int AllSTATUS = 2;

	
	public static final String NOTICEID = "noticeid";
	public static final String TYPE = "type";
	public static final String TITLE = "title";
	public static final String CONTENT = "title";
	public static final String FROM = "from";
	public static final String TIME = "time";
	public static final String STATUS = "status";

	@Id
	private long id = -1L; // 主键
	private int type; // 消息类型 1.好友请求 2.系统消息
	private String title; // 标题
	private String content; // 内容
	private String from; // 通知来源
	private long time; // 通知时间
	private int status = UNREAD; // 状态 0已读 1未读
	
	/**
	 * 构造函数,需要noticeid值
	 */
	public Notice(){}
	public Notice(long noticeid, int type, String title, String content, String from, long time,
			int status) {
		this.id = noticeid;
		this.type = type;
		this.title = title;
		this.content = content;
		this.from = from;
		this.time = time;
		this.status = status;
	}
	
	/**
	 * 构造函数,不需要noticeid
	 */
	public Notice(int type, String title, String content, String from, long time, int status) {
		this.type = type;
		this.title = title;
		this.content = content;
		this.from = from;
		this.time = time;
		this.status = status;
	}
	
	/**
	 * 获取消息类型 1.好友请求 2.系统消息3.聊天消息
	 * @return Integer
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置消息类型
	 * @param noticeType
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 获取ID主键
	 * @return String
	 */
	public long getId() {
		return id;
	}

	/**
	 * 设置ID主键
	 * @param id
	 */
	public void setId(long noticeid) {
		this.id = noticeid;
	}

	/**
	 * 获取标题
	 * @return String
	 */
	public String getTitle() {
		return title.trim();
	}

	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title.trim();
	}

	/**
	 * 获取内容
	 * @return String
	 */
	public String getContent() {
		return content.trim();
	}

	/**
	 * 设置内容
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content.trim();
	}

	/**
	 * 获取状态,0已读,1未读
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置状态,0已读,1未读
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 获取通来源
	 * @return String
	 */
	public String getFrom() {
		return from.trim();
	}

	/**
	 * 设置通来源
	 * @param from
	 */
	public void setFrom(String from) {
		this.from = from.trim();
	}
	
	/**
	 * 获取通知时间
	 * @return String
	 */
	public long getTime() {
		return time;
	}

	/**
	 * 设置通知时间
	 * @param time
	 */
	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public int compareTo(Notice notice) {
		if(this.getTime()>notice.getTime()) return 1;
		else if(this.getTime()==notice.getTime()) return 0;
		else return -1;
	}

}
