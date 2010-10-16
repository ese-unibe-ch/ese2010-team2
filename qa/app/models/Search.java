package models;

import java.util.ArrayList;

public class Search {
	private String text;
	private UserQuestionAnswerManager manager;
	private ArrayList<Integer> questionIndexes;
	private ArrayList<Integer> answerIndexes;

	public Search(String text) {
		this.text = text;
		this.manager = UserQuestionAnswerManager.getInstance();
		questionIndexes = new ArrayList<Integer>();
		answerIndexes = new ArrayList<Integer>();
	}

	public void fullTextOnly() {

		for (int i = 0; i < manager.getQuestions().size(); i++) {
			if (manager.getQuestions().get(i).getContent().contains(text)) {
				questionIndexes.add(i);
			}
		}
		for (int i = 0; i < manager.getAnswers().size(); i++) {
			if (manager.getAnswers().get(i).getContent().contains(text)) {
				answerIndexes.add(i);
			}
		}
	}

	public String getText() {
		return text;
	}

	public ArrayList<Integer> getQuestionIndexes() {
		return questionIndexes;
	}

	public ArrayList<Integer> getAnswerIndexes() {
		return answerIndexes;
	}

	public void setText(String text) {
		this.text = text;
	}
}
