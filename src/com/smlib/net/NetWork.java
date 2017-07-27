package com.smlib.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络状态
 */

public class NetWork {

	public static boolean isNetWorked(Context context) {
		ConnectivityManager nw = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = nw.getActiveNetworkInfo();

		if (netinfo != null) {
			return netinfo.isAvailable();
		}
		return false;
	}
}
