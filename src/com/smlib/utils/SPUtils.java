package com.smlib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtils {
	public static final String SP_NAME = "empuse";
	public static final String KEY_ACCESSTOKEN = "accessToken";

	public static String getAccessToken(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

		return sp.getString(KEY_ACCESSTOKEN, "");
	}

	public static void setAccessToken(Context context, String accessToken) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY_ACCESSTOKEN, accessToken);
		editor.commit();
	}
}
