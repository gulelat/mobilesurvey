package wsi.survey.result;

import java.util.HashMap;
import java.util.Map;

public class inf_PWB implements OnSurveyResult {
	private Map<String, Float> tagScoreMap = new HashMap<String, Float>();
	
	
	@Override
	public int calQuestionItems(int idx, int answer) {
		return 0;
	}

	@Override
	public String getSurveyResultTotal(String answerScore) {

		String []questionList = answerScore.split("#");
		String []questionListScore;
		float totalScore = 0.0f;		// 统计总分 	map格式为：<"total", totalScore>
		int positiveNum = 0;			// 统计阳性题目数（大于0的得分）， map格式为： <"positivetotal", totalScore/positiveNum>
		
		int listLen = questionList.length;
		for(int i=0; i<listLen-1; i++) {
			questionListScore = questionList[i].split("@");
			
			String tag = questionListScore[2];
			float score = Float.parseFloat(questionListScore[1]);
			totalScore += score;
			
			if(score>0) {
				positiveNum++;
			}
			
			if(tagScoreMap.containsKey(tag)) {
				float tagScore = tagScoreMap.get(tag);
				tagScore += score;
				tagScoreMap.put(tag, tagScore);
			} else {
				tagScoreMap.put(tag, score);
			}
		}
		
		tagScoreMap.put("total", totalScore / listLen);
		tagScoreMap.put("positivetotal", totalScore/positiveNum);
		

		System.out.println(tagScoreMap.toString());
		return tagScoreMap.toString();
	}

	@Override
	public String getSurveyResultStd() {
		
		return tagScoreMap.toString();
	}

	@Override
	public String getSurveyResultRemark() {
		
		return null;
	}

	@Override
	public String getSurveyResultTagTitle() {
		return null;
	}

	@Override
	public String float2String(float[] array) {
		return null;
	}

}
