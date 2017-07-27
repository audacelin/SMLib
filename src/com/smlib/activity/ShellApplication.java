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
		//ע�Ṥ����
		AndroidUtils.regAppContext(this);
		Log.e("ShellApplication", "application init");
		if(!ShellContextParamsModule.getInstance().initContext(this).load()){
			throw new RuntimeException("��ʼ��Ӧ�ÿ�ʧ�ܣ���ȷ����Manifest������metaData��");
		}
		
		ShellSPConfigModule.getInstance().initContext(this).load();
		ShellContextParamsModule.getInstance().copyCustSP();
	}
	
	//ϵͳ�ڴ����Ҫkill������ʱ�������ڴ�
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
