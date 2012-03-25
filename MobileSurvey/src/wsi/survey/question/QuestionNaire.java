package wsi.survey.question;


public class QuestionNaire {

	private QuestionItem quetion[];
	private int qNum;
	private String desc;

	private int questionType; // 0表示单选 ，1表示多选

	public QuestionNaire() {

	}

	public void setQuestionType(String type) {
		this.questionType = Integer.parseInt(type);
	}

	public int getQuestionType() {
		return this.questionType;
	}

	public void setQuestionNum(String qNum) {
		this.qNum = Integer.parseInt(qNum);
		this.quetion = new QuestionItem[this.qNum];
	}

	public int getQuestionNum() {
		return qNum;
	}

	public void setQuestionItem(int idx, QuestionItem qitem) {
		this.quetion[idx] = qitem;
	}

	public String getQuestionItemTitle(int idx) {
		return quetion[idx].getTitle();
	}

	public int getQuestionItemOptionNum(int idx) {
		return quetion[idx].getOptionNum();
	}

	public String getQuestionItemOption(int idx, int oidx) {
		return quetion[idx].getOptionItem(oidx);
	}

	public void setQuestionItemAnswer(int idx, int answer) {
		quetion[idx].setAnswer(answer);
	}

	public int getQuestionItemAnswer(int idx) {
		return quetion[idx].getAnswer();
	}

	public void setQuestionItemAnswerOfN(int itemidx, boolean answer, int idx) {
		quetion[itemidx].setAnswerOfM(answer, idx);
	}

	public boolean getQuestionItemAnswerOfN(int itemidx, int idx) {
		return quetion[itemidx].getAnswerofM(idx);
	}

	public void setDescription(String desc) {
		this.desc = desc;
	}

	public String getDescription() {
		return this.desc;
	}
}
