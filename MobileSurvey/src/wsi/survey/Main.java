package wsi.survey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import wsi.survey.media.AudioProcess;
import wsi.survey.result.GConstant;
import wsi.survey.util.ImageAdapter;
import wsi.survey.util.ImageGallery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Activity {
	private final String TAG = "Main";
	
	private ImageGallery imgGallery;
	private TextView tvTitle;
	private TextView tvDescrp;
	private ImageView ivWelcome;
	private RelativeLayout layout_main;
	
	private TelephonyManager mTelManager;

	private List<Map<String, Object>> imgList = new ArrayList<Map<String, Object>>();	// 问卷的图片资源
	private List<Map<String, String>> mList = new ArrayList<Map<String, String>>();		// 问卷的文件名、标题、描述

	private static boolean isFirstLoad = true;		// 是否是第一次加载（第一次加载，开机动画播放完后，开始播放背景音乐）
	private static boolean isStartActivity = false;		// 是否是开启新Activity（是开启新Activity，true不暂停背景音乐；不是开启新Activity，false暂停背景音乐，如按Home键）
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.i(TAG, "onCreate()");
		
		isFirstLoad = true;
		AudioProcess.loadMedia(this);
		
		initResource();
		animPlay();
	}

	/** 初始化控件 */
	private void initResource(){
		ivWelcome = (ImageView)findViewById(R.id.ivWelcome);
		imgGallery = (ImageGallery)findViewById(R.id.imgGallery);
		tvTitle = (TextView)findViewById(R.id.tvCaption);
		tvDescrp = (TextView)findViewById(R.id.tvDescrp);		 
		layout_main = (RelativeLayout)findViewById(R.id.layout_main);
		
		mTelManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		GConstant.IMEI = mTelManager.getDeviceId();
		
		// 获取物理屏幕
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		GConstant.adjustFontSize(screenWidth, screenHeight);
		
		Log.i(TAG, "dm.density = " + dm.density + "; screenWidth = " + screenWidth + "; screenHeight = " + screenHeight);
	}
	
	/** 播放开机动画 */
	private void animPlay(){
		Animation anim = AnimationUtils.loadAnimation(Main.this, R.anim.anim_main_welcome);
		anim.setFillEnabled(true); 
		anim.setFillAfter(true);  
		ivWelcome.startAnimation(anim);
		
		Message msg = Message.obtain();
		mHandler.sendMessageDelayed(msg, 3000);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			ivWelcome.setVisibility(View.INVISIBLE);
			layout_main.setBackgroundResource(R.drawable.bg_main);
			loadResource();			// 开机动画后，开始加载资源并显示
		}
	};
	
	/** 加载资源，为控件赋值 */
	private void loadResource(){
		AudioProcess.playMedia();
		isFirstLoad = false;
		
		for(int i=0; i<GConstant.surveyFiles.length; i++){
			Map<String, Object> imgMap = new HashMap<String, Object>();	// 图片资源
			imgMap.put("images", GConstant.imgs[i]);
			imgList.add(imgMap);
			
			Map<String, String> map = new HashMap<String, String>();		// 文件名、标题、描述
			map.put("fileName", GConstant.surveyFiles[i][0]);
			map.put("fileTitle", GConstant.surveyFiles[i][1]);
			map.put("fileDescrp", GConstant.surveyFiles[i][2]);
			mList.add(map);
		}
		
		ImageAdapter imgAdapter = new ImageAdapter(this, imgList);
		imgAdapter.createReflectedImages();
		imgGallery.setAdapter(imgAdapter);
		
		imgGallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				tvTitle.setTextSize(GConstant.titleFontSize);
				tvTitle.setText(GConstant.surveyFiles[position][1]);
				tvDescrp.setText(GConstant.surveyFiles[position][2]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		imgGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(Main.this, AnswerQuesion.class);
				intent.putExtra("fileName", GConstant.surveyFiles[position][0]);
				isStartActivity = true;
				startActivity(intent);
			}
		});
	}

	@Override
	protected  void onStart(){
		super.onStart();
		Log.i(TAG, "onStart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume()");
		isStartActivity = false;
		
		if(!isFirstLoad){
			AudioProcess.playMedia();
			Log.i(TAG, "isFirstLoad = " + isFirstLoad);
		} else {
			Log.i(TAG, "isFirstLoad = " + isFirstLoad);
		}
		
	}

	@Override
	protected  void onPause() {
		super.onPause();
		Log.i(TAG, "onPause()");
		if(!isStartActivity){
			AudioProcess.pauseMedia();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "onStop()");
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		
		AudioProcess.stopMedia();
		AudioProcess.stopSound();
	}
}