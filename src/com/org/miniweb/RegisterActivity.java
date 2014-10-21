package com.org.miniweb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class RegisterActivity extends Activity {

    public static void start(Activity activity,int reqCode){
        activity.startActivityForResult(new Intent().setClass(activity, RegisterActivity.class),reqCode);
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
	}
	
}
