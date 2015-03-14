package com.eethan.ineedu.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.eethan.ineedu.util.DateUtil;
import com.lidroid.xutils.db.annotation.Id;

import android.R.string;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;



/**
 * 用来保存到本地数据的消息类
 * IM消息类,实现Parcelable,Comparable<IMMessage>接口
 * @author Administrator
 *
 */
public class IMMessage implements Parcelable, Comparable<IMMessage> {
	public static final String IMMESSAGE_KEY = "immessage_key";
	public static final String KEY_TIME = "immessage_time";
	public static final String[] MSG_TYPE= {"IN","OUT"};
	
	@Id
	private long id = -1L;
	/**
	 * 聊天内容
	 */
	private String content;
	/**
	 * 表示与谁聊天，本地保存的时候相当于一个标志，通过这个搜索聊天记录
	 */
	private String with;//id
	
	/**
	 * 用户id，这条消息是谁发的，用户寻找头像
	 */
	private String userid;
	/**
	 * IN接受 OUT发送,默认接收
	 */
	private String type = MSG_TYPE[0];
	private String time;
	
	public static final String USERID = "userid";
	public static final String WITH ="with";
	public static final String CONTENT ="content";//消息内容
	public static final String TYPE ="type";//发出还是收到
	public static final String Time ="time";//时间，long类型转为String

	/**
	 * 带有ID的构造函数
	 * @param type
	 * @param with
	 * @param content
	 * @param time
	 * @param msg_id
	 */
	public IMMessage(long msg_id,String userid, String type, String with, String content, String time) {
		super();
		this.id = msg_id;
		this.userid = userid;
		this.type = type;
		this.with = with;
		this.content = content;
		this.time = time;
	}
	
	/**
	 * 消息的构造方法
	 * @param content 内容
	 * @param time 时间
	 * @param withSb 对象
	 * @param msgType 消息类型 
	 */
	public IMMessage(String userid,String type, String with, String content, String time) {
		super();
		this.userid = userid;
		this.type = type;
		this.with = with;
		this.content = content;
		this.time = time;
	}
	
	public IMMessage(String userid,String type, String with, String content) {
		this(userid,type, with, content, DateUtil.getStringTime());
	}
	
	public IMMessage() {
		
	}

	public long getId() {
		return id;
	}
	
	public void setId(long msg_id) {
		this.id = msg_id;
	}
	
	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	/**
	 * 获取类型type,RECEIVE接受 SEND发送
	 * @return int
	 */
	public String getType() {
		return type;
	}

	/**
	 * 设置type,RECEIVE接受 SEND发送
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取内容
	 * @return String
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置内容
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取时间
	 * @return String
	 */
	public String getTime() {
		return time;
	}

	/**
	 * 设置时间
	 * @param time
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * 获取聊天对象
	 * @return String
	 */
	public String getWith() {
		return with;
	}

	/**
	 * 设置聊天对象
	 * @param from
	 */
	public void setWith(String with) {
		this.with = with;
	}
	


	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "Msg [userid=" + userid + ",with=" + with + ", content=" + content + ", type=" + type+ ", time=" + time + "]";
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userid);
		dest.writeString(type);
		dest.writeString(with);
		dest.writeString(content);
		dest.writeString(time);
	}

	public static final Parcelable.Creator<IMMessage> CREATOR = new Parcelable.Creator<IMMessage>() {

		@Override
		public IMMessage createFromParcel(Parcel source) {
			IMMessage message = new IMMessage();
			message.setUserid(source.readString());
			message.setType(source.readString());
			message.setWith(source.readString());
			message.setContent(source.readString());
			message.setTime(source.readString());
			return message;
		}

		@Override
		public IMMessage[] newArray(int size) {
			return new IMMessage[size];
		}

	};

	

	@Override
	public int compareTo(IMMessage msg) {
		if(Long.parseLong(this.getTime())>Long.parseLong(msg.getTime())) return 1;
		else if(Long.parseLong(this.getTime())==Long.parseLong(msg.getTime())) return 0;
		else return -1;
	}
	
	/**
	 * 分析消息内容
	 * @param body
	 * Json
	 */
	@SuppressWarnings("finally")
	public static IMMessage analyseMsgBody(String jsonStr) {
		IMMessage msg = new IMMessage();
		// 获取用户、消息、时间、IN
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			msg.setUserid(jsonObject.getString(IMMessage.USERID));
			msg.setWith(jsonObject.getString(IMMessage.WITH));
			msg.setType(jsonObject.getString(IMMessage.TYPE));
			msg.setContent(jsonObject.getString(IMMessage.CONTENT));
			msg.setTime(jsonObject.getString(IMMessage.Time));
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			return msg;
		}
		 
		 
	}
	
	
	/**
	 * 传json 
	 */
	@SuppressWarnings("finally")
	public static  String  toJson(IMMessage msg){
		JSONObject jsonObject=new JSONObject();
		String jsonStr="";
		try {
			jsonObject.put(IMMessage.USERID, msg.getUserid()+"");
			jsonObject.put(IMMessage.WITH, msg.getWith()+"");
			jsonObject.put(IMMessage.CONTENT, msg.getContent()+"");
			jsonObject.put(IMMessage.Time, msg.getTime()+"");
			jsonObject.put(IMMessage.TYPE, msg.getType()+"");
			jsonStr= jsonObject.toString();
			Log.d("msg json", jsonStr+""); 
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			return jsonStr;
		}
	}
}
