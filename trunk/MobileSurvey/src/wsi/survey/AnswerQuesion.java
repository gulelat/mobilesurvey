package wsi.survey;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import wsi.survey.media.AudioProcess;
import wsi.survey.question.QuestionNaire;
import wsi.survey.question.QuestionXMLResolve;
import wsi.survey.result.GConstant;
import wsi.survey.util.Rotate3D;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class AnswerQuesion extends Activity {
	private final String TAG = "AnswerQuesion";

	private Rotate3D lQuest1Animation;
	private Rotate3D lQuest2Animation;
	private Rotate3D rQuest1Animation;
	private Rotate3D rQuest2Animation;
	private int mCenterX = 160;
	private int mCenterY = 0;

	private static final int MSG_LAYOUTA = 1;		// 布局一
	private static final int MSG_LAYOUTB = 2;		// 布局二（用于试题切换动画）

	private static final int MSG_START = 5;			// 问卷开始测试试题（描述之后）
	private static final int MSG_NOCHOICE = 6;		// 试题没有完成（无选项）

	private int currentIdx = -1;					// 当前正是测试的试题下标（idx）

	public static String fileName;					// 问卷文件名
	public static QuestionNaire qnNaire;			// 问卷对应的问题实例

	private float volume = 0.5f;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate()");
		
		init();
	}
	
	private void init(){
		initAnimation();

		AudioProcess.loadSound(this);
		AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int curVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		volume = curVolume / (float)maxVolume;
		
		fileName = this.getIntent().getExtras().getString("fileName");
		loadXMLFile(fileName);	// 读取xml问卷，并解析xml
		
		goToDescription();
	}

	public void initAnimation() {
		int duration = 1000;
		lQuest1Animation = new Rotate3D(0,  -90,  0,  0,  mCenterX,  mCenterY);		// 下一题的【question1】旋转方向（从0度转到-90，参考系为水平方向为0度）
		lQuest1Animation.setFillAfter(true);
		lQuest1Animation.setDuration(duration);

		lQuest2Animation = new Rotate3D(90,  0,  0,  0,  mCenterX, mCenterY);		// 下一题的【question2】旋转方向（从0度转到-90，参考系为水平方向为0度）（起始第一题）
		lQuest2Animation.setFillAfter(true);
		lQuest2Animation.setDuration(duration);

		rQuest1Animation = new Rotate3D(0,  90,  0,  0,  mCenterX,  mCenterY);		// 上一题的【question1】旋转方向（从0度转到90，参考系为水平方向为0度）
		rQuest1Animation.setFillAfter(true);
		rQuest1Animation.setDuration(duration);

		rQuest2Animation = new Rotate3D(-90,  0,  0,  0,  mCenterX,  mCenterY);		// 上一题的【question2】旋转方向（从-90度转到0，参考系为水平方向为0度）
		rQuest2Animation.setFillAfter(true);
		rQuest2Animation.setDuration(duration);
	}

	/** 读取xml文件，并且解析xml */
	public void loadXMLFile(String fileName) {
		try {
			InputStream is = this.getResources().getAssets().open(GConstant.surveyFileFolder + "/" + fileName);
			
			int len = is.available();
			byte[] buffer = new byte[len];
			is.read(buffer);

			String fileContent = EncodingUtils.getString(buffer, "utf-8");
			qnNaire = new QuestionNaire();
			QuestionXMLResolve.XML2QuestionNaire(qnNaire, fileContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 问卷的详细描述信息（info） */
	public void goToDescription() {
		setContentView(R.layout.descrip);
		
		TextView tvCaption = (TextView)findViewById(R.id.tvCaption);	// catption
		tvCaption.setTextSize(GConstant.titleFontSize);
		tvCaption.setText(qnNaire.getCaption());
		
		TextView tvInfo = (TextView) findViewById(R.id.tvInfo);			// info
		tvInfo.setText(qnNaire.getInfo());	
		
		Button btnStartTest = (Button) findViewById(R.id.start);			// start test
		btnStartTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Message msg = Message.obtain();
				msg.what = MSG_START;
				mHander.sendMessage(msg);
			}
		});
	}

	/** question xml layout */
	public void goToLayoutA(final int idx) {
		setContentView(R.layout.question);
		
		final LinearLayout layout_question = (LinearLayout) findViewById(R.id.layout_question);		// main.xml  --  LinearLayout
		
		if (idx > currentIdx) {
			layout_question.startAnimation(lQuest2Animation);
			currentIdx = idx;
		} else {
			layout_question.startAnimation(rQuest2Animation);
			currentIdx = idx;
		}
		
		initLayout(idx, layout_question);
	}

	/** question2 xml layout */
	public void goToLayoutB(final int idx) {
		setContentView(R.layout.question2);
		
		final LinearLayout layout_question2 = (LinearLayout) findViewById(R.id.layout_question2);

		if (idx > currentIdx) {
			layout_question2.startAnimation(lQuest2Animation);
			currentIdx = idx;
		} else {
			layout_question2.startAnimation(rQuest2Animation);
			currentIdx = idx;
		}
		
		initLayout(idx, layout_question2);
	}
	
	/** 初始化布局文件 */
	private void initLayout(final int idx, final LinearLayout layout){
		TextView tvCaption = (TextView)findViewById(R.id.tvCaption);		// caption
		tvCaption.setTextSize(GConstant.titleFontSize);
		tvCaption.setText(qnNaire.getCaption());
		
		TextView tvQuestionTitle = (TextView) findViewById(R.id.tvQuestionTitle);		// question title
		tvQuestionTitle.setText((idx + 1) + ". " + qnNaire.getQuestionItemTitle(idx));

		RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.rgOptionsGroup);
		int optionsNum = qnNaire.getQuestionItemOptionNum(idx);				// 得到第idx道问题的选项个数
		final RadioButton[] mRadioButton = new RadioButton[optionsNum];			// 根据选项个数，声明单选按钮数组
		for(int i=0; i<optionsNum; i++){
			mRadioButton[i] = new RadioButton(this);							// 创建单选按钮
			mRadioButton[i].setId(i);										// 设置id
			mRadioButton[i].setChecked(false);
			mRadioButton[i].setTextColor(Color.BLACK);
			mRadioButton[i].setText( qnNaire.getQuestionItemOptionText(idx, i) );
			mRadioGroup.addView(mRadioButton[i], i);
		}
		
		Button btnPrevious = (Button) findViewById(R.id.btnPrevious);		// 上一题
		Button btnNext = (Button) findViewById(R.id.btnNext);			// 下一题
		if(idx == 0){
			btnPrevious.setEnabled(false);
		} else if(idx == qnNaire.getQuestionsNum()-1){
			btnNext.setText("提  交");
		}

		TextView tvQuestionsTotal = (TextView) findViewById(R.id.tvQuestionsTotal);			// 题目总数
		TextView tvQuestionsRemain = (TextView) findViewById(R.id.tvQuestionsRemain);		// 题目剩余数
		tvQuestionsTotal.setText("总共有" + qnNaire.getQuestionsNum() + "道题");
		tvQuestionsRemain.setText("还剩" + (qnNaire.getQuestionsNum() - currentIdx - 1) + "道题");

		int oidx_selected = qnNaire.getQuestionItemAnswerOptionIndex(idx);
		if(oidx_selected>=0){
			mRadioButton[oidx_selected].setChecked(true);
		}
		
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {		// RadioGroup选项监听
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int oidx_selected = checkedId;
				qnNaire.setQuestionItemAnswerOptionIndex(idx, oidx_selected);
			}
		});

		btnPrevious.setOnClickListener(new OnClickListener() {		// 上一题的监听事件
			@Override
			public void onClick(View arg0) {
				if (idx != 0) {
					layout.startAnimation(rQuest1Animation);
					Message msg = Message.obtain();
					msg.what = MSG_LAYOUTA;
					msg.arg1 = idx - 1;
					mHander.sendMessage(msg);
				}
			}
		});

		btnNext.setOnClickListener(new OnClickListener() {		// 下一题的监听事件
			@Override
			public void onClick(View arg0) {
				if(!isRadioChecked(mRadioButton)){
					AudioProcess.playBtnWrong(volume);
					toastShow("请选择您的答案... ^_^");
					return;
				}
				if ((idx + 1) == qnNaire.getQuestionsNum()) {
					submitResult();
				} else {
					layout.startAnimation(lQuest1Animation);
					Message msg = Message.obtain();
					msg.what = MSG_LAYOUTA;
					msg.arg1 = idx + 1;
					mHander.sendMessage(msg);
				}
			}
		});
	}
	
	private boolean isRadioChecked(RadioButton[] mRadioButtons){
		int childCount = mRadioButtons.length;
		
		for(int i=0; i<childCount; i++){
			if(mRadioButtons[i].isChecked()){
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.answeroption, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.handin:
			submitResult();
			break;
		case R.id.cancel:
			finish();
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showDialog("调查问卷", "确定要退出吗？");
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	
	public void showDialog(String title, String msg){
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle(title)
											.setMessage(msg)
											.setPositiveButton("退出", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													finish();
												}
											}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
												
												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.cancel();
												}
											}).create();
		
		dialog.show();
	}
	
	
	
	
	

	private Handler mHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MSG_START:
				goToLayoutA(0);
				break;
			case MSG_LAYOUTA:
				goToLayoutA(msg.arg1);
				break;
			case MSG_LAYOUTB:
				goToLayoutB(msg.arg1);
				break;
			case MSG_NOCHOICE:
				toastShow("请做出选择");
				break;
			}
		}
	};

	/** 没有选择任何选项，弹出提示选择 */
	private void toastShow(String msg){
		Toast toast = Toast.makeText(AnswerQuesion.this, msg, 200);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	/** 提交结果 */
	private void submitResult(){
		AudioProcess.playBtnRight(volume);
		
		Intent intent = new Intent(AnswerQuesion.this, ShowResult.class);
		intent.putExtra("fileName", fileName);
		startActivity(intent);
	}

	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume()");
		
		AudioProcess.playMedia();
	}

	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause()");

		AudioProcess.pauseMedia();
	}
}
