package com.smlib.net;
import com.smlib.utils.*;


import android.content.Context;

public class EntranceHttpRequest extends KDHttpRequest {

	public EntranceHttpRequest(Context context,String url, HttpMethod method) {
		super(url, method);
		// TODO Auto-generated constructor stub
		// 编译业务查询时，请注释该行
				setHeader("Content-Type", "application/json;charset=utf-8");
				setHeader("AccessToken", SPUtils.getAccessToken(context));
				// setHeader("Accept-Encoding", "gzip");
				setHeader("DeviceType", "Android");
				setHeader("DeviceToken", "");
				setHeader("AppID", "445");
				setHeader("UID", ConfigGetter.uuId(context));
				setHeader("AppVersion", ConfigGetter.GetVersionName(context));
				setHeader("User-Agent", ConfigGetter.userAgent());
	}



}
