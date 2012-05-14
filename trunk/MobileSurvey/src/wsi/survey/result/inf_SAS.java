package wsi.survey.result;

import java.util.HashMap;
import java.util.Map;

public class inf_SAS implements OnSurveyResult {
	private Map<String, Float> tagScoreMap = new HashMap<String, Float>();
	
	
	@Override
	public int calQuestionItems(int idx, int answer) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSurveyResultTotal(String answerScore) {

		System.out.println("before getSurveyResultTotal() = " + tagScoreMap.toString());

		String []questionList = answerScore.split("#");
		String []questionListScore;
		float totalScore = 0.0f;		// 统计总分 	map格式为：<"total", totalScore>
		
		int listLen = questionList.length;
		for(int i=0; i<listLen-1; i++) {
			questionListScore = questionList[i].split("@");
			
			String tag = questionListScore[2];
			float score = Float.parseFloat(questionListScore[1]);
			totalScore += score;
			
			if(tagScoreMap.containsKey(tag)) {
				float tagScore = tagScoreMap.get(tag);
				tagScore += score;
				tagScoreMap.put(tag, tagScore);
			} else {
				tagScoreMap.put(tag, score);
			}
		}
		totalScore = (int)(totalScore * 1.25);
		
		tagScoreMap.put("total", totalScore);
		

		System.out.println("after getSurveyResultTotal() = " + tagScoreMap.toString());
		return tagScoreMap.toString();
	}

	@Override
	public String getSurveyResultStd() {

		return tagScoreMap.toString();
	}

	@Override
	public String getSurveyResultRemark() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSurveyResultTagTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String float2String(float[] array) {
		// TODO Auto-generated method stub
		return null;
	}

}
