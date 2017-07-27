package com.smlib.activity;

import com.smlib.R;

import android.content.Context;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ShellSMLoginActivity extends TraceActivity{

	
	protected EditText userNameET;
	protected EditText pswET;
	protected Context mContext;
	protected Button demoBtn;
	protected Button loginBtn;
	protected TextView mRegister;
	protected TextView mFindPSW;
	
	View.OnClickListener onClickListener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			int id=v.getId();
//			switch(id){
//			       //演示
//			   case R.id.login_btn_demo:
//				   
//				   break;
//				   //登陆
//			   case R.id.login_btn_submit:
//				   
//				   break;
//				   //注册
//			   case R.id.login_register:
//				   
//				   break;
//				   //找回密码
//			   case R.id.login_find_psw:
//				   
//				   break;
//			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext=this;
		//setContentView(R.layout.login_activity);
		//initUI();
	}
	private void initUI(){
		mRegister=(TextView) this.findViewById(R.id.login_register);
		mFindPSW=(TextView)this.findViewById(R.id.login_find_psw);
		loginBtn=(Button)this.findViewById(R.id.login_btn_submit);
		demoBtn=(Button)this.findViewById(R.id.login_btn_demo);
		userNameET=(EditText)this.findViewById(R.id.login_username);
		userNameET.setSingleLine(true);
		pswET=(EditText)this.findViewById(R.id.login_psw);
		pswET.setSingleLine(true);
		pswET.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if(actionId==EditorInfo.IME_ACTION_GO){
					//输入密码后直接点击软键盘的GO按钮进行登陆
					
					return true;
				}
				return false;
			}
			
			
		});
		//代码设置密码不可见
		pswET.setTransformationMethod(PasswordTransformationMethod.getInstance());
		
		
	}
	

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
