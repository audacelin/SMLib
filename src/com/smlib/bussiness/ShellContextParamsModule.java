package com.smlib.bussiness;

import java.io.File;

import com.smlib.bussiness.ShellSPConfigModule.CustSP;
import com.smlib.common.Shell;
import com.smlib.utils.AndroidUtils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/*
 * 通过该壳运行期上下文获取应用信息
 */
public final class ShellContextParamsModule {

	private Context mContext;
	
	private String curCustName;//
	private String curCustNo;
	private String curUserName;
	private String curPassWord;
	
	private String demoCustName;
	private String demoCustNo;
	private String demoUserName;
	private String demoUserPassWord;
	
	private String deviceID;
	private String appDirPath;
	private String appId;
	
	private ShellContextParamsModule(){
		
	}
	private static ShellContextParamsModule instance=new ShellContextParamsModule();
	
	public ShellContextParamsModule initContext(Context context){
		this.mContext=context;
		this.deviceID=AndroidUtils.System.genDeviceID();
		return this;
	}
	public static ShellContextParamsModule getInstance(){
		return instance;
	}
	//初始化系统参数
	public boolean load(){
		
		if(!loadAppMetaData()){
			Log.e("ShellApplication", "初始化应用壳失败！");
			return false;
		}
		
		this.appDirPath=Shell.SHELL_APP_DATA_BASE_DIR+File.separator+this.appId;
		File appDirFile=new File(this.appDirPath);
		if(!appDirFile.exists()){
			appDirFile.mkdirs();
		}
		return true;
	}
	
	private boolean loadAppMetaData(){
		
		ApplicationInfo info;
		try{
			info=this.mContext.getPackageManager().getApplicationInfo(this.mContext.getPackageName(),
					PackageManager.GET_META_DATA);
			this.setDemoCustName(info.metaData.getString("DemoCustName"));
			this.setDemoCustNo(String.valueOf(info.metaData.get("DemoCustNo")));
			Log.e("ShellApplication", "CustName is "+info.metaData.getString("DemoCustName")
					+"and AppID is "+String.valueOf(info.metaData.get("AppID")));
			this.setDemoUserName(info.metaData.getString("DemoName"));
			this.setDemoUserPassWord(info.metaData.getString("DemoSecret"));
			this.setAppId(String.valueOf(info.metaData.get("AppID")));
			
		}catch(NameNotFoundException ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	//从SP拷贝当前用户信息
	public void copyCustSP(){
	   CustSP custSP=ShellSPConfigModule.getInstance().getLastestCustSP();
	   if(custSP!=null){
		   this.curCustNo=custSP.custNo;
		   this.curCustName=custSP.customerName;
		   this.curUserName=custSP.userName;
		   this.curPassWord=custSP.userPassWord;
	   }
	 
	}
	
	public Context getmContext() {
		return mContext;
	}
	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}
	public void setCurCustName(String curCustName) {
		this.curCustName = curCustName;
	}
	public void setCurCustNo(String curCustNo) {
		this.curCustNo = curCustNo;
	}
	public String getCurCustNo(){
		return this.curCustNo;
	}
	public void setCurUserName(String curUserName) {
		this.curUserName = curUserName;
	}
	public void setCurPassWord(String curPassWord) {
		this.curPassWord = curPassWord;
	}
	public void setDemoCustName(String demoCustName) {
		this.demoCustName = demoCustName;
	}
	public void setDemoCustNo(String demoCustNo) {
		this.demoCustNo = demoCustNo;
	}
	public void setDemoUserName(String demoUserName) {
		this.demoUserName = demoUserName;
	}
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	public void setDemoUserPassWord(String demoUserPassWord) {
		this.demoUserPassWord = demoUserPassWord;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
}
