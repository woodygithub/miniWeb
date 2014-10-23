package com.org.miniweb.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.org.miniweb.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WXShareUtils {
	public static final String WX_APP_ID="wx5711daf42e137b8f";
	public static void shareUrl(final Context context,final String title,final String desc,final String url){
		final IWXAPI api=WXAPIFactory.createWXAPI(context, WX_APP_ID);
		api.registerApp(WX_APP_ID);
		
		new AlertDialog.Builder(context).setItems(new String[]{"分享到朋友圈","分享给好友"}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				WXWebpageObject webpage = new WXWebpageObject();
			    webpage.webpageUrl = url;
			    WXMediaMessage msg = new WXMediaMessage(webpage);
			    msg.title = title;
			    msg.description = context.getString(R.string.app_name);
			    //这里替换一张自己工程里的图片资源
			    Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_miniweb);
			    msg.setThumbImage(thumb);
			     
			    SendMessageToWX.Req req = new SendMessageToWX.Req();
			    req.transaction = String.valueOf(System.currentTimeMillis());
			    req.message = msg;
			    req.scene = which==1?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
			    api.sendReq(req);
			}
		}).setPositiveButton(android.R.string.cancel, null).create().show();
	}
}
