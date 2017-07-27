package com.smlib.net;

import java.io.IOException;
import java.net.SocketException;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

public class KDHttpClient {

	// 单例模式,一个浏览器
	private static DefaultHttpClient httpClient;

	public static void shutdown() {
		getHttpClient().getConnectionManager().shutdown();
	}

	public static synchronized HttpClient getHttpClient() {
		if (null == httpClient) {
			HttpParams params = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			HttpProtocolParams.setUseExpectContinue(params, false);
			// HttpProtocolParams.setUserAgent(params,
			// AndroidUtils.userAgent());

			// 超时设置
			// 从连接池中取连接的超时时间
			ConnManagerParams.setTimeout(params, 1000 * 10);
			// 连接超时
			HttpConnectionParams.setConnectionTimeout(params, 1000 * 20);
			// 请求超时
			HttpConnectionParams.setSoTimeout(params, 1000 * 20);

			// 设置我们的HttpClient支持HTTP和HTTPS两种模式
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

			// 使用线程安全的连接管理来创建HttpClient
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
			httpClient = new DefaultHttpClient(conMgr, params);
			httpClient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {

				@Override
				public boolean retryRequest(IOException exception, int executionCount,
						HttpContext context) {
					// TODO Auto-generated method stub

					if (executionCount < 2 && exception instanceof SocketException
							&& exception.getMessage().contains("Connection reset by peer")) {
						Log.d("KDHttpClient", "mCloud服务器出现 Connection reset by peer 异常,正在重试");
						return true;
					}

					return false;
				}
			});
		}
		return httpClient;
	}

}
