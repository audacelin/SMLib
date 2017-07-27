package com.smlib.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smlib.*;

/**
 * Android平台中常用的辅助方法集合。<br>
 * 
 * eg: 通过该类可以以如下方式取得字符串资源 <br>
 * <code>
 * ＡndroidUtils.s(R.string.stringid)
 * </code>
 * 
 * 功能分为几个部分：<br>
 * 1.System 系统的某些服务方法<br>
 * 2.Keyboard 键盘的某些服务方法<br>
 * 3.Screen 屏幕的某些服务方法<br>
 * 4.Image 图片的某些服务方法<br>
 * 5.Dialog 对话框的某些服务方法<br>
 * 
 * 使用方法：<br>
 * -----------------<br>
 * 1.在Application中初始化:<br>
 * <code>
 * AndroidUtils.regAppContext(application)
 * </code>
 * 
 * 2.使用该类的服务方法，基本是static方法。
 * 
 * 
 * @author teddy
 * 
 */
public class AndroidUtils {

	// context保留对Application对象的应用,在Application完成注册
	private static Context context;

	public static void regAppContext(Context ctx) {
		AndroidUtils.context = ctx;
	}

	public static Context appCtx() {
		return context;
	}

	// ////////////////////////

	// //////////////////

	// 从mime中取得图片的文件格式
	// image/jpeg image/gif等
	public static String getImageFormatFromMIME(String contentType, String def) {
		if (contentType == null || contentType.trim().length() == 0) {
			return def;
		}

		int index = contentType.indexOf("/");
		return contentType.substring(index + 1);

	}

	// 通过资源名返回对应的ID
	public static int getIdByName(Context ctx, String idName) {
		return ctx.getResources().getIdentifier(idName, "id",
				ctx.getApplicationInfo().packageName);

	}

	public static int getDrawableIdByName(Context ctx, String idName) {
		return ctx.getResources().getIdentifier(idName, "drawable",
				ctx.getApplicationInfo().packageName);

	}

	// 获得字符串资源
	public static String s(int stringid) {
		return context.getResources().getString(stringid);
	}

	// 获得字符串资源
	public static String getResourceString(Context ctx, int stringid) {
		return ctx.getResources().getString(stringid);

	}

	// 获得字符串短方法
	public static String s(Context ctx, int stringid) {
		return getResourceString(ctx, stringid);
	}

	// ///////////////////////////////////////////////

	/**
	 * 启动指定的Activity组件
	 */
	public static void startActivity(Activity c, Class<? extends Activity> clazz) {
		c.startActivity(new Intent(c, clazz));
	}

	/**
	 * toast一条消息
	 * 
	 * @param ctx
	 * @param msg
	 */
	public static void toastShort(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void toastLong(String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

	}

	// 启动浏览器
	public static void startBrowser(Activity ctx, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		ctx.startActivity(intent);
	}

	// 让一组中文字体加粗
	public static void boldText(TextView... tvs) {
		for (TextView tv : tvs) {
			boldText(tv);
		}

	}

	// 让中文字体加粗
	// TC: why?
	public static void boldText(TextView textView) {

		// 中文必须需要采用下面的方式设置粗体，在layout配置文件中无法通过textStyle设置为Bold来设置粗体，英文的没有这个问题
		Paint paint = textView.getPaint();
		paint.setFakeBoldText(true);

	}

	/**
	 * 将浮点数转化为字符串的描述，不用科学计数法显示,并保留scale位小数
	 * 
	 * @param d
	 *            要转化的数值
	 * @param scale
	 *            保留的位数
	 * @return
	 */

	public static String plainDescDouble(Double d, int scale) {
		return new BigDecimal(d).setScale(scale, RoundingMode.HALF_UP)
				.toString();

	}

	/**
	 * 
	 * 判断某个服务是否正处于运行状态 。
	 * 
	 * @param ctx
	 *            应用的上下文
	 * @param className
	 *            服务的包名 ".sche.ScheManagerService"
	 * @return
	 */
	public static boolean isServiceRunning(Context ctx, String className) {
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runingService = am
				.getRunningServices(Integer.MAX_VALUE);
		for (RunningServiceInfo info : runingService) {
			String shortName = info.service.getShortClassName();
			if (shortName.equals(className)) {
				return true;
			}
		}

		return false;

	}

	// //////////////////////////////////////////////
	// /////////////////系统相关的操作////////////////////
	// //////////////////////////////////////////////
	public static class System {

		public static boolean isNetworkAvailable() {

			// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
			try {
				ConnectivityManager connectivity = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (connectivity != null) {
					// 获取网络连接管理的对象
					NetworkInfo info = connectivity.getActiveNetworkInfo();
					if (info != null && info.isConnected()) {
						// 判断当前网络是否已经连接
						if (info.getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}

			} catch (Exception e) {
				return false;
			}

			return false;

		}

		public static String getPhoneNo() {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String tel = tm.getLine1Number();

			return tel;

		}

		public static String getMacAddr() {
			String macAddress = null;
			WifiManager wifiMgr = (WifiManager) appCtx().getSystemService(
					Context.WIFI_SERVICE);

			boolean enabled = false;
			if (!wifiMgr.isWifiEnabled()) {
				wifiMgr.setWifiEnabled(true);
				enabled = true;
			}

			WifiInfo info = (null == wifiMgr ? null : wifiMgr
					.getConnectionInfo());
			if (null != info) {
				macAddress = info.getMacAddress();
			}

			if (enabled) {
				wifiMgr.setWifiEnabled(false);
			}

			return macAddress;

		}

		public static String genDeviceID() {

			SharedPreferences sp = context.getSharedPreferences("emp.system",
					Activity.MODE_PRIVATE);
			String deviceID = sp.getString("deviceID", "");
			if (!StringUtils.isBlank(deviceID)) {
				return deviceID;
			}

			String macAddr = getMacAddr();
			if (!StringUtils.isBlank(macAddr)) {
				deviceID = MD5.toMD5(macAddr);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("deviceID", deviceID);
				editor.commit();
			}

			return StringUtils.isBlank(deviceID) ? MD5.toMD5("") : deviceID;
		}

		/** 获取客户端版本 **/
		public static String getVersionName() {
			PackageManager pm = context.getPackageManager();
			String versionName = "";
			try {
				PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
				versionName = pi.versionName;
			} catch (NameNotFoundException e) {
				return versionName;
			}
			return versionName;
		}

		/**
		 * 获取CPU序列号
		 * 
		 * @return CPU序列号(16位) 读取失败为"0000000000000000"
		 */
		public static String getCPUSerial() {
			String str = "", strCPU = "", cpuAddress = "0000000000000000";
			try {
				// 读取CPU信息
				Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
				InputStreamReader ir = new InputStreamReader(
						pp.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);
				// 查找CPU序列号
				for (int i = 1; i < 100; i++) {
					str = input.readLine();
					if (str != null) {
						// 查找到序列号所在行
						if (str.indexOf("Serial") > -1) {
							// 提取序列号
							strCPU = str.substring(str.indexOf(":") + 1,
									str.length());
							// 去空格
							cpuAddress = strCPU.trim();
							break;
						}
					} else {
						// 文件结尾
						break;
					}
				}
			} catch (IOException ex) {
				// 赋予默认值
				ex.printStackTrace();
			}
			return cpuAddress;
		}

		// 获得模拟的设备ID(保证是唯一值)
		@Deprecated
		private static String genFakeDeviceId() {
			final String tmDevice, cpuSerial, androidId;
			final TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			tmDevice = "" + tm.getDeviceId();
			cpuSerial = "" + getCPUSerial();
			androidId = ""
					+ android.provider.Settings.Secure.getString(
							context.getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);
			UUID deviceUuid = new UUID(androidId.hashCode(),
					((long) tmDevice.hashCode() << 32) | cpuSerial.hashCode());
			String uniqueId = deviceUuid.toString();

			return MD5.toMD5(uniqueId);

			// return uniqueId;
		}

		// SD卡是否就绪
		public static boolean isSDReady() {
			return Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED);
		}

		// 检测WIFI是否可用
		public static boolean isWifiEnable(Activity ctx) {
			WifiManager wifiManager = (WifiManager) ctx
					.getSystemService(Context.WIFI_SERVICE);

			return wifiManager.isWifiEnabled();

		}

		// 获得正在用的WIFI SSID名称
		public static String getActiveWIFI(Activity ctx) {

			WifiManager wifiManager = (WifiManager) ctx
					.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager.isWifiEnabled()) {
				return wifiManager.getConnectionInfo().getSSID();
			}

			return "";

		}

		// 启动WIFI设置界面
		public static void startWifiSetting(Activity ctx) {
			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_WIFI_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.startActivity(intent);
		}

		/**
		 * 检查前台程序是否存在
		 * 
		 * @param packageName
		 * @return
		 */
		public static boolean checkApp(String packageName) {

			try {
				AndroidUtils
						.appCtx()
						.getPackageManager()
						.getApplicationInfo(packageName,
								PackageManager.GET_UNINSTALLED_PACKAGES);

				return true;
			} catch (NameNotFoundException e) {

				return false;
			}
		}

	}

	// //////////////////////////////////////////////
	// /////////////////包相关的检测////////////////////
	// //////////////////////////////////////////////
	public static class Package {

		public static boolean isPackageExists(String targetPackage) {
			List<ApplicationInfo> packages;
			PackageManager pm;
			pm = context.getPackageManager();
			packages = pm.getInstalledApplications(0);
			for (ApplicationInfo packageInfo : packages) {
				if (packageInfo.packageName.equals(targetPackage))
					return true;
			}
			return false;
		}

		public static boolean isPackageExists1(String targetPackage) {
			PackageManager pm = context.getPackageManager();
			try {
				pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
			} catch (NameNotFoundException e) {
				return false;
			}
			return true;
		}

		public static String getPackageVersion(String targetPackage) {

			PackageManager pm = context.getPackageManager();
			try {
				PackageInfo info = pm.getPackageInfo(targetPackage,
						PackageManager.GET_META_DATA);
				return info.versionName;

			} catch (NameNotFoundException e) {
				return null;
			}

		}

	}

	// //////////////////////////////////////////////
	// /////////////////键盘相关的操作////////////////////
	// //////////////////////////////////////////////
	public static class Keyboard {

		// 显示或者隐藏输入键盘
		public static void hideKeyboard(Activity ctx, final Runnable afterHide) {

			InputMethodManager imm = (InputMethodManager) ctx
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			View view = ctx.getCurrentFocus();
			if (view != null) {
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0,
						new ResultReceiver(new Handler()) {

							protected void onReceiveResult(int resultCode,
									Bundle resultData) {
								afterHide.run();
							}

						});
			}
		}

		// 隐藏软键盘
		public static void hideKeyboard(Activity ctx) {

			InputMethodManager imm = (InputMethodManager) ctx
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			View view = ctx.getCurrentFocus();
			if (view != null && imm.isActive()) {
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);// 隐藏软键盘

			}
		}

		// 键盘是否已经显示出来
		public static boolean isKeybordShown(Activity ctx, View focusView) {
			InputMethodManager imm = (InputMethodManager) ctx
					.getSystemService(Activity.INPUT_METHOD_SERVICE);

			return imm.isActive(focusView);

		}

		// 这个方法是可用的
		public static void showKeyboard(Activity ctx, View focusView) {

			InputMethodManager imm = (InputMethodManager) ctx
					.getSystemService(Activity.INPUT_METHOD_SERVICE);

			imm.showSoftInput(focusView, 0);

		}

		/**
		 * 默认不显示输入面板
		 * 
		 * @param ctx
		 */
		public static void noExplicityInputMethod(Activity ctx) {
			ctx.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}

	}

	// //////////////////////////////////////////////
	// /////////////////屏幕相关的操作////////////////////
	// //////////////////////////////////////////////
	// 对屏幕的操作
	public static class Screen {

		static float density = -1;

		/**
		 * 获得屏幕的大小
		 * 
		 * @param ctx
		 * @return
		 */
		public static int[] getDisplay() {
			DisplayMetrics dm = getMetrics();
			return new int[] { dm.widthPixels, dm.heightPixels };
		}

		public static float getDentisy() {
			if (density != -1) {
				return density;
			}

			density = getMetrics().densityDpi;
			return density;

		}

		public static DisplayMetrics getMetrics() {
			return context.getResources().getDisplayMetrics();

		}

		/**
		 * 将dp转化为平台大小的像素
		 * 
		 * @param dp
		 * @return
		 */
		public static int dp2pix(float dp) {
			return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					(float) dp, context.getResources().getDisplayMetrics());
		}
		
		public static float convertDp2Pixel(float dp){
			Resources resources=context.getResources();
			DisplayMetrics displayMetrics=resources.getDisplayMetrics();
			float px=dp*((float)displayMetrics.density/DisplayMetrics.DENSITY_DEFAULT);
			return px;
		}

		/**
		 * 设置某个Activity界面，使其没有标题栏
		 * 
		 * @param ctx
		 */
		public static void noWindowTitle(Activity ctx) {
			ctx.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		/**
		 * 全屏
		 * 
		 * @param ctx
		 */
		public static void fullScreen(Activity ctx) {
			ctx.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

		}

	}

	// //////////////////////////////////////////////
	// /////////////////图片相关的操作////////////////
	// //////////////////////////////////////////////
	public static class Image {

		// 设置图片圆角
		public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(rect);
			final float roundPx = pixels;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		}

		/**
		 * 将btyes数组转化为bitmap对象
		 * 
		 * @param b
		 * @return
		 */
		public static Bitmap bytes2bitmap(byte[] b) {
			if (b.length != 0) {
				return BitmapFactory.decodeByteArray(b, 0, b.length);
			} else {
				return null;
			}
		}

		/**
		 * 将bitmap对象转化为字节数组
		 * 
		 * @param bm
		 * @return
		 */
		public static byte[] bitmap2bytes(Bitmap bm) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		}

		/**
		 * 从资源文件中获取Drawable对象并转化为Bitmap对象
		 * 
		 * @param drawableId
		 * @return
		 */
		public static Bitmap getBitmapFromResourceDrawable(int drawableId) {
			Resources res = context.getResources();
			return BitmapFactory.decodeResource(res, drawableId);

		}

		/**
		 * 将Drawable对象转化为Bitmap对象
		 * 
		 * @param drawable
		 * @return
		 */
		public static Bitmap drawable2Bitmap(Drawable drawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		}

		/**
		 * 等比例缩放
		 * 
		 * @param src
		 * @param newWidth
		 * @param newheight
		 * @return
		 */
		public static Bitmap scaleImageKeepRatio(Bitmap src, int newWidth,
				int newheight) {

			// 并设置照相机的图标为照片的缩略图
			int width = src.getWidth();
			int height = src.getHeight();

			// 定义缩放的高和宽的尺寸
			float sw = ((float) newWidth) / width;
			float sh = ((float) newheight) / height;

			float ratio = sw > sh ? sw : sh;

			// 创建操作图片的用的Matrix对象
			Matrix matrix = new Matrix();
			matrix.postScale(ratio, ratio);
			Bitmap resizeBitmap = Bitmap.createBitmap(src, 0, 0, width, height,
					matrix, true);

			return resizeBitmap;

		}

		/**
		 * 缩小放大图片
		 * 
		 * @param src
		 * @param towidth
		 * @param toheight
		 * @return
		 */
		public static Bitmap scaleImageTo(Bitmap src, int newWidth,
				int newheight) {

			// 并设置照相机的图标为照片的缩略图
			int width = src.getWidth();
			int height = src.getHeight();

			// 定义缩放的高和宽的尺寸
			float sw = ((float) newWidth) / width;
			float sh = ((float) newheight) / height;

			// 创建操作图片的用的Matrix对象
			Matrix matrix = new Matrix();
			matrix.postScale(sw, sh);
			Bitmap resizeBitmap = Bitmap.createBitmap(src, 0, 0, width, height,
					matrix, true);

			return resizeBitmap;

		}

		// 按指定大小解码bitmap,防止OOM
		// 对大图片的处理
		public static Bitmap decodeBitmapFile(Activity ctx, Uri uri, int maxSize)
				throws Exception {
			Bitmap b = null;
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(ctx.getContentResolver()
					.openInputStream(uri), null, o);
			int scale = 1;
			if (o.outHeight > maxSize || o.outWidth > maxSize) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(maxSize
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			b = BitmapFactory.decodeStream(ctx.getContentResolver()
					.openInputStream(uri), null, o2);

			return b;
		}

		// 从字节数组中解码出一张图片
		public static Bitmap decodePicFromByteArray(byte[] bytes,
				BitmapFactory.Options opts) {

			if (bytes != null)
				if (opts != null)
					return BitmapFactory.decodeByteArray(bytes, 0,
							bytes.length, opts);
				else
					return BitmapFactory
							.decodeByteArray(bytes, 0, bytes.length);
			return null;
		}

	}

	// //////////////////////////////////////////////
	// /////////////////对话框 相关的操作////////////////
	// //////////////////////////////////////////////
	public static class Dialog {

		// 对话框所用到的资源,在application onCreate的时候初始化
		public static int gzit_dialog_alert_title = R.string.dialog_alert_title;
		public static int gzit_dialog_alert_ok = R.string.dialog_alert_ok;
		public static int gzit_dialog_pic_save = R.string.dialog_alert_save;
		public static int gzit_pic_chooser_title = R.string.dialog_alert_title;
		public static int gzit_dialog_back = R.string.dialog_back;
		public static int gzit_dialog_pic_abandon = R.string.dialog_alert_cancel;
		public static int gzit_dialog_alert_cancel = R.string.dialog_alert_cancel;

		public static AlertDialog showPicChooser(String[] items,
				OnClickListener listener, Activity ctx) {

			// Create a builder
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			builder.setTitle(
					AndroidUtils.getResourceString(ctx, gzit_pic_chooser_title))
					.setItems(items, listener);

			// add buttons and listener
			builder.setPositiveButton(
					AndroidUtils.getResourceString(ctx, gzit_dialog_back),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});

			// Create the dialog
			AlertDialog ad = builder.create();

			// show
			ad.show();

			return ad;

		}

		// 在对话框中显示某个出某个视图
		public static void show(Activity context, View view) {

			// Create a builder
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Content Window");

			builder.setView(view);
			// add buttons and listener
			builder.setPositiveButton("OK", null);

			// Create the dialog
			AlertDialog ad = builder.create();

			// show
			ad.show();
		}

		public static void alert(Context ctx, String message) {

			alert(ctx, s(ctx, gzit_dialog_alert_title),
					s(ctx, gzit_dialog_alert_ok), message, null);

		}

		public static void alert(Context ctx, String message,
				OnClickListener listener) {

			alert(ctx, s(ctx, gzit_dialog_alert_title),
					s(ctx, gzit_dialog_alert_ok), message, listener);

		}

		public static void alert(Context context, String title,
				String postBtnText, String message, OnClickListener listener) {

			// Create a builder
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle(title);

			builder.setMessage(message);
			// add buttons and listener
			builder.setPositiveButton(postBtnText, listener);

			// Create the dialog
			AlertDialog ad = builder.create();

			// show
			ad.show();

		}

		public static void showPic(Activity context, Bitmap bitmap) {
			ImageView view = new ImageView(context);
			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			view.setImageBitmap(bitmap);
			show(context, view);

		}

		public static void showPicConfim(Activity ctx, Uri uri,
				final OnClickListener okListener) throws Exception {

			showPicConfim(ctx, uri, -1, okListener);

		}

		public static void showPicConfim(Activity ctx, Uri uri, int maxSize,
				final OnClickListener okListener) throws Exception {

			AlertDialog.Builder builder = new Builder(ctx);
			ImageView view = new ImageView(ctx);
			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));

			if (maxSize == -1) {
				view.setImageURI(uri);
			} else {
				view.setImageBitmap(Image.decodeBitmapFile(ctx, uri, maxSize));
			}

			builder.setView(view);
			builder.setTitle(s(ctx, gzit_dialog_alert_title));
			OnClickListener listener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (which == DialogInterface.BUTTON_POSITIVE) {
						okListener.onClick(dialog, which);
					}

				}
			};

			builder.setPositiveButton(s(ctx, gzit_dialog_pic_save), listener);
			builder.setNegativeButton(s(ctx, gzit_dialog_pic_abandon), listener);
			AlertDialog dialog = builder.create();
			dialog.show();
		}

		public static void showPicConfim(Activity ctx, Bitmap bitmap,
				OnClickListener okListener) {

			AlertDialog.Builder builder = new Builder(ctx);
			ImageView view = new ImageView(ctx);
			view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			view.setImageBitmap(bitmap);
			builder.setView(view);
			builder.setTitle(s(ctx, gzit_dialog_alert_title));
			builder.setPositiveButton(s(ctx, gzit_dialog_pic_save), okListener);

			builder.setNegativeButton(s(ctx, gzit_dialog_pic_abandon),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
		}

		public static void confirm(Activity ctx, String title, String msg,
				String okBtnLabel, String cancelBtnLabel,
				OnClickListener okListener, OnClickListener cancelListener) {

			AlertDialog.Builder builder = new Builder(ctx);
			builder.setMessage(msg);
			builder.setTitle(title);
			builder.setPositiveButton(okBtnLabel, okListener);
			builder.setNegativeButton(cancelBtnLabel, cancelListener);
			AlertDialog dialog = builder.create();
			dialog.show();

		}

		public static void confirm(Activity ctx, String title, String msg,
				String okBtnLabel, String cancelBtnLabel,String extBtnLabel,
				OnClickListener okListener, OnClickListener cancelListener,OnClickListener extListener){
			AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
			dialog.setTitle(title).setMessage(
					msg).setPositiveButton(okBtnLabel,
							okListener).setNegativeButton(cancelBtnLabel, cancelListener).setNeutralButton(extBtnLabel, extListener).create();
			 dialog.show();
		}
		
		public static void confirm(Activity ctx, String title, String msg,
				OnClickListener okListener) {

			confirm(ctx, title, msg, okListener, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});

		}

		public static void confirm(Activity ctx, String title, String msg,
				OnClickListener okListener, OnClickListener cancelListener) {

			confirm(ctx, title, msg, s(ctx, gzit_dialog_alert_ok),
					s(ctx, gzit_dialog_alert_cancel), okListener,
					cancelListener);

		}

		public static void showConfirm(Activity ctx, String msg,
				OnClickListener okListener) {
			confirm(ctx, s(ctx, gzit_dialog_alert_title), msg, okListener);

		}

		public static void showConfirm(Activity ctx, String title, String msg,
				OnClickListener okListener) {
			confirm(ctx, title, msg, okListener);

		}

		public static void prompt(Activity ctx, String title, int layout_id,
				OnClickListener okListener) {
			AlertDialog.Builder builder = new Builder(ctx);
			builder.setTitle(title);
			builder.setView(ctx.getLayoutInflater().inflate(layout_id, null));
			builder.setPositiveButton(s(ctx, gzit_dialog_alert_ok), okListener);
			builder.setNegativeButton(s(ctx, gzit_dialog_alert_cancel),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();

						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();

		}

		// ////////////////
	}

}
