package com.eethan.ineedu.constant;

 


public class Constant {


	public static boolean OFFLINE_MODE=false;
	public static boolean LOCAL_MODE=false;//是否使用本地服务器
	public static boolean IS_USE_OPENFIRE=true;//是否使用openfire
	/**
	 * Openfire服务器设置
	 */
	//public static final String XMPPHOST = "eethan.com";
	//public static final String XMPPHOST = "115.29.179.60"; 
	
	//public static String IP="192.168.1.110";
	public static String IP="www.eehtan.com";
	//public static final String LOCAL_IP="192.168.155.1";
	public static final String LOCAL_IP="www.eehtan.com";
	public static final String XMPPHOST = IP;
	public static final int XMPPPORT = 5222;
	public static final String XMPPNAME = "eethan.com";
	/** 
	 * SharedPreferences参数设置
	 */ 
	public final static String INEEDUSPR="ineeduspr";	//SharedPreferences名
	public final static String ID="id";	//用户id
	public final static String TELE="tele";//用户手机号码
	public final static String PASSWORD="password";
	public final static String EMAIL="email";
	public final static String NICKNAME="nickname";
	public final static String REALNAME="realName";
	public final static String SEX="sex";
	public final static String DEFAULT_HEAD="defaulthead";//默认头像
	public final static String HAS_DEFINE_HEAD="hasdefinehead";//是否指定了头像
	public final static String DAILY_LOVE="dailylove";//每日送爱心   判断是否过了一天

	public final static String IS_AUTO_LOGIN="isautologin";
	//
	public final static String IS_FIRST_START = "is_first_start";
	//地理位置
	
	public final static String LNG="lng";//经度
	public final static String LAT="lat";//纬度
	//默认地理位置，武汉大学国际软件学院~~
	
	public final static String DEFAULT_LNG = "114.350624";
	public final static String DEFAULT_LAT = "30.53009";
	
	public final static String IS_SOUND = "isSound";//消息通知是否声音
	public final static String IS_SHAKE = "isShake";//消息通知是否震动
	public final static String FIRST_USE_SHARE="firstuseshare";//分享使用提示
	
	/**
	 * 时间显示格式
	 */
	public static final String TIME_FORMART = "yyyy-MM-dd HH:mm";
	//连接openfire的模式

	public static final int LOGIN_MODE = 0;
	public static final int REGISTER_MODE = 1;
	public static final int QQ_REGISTER_MODE = 2;
	//openfire登陆返回
	/**
	 * 登陆成功
	 */
	public static final int LOGIN_SUCCESS = 0;
	/**
	 * 注册成功
	 */
	public static final int REGISTER_SUCCESS = 1;
	/**
	 * 用户名冲突
	 */
	public static final int REGISTER_CONFLICT = 2;
	/**
	 * 账号或密码错误
	 */
	public static final int LOGIN_ERROR_ACCOUNT_PASS = 3;
	/**
	 * 无法连接到服务器
	 */
	public static final int SERVER_UNAVAILABLE = 4;
	/**
	 * 未知原因连接失败
	 */
	public static final int CONNECT_ERROR = 5;
	/**
	 * 未知原因连接失败
	 */
	public static final int NOT_CONNECTED_TO_SERVER = 6;
	/**
	 * 未知原因连接失败
	 */
	public static final int ALREADY_LOGGED = 7;
	/**
	 * qq第三方登陆注册成功
	 */
	public static final int QQ_REGISTER_SUCCESS = 8;
	
	/**
	 * solveId 未解决默认为-1
	 */
	public static final int NOT_SOLVED=-1;
	
	/**
	 * Need的类型(Type)
	 */
	public static final int TYPE_ALL=-1;
	public static final int TYPE_ASK=1;
	public static final int TYPE_BORROW=2;
	public static final int TYPE_INVITE=3;
	public static final int TYPE_BRING=4;
	public static final int TYPE_BUY=5;
	
	/**
	 * ToastHandler页面信息
	 */
	public static final int THANK_SUCCESS=1;
	public static final int THANK_FAILED=2;
	public static final int GIVEHEART_SUCCESS=3;
	public static final int HAS_GIVEHEART=4;
	public static final int GIVEHEART_MYSELF=5;
	public static final int DAILY_GIVEHEART_SUCCESS=6;
	public static final int DELETE_SUCCESS=7;
	public static final int DELETE_FAILED=8;
	public static final int CONNECT_FAILED=9;
	/**
	 * 点赞
	 */
	public static final int PHOTO_LIKE_SUCCESS=10;
	public static final int PHOTO_LIKE_ALREADY=11;
	public static final int PHOTO_LIKE_MYSELF=12;
	/**
	 * 参加活动
	 */
	public static final int PLAY_JOIN_SUCCESS=13;
	public static final int PLAY_JOIN_ALREADY=14;
	public static final int PLAY_JOIN_MYSELF=15;
	public static final int PLAY_JOIN_FAIL=16;
	/**
	 * 花朵
	 */
	public static final int WISH_FLOWER_SUCCESS=17;
	public static final int WISH_FLOWER_ALREADY=18;
	public static final int WISH_FLOWER_FAIL=19;
	public static final int WISH_FLOWER_SELF=20;
	/**
	 * 表情数目
	 */
	public static final int NUMOFFACE = 107;
	/**
	 * 聊天记录页数
	 */
	public static final int PAGESIZE = 10;
	
	/**
	 * 评论标签（评论need，评论pourlisten）
	 */
	
	public static final int COMMENT_NEED = 1;
	public static final int COMMENT_NEED_DETAIL = 2;
	public static final int COMMENT_POUR_LISTEN = 3;
	public static final int COMMENT_POUR_LISTEN_DETAIL = 4;
	public static final int REPLY_NEED_COMMENT = 5;
	public static final int REPLY_POUL_LISTEN_COMMENT = 6;
	public static final int COMMENT_PHOTONEWS = 7;
	public static final int COMMENT_PHOTONEWS_DETAIL = 8;
	public static final int TRANSMIT_PHOTONEWS = 9;
	public static final int TRANSMIT_PHOTONEWS_DETAIL = 10;
	public static final int COMMENT_PLAYS = 11;
	public static final int COMMENT_WISH = 12;
	public static final int COMMENT_MOOD = 13;
	public static final int COMMENT_MOOD_PHOTO = 14;
	public static final int TRANSMIT_PHOTONEWS_MOOD = 15;
	/**
	 * 时间Long常量
	 */	
	public static final int YEAR=1000*60*60*24*30*12;
	public static final int MONTH=1000*60*60*24*30;
	public static final int DAY=1000*60*60*24;
	public static final int HOUR=1000*60*60;
	public static final int MIN=1000*60;
	
	public static boolean is_chatting = false;
	
	/**
	 * 新消息广播
	 */
	public static final String NEW_MESSAGE_ACTION = "newmessage";
	/**
	 * 给谁发消息关键字 
	 */
	public static final String MESSAGE_TO = "meesage_to";
	public static final String MESSAGE_TO_NAME = "message_to_name";
	
	/**
	 *  性别筛选
	 */
	public static final String SEX_BOY="男";
	public static final String SEX_GRIL="女";
	public static final String SEX_ALL="";
	
	/**
	 *  距离筛选
	 */
	public static final double DISTANCE_NEAR=500;
	public static final double DISTANCE_SCHOOL=3000;
	public static final double DISTANCE_NATIONWIDE = 40000000;
	public static final double DISTANCE_DEFAULT=3000;
	/**
	 * Toast
	 */
	public static int GET_MORE_TOAST_TIME_DELAY=5000;//两次Toast之间的时间间隔

	/**
	 * 重写返回键
	 */
	public static int APP_EXIT=1;

	/**
	 * 刷新延迟
	 */
	public static int REFRESH_DELAY=500;
	//checkNotification延迟
	public static int CHECK_DELAY=10*1000;
	
	/**
	 * HttpClient Post异常信息
	 */
	public static String ConnectTimeoutException="服务器连接超时!";
	public static String SocketTimeoutException="服务器请求超时!";
	public static String HttpHostConnectException="服务器连接失败,请检查网络后再试!";
	public static String ConnectException="服务器连接失败,请检查网络后再试!";
	public static String POST_ERROR="ERROR";
	
	/**
	 * 摄像头拍摄照片存放地址
	 */
	public static String PHOTO_PATH="sdcard/DCIM/Camera/";
	public static String HEAD_PATH="sdcard/DCIM/";
	
	public static final int CONTACT_CHAT_TYPE = 15;
	
	
	public final static String GOD_VALUE_STRING  = "男神值 ";
	public final static String GODNESS_VALUE_STRING ="女神值 ";
	public final static String GOD_VALUE_ADD_STRING  = "男神值+";
	public final static String GODNESS_VALUE_ADD_STRING ="女神值+";
	
	public final static int MOOD_FLAG_POUR = 0;
	public final static int MOOD_FLAG_PHOTO = 1;
	public final static int MOOD_FLAG_NOTE = 2;
	
	public final static int GET_PROVINCE = 1;
	public final static int GET_CITY = 2;
	public final static int GET_COLLEGE = 3;
	public final static int GET_SCHOOL = 4;
}
