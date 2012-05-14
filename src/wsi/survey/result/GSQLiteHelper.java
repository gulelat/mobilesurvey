package wsi.survey.result;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;



public class GSQLiteHelper extends SQLiteOpenHelper {
	
	public static final String DB_NAME = "db_survey";
	public static final String TABLE_NAME = "table_survey";

	public static final String survey_id = "_id";	
	public static final String survey_imei = "imei";			
	public static final String survey_filename= "filename";	
	public static final String survey_dtime= "dtime";			
	public static final String survey_questionsNum = "questionsNum";	
	public static final String survey_questionsAnswerScore = "questionsAnswerScore";	
	public static final String survey_questionsAnswerScoreTotal = "questionsAnswerScoreTotal";	
	public static final String survey_questionsAnswerScoreStd = "questionsAnswerScoreStd";			
	
	private static GSQLiteHelper instance = null;
	
	public GSQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	public synchronized static GSQLiteHelper getInstance(Context context, String name, CursorFactory factory, int version) {
		if(instance == null) {
			instance = new GSQLiteHelper(context, name, factory, version);
			return instance;
		} else {
			return instance;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists " 
				+ TABLE_NAME + "("
				+ survey_id+ " integer primary key autoincrement, "
				+ survey_imei + " varchar, "
				+ survey_filename + " varchar, "
				+ survey_dtime + " varchar, "
				+ survey_questionsNum + " int, "
				+ survey_questionsAnswerScore +" varchar, "				// 问卷每道题目的答案answer的json格式
				+ survey_questionsAnswerScoreTotal +" varchar, "		// 问卷累计统计总分
				+ survey_questionsAnswerScoreStd +" varchar)";		// 问卷最后评价指数
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
