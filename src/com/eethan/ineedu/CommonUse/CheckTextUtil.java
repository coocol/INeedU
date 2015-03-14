package com.eethan.ineedu.CommonUse;

import android.text.Html;
import android.widget.EditText;

public class CheckTextUtil {
	/*
	 * 使用方法
	 * CheckTextUtil c = CheckTextUtil.getInstance();
	 * c.setEmptyText("ErrorText");
	 * c.checkText(EditText);
	 */
	private static CheckTextUtil util = null;
	private String emptyText = "内容不能为空!";
	public static CheckTextUtil getInstance()
	{
		if(util == null)
			util = new CheckTextUtil();
		//回复默认
		util.setEmptyText("内容不能为空!");
		return util;
	}
	public boolean checkText(EditText editText){
		String content = editText.getText().toString().trim();
		if(content.equals(""))
		{
			editText.setError(Html.fromHtml("<font color=#E10979>"+emptyText+"</font>"));
			return false;
		}
		return true;
	}
	public void setEmptyText(String emptyText) {
		this.emptyText = emptyText;
	}
}
