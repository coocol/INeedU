package com.eethan.ineedu.mycontrol;

import com.baidu.platform.comapi.map.t;
import com.eethan.ineedu.primaryactivity.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ThreeButtonDialog extends Dialog {
    Context context;
    private TextView content;
    private String text="";
    private Button button1;
    private Button button2;
    private Button button3;
    private String firstBtnString = "";
    private String secondBtnString = "";
    private String thirdBtnString = "";
    
    public ThreeButtonDialog(Context context) {
    		super(context, R.style.MyDialog);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public ThreeButtonDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.three_button_dialog);
        content=(TextView)findViewById(R.id.content);
        button1 = (Button) findViewById(R.id.bt_first);
        button2 = (Button) findViewById(R.id.bt_second);
        button3 = (Button) findViewById(R.id.bt_third);
        
        if(!text.equals(""))
        		content.setText(text);
        if(!firstBtnString.equals(""))
        		button1.setText(firstBtnString);
        if(!secondBtnString.equals(""))
    			button2.setText(secondBtnString);
        if(!thirdBtnString.equals(""))
    			button3.setText(thirdBtnString);
        
        button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				firstOnclick();
				dismiss();
				actionAfterClick();
			}

			
		});
        button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				secondOnclick();
				dismiss();
				actionAfterClick();
			}

			
		});
        button3.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				thirdOnclick();
				dismiss();
				actionAfterClick();
			}

			
		});
    }
    public void actionAfterClick() {
		// TODO Auto-generated method stub
		
	}
	public void setText(String text)
    {
    		this.text=text;
    }
    public void firstOnclick() {
		// TODO Auto-generated method stub
		
	}
    public void secondOnclick() {
		// TODO Auto-generated method stub
		
	}
    public void thirdOnclick() {
		// TODO Auto-generated method stub
		
	}
    public void setFirstBtnText(String text)
    {
    		firstBtnString = text;
    }
    public void setSecondBtnText(String text)
    {
    		secondBtnString = text;
    }
    public void setThirdBtnText(String text)
    {
    		thirdBtnString = text;
    }
}