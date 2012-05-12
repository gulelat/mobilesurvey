package wsi.survey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import wsi.survey.question.QuestionNaire;
import wsi.survey.question.QuestionXMLResolve;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class AnswerQuesionNaire extends Activity {

	private QuestionNaire mQuestionNaire;
	private String filePath;
	private TextView mTitle;
	private RadioGroup mRadioGroup;
	private RadioButton[] mOption;
	private Button mLast, mNext;

	private TextView mTextView;
	private Button mButton;

	private TextView mTextViewProgressAll, mTextViewProgressRemainder;
	private ViewGroup layoutmain;
	private ViewGroup layoutnext;

	private CheckBox[] mOpCheckBox;

	private int questionType = 0; 	// 0表示单选，1表示多选

	private int mCenterX = 160;
	private int mCenterY = 0;

	private static final int duration = 1000;
	private static final int tsize = 20;
	private static final String tp = "��Բ";

	private static final int desctSize = 20;

	private Rotate3d lrightAnimation;
	private Rotate3d lleftAnimation;
	private Rotate3d rrightAnimation;
	private Rotate3d rleftAnimation;

	private static final int MSG_LAYOUTA = 1;
	private static final int MSG_LAYOUTB = 2;
	private static final int MSG_FIRSTQ = 3;
	private static final int MSG_LASTQ = 4;

	private static final int MSG_START = 5;
	private static final int MSG_NOCHOICE = 6;

	private static final String TAG = "================= AnswerQuesionNaire =======================";

	private int currentidx = -1;

	private int layoutflag = 0;
	private Context mContext;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		
		filePath = Environment.getExternalStorageDirectory() + "/CompterThink/" + this.getIntent().getExtras().getString(Main.filename);
		getQuestionNaire(filePath);
		Log.d(TAG, filePath);
		
		this.questionType = mQuestionNaire.getQuestionType();
		initAnimation();
		goToDescription();
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
//			finish();
			break;
		case R.id.cancel:
//			finish();
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layoutflag == 0) {
				return super.onKeyDown(keyCode, event);
			} else {
				if (currentidx == 0) {
					Message msg = Message.obtain();
					msg.what = MSG_FIRSTQ;
					mHander.sendMessage(msg);
				} else {

					layoutnext.startAnimation(rleftAnimation);
					Message msg = Message.obtain();
					msg.what = MSG_LAYOUTA;
					msg.arg1 = currentidx - 1;
					mHander.sendMessage(msg);
				}
				return false;
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void initAnimation() {
		lrightAnimation = new Rotate3d(90,  0,  0.0f,  0.0f,  mCenterX, mCenterY);
		lrightAnimation.setFillAfter(true);
		lrightAnimation.setDuration(duration);

		lleftAnimation = new Rotate3d(0,  -90,  0,  0,  mCenterX,  mCenterY);
		lleftAnimation.setFillAfter(true);
		lleftAnimation.setDuration(duration);

		rrightAnimation = new Rotate3d(-90,  0,  0.0f,  0.0f,  mCenterX,  mCenterY);
		rrightAnimation.setFillAfter(true);
		rrightAnimation.setDuration(duration);

		rleftAnimation = new Rotate3d(0,  90,  0,  0,  mCenterX,  mCenterY);
		rleftAnimation.setFillAfter(true);
		rleftAnimation.setDuration(duration);
	}

	public void getQuestionNaire(String filePath) {
		mQuestionNaire = new QuestionNaire();
		
		File file = new File(filePath);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}

			String fileContent = buffer.toString();
			QuestionXMLResolve.toQuestionNaire(mQuestionNaire, fileContent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void goToDescription() {
		setContentView(R.layout.descrip);
		
		mTextView = (TextView) findViewById(R.id.descrip);
		mTextView.setTextSize(desctSize);
		
		mButton = (Button) findViewById(R.id.start);
		mButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Message msg = Message.obtain();
				msg.what = MSG_START;
				mHander.sendMessage(msg);
				layoutflag = 1;
			}
		});

		mTextView.setText(mQuestionNaire.getDescription());
	}

	public void goToLayoutA(final int idx, int type) {
		switch (type) {
		case 0:
			setContentView(R.layout.main);
			layoutmain = (ViewGroup) findViewById(R.id.layoutmain);
			if (!(idx < currentidx)) {
				layoutmain.startAnimation(lrightAnimation);
				currentidx = idx;
			} else {
				layoutmain.startAnimation(rrightAnimation);
				currentidx = idx;
			}

			mTitle = (TextView) findViewById(R.id.title);

			Typeface typeface = Typeface.create(tp, Typeface.BOLD);
			mTitle.setTypeface(typeface);
			mTitle.setTextSize(tsize);

			mRadioGroup = (RadioGroup) findViewById(R.id.optiongroup);

			mOption = new RadioButton[6];
			mOption[0] = (RadioButton) findViewById(R.id.option1);
			mOption[1] = (RadioButton) findViewById(R.id.option2);
			mOption[2] = (RadioButton) findViewById(R.id.option3);
			mOption[3] = (RadioButton) findViewById(R.id.option4);
			mOption[4] = (RadioButton) findViewById(R.id.option5);
			mOption[5] = (RadioButton) findViewById(R.id.option6);
			mLast = (Button) findViewById(R.id.last);
			mNext = (Button) findViewById(R.id.next);
			mTextViewProgressAll = (TextView) findViewById(R.id.progressall);

			mTextViewProgressAll.setText("总共有" + mQuestionNaire.getQuestionNum() + "道题");
			mTextViewProgressRemainder = (TextView) findViewById(R.id.progressremainder);
			mTextViewProgressRemainder.setText("还剩" + (mQuestionNaire.getQuestionNum() - currentidx - 1) + "道题");

			Log.d(TAG, "=============================init view  finished=======================");

			mTitle.setText(mQuestionNaire.getQuestionItemTitle(idx));

			int n = mQuestionNaire.getQuestionItemOptionNum(idx);
			Log.d(TAG, ":" + n);
			for (int i = 0; i < n; i++) {
				mOption[i].setText(mQuestionNaire.getQuestionItemOption(idx, i));
			}

			for (int i = n; i < 6; i++) {
				mOption[i].setVisibility(8);
			}
			int a = mQuestionNaire.getQuestionItemAnswer(idx);
			mOption[a].setChecked(true);

			mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					switch (arg1) {
					case R.id.option1:
						mQuestionNaire.setQuestionItemAnswer(idx, 0);
						break;
					case R.id.option2:
						mQuestionNaire.setQuestionItemAnswer(idx, 1);
						break;
					case R.id.option3:
						mQuestionNaire.setQuestionItemAnswer(idx, 2);
						break;
					case R.id.option4:
						mQuestionNaire.setQuestionItemAnswer(idx, 3);
						break;
					case R.id.option5:
						mQuestionNaire.setQuestionItemAnswer(idx, 4);
						break;
					case R.id.option6:
						mQuestionNaire.setQuestionItemAnswer(idx, 5);
						break;
					}
				}
			});

			mLast.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					if (idx == 0) {
						Message msg = Message.obtain();
						msg.what = MSG_FIRSTQ;
						mHander.sendMessage(msg);

						// Toast.makeText(mContext, "这是第一题！", Toast.LENGTH_LONG);
					} else {
						layoutmain.startAnimation(rleftAnimation);
						Message msg = Message.obtain();
						msg.what = MSG_LAYOUTB;
						msg.arg1 = idx - 1;
						mHander.sendMessage(msg);

					}
				}
			});

			mNext.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					if ((idx + 1) == mQuestionNaire.getQuestionNum()) {
						Message msg = Message.obtain();
						msg.what = MSG_LASTQ;
						mHander.sendMessage(msg);
						
					} else {
						layoutmain.startAnimation(lleftAnimation);
						Message msg = Message.obtain();
						msg.what = MSG_LAYOUTB;
						msg.arg1 = idx + 1;
						mHander.sendMessage(msg);
					}
				}
			});
			break;
		case 1:
			setContentView(R.layout.mainm);
			layoutmain = (ViewGroup) findViewById(R.id.layoutmain);
			if (!(idx < currentidx)) {
				layoutmain.startAnimation(lrightAnimation);
				currentidx = idx;
			} else {
				layoutmain.startAnimation(rrightAnimation);
				currentidx = idx;
			}

			mTitle = (TextView) findViewById(R.id.title);

			Typeface typeface2 = Typeface.create(tp, Typeface.BOLD);
			mTitle.setTypeface(typeface2);
			mTitle.setTextSize(tsize);

			mOpCheckBox = new CheckBox[6];
			mOpCheckBox[0] = (CheckBox) findViewById(R.id.moption1);
			mOpCheckBox[1] = (CheckBox) findViewById(R.id.moption2);
			mOpCheckBox[2] = (CheckBox) findViewById(R.id.moption3);
			mOpCheckBox[3] = (CheckBox) findViewById(R.id.moption4);
			mOpCheckBox[4] = (CheckBox) findViewById(R.id.moption5);
			mOpCheckBox[5] = (CheckBox) findViewById(R.id.moption6);

			mLast = (Button) findViewById(R.id.last);
			mNext = (Button) findViewById(R.id.next);
			mTextViewProgressAll = (TextView) findViewById(R.id.progressall);

			mTextViewProgressAll.setText("总共有" + mQuestionNaire.getQuestionNum() + "道题");
			mTextViewProgressRemainder = (TextView) findViewById(R.id.progressremainder);
			mTextViewProgressRemainder.setText("还剩" + (mQuestionNaire.getQuestionNum() - currentidx - 1) + "道题");

			Log.d(TAG, "=============================init view  finished=======================");

			mTitle.setText(mQuestionNaire.getQuestionItemTitle(idx));

			int nb = mQuestionNaire.getQuestionItemOptionNum(idx);
			Log.d(TAG, ":" + nb);
			for (int i = 0; i < nb; i++) {
				mOpCheckBox[i].setText(mQuestionNaire.getQuestionItemOption(idx, i));
				mOpCheckBox[i].setChecked(mQuestionNaire.getQuestionItemAnswerOfN(idx, i));
			}

			for (int i = nb; i < 6; i++) {
				mOpCheckBox[i].setVisibility(8);
			}

			mLast.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					if (idx == 0) {
						Message msg = Message.obtain();
						msg.what = MSG_FIRSTQ;
						mHander.sendMessage(msg);

						// Toast.makeText(mContext, "这是第一题！", Toast.LENGTH_LONG);
					} else {
						layoutmain.startAnimation(rleftAnimation);
						Message msg = Message.obtain();
						msg.what = MSG_LAYOUTB;
						msg.arg1 = idx - 1;
						mHander.sendMessage(msg);

					}
					for (int i = 0; i < 6; i++) {
						mQuestionNaire.setQuestionItemAnswerOfN(idx,
								mOpCheckBox[i].isChecked(), i);
					}
				}
			});

			mNext.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					int n = mQuestionNaire.getQuestionItemOptionNum(idx);
					boolean flag = true;
					for (int i = 0; i < n; i++) {
						if (mOpCheckBox[i].isChecked()) {
							mQuestionNaire.setQuestionItemAnswerOfN(idx, true,  i);
							flag = false;
						}
					}

					if (flag) {
						Message msg = Message.obtain();
						msg.what = MSG_NOCHOICE;
						mHander.sendMessage(msg);
					} else {
						if ((idx + 1) == mQuestionNaire.getQuestionNum()) {
							Message msg = Message.obtain();
							msg.what = MSG_LASTQ;
							mHander.sendMessage(msg);
						} else {
							layoutmain.startAnimation(lleftAnimation);
							Message msg = Message.obtain();
							msg.what = MSG_LAYOUTB;
							msg.arg1 = idx + 1;
							mHander.sendMessage(msg);
						}
					}
					for (int i = 0; i < 6; i++) {
						mQuestionNaire.setQuestionItemAnswerOfN(idx, mOpCheckBox[i].isChecked(), i);
					}
				}
			});
			break;
		}
	}

	public void goToLayoutB(final int idx, int type) {
		switch (type) {
		case 0:
			setContentView(R.layout.next);
			layoutnext = (ViewGroup) findViewById(R.id.layoutmext);
			if (!(idx < currentidx)) {
				layoutnext.startAnimation(lrightAnimation);
				currentidx = idx;
			} else {
				layoutnext.startAnimation(rrightAnimation);
				currentidx = idx;
			}

			mTitle = (TextView) findViewById(R.id.title);
			Typeface typeface = Typeface.create(tp, Typeface.BOLD);
			mTitle.setTypeface(typeface);
			mTitle.setTextSize(tsize);
			mRadioGroup = (RadioGroup) findViewById(R.id.optiongroup);

			mOption = new RadioButton[6];
			mOption[0] = (RadioButton) findViewById(R.id.option1);
			mOption[1] = (RadioButton) findViewById(R.id.option2);
			mOption[2] = (RadioButton) findViewById(R.id.option3);
			mOption[3] = (RadioButton) findViewById(R.id.option4);
			mOption[4] = (RadioButton) findViewById(R.id.option5);
			mOption[5] = (RadioButton) findViewById(R.id.option6);
			mLast = (Button) findViewById(R.id.last);
			mNext = (Button) findViewById(R.id.next);
			mTitle.setText(mQuestionNaire.getQuestionItemTitle(idx));

			mTextViewProgressAll = (TextView) findViewById(R.id.progressall);

			mTextViewProgressAll.setText("总共有" + mQuestionNaire.getQuestionNum() + "道题");
			mTextViewProgressRemainder = (TextView) findViewById(R.id.progressremainder);
			mTextViewProgressRemainder.setText("还剩" + (mQuestionNaire.getQuestionNum() 	- currentidx - 1) + "道题");

			int n = mQuestionNaire.getQuestionItemOptionNum(idx);

			Log.d(TAG, ":" + n);
			for (int i = 0; i < n; i++) {
				mOption[i].setText(mQuestionNaire.getQuestionItemOption(idx, i));
			}

			for (int i = n; i < 6; i++) {
				mOption[i].setVisibility(8);
			}

			int a = mQuestionNaire.getQuestionItemAnswer(idx);
			mOption[a].setChecked(true);
			mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				public void onCheckedChanged(RadioGroup arg0,
						int arg1) {
					switch (arg1) {
					case R.id.option1:
						mQuestionNaire.setQuestionItemAnswer(idx, 0);
						break;
					case R.id.option2:
						mQuestionNaire.setQuestionItemAnswer(idx, 1);
						break;
					case R.id.option3:
						mQuestionNaire.setQuestionItemAnswer(idx, 2);
						break;
					case R.id.option4:
						mQuestionNaire.setQuestionItemAnswer(idx, 3);
						break;
					case R.id.option5:
						mQuestionNaire.setQuestionItemAnswer(idx, 4);
						break;
					case R.id.option6:
						mQuestionNaire.setQuestionItemAnswer(idx, 5);
						break;
					}
				}
			});

			mLast.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					if (idx == 0) {
						Message msg = Message.obtain();
						msg.what = MSG_FIRSTQ;
						mHander.sendMessage(msg);
					} else {

						layoutnext.startAnimation(rleftAnimation);
						Message msg = Message.obtain();
						msg.what = MSG_LAYOUTA;
						msg.arg1 = idx - 1;
						mHander.sendMessage(msg);
					}
				}
			});

			mNext.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					if ((idx + 1) == mQuestionNaire.getQuestionNum()) {
						Message msg = Message.obtain();
						msg.what = MSG_LASTQ;
						mHander.sendMessage(msg);
						
					} else {
						layoutnext.startAnimation(lleftAnimation);

						Message msg = Message.obtain();
						msg.what = MSG_LAYOUTA;
						msg.arg1 = idx + 1;
						mHander.sendMessage(msg);
					}
				}
			});
			break;
		case 1:
			setContentView(R.layout.nextm);
			layoutnext = (ViewGroup) findViewById(R.id.layoutmext);
			if (!(idx < currentidx)) {
				layoutnext.startAnimation(lrightAnimation);
				currentidx = idx;
			} else {
				layoutnext.startAnimation(rrightAnimation);
				currentidx = idx;
			}

			mOpCheckBox = new CheckBox[6];
			mOpCheckBox[0] = (CheckBox) findViewById(R.id.moption1);
			mOpCheckBox[1] = (CheckBox) findViewById(R.id.moption2);
			mOpCheckBox[2] = (CheckBox) findViewById(R.id.moption3);
			mOpCheckBox[3] = (CheckBox) findViewById(R.id.moption4);
			mOpCheckBox[4] = (CheckBox) findViewById(R.id.moption5);
			mOpCheckBox[5] = (CheckBox) findViewById(R.id.moption6);

			mTitle = (TextView) findViewById(R.id.title);
			Typeface typeface2 = Typeface.create(tp, Typeface.BOLD);
			mTitle.setTypeface(typeface2);
			mTitle.setTextSize(tsize);

			mLast = (Button) findViewById(R.id.last);
			mNext = (Button) findViewById(R.id.next);
			mTitle.setText(mQuestionNaire.getQuestionItemTitle(idx));

			mTextViewProgressAll = (TextView) findViewById(R.id.progressall);

			mTextViewProgressAll.setText("总共有" + mQuestionNaire.getQuestionNum() + "道题");
			mTextViewProgressRemainder = (TextView) findViewById(R.id.progressremainder);
			mTextViewProgressRemainder.setText("还剩" + (mQuestionNaire.getQuestionNum() - currentidx - 1) + "道题");

			int nb = mQuestionNaire.getQuestionItemOptionNum(idx);

			Log.d(TAG, ":" + nb);

			for (int i = 0; i < nb; i++) {
				mOpCheckBox[i].setText(mQuestionNaire.getQuestionItemOption(idx, i));
				mOpCheckBox[i].setChecked(mQuestionNaire.getQuestionItemAnswerOfN(idx, i));
			}

			for (int i = nb; i < 6; i++) {
				mOpCheckBox[i].setVisibility(8);
			}
			mLast.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					if (idx == 0) {
						Message msg = Message.obtain();
						msg.what = MSG_FIRSTQ;
						mHander.sendMessage(msg);
					} else {

						layoutnext.startAnimation(rleftAnimation);
						Message msg = Message.obtain();
						msg.what = MSG_LAYOUTA;
						msg.arg1 = idx - 1;
						mHander.sendMessage(msg);
					}
					for (int i = 0; i < 6; i++) {
						mQuestionNaire.setQuestionItemAnswerOfN(idx, mOpCheckBox[i].isChecked(), i);
					}
				}
			});

			mNext.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					int n = mQuestionNaire.getQuestionItemOptionNum(idx);
					boolean flag = true;
					for (int i = 0; i < n; i++) {
						if (mOpCheckBox[i].isChecked()) {
							mQuestionNaire.setQuestionItemAnswerOfN(idx, true, i);
							flag = false;
						}
					}

					if (flag) {
						Message msg = Message.obtain();
						msg.what = MSG_NOCHOICE;
						mHander.sendMessage(msg);
					} else {

						if ((idx + 1) == mQuestionNaire.getQuestionNum()) {
							Message msg = Message.obtain();
							msg.what = MSG_LASTQ;
							mHander.sendMessage(msg);
							
						} else {
							layoutnext.startAnimation(lleftAnimation);

							Message msg = Message.obtain();
							msg.what = MSG_LAYOUTA;
							msg.arg1 = idx + 1;
							mHander.sendMessage(msg);
						}
					}

					for (int i = 0; i < 6; i++) {
						mQuestionNaire.setQuestionItemAnswerOfN(idx, mOpCheckBox[i].isChecked(), i);
					}
				}
			});
			break;
		}
	}

	private Handler mHander = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MSG_START:
				goToLayoutA(0, questionType);
				break;
			case MSG_LAYOUTA:
				goToLayoutA(msg.arg1, questionType);
				break;
			case MSG_LAYOUTB:
				goToLayoutB(msg.arg1, questionType);
				break;
			case MSG_FIRSTQ:
				Toast.makeText(mContext, "这是第一题！", 	Toast.LENGTH_SHORT).show();
				break;
			case MSG_LASTQ:
				Toast.makeText(mContext, "这是最后一题！", Toast.LENGTH_SHORT).show();
				break;
			case MSG_NOCHOICE:
				Toast.makeText(mContext, "请做出选择", 	Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

}
