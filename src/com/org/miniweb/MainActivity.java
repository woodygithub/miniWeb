package com.org.miniweb;

import com.fax.utils.view.TopBarContain;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	public static void strart(Activity activity){
		activity.startActivity(new Intent().setClass(activity, MainActivity.class));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		TopBarContain topBarContain = (TopBarContain)findViewById(R.id.main_topbar_layout);
		topBarContain.setRightBtn(R.drawable.main_setting_button, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
	}
	
	public void myMiniwebClick(View  v){
		
	}
	
	public void newMiniwebClick(View v){
		MiniWebCreateActivity.start(this);
	}
	
	public void hotMinwebClick(View v){
		
	}
}