package wsi.survey;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import wsi.survey.question.QuestionNaire;
import wsi.survey.result.GConstant;
import wsi.survey.result.GSQLiteHelper;
import wsi.survey.util.CompareString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ShowResult extends Activity {
	private final static String TAG = "ShowResult";
	
	private String fileName;
	private QuestionNaire qnNaire;
	private String lastTestTime;
	
	
	private Map<String, String> tagsMap;
	private List<String> tagsList;
	private int tagsLen;
	
	private Map<String, String> remarksMap;
	private String[] tagScoreArray;
	private StringBuffer tagScoreRemark;
	
	
	private static String []checkListItems;		// 维度过滤
	private static boolean[] isCheckListItems ;	// 维度过滤标记
	
	private Context mContext;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.mContext = this;
		
		tagsList = new ArrayList<String>();
		fileName = this.getIntent().getExtras().getString("fileName");
		lastTestTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new Date(System.currentTimeMillis() ));
		
		SaveResult saveResult = new SaveResult(this);
		

		this.qnNaire = AnswerQuesion.qnNaire;
		remarksMap = qnNaire.getRemarksMap();
		System.out.println("remarksMap = " + remarksMap.toString());
		
		tagsMap = qnNaire.getTagsMap();
		tagsLen = tagsMap.size();
		
		Object []tagsArray = tagsMap.keySet().toArray();
		for(int i=0; i<tagsLen; i++) {
			tagsList.add( (String)tagsArray[i] );
		}
		
		System.out.println("before sort, tagsList  = " + tagsList.toString());
		tagsSort(tagsList);
		System.out.println("aftersort, tagsList  = " + tagsList.toString());
		
		isCheckListItems = new boolean[tagsLen];
		isCheckListItems[0] = true;
		checkListItems = new String[tagsLen];
		
		showResultView();
	}
	
	
	private void tagsSort(List<String> list) {
		CompareString comp = new CompareString();
		Collections.sort(list, comp);
	}
	
	
	
	/** 读取SQLite数据库，显示历次测试结果 */
	private void showResultView(){
		View view = createChartView();
		if(view == null){
			Toast.makeText(this, "量表有错！请检查...", 200);
			return;
		}
		setContentView(view);	
	}
	

		/** 创建结果视图（View） */
		public View createChartView(){
			GSQLiteHelper sqlHelper = null;
			SQLiteDatabase sqlDB = null;
			
			View chartView = null;
			XYMultipleSeriesDataset dataset = null;;
			XYMultipleSeriesRenderer renderer = null;
			
			Cursor cursor = null;
			try {
				dataset = new XYMultipleSeriesDataset();
				
				
				sqlHelper = GSQLiteHelper.getInstance(mContext, GSQLiteHelper.DB_NAME, null, 1);
				sqlDB = sqlHelper.getReadableDatabase();
				
				String[] columns = new String[]{GSQLiteHelper.survey_imei, GSQLiteHelper.survey_dtime, GSQLiteHelper.survey_questionsAnswerScoreStd};
				String selection = GSQLiteHelper.survey_imei + "=? and " + GSQLiteHelper.survey_filename + "=?";
				String[] selectionArgs = new String[]{GConstant.IMEI, fileName};		// 数据库查询条件： imei 和 fileName
				String orderBy = GSQLiteHelper.survey_dtime + " asc";				// 数据库查询结果排名
				cursor = sqlDB.query(GSQLiteHelper.TABLE_NAME, columns, selection, selectionArgs, null, null, orderBy);
				
				if(cursor != null){
					XYSeries []xySeries = new XYSeries[tagsLen];
					float []resultStdTotal = new float[tagsLen];
					float []resultStdCur = new float[tagsLen];
					tagScoreArray = new String[tagsLen];
					
					
					for(int i=0; i<tagsLen; i++){
						checkListItems[i] = tagsMap.get(tagsList.get(i)); 		// 获取tags对应的中文名称
						if(isCheckListItems[i] || isCheckListItems[0]){
							xySeries[i] = new XYSeries(tagsMap.get(tagsList.get(i))); 		// 从已排序的tagsList选择tags
						}
					}
						
					cursor.moveToFirst();
					int idx = 0;
					do {
						int indexStd = cursor.getColumnIndex(GSQLiteHelper.survey_questionsAnswerScoreStd);
						String strScoreStd = cursor.getString(indexStd);
						Log.i(TAG, "strScoreStd = " + strScoreStd);
						
						if(strScoreStd != null) {
							String strResultStd[] = strScoreStd.split(",");
							for (int i = 1; i < tagsLen; i++) {
								if(isCheckListItems[i] || isCheckListItems[0]){
									String strTagScore = strResultStd[i-1].split("=")[1];
									float fltTagScore = Float.parseFloat(strTagScore);
									xySeries[i].add(idx, fltTagScore);
									resultStdTotal[i] += fltTagScore;		// 统计tag总分
									
									if(idx == tagsLen-1) {
										resultStdCur[i] = fltTagScore; 
									}
								}
							}							
						}
						
						idx++;
					} while (cursor.moveToNext());
					System.out.println("resultStdCur = " + resultStdCur.toString());
					
					for(int i=1; i<tagsLen; i++){
						if(isCheckListItems[i] || isCheckListItems[0]){
							dataset.addSeries(xySeries[i]);
							
							String tag = tagsList.get(i);
							String score = "low";
							float resultStdAVG = resultStdTotal[i] / cursor.getCount(); 
							if(resultStdCur[i] >= resultStdAVG) {
								score = "high";
							} else {
								score = "low";
							}
							tagScoreArray[i] = tag + "@" + score;
						}
					}
					System.out.println("tagScoreArray = " + tagScoreArray.toString());
				}
				
				int []colors = new int[] { Color.RED, Color.MAGENTA, Color.CYAN, Color.GREEN, Color.YELLOW, Color.DKGRAY, 
									Color.GRAY, Color.LTGRAY, Color.BLUE, Color.BLACK, Color.WHITE, Color.BLACK };
				
				renderer = new XYMultipleSeriesRenderer();
//				int len = questionAnswersResultStr.split(",").length;
				for(int i=1; i<tagsLen; i++){
					XYSeriesRenderer xyRenderer = null;
					if(isCheckListItems[i] || isCheckListItems[0]){
						xyRenderer = new XYSeriesRenderer();
						xyRenderer.setColor(colors[i]);
//						xyRenderer.setDisplayChartValues(true);
						xyRenderer.setPointStyle(PointStyle.CIRCLE);
						xyRenderer.setFillPoints(true);
					}
					if(xyRenderer != null){
						renderer.addSeriesRenderer(xyRenderer);
					}
				}
				renderer.setZoomButtonsVisible(true);

				chartView = ChartFactory.getLineChartView(this, dataset, renderer);
//				chartView.setBackgroundResource(R.drawable.bg_main);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cursor != null) {
					cursor.close();
					cursor = null;
				}
				if(sqlDB != null){
					sqlDB.close();
					sqlDB = null;
				}
			}
			
			return chartView;
		}
		
//		private boolean 
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.result_filter, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.checklist:
			showCheckList();
			break;
		case R.id.detailresult:
			showDetailResult();
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	

	private void showCheckList() {
		Builder b = new AlertDialog.Builder(this);
		b.setIcon(R.drawable.header);
		b.setTitle("维度选项过滤");
		b.setMultiChoiceItems(checkListItems, isCheckListItems, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					}
				});
		b.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						View view = createChartView();
						if (view == null) {
							Toast.makeText(mContext, "量表有错！请检查...", 200);
							return;
						}
						setContentView(view);
					}
				});
		b.create().show();
	}
	
	private void showDetailResult(){
		tagScoreRemark = new StringBuffer();
		
		for(int i=1; i<tagsLen; i++){
			if(isCheckListItems[i] || isCheckListItems[0]){
				String tag = tagsMap.get(tagsList.get(i));
				String tagScore = tagScoreArray[i];
				String remark = qnNaire.getRemarksMap().get(tagScore);
				
				tagScoreRemark.append(tag).append("\n");
				tagScoreRemark.append(remark).append("\n\n");
			}
		}
		tagScoreRemark.append("\n\n" + "测试日期：").append(lastTestTime);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.result);
		builder.setTitle("测试结果");
		builder.setMessage(tagScoreRemark.toString());
		builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}
	

	
	@Override
	protected  void onResume() {
		super.onResume();
	}
	
	@Override
	protected  void onDestroy() {
		super.onDestroy();
	}


}








