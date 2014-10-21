package com.org.miniweb;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fax.utils.http.RequestFactory;
import com.fax.utils.task.GsonAsyncTask;
import com.org.miniweb.model.User;

public class LoginActivity extends Activity {

    final int REQCODE = 1;

	public static void strart(Activity activity){
		activity.startActivity(new Intent().setClass(activity, LoginActivity.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
	}
	
	public void loginClick(View v){
		final String userName = ((TextView)findViewById(R.id.username)).getText().toString();
		String passWord = ((TextView)findViewById(R.id.password)).getText().toString();
		if (userName.length()<1||passWord.length()<1) {
			Toast.makeText(this, getString(R.string.please_fill_out), Toast.LENGTH_SHORT).show();
			return;
		}
		HttpRequestBase httpRequest = RequestFactory.createPost(MyApp.ApiUrl + "Api/login",
				new BasicNameValuePair("username", userName),
				new BasicNameValuePair("password", passWord) );
		new GsonAsyncTask<User>(this, MyApp.ApiUrl+"Api/login?username="
			+ userName + "&password=" + passWord)  {
			@Override
			protected void onPostExecuteSuc(User result) {
				if(result.isSuccess()){
					result.setUsername(userName);
					MyApp.saveLogedUser(result);
					setResult(RESULT_OK, new Intent().putExtra(User.class.getName(), result));
					MainActivity.strart(LoginActivity.this);
					finish();
				}
			}
		}.setProgressDialog().execute();
	}
	
	public void registerClick(View v){
        RegisterActivity.start(this, REQCODE);
	}
	
	public void ignoreClick(View v){
		MainActivity.strart(this);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQCODE:
                    break;
                default:
                    break;
            }
        }
    }
}