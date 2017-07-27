package com.smlib.utils;

import org.json.JSONException;
import org.json.JSONObject;

import com.smlib.utils.MD5;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

public class ConfigGetter {
    //获取客户端的版本号，可用于跟服务器端对应的版本号相比，是否需要强制升级
	public static String GetVersionName(Context context) {
		String version="";
		PackageManager pm=context.getPackageManager();
		PackageInfo packageInfo;
		try {
		   packageInfo=pm.getPackageInfo(context.getPackageName(), 0);
		   version=packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return version;
	}
	// 设备UUID
	public static String uuId(Context context) {
		SharedPreferences sp = context.getSharedPreferences("system", Activity.MODE_PRIVATE);
		String deviceID = sp.getString("deviceID", "");
		if (deviceID != null && !deviceID.equals("")) {
			return deviceID;
		}

		String macAddr = getMacAddr(context);
		if (macAddr != null && !macAddr.equals("")) {
			deviceID = MD5.toMD5(macAddr);
			SharedPreferences.Editor editor = sp.edit();
			editor.putString("deviceID", deviceID);
			editor.commit();
		}
		return (deviceID != null && deviceID.equals("")) ? MD5.toMD5("device_id_not_found") : deviceID;
	}

	// 设备Mac地址
	public static String getMacAddr(Context context) {
		String macAddress = null;
		WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		boolean enabled = false;
		if (!wifiMgr.isWifiEnabled()) {
			wifiMgr.setWifiEnabled(true);
			enabled = true;
		}

		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info) {
			macAddress = info.getMacAddress();
		}

		if (enabled) {
			wifiMgr.setWifiEnabled(false);
		}

		return macAddress;

	}
	
	
	/**
	 * 业务服务器 User-Agent
	 * */
	public static String userAgent() {
		JSONObject jo = new JSONObject();
		try {
			jo.put("OS", "Android " + Build.VERSION.RELEASE);
			jo.put("Brand", Build.MANUFACTURER);
			jo.put("Model", Build.MODEL);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jo.toString();
	}
	
}
