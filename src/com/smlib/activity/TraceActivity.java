package com.smlib.activity;

import com.smlib.utils.TimeUtils.DateTime;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
/*
 * 通过友盟等第三方平台进行数据统计
 * 
 */
public class TraceActivity extends FragmentActivity {
    protected int keyCount=0;
    protected long lastTime=0;
    protected long curTime=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.KEYCODE_BACK==keyCode){
		   keyCount++;
		   if(curTime==0){
			   curTime=System.currentTimeMillis();
		   }else{
			   lastTime=curTime;
		       curTime=System.currentTimeMillis();
		   }
		   if(keyCount<2&&lastTime==0)
			   Toast.makeText(this, "再按一次退出程序！", Toast.LENGTH_LONG).show();
		   else if(keyCount==2&&lastTime!=0&&curTime-lastTime<5000){
			   finish();
		   } else if(keyCount==2&&lastTime!=0&&(curTime-lastTime)>5000){
			   keyCount=0;
			   curTime=lastTime=0;
		   }
		}
		return false;
	}
	
	

}
