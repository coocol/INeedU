package com.eethan.ineedu.mycontrol;

import android.view.View;

 public class AdapterOnClickListener implements android.view.View.OnClickListener{
    private int position;
    public AdapterOnClickListener(int p) {
            // TODO Auto-generated constructor stub
            position=p;
    }
    public int getPosition()
    {
    	return position;
    }
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}