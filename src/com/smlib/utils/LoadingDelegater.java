package com.smlib.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.smlib.R;

/**
 * 管理一个loading条
 * 
 * @author teddy
 * 
 */
public class LoadingDelegater {

	View loadingView;

	// 创建loading条
	public void init(Activity ctx) {
		loadingView = ctx.getLayoutInflater().inflate(
				R.layout.fullscreen_loading_indicator, null);
		loadingView.setVisibility(View.INVISIBLE);
		ctx.addContentView(loadingView, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public void show() {
		this.show("正在处理中，请稍候...");

	}

	public void show(String text) {

		TextView tv = (TextView) loadingView.findViewById(R.id.loading_text);
		tv.setText(text);
		loadingView.setVisibility(View.VISIBLE);
	}

	public void hide() {
		loadingView.setVisibility(View.INVISIBLE);
	}

}
