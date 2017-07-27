package com.smlib.activity;

import com.smlib.bussiness.ShellContextParamsModule;
import com.smlib.bussiness.ShellSPConfigModule;
import com.smlib.utils.AndroidUtils;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class ShellApplication extends Application {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//注册工具类
		AndroidUtils.regAppContext(this);
		Log.e("ShellApplication", "application init");
		if(!ShellContextParamsModule.getInstance().initContext(this).load()){
			throw new RuntimeException("初始化应用壳失败，请确定在Manifest配置了metaData！");
		}
		
		ShellSPConfigModule.getInstance().initContext(this).load();
		ShellContextParamsModule.getInstance().copyCustSP();
	}
	
	//系统内存紧张要kill掉程序时，析放内存
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
