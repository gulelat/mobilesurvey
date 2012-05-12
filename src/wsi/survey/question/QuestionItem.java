package wsi.survey.question;

public class QuestionItem {
	private int id;
	private String title;
	private int optionNum;
	private String option[];
	private int idx = 0;

	private int answer = 0;

	private boolean[] answerMoption;

	public QuestionItem() {
		this.option = new String[6];
		this.answerMoption = new boolean[6];
		for (int i = 0; i < 6; i++) {
			answerMoption[i] = false;
		}
	}

	public QuestionItem(int id, String title, int optionNum) {
		this.id = id;
		this.title = title;
		this.optionNum = optionNum;
		this.option = new String[optionNum];
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setId(String id) {
		this.id = Integer.parseInt(id);
	}

	public void setOption(String optionItem) {
		this.option[idx] = optionItem;
		idx++;
	}

	public int getOptionNum() {
		return idx;
	}

	public String getOptionItem(int idx) {
		return option[idx];
	}

	public void setAnswer(int idx) {
		this.answer = idx;
	}

	public int getAnswer() {
		return this.answer;
	}

	public void setAnswerOfM(boolean answer, int idx) {
		this.answerMoption[idx] = answer;
	}

	public boolean getAnswerofM(int idx) {
		return this.answerMoption[idx];
	}

}
