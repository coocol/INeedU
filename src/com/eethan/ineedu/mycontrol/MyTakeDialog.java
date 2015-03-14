package com.eethan.ineedu.mycontrol;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eethan.ineedu.primaryactivity.R;

public class MyTakeDialog extends Dialog {
    Context context;
    private TextView content;
    private String text="";

    Button t_button_y;
    Button t_button_n;
    
    String nString = null;
    String yString = null;
    
    public MyTakeDialog(Context context) {
    		super(context, R.style.MyDialog);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public MyTakeDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    public MyTakeDialog(Context context, int theme,String nString,String yString){
        super(context, theme);
        this.context = context;
        this.nString = nString;
        this.yString = yString;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.take_dialog);
        content=(TextView)findViewById(R.id.thank_who);
        if(!text.equals(""))
        		content.setText(text);
        
        t_button_y = (Button) findViewById(R.id.take_button_yes);
        t_button_y.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onYesButtonClick();
			}
        });
     
       t_button_n = (Button) findViewById(R.id.take_button_cancel);
        t_button_n.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyTakeDialog.this.dismiss();
				onNoButtonClick();
			}
        });
        if(nString!=null && yString!=null){
        	t_button_y.setText(yString);
        	t_button_n.setText(nString);
        }
    }
    public void setText(String text)
    {
    		this.text=text;
    }
    public void onYesButtonClick()
    {
    	
    }
    public void onNoButtonClick()
    {
    	
    }
    
    public void setYesNoText(String yString,String nString){
    	t_button_n.setText(nString);
    	t_button_y.setText(yString);
    }
}