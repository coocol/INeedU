package com.eethan.ineedu.mycontrol;

import com.eethan.ineedu.primaryactivity.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageDialog extends Dialog {
    Context context;
    private TextView content;
    private String text="";
    
    public MessageDialog(Context context) {
    		super(context, R.style.MyDialog);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public MessageDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.message_dialog);
        content=(TextView)findViewById(R.id.thank_who);
        content.setAutoLinkMask(Linkify.ALL); 
        if(!text.equals(""))
        		content.setText(text);
        
        Button t_button_y = (Button) findViewById(R.id.take_button_yes);
        t_button_y.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				yesButtonOnClick();
			}
        });
        
    }
    public void setText(String text)
    {
    		this.text=text;
    }
    public void yesButtonOnClick()
    {
    	
    }
   
}