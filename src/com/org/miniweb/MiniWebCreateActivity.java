package com.org.miniweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fax.utils.bitmap.BitmapManager;
import com.fax.utils.http.HttpUtils;
import com.fax.utils.task.ResultAsyncTask;
import com.fax.utils.view.TopBarContain;
import com.google.gson.Gson;
import com.org.miniweb.model.MiniWebData;
import com.org.miniweb.model.MiniWebData.Content;
import com.org.miniweb.model.StringData;
import com.org.miniweb.model.User;
import com.org.miniweb.utils.WXShareUtils;

public class MiniWebCreateActivity extends Activity {
	static final int CHOOSE_IMAGE_FROM_SDCARD=1;
	static final int CHOOSE_IMAGE_FROM_CAMERA=2;
	TopBarContain topBarContain;
	EditText titleEt;
	EditText subTitleEt;
	TextView titleDataTv;
	ListView listView;
	MyAdapter mAdapter=new MyAdapter();
	MiniWebData miniWebData=new MiniWebData();
	
	private boolean isPreView;
	User user;
	public static void start(Activity activity){
		activity.startActivity(new Intent().setClass(activity, MiniWebCreateActivity.class));
	}
	
	/**开始分享 */
	private void startShareData(){
		user = MyApp.getLogedUser();
		if(user==null){
			Toast.makeText(this, getString(R.string.please_login), Toast.LENGTH_SHORT).show();
			return;
		}
		new ResultAsyncTask<StringData>(this) {
			@Override
			protected StringData doInBackground(Void... params) {
				miniWebData.setTitle(titleEt.getText().toString());
				miniWebData.setFu_title(subTitleEt.getText().toString());
				miniWebData.setUid(UUID.randomUUID().toString());
				miniWebData.setUsername(user.getUsername());
				String dataJson=new Gson().toJson(miniWebData);
				//Log.d("fax", "send:\n"+dataJson);
				try {
					StringBody dataBody=new StringBody(dataJson);
					HashMap<String, ContentBody> map=new HashMap<String, ContentBody>();
					//准备image.zip
					File[] images = getSubDir().listFiles();
					if(images.length>0){
						File imageZip = getImageFile("image.zip");
						ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(imageZip));
						for(File file:images){
							zos.putNextEntry(new ZipEntry(file.getName()));
							byte[] bytes = new byte[(int) file.length()];
							FileInputStream fis = new FileInputStream(file);
							fis.read(bytes);
							zos.write(bytes);
							bytes = null;
							fis.close();
						}
						zos.close();
						FileBody imageBody=new FileBody(imageZip);
						map.put("image", imageBody);
					}
					
					map.put("data", dataBody);
					String result = HttpUtils.reqForPost(MyApp.ApiUrl+"Articlinfo/prevew", map);
					return new Gson().fromJson(result, StringData.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void onPostExecuteSuc(StringData result) {
				if(result!=null){
//					Log.w("fax", "result:\n"+result.getData());
					WXShareUtils.shareUrl(MiniWebCreateActivity.this, miniWebData.getTitle(),
						miniWebData.getFu_title(), result.getData());
				}
			}
		}.setProgressDialog().execute();
	}
	
	/**开始预览 */
	private void startPreViewMode(){
		isPreView=true;
		listView.setSelectionAfterHeaderView();
		mAdapter.notifyDataSetChanged();
		listView.setOnItemClickListener(null);

		topBarContain.setTitle(R.string.MiniWeb_PreView)
		.setRightBtn(getString(R.string.MiniWeb_Share), new View.OnClickListener() {
			public void onClick(View v) {
				startShareData();
			}
		}).setLeftBtn(R.drawable.topbar_back, new View.OnClickListener() {
			public void onClick(View v) {
				startEditMode();
			}
		});
		findViewById(R.id.miniweb_add_bar).setVisibility(View.GONE);
		findViewById(R.id.miniweb_title_div).setVisibility(View.GONE);
//		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		titleDataTv.setVisibility(View.VISIBLE);
		titleDataTv.setText(SimpleDateFormat.getDateTimeInstance().format(new Date()));
		titleEt.setEnabled(false);
		titleEt.setGravity(Gravity.LEFT);
		subTitleEt.setEnabled(false);
		subTitleEt.setGravity(Gravity.LEFT);
	}
	
	/**编辑模式 */
	private void startEditMode(){
		isPreView=false;
		listView.setSelection(0);
		mAdapter.notifyDataSetChanged();
		listView.setOnItemClickListener(onItemClickListener);

		topBarContain.setTitle(R.string.Menu_MiniWebCreata)
		.setRightBtn(getString(R.string.MiniWeb_PreView), new View.OnClickListener() {
			public void onClick(View v) {
				startPreViewMode();
			}
		}).setLeftFinish(null, R.drawable.topbar_back);
		findViewById(R.id.miniweb_add_bar).setVisibility(View.VISIBLE);
		findViewById(R.id.miniweb_title_div).setVisibility(View.VISIBLE);
		titleDataTv.setVisibility(View.GONE);
		titleEt.setEnabled(true);
		titleEt.setGravity(Gravity.CENTER);
		subTitleEt.setEnabled(true);
		subTitleEt.setGravity(Gravity.CENTER);
	}
	
	AdapterView.OnItemClickListener onItemClickListener=new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			final MiniWebData.Content content=(Content) listView.getItemAtPosition(position);
			if(!content.isImageType()){//文字
				final EditText editText=new EditText(MiniWebCreateActivity.this);
				editText.setText(content.getContent());
				new AlertDialog.Builder(MiniWebCreateActivity.this).setTitle(R.string.MiniWeb_Edit_Text)
					.setView(editText).setNegativeButton(android.R.string.cancel, null)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							content.setContent(editText.getText().toString());
							mAdapter.notifyDataSetChanged();
						}
					}).create().show();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		topBarContain=new TopBarContain(this)
			.setContentView(R.layout.activity_miniweb_create);
		setContentView(topBarContain);
		
		titleEt=(EditText) findViewById(R.id.miniweb_title);
		subTitleEt=(EditText) findViewById(R.id.miniweb_subtitle);
		titleDataTv=(TextView) findViewById(R.id.miniweb_title_data);
		listView=(ListView) findViewById(android.R.id.list);
		listView.setAdapter(mAdapter);
		
		findViewById(R.id.miniweb_add_text).setOnClickListener(new View.OnClickListener() {//增加文字
			public void onClick(View v) {
				final EditText editText=new EditText(MiniWebCreateActivity.this);
				new AlertDialog.Builder(MiniWebCreateActivity.this).setTitle(R.string.MiniWeb_Add_Text)
					.setView(editText).setNegativeButton(android.R.string.cancel, null)
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
								miniWebData.addXinxi(editText.getText().toString(), 1);
								mAdapter.notifyDataSetChanged();
						}
					}).create().show();
			}
		});
		findViewById(R.id.miniweb_add_img_local).setOnClickListener(new View.OnClickListener() {//本地图片
			public void onClick(View v) {
				Intent intent = new Intent();
				/* 开启Pictures画面Type设定为image */
				intent.setType("image/*");
				/* 使用Intent.ACTION_GET_CONTENT这个Action */
				intent.setAction(Intent.ACTION_GET_CONTENT);
				/* 取得相片后返回本画面 */
				startActivityForResult(intent, CHOOSE_IMAGE_FROM_SDCARD);
			}
		});
		findViewById(R.id.miniweb_add_camera).setOnClickListener(new View.OnClickListener() {//摄像头
			public void onClick(View v) {
				tempCameraFile = new File(getExternalCacheDir(),"tempCameraImage");
				if(tempCameraFile.exists()) tempCameraFile.delete();
				Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
					.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempCameraFile));
				startActivityForResult(intent2, CHOOSE_IMAGE_FROM_CAMERA);
			}
		});
		startEditMode();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(tempCameraFile!=null) MyApp.delectFile(tempCameraFile);
		MyApp.delectFile(getSubDir());
	}
	
	@Override  
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        if (resultCode == RESULT_OK) {
        	try {
				switch (requestCode) {
				case CHOOSE_IMAGE_FROM_SDCARD:
					Uri uri = data.getData();
					ContentResolver cr = this.getContentResolver();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inJustDecodeBounds = true;
					Bitmap photo = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
					int scale = Math.max(options.outWidth / 800, options.outHeight / 800);
					scale++;
					if (scale < 1) scale = 1;
					options.inJustDecodeBounds = false;
					options.inSampleSize = scale;
					photo = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
					
					addImage(photo);
					break;
				case CHOOSE_IMAGE_FROM_CAMERA:
					photo = BitmapFactory.decodeFile(tempCameraFile.getPath());
					ExifInterface exifInterface = new ExifInterface(tempCameraFile.getPath());
					int tag = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
					Matrix m=new Matrix();
					if (tag == ExifInterface.ORIENTATION_ROTATE_90) {//如果是旋转的图片则先旋转
						m.postRotate(90, photo.getWidth()/2, photo.getHeight()/2);
						Bitmap bitmap=Bitmap.createBitmap(photo,0,0,photo.getWidth(),photo.getHeight(),m,false);
						photo.recycle();
						photo=bitmap;
					}else if (tag == ExifInterface.ORIENTATION_ROTATE_270) {//如果是旋转的图片则先旋转
						m.postRotate(270, photo.getWidth()/2, photo.getHeight()/2);
						Bitmap bitmap=Bitmap.createBitmap(photo,0,0,photo.getWidth(),photo.getHeight(),m,false);
						photo.recycle();
						photo=bitmap;
					}
					addImage(photo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
			}
        }
    }
	
	private void addImage(Bitmap photo) throws Exception {
		photo = BitmapManager.scaleToMiniBitmap(photo, 800, 800);
		String imageName=System.currentTimeMillis()+".jpg";
		photo.compress(CompressFormat.JPEG, 80, new FileOutputStream(getImageFile(imageName)));
		
		miniWebData.addXinxi(imageName, 0);
		mAdapter.notifyDataSetChanged();
	}
	private File tempCameraFile;
	private String subDirName="miniWebImageCache_"+System.currentTimeMillis();
	private File getSubDir(){
		File dir=new File(getExternalCacheDir(),subDirName);
		if(!dir.exists()) dir.mkdirs();
		return dir;
	}
	public File getImageFile(String imgFileName){
		return new File(getSubDir(), imgFileName);
	}
	
	class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return miniWebData.getXinxi().size();
		}
		@Override
		public MiniWebData.Content getItem(int position) {
			return miniWebData.getXinxi().get(position);
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public int getItemViewType(int position) {
			MiniWebData.Content content = getItem(position);
			return content.getType();
		}
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MiniWebData.Content content = getItem(position);
			if(content.isImageType()){//图片
				if (convertView == null)
					convertView = View.inflate(MiniWebCreateActivity.this,
						R.layout.activity_miniweb_create_item_img,null);
				ImageView imageView=(ImageView)convertView.findViewById(android.R.id.content);
				imageView.setImageBitmap(BitmapFactory.decodeFile(getImageFile(content.getContent()).getPath()));
				if(isPreView) convertView.findViewById(android.R.id.icon).setVisibility(View.GONE);
				else convertView.findViewById(android.R.id.icon).setVisibility(View.VISIBLE);
			}else{//文字
				if (convertView == null)
					convertView = View.inflate(MiniWebCreateActivity.this,
						R.layout.activity_miniweb_create_item_text,null);
				((TextView)convertView.findViewById(android.R.id.content)).setText(content.getContent());
				if(isPreView) convertView.findViewById(android.R.id.icon).setVisibility(View.GONE);
				else convertView.findViewById(android.R.id.icon).setVisibility(View.VISIBLE);
			}
			if(isPreView) {
				convertView.setClickable(true);
			}
			else {
				convertView.setClickable(false);
			}
			return convertView;
		}
	}
}