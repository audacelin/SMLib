package com.smlib.net;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URI;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
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

import android.os.AsyncTask;
import android.util.Log;

/**
 * 封装包含一个http请求的HttpClient
 * */
public class KDHttpRequest_old {

	public enum HttpMethod {
		GET, POST
	};

	public static final int REQUSET_UNKNOWN = -1;
	public static final int REQUSET_SUCCESS = 0;
	public static final int REQUSET_FAILED = 1;
	public static final int REQUSET_ERROR = 2;

	private int flag;
	private HttpMethod method;
	private String strEntity;
	private HttpEntity entity;
	private int timeout;
	private Header header;
	private byte[] responseBytes;
	private Exception exception;
	private boolean isCancel;

	private KDHttpRequestLinstener linstener;
	private HttpRequestBase request;

	public KDHttpRequest_old(URI uri, HttpMethod method) {
		Log.d("KDHttpRequest_old", "uri=" + uri.toString());
		this.method = method;
		if (method == HttpMethod.GET) {
			request = new HttpGet(uri);
		} else if (method == HttpMethod.POST) {
			request = new HttpPost(uri);
		}
	}

	public KDHttpRequest_old(String uriStr, HttpMethod method) {
		Log.d("KDHttpRequest_old", "uriStr=" + uriStr);
		this.method = method;
		if (method == HttpMethod.GET) {
			request = new HttpGet(uriStr);
		} else if (method == HttpMethod.POST) {
			request = new HttpPost(uriStr);
		}
	}

	/**
	 * 取消当前请求
	 */
	public void cancel() {
		isCancel = true;
		if (request != null && !request.isAborted())
			request.abort();
	}

	/**
	 * 登出时调用
	 * */
	public void shutdown() {
		getHttpClient().getConnectionManager().shutdown();
	}

	/**
	 * 发起同步请求
	 * */
	public void startSynchronous() {

		if (method == HttpMethod.GET) {
			get();
		} else if (method == HttpMethod.POST) {
			post();
		}
	}

	/**
	 * 发起异步请求
	 * */
	public void startAsynchronous() {

		new AsyncTask<Boolean, Integer, Integer>() {

			@Override
			protected Integer doInBackground(Boolean... params) {
				// TODO Auto-generated method stub
				if (params[0]) {
					return KDHttpRequest_old.this.get();
				} else {
					return KDHttpRequest_old.this.post();
				}
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				if (isCancel) {
					return;
				}

				switch (result) {
				case REQUSET_SUCCESS:
					if (linstener != null) {
						linstener.onRequsetSuccess(KDHttpRequest_old.this);
					}
					break;

				case REQUSET_FAILED:
					if (linstener != null) {
						linstener.onRequsetFailed(KDHttpRequest_old.this);
					}
					break;

				case REQUSET_ERROR:
					if (linstener != null) {
						linstener.onRequsetError(KDHttpRequest_old.this, exception);
					}
					break;
				case REQUSET_UNKNOWN:
					// 未知错误
					break;
				default:
					break;
				}

				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

		}.execute(method == HttpMethod.GET);
	}

	private int get() {
		// GET
		HttpGet httpGet = (HttpGet) request;
		try {
			if (httpGet == null)
				return -1;

			HttpResponse response = getHttpClient().execute(httpGet);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				HttpEntity respEntity = response.getEntity();
				InputStream responseStream = respEntity.getContent();
				responseBytes = streamToByteArray(responseStream);
				responseStream.close();

				return REQUSET_SUCCESS;
			} else {
				return REQUSET_FAILED;
			}
		} catch (ClientProtocolException e) {
			exception = e;
			e.printStackTrace();
			return REQUSET_ERROR;
		} catch (IOException e) {
			exception = e;
			e.printStackTrace();
			return REQUSET_ERROR;
		} catch (Exception e) {
			exception = e;
			e.printStackTrace();
			return REQUSET_ERROR;
		} finally {
			if (request != null)
				request.abort();
		}

	}

	private int post() {
		HttpPost httpPost = (HttpPost) request;
		try {
			if (httpPost == null)
				return -1;

			httpPost.setEntity(entity);
			HttpResponse response = getHttpClient().execute(httpPost);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity respEntity = response.getEntity();
				InputStream is = respEntity.getContent();
				BufferedInputStream bis = new BufferedInputStream(is);
				bis.mark(2);
				// 取前两个字节
				byte[] header = new byte[2];
				int result = bis.read(header);
				// reset输入流到开始位置
				bis.reset();
				// 判断是否是GZIP格式
				int headerData = getShort(header);
				// Gzip 流 的前两个字节是 0x1f8b
				if (result != -1 && headerData == 0x1f8b) {
					Log.d("KDHttpRequest_old", "http inpustream is gzip format!!!");
					is = new GZIPInputStream(bis);
				} else {
					Log.d("KDHttpRequest_old", "http inpustream is not gzip format!!!");
					is = bis;
				}
				responseBytes = streamToByteArray(is);

				bis.close();
				is.close();
				return REQUSET_SUCCESS;
			} else {
				return REQUSET_FAILED;
			}

		} catch (ClientProtocolException e) {
			exception = e;
			e.printStackTrace();
			return REQUSET_ERROR;
		} catch (IOException e) {
			exception = e;
			e.printStackTrace();
			return REQUSET_ERROR;
		} catch (Exception e) {
			exception = e;
			e.printStackTrace();
			return REQUSET_ERROR;
		} finally {
			if (httpPost != null)
				httpPost.abort();
		}
	}

	// 字节流转换byte[]
	private byte[] streamToByteArray(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			return baos.toByteArray();
		} catch (IOException e) {
			// TODO: handle exception
		} finally {
			try {
				baos.flush();
				baos.close();
			} catch (IOException e2) {
				// TODO: handle exception
			}
		}
		return null;
	}

	private int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}

	public void setHeader(String name, String value) {
		if (request != null) {
			request.setHeader(name, value);
		}
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	public String getStrEntity() {
		return strEntity;
	}

	public void setStrEntity(String strEntity) {
		this.strEntity = strEntity;
	}

	public void setLinstener(KDHttpRequestLinstener linstener) {
		this.linstener = linstener;
	}

	public byte[] getResponseBytes() {
		return responseBytes;
	}

	public String getResponseString() {
		return new String(responseBytes);
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public static interface KDHttpRequestLinstener {

		public void onRequsetSuccess(KDHttpRequest_old request);

		public void onRequsetFailed(KDHttpRequest_old request);

		public void onRequsetError(KDHttpRequest_old request, Exception e);
	}

	// 单例模式,一个浏览器
	private static DefaultHttpClient httpClient;

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
						Log.d("KDHttpRequest_old", "mCloud服务器出现 Connection reset by peer 异常,正在重试");
						return true;
					}

					return false;
				}
			});
		}
		return httpClient;
	}

}