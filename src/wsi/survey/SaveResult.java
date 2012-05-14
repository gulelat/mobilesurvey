package wsi.survey;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import wsi.survey.question.OptionItem;
import wsi.survey.question.QuestionNaire;
import wsi.survey.result.GConstant;
import wsi.survey.result.GSQLiteHelper;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SaveResult {
	private static final String TAG = "SaveResult";

	private QuestionNaire qnNaire;
	
	private String imei;
	private String fileName;
	private String dtime ;
	private int questionsNum;
	private String answerScore;
	
	private String answerScoreTotal;
	private String answerScoreStd;
	
	private Map<String, String> tagsMap;
	
	
	private Context mContext;
	
	public SaveResult(Context context) {
		this.mContext = context;
		
		fileName = AnswerQuesion.fileName;
		qnNaire = AnswerQuesion.qnNaire;
		
		runResult();
	}
	
	
	/** 处理结果 */
	public void runResult(){
		
		savedQuestionsResult();
		
		calSurveyResult();
		
		showSurveyResult();
		
		saveSurveyResult2SQL();
	}
	
	
	/** 保存问卷每道题的得分记录 */
	private void savedQuestionsResult() {
		imei = GConstant.IMEI;
		dtime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(System.currentTimeMillis())) ;
		questionsNum = qnNaire.getQuestionsNum();
		
		tagsMap = new HashMap<String, String>();
		answerScore = "";
		for(int idx=0; idx<questionsNum; idx++){
			String qid = qnNaire.getQuestionItem(idx).getQid();			// qid
			String tag = qnNaire.getQuestionItem(idx).getTag();			// tag
			
			if(!tagsMap.containsKey( tag)) {
				tagsMap.put(tag, qnNaire.getQuestionItem(idx).getTag());	// 记录tags
			}
			
			String answer = "";									// answer
			int oidx_selected = qnNaire.getQuestionItemAnswerOptionIndex(idx);				
			if(oidx_selected >= 0) {
				OptionItem optionItem = qnNaire.getQuestionItem(idx).getOptionItem(oidx_selected);		// 获取选中项
				answer = optionItem.getScore();						
			} else {
				answer = "0";
			}
			answerScore = answerScore + qid + "@" + answer + "@" + tag + "#";		// 格式 q01@0@so#q02@1@an#q03@2@oc#
		}
		System.out.println("answerScore = " + answerScore);
		System.out.println("getTagsMap = " + qnNaire.getTagsMap().toString());
	}
	
	
	/** 计算问卷每道题的总分和标准分（最终结果） */
	private void calSurveyResult() {
		try {
			
			String className = "wsi.survey.result." + qnNaire.getInterfaceType();
			Class<?> clazz = Class.forName(className);
			Object instance = clazz.newInstance();
			
			Method method_getSurveyResultTotal = clazz.getMethod("getSurveyResultTotal", String.class);
			answerScoreTotal = (String)method_getSurveyResultTotal.invoke(instance, answerScore);
			Log.i(TAG, "answerScoreTotal = " + answerScoreTotal);
			
			
			Method method_getSurveyResultStd = clazz.getMethod("getSurveyResultStd");
			answerScoreStd = (String)method_getSurveyResultStd.invoke(instance);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/** 显示问卷属性，及计算的分数 */
	private void showSurveyResult(){
		JSONObject jsonSurvey= new JSONObject();			// 问卷试题属性
		try {
			jsonSurvey.put("imei", imei);
			jsonSurvey.put("filename", fileName);
			jsonSurvey.put("dtime", dtime);
			jsonSurvey.put("questionsNum", questionsNum);
			jsonSurvey.put("questionsAnswerScore", answerScore);
			jsonSurvey.put("questionsAnswerScoreTotal", answerScoreTotal);
			jsonSurvey.put("questionsAnswerScoreStd", answerScoreStd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i(TAG, jsonSurvey.toString());
	}
	
	
	/** 保存结果到SQLite数据库（每道题得分、总分、标准分） */
	private void saveSurveyResult2SQL(){
		GSQLiteHelper sqlHelper = GSQLiteHelper.getInstance(mContext, GSQLiteHelper.DB_NAME, null, 1);
		SQLiteDatabase sqlDB = null;
		
		try {
			sqlDB = sqlHelper.getWritableDatabase();
			
			ContentValues values = new ContentValues();
			values.put(GSQLiteHelper.survey_imei, imei);
			values.put(GSQLiteHelper.survey_filename, fileName);
			values.put(GSQLiteHelper.survey_dtime, dtime);
			values.put(GSQLiteHelper.survey_questionsNum, questionsNum);
			values.put(GSQLiteHelper.survey_questionsAnswerScore, answerScore);
			values.put(GSQLiteHelper.survey_questionsAnswerScoreTotal, answerScoreTotal);
			values.put(GSQLiteHelper.survey_questionsAnswerScoreStd, answerScoreStd);
			sqlDB.insert(GSQLiteHelper.TABLE_NAME, null, values);
			
		} catch (Exception e) {
			if(sqlDB != null){
				sqlDB.close();
				sqlDB = null;
			}
			e.printStackTrace();
		} finally {
			if(sqlDB != null){
				sqlDB.close();
				sqlDB = null;
			}
		}
	}
}
