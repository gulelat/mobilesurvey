package wsi.survey.question;

import java.io.File;

public class TestXMLResolve {

	public static void main(String[] args) {
		File filePath = new File("d:\\CompterThink\\");

		QuestionNaire mQuestionNaire = new QuestionNaire();
		File[] files = filePath.listFiles();
		int len = files.length;
		for (int i = 0; i < len; i++) {
			File file = files[i];
			String s = file.toString();
			QuestionXMLResolve.toQuestionNaire(mQuestionNaire, s);
		}
	}

}
