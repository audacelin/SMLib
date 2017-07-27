package com.smlib.activity;

import com.smlib.R;
import com.smlib.utils.SystemBarTintManager;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class ShellSMActivity extends TraceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
	            setTranslucentStatus(true);  
	            SystemBarTintManager tintManager = new SystemBarTintManager(this);  
	            tintManager.setStatusBarTintEnabled(true);  
	            tintManager.setStatusBarTintResource(R.color.barcolor);//通知栏所需颜色  
	    } 
	}
	
	private void setTranslucentStatus(boolean on) {  
        Window win = getWindow();  
        WindowManager.LayoutParams winParams = win.getAttributes();  
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;  
        if (on) {  
            winParams.flags |= bits;  
        } else {  
            winParams.flags &= ~bits;  
        }  
        win.setAttributes(winParams);  
   } 
}
