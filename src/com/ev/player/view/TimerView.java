package com.ev.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TimerView extends TextView{
	
	public TimerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TimerView(Context context) {
		super(context);
	}
	
	public void setTextByMillisecond(int millisecond){
		int j = millisecond / 1000;
		int k = j / 60;
		int m = k / 60;
		int n = j % 60;
		int i1 = k % 60;
		Object[] arrayOfObject = new Object[3];
		arrayOfObject[0] = Integer.valueOf(m);
		arrayOfObject[1] = Integer.valueOf(i1);
		arrayOfObject[2] = Integer.valueOf(n);
		setText(String.format("%02d:%02d:%02d",
				arrayOfObject));
	}
	
	
}
