package com.smlib.bussiness;

import java.util.HashMap;
import java.util.Map;
import com.smlib.utils.StringUtils;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public final class ShellSPConfigModule {
    private Context mContext;
    private static String SHELL_SM_SP_KEY="shell_sm_sp_key";
    private final static String SP_CUSTOMERNAME="CUSTOMERNAME_";
    private final static String SP_ISAUTOLOGIN="AUTOLOGIN_",SP_USERNAME="USERNAME_",
    		SP_CUSTNO="CUSTNO_",SP_USERPSW="USERPSW_";
    
    private String lastersCustNo;
    private Map<String, CustSP> maper=new HashMap<String,CustSP>();
	private ShellSPConfigModule(){
		
	}
	private static ShellSPConfigModule instance=new ShellSPConfigModule();
	
	public ShellSPConfigModule initContext(Context context){
		this.mContext=context;
		return this;
	}
	
	public static ShellSPConfigModule getInstance(){
		return instance;
	}
	public void load(){
		SharedPreferences sp=mContext.getSharedPreferences(SHELL_SM_SP_KEY, Activity.MODE_PRIVATE);
		this.lastersCustNo=sp.getString("lastestCustNo", "");
		//如果当前SP内存储有最近登录的公司Id，则取出相应的用户信息
		if(!StringUtils.isStickBlank(this.lastersCustNo)){
			loadCustSP(sp,this.lastersCustNo);
		}
	}
	private CustSP loadCustSP(SharedPreferences sp,String custNo){
		CustSP custSP=new CustSP();
		custSP.custNo=custNo;
		custSP.customerName=sp.getString(SP_CUSTOMERNAME+custNo, "");
		custSP.isAutoLogin=Boolean.valueOf(sp.getString(SP_ISAUTOLOGIN+custNo, ""));
		custSP.userPassWord=sp.getString(SP_USERPSW+custNo, "");
		custSP.userName=sp.getString("USERNAME_"+custNo, "");
		this.maper.put(custNo, custSP);
		return custSP;
	}
	private SharedPreferences.Editor prepare(){
		SharedPreferences sp=mContext.getSharedPreferences(SHELL_SM_SP_KEY, Activity.MODE_PRIVATE);
		Editor editor=sp.edit();
		return editor;
	}
	private CustSP ensure(String custNo){
		CustSP custSP=this.maper.get(custNo);
		if(custSP==null){
			custSP=new CustSP();
			custSP.custNo=custNo;
			this.maper.put(custNo, custSP);
		}
		return custSP;
	}
	
	public final class CustSP{
		public String custNo;
	    public String customerName;
	    public String userName;
	    public String userPassWord;
	    public boolean isAutoLogin;
	}
	
	public String getLastestCustNO(){
		return this.lastersCustNo;
	}
	public CustSP getLastestCustSP(){
		return this.maper.get(this.getLastestCustNO());
	}
	
	public String getUserName(String custNo){
		CustSP custSP=this.maper.get(custNo);
		if(custSP==null){
			return null;
		}
		return custSP.userName;
	}
	
	public void setUserName(String custNo,String userName){
		if(this.prepare().putString(SP_USERNAME+custNo, userName).commit()){
			CustSP custSP=ensure(custNo);
			custSP.userName=userName;
		}
	}
	
	public String getUserPSW(String custNo){
		CustSP custSP=this.maper.get(custNo);
		if(custSP==null)
			return null;
		return custSP.userPassWord;
	}
	public void setUserPSW(String custNo,String userPSW){
		if(this.prepare().putString(SP_USERPSW+custNo, userPSW).commit()){
			CustSP custSP=ensure(custNo);
			custSP.userPassWord=userPSW;
		}
	}
	
	public String getCustName(String custNo){
		CustSP custSP=this.maper.get(custNo);
		if(custSP==null)
			return null;
		return custSP.customerName;
	}
	public void setCustName(String custNo,String custName){
		if(this.prepare().putString(SP_CUSTOMERNAME+custNo, custName).commit()){
			CustSP custSP=ensure(custNo);
			custSP.customerName=custName;
		}
	}
	
	public boolean getIsAutoLogin(String custNo){
	     CustSP custSP=this.maper.get(custNo);
	     if(custSP==null){
	    	 return false;
	     }
	     return custSP.isAutoLogin;
	}
	public void setIsAutoLogin(String custNo,boolean isAutoLogin) {
		if(this.prepare().putBoolean(SP_ISAUTOLOGIN+custNo, isAutoLogin).commit()){
			CustSP custSP=ensure(custNo);
			custSP.isAutoLogin=isAutoLogin;
		}
	}
	public boolean isAutoLogin(String custNo){
		CustSP custSP=this.maper.get(custNo);
		if(custSP==null)
			return true;
		return custSP.isAutoLogin;
	}
	
	public String getCustNo(String custNo){
		CustSP custSP=this.maper.get(custNo);
        if(custSP==null){
        	return null;
        }
        return custSP.custNo;
	}
	
	public void setCustNo(String custNo){
		if(this.prepare().putString(SP_ISAUTOLOGIN+custNo, custNo).commit()){
			CustSP custSP=ensure(custNo);
			custSP.custNo=custNo;
		}
	}
	
	
	
}
