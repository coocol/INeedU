package com.eethan.ineedu.CommonUse;

import com.eethan.ineedu.constant.Constant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//SharedPreferences
public class SPHelper {
	private static SharedPreferences setting;
	private static Editor editor;
	private Context context;
	
	public final static String ID="id";	//用户id
	public final static String TELE="tele";//用户手机号码
	public final static String PASSWORD="password";
	public final static String EMAIL="email";
	public final static String NICKNAME="nickname";
	public final static String REALNAME="realName";
	public final static String SEX="sex";
	public final static String CHATPASSWORD="chatpassword";
	public final static String IS_AUTO_LOGIN="isautologin";
	public final static String IS_FIRST_START = "is_first_start";
	public final static String DAILY_LOVE="dailylove";
	public final static String DEFAULT_HEAD="defaulthead";
	public final static String HAS_DEFINE_HEAD="hasdefinehead";
	public final static String REQUIRE="require";
	public final static String MATCH_TIME = "matchTime";//起床困难户 匹配次数
	
	public final static String FIRST_USE_SHARE="firstuseshare";
	public final static String INFINITEMODE="infinitemode";
	public final static String IS_QQ_LOGIN="isqqlogin";
	
	public final static String PICK_LAST_TIME="picklast";
	
	public SPHelper(Context context)
	{
		this.context=context;
	}
	public SharedPreferences getSharedPreference()
	{
		if(setting==null)
			setting=context.getSharedPreferences(Constant.INEEDUSPR, 0);
		return setting;
	}
	public SharedPreferences.Editor GetEditor()
	{
		if(editor==null)
			editor=context.getSharedPreferences(Constant.INEEDUSPR, 0)
					.edit();
		return editor;
	}
	
	public int GetUserId()
	{
		if(setting==null)
			getSharedPreference();
		return setting.getInt(Constant.ID, -1);
	}
	public double GetLat()
	{
		if(setting==null)
			getSharedPreference();
		return Double.parseDouble(setting.getString(Constant.LAT, setting.getString(Constant.DEFAULT_LAT, "30.53009")));
	}
	public double GetLng()
	{
		if(setting==null)
			getSharedPreference();
		return Double.parseDouble(setting.getString(Constant.LNG, setting.getString(Constant.DEFAULT_LNG, "114.350624")));
	}
	public boolean isShake()
	{
		if(setting==null)
			getSharedPreference();
		return setting.getBoolean(Constant.IS_SHAKE, true);
	}
	public boolean isSound()
	{
		if(setting==null)
			getSharedPreference();
		return setting.getBoolean(Constant.IS_SOUND, true);
	}
	public void changeShake(boolean b)
	{
		GetEditor().putBoolean(Constant.IS_SHAKE, b);
		editor.commit();
	}
	public void changeSound(boolean b)
	{
		GetEditor().putBoolean(Constant.IS_SOUND, b);
		editor.commit();
	}
	
	public Object get(String what)
	{
		getSharedPreference();
		if(what.equals(PASSWORD)//String
				||what.equals(CHATPASSWORD)
				||what.equals(NICKNAME)
				||what.equals(REALNAME)
				||what.equals(SEX)
				||what.equals(TELE)
				||what.equals(EMAIL)
				||what.equals(DAILY_LOVE)
				||what.equals(REQUIRE))
			return setting.getString(what, "");
		//Boolean
		if(what.equals(IS_AUTO_LOGIN)
				||what.equals(IS_FIRST_START)
				||what.equals(HAS_DEFINE_HEAD)
				||what.equals(IS_AUTO_LOGIN)
				||what.equals(INFINITEMODE)
				||what.equals(IS_QQ_LOGIN))
			return setting.getBoolean(what, false);//默认为false的
		
		if(what.equals(FIRST_USE_SHARE))
			return setting.getBoolean(what, true);//默认为true的
		
		//Int 默认-1
		if(what.equals(DEFAULT_HEAD))
			return setting.getInt(what, -1);
		//Int 默认0
		if(what.equals(MATCH_TIME))
			return setting.getInt(what, 0);
		
		return null;
	}
	public void put(String what,Object value)
	{
		GetEditor();
		//String
		if(what.equals(PASSWORD)
				||what.equals(CHATPASSWORD)
				||what.equals(NICKNAME)
				||what.equals(REALNAME)
				||what.equals(SEX)
				||what.equals(DAILY_LOVE)
				||what.equals(REQUIRE))
			editor.putString(what, (String)value);
		//Boolean
		if(what.equals(IS_AUTO_LOGIN)
				||what.equals(IS_FIRST_START)
				||what.equals(HAS_DEFINE_HEAD)
				||what.equals(FIRST_USE_SHARE)
				||what.equals(INFINITEMODE)
				||what.equals(IS_QQ_LOGIN))
			editor.putBoolean(what, (Boolean)value);
		//Int
		if(what.equals(DEFAULT_HEAD)
				||what.equals(ID)
				||what.equals(MATCH_TIME))
			editor.putInt(what, (Integer)value);
		
		editor.commit();
	}
	
	
}
