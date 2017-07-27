package com.smlib.activity;

import com.smlib.R;
import com.smlib.bussiness.ShellContextParamsModule;
import com.smlib.bussiness.ShellSPConfigModule;
import com.smlib.bussiness.ShellSPConfigModule.CustSP;
import com.smlib.utils.AndroidUtils;
import com.smlib.utils.StringUtils;
import com.smlib.utils.SystemBarTintManager;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ShellSplashActivity extends TraceActivity{

	private TextView tv_indicator,tv_appVersion,tv_appName;
	private final long SPLASH_WAITING=1000;
	private ShellContextParamsModule shellContextParamsModule=ShellContextParamsModule.getInstance();
	private ShellSPConfigModule spConfigModule=ShellSPConfigModule.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e("ShellSplashActivity", "test");
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
//	            setTranslucentStatus(true);  
//	            SystemBarTintManager tintManager = new SystemBarTintManager(this);  
//	            tintManager.setStatusBarTintEnabled(true);  
//	            tintManager.setStatusBarTintResource(R.color.barcolor);//通知栏所需颜色  
//	    } 
	    setContentView(R.layout.shell_splash_activity);
	    init();
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
   public void init(){
	   
	   initUI();
	   
	   //检查是否存在登录的用户
	   tryLogin();
   } 	
   public void initUI(){
	   
	   tv_indicator=(TextView)this.findViewById(R.id.idStartIndicator);
	   tv_appVersion=(TextView)this.findViewById(R.id.app_version_tv);
	   tv_appName=(TextView)this.findViewById(R.id.app_name_tv);
	   
	   tv_appName.setText(AndroidUtils.s(R.string.app_name));
	   tv_appVersion.setText("V"+AndroidUtils.System.getVersionName());
	   
   }
   private void tryLogin(){
	   
	   if(canAutoLogin()){
		   //满足自动登录条件
		   Login();
		   return;
	   }
	   new Thread(new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				Thread.sleep(SPLASH_WAITING);
			
			}catch(InterruptedException ex){
				ex.printStackTrace();
			}
			goToLogin();
			
		}
	  }).start();;
   }
   private boolean canAutoLogin(){
	   if(!spConfigModule.isAutoLogin(shellContextParamsModule.getCurCustNo()))
		   return false;
	   CustSP custSP=spConfigModule.getLastestCustSP();
	   if(custSP==null)
	      return false;
	   if(StringUtils.isBlank(custSP.custNo))
		   return false;
	   return true;
   }
   //跳转登录界面
   protected void goToLogin(){
	   
   }
   //自动登录
   protected void Login(){
	   //
	   
   }
   
}
