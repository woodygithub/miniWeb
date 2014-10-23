package com.org.miniweb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fax.utils.task.HttpAsyncTask;
import com.org.miniweb.model.User;

public class RegisterActivity extends Activity {

    public static final String Extra_UserName="userName";
    public static final String Extra_UserPassword = "passWord";

    public static void start(Activity activity,int reqCode){
        activity.startActivityForResult(new Intent().setClass(activity, RegisterActivity.class),reqCode);
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_activity);

	}
    public void registerClick(View v){
        final String userName = ((TextView)findViewById(R.id.register_username)).getText().toString();
        final String passWord = ((TextView)findViewById(R.id.register_password)).getText().toString();
        final String passWordConfirm = ((TextView)findViewById(R.id.register_password_confirm)).getText().toString();
        if (userName.length()<1||passWord.length()<1) {
            Toast.makeText(this, getString(R.string.please_fill_out), Toast.LENGTH_SHORT).show();
            return;
        }
        if (passWordConfirm.length()<1){
            Toast.makeText(this, getString(R.string.please_confirm_password), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!passWordConfirm.equals(passWord)){
            Toast.makeText(this, getString(R.string.password_differ), Toast.LENGTH_SHORT).show();
            return;
        }
        new HttpAsyncTask<Boolean>(this, MyApp.ApiUrl+"Userinfo/registuser?username="
            + userName + "&password=" + passWord)  {
            @Override
            protected void onPostExecuteSuc(Boolean result) {
                if(result){
                    regSuc(userName, passWord);
                }
            }
            @Override
            protected Boolean instanceObject(String json) throws Exception {
                json.replace(" ","");
                return "1".equals(json);
            }
        }.setProgressDialog().execute();
    }
    private void regSuc(String userName, String passWord){
        setResult(RESULT_OK, new Intent().putExtra(Extra_UserName, userName)
            .putExtra(Extra_UserPassword, passWord));
        finish();
    }
}
