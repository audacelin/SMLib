package com.smlib.net;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import android.os.AsyncTask;
import android.util.Log;

/**
 * 封装包含一个http请求的HttpClient
 * */
public class KDHttpRequest {
	public static final String TAG = "KDHttpRequest";

	public static final int REQUSET_UNKNOWN = -99;
	public static final int REQUSET_SUCCESS = 1;
	public static final int REQUSET_FAILED = 0;
	public static final int REQUSET_ERROR = -1;

	private int flag;
	private HttpMethod method;
	private HttpEntity entity;
	private String contentType;
	private Header header;

	private InputStream inputStream;
	private Exception exception;
	private HttpRequestBase request;
	private KDHttpRequestLinstener callbak;
	// 默认为自动释放
	private boolean isAutoRelease = true;
	private boolean isCancel;

	public KDHttpRequest(URI uri, HttpMethod method) {
		Log.d(TAG, "uri=" + uri.toString());
		this.method = method;
		if (method == HttpMethod.GET) {
			request = new HttpGet(uri);
		} else if (method == HttpMethod.POST) {
			request = new HttpPost(uri);
		}
	}

	public KDHttpRequest(String url, HttpMethod method) {
		Log.d(TAG, "url=" + url);
		this.method = method;
		if (method == HttpMethod.GET) {
			request = new HttpGet(url);
		} else if (method == HttpMethod.POST) {
			request = new HttpPost(url);
		}
	}

	public void release() {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void cancel() {
		isCancel = true;
		if (request != null && !request.isAborted())
			request.abort();
	}

	/**
	 * 登出时调用
	 * */
	public void shutdown() {
		KDHttpClient.shutdown();
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
				if (isCancel) {
					return REQUSET_UNKNOWN;
				}
				if (params[0]) {
					return KDHttpRequest.this.get();
				} else {
					return KDHttpRequest.this.post();
				}
			}

			@Override
			protected void onPostExecute(Integer result) {
				// TODO Auto-generated method stub
				if (isCancel) {
					release();
					return;
				}

				if (callbak != null) {
					if (REQUSET_SUCCESS == result) {
						callbak.onRequsetSuccess(KDHttpRequest.this);
					} else if (REQUSET_FAILED == result) {
						callbak.onRequsetFailed(KDHttpRequest.this);
					} else if (REQUSET_ERROR == result) {
						callbak.onRequsetError(KDHttpRequest.this, exception);
					} else {
						// 未知错误
					}
				}
				if (isAutoRelease) {
					release();
				} else {
					// 手动释放
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
				return REQUSET_UNKNOWN;

			HttpResponse response = KDHttpClient.getHttpClient().execute(
					httpGet);
			Log.d(TAG, "statuscode=" + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity respEntity = response.getEntity();
				if (respEntity.getContentType() != null) {
					contentType = respEntity.getContentType().getValue();
				}
				inputStream = respEntity.getContent();
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

		}

	}

	private int post() {
		HttpPost httpPost = (HttpPost) request;
		try {
			if (httpPost == null)
				return REQUSET_UNKNOWN;

			httpPost.setEntity(entity);
			HttpResponse response = KDHttpClient.getHttpClient().execute(
					httpPost);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity respEntity = response.getEntity();
				if (respEntity.getContentType() != null) {
					contentType = respEntity.getContentType().getValue();
				}
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
					Log.d(TAG, "http inpustream is gzip format!!!");
					inputStream = new GZIPInputStream(bis);
				} else {
					Log.d(TAG, "http inpustream is not gzip format!!!");
					inputStream = new BufferedInputStream(bis);
				}
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

		}
	}

	/**
	 * 返回结果是字节流还是String
	 * */
	public boolean isShouldToString() {
		return contentType != null
				&& contentType.startsWith("application/json");
	}

	private int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}

	public void setHeader(String name, String value) {
		if (request != null) {
			request.setHeader(name, value);
		}
	}

	// 字节流转换byte[]
	private byte[] stream2ByteArray(InputStream is) {
		if (is == null) {
			return null;
		}
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

	private String stream2String(InputStream is) {
		if (is == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			return baos.toString();
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

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	public void setLinstener(KDHttpRequestLinstener callback) {
		this.callbak = callback;
	}

	public void setIsAutoRelease(boolean isAutoRelease) {
		this.isAutoRelease = isAutoRelease;
	}

	public InputStream getRepsponeInputStream() {
		return inputStream;
	}

	public byte[] getResponseBytes() {
		return stream2ByteArray(inputStream);
	}

	private String streamString = "";

	// inputStream 只读取一次
	public String getResponseString() {
		if (streamString.equals("")) {
			return streamString = stream2String(inputStream);
		} else {
			return streamString;
		}
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	/*
	 * 请求回调接口
	 */
	public interface KDHttpRequestLinstener {

		public void onRequsetSuccess(KDHttpRequest request);

		public void onRequsetFailed(KDHttpRequest request);

		public void onRequsetError(KDHttpRequest request, Exception e);
	}

	public enum HttpMethod {
		GET, POST
	};

	public static HttpClient getHttpClient() {
		return KDHttpClient.getHttpClient();
	}
}