package com.eethan.ineedu.mycontrol;

import android.widget.ListView;

//实现ListView不滚动
	public class ScrollListView extends ListView{
	    public ScrollListView(android.content.Context context,android.util.AttributeSet attrs){  
	        super(context, attrs);  
	    }  
	    /** 
	     * 设置不滚动 
	     */  
	    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
	    {  
	        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
	                MeasureSpec.AT_MOST);  
	        super.onMeasure(widthMeasureSpec, expandSpec);  
	  
	    }  
	}