package com.ev.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class ScrollAlwaysTextView extends TextView {

	public ScrollAlwaysTextView(Context context) {  
        super(context);  
    }  
  
    public ScrollAlwaysTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public ScrollAlwaysTextView(Context context, AttributeSet attrs,  
            int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    @Override  
    public boolean isFocused() {  
        return true;  
    }  
}
