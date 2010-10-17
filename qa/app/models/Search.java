package models;

import java.util.ArrayList;

import annotations.Unused;

public class Search {
	private String text;
	/** Number of search results in comment search */
	private int n;
	private UserQuestionAnswerManager manager;
	/** Stores the indexes of the Questions, with tag search result */
	private ArrayList<Integer> questionTagIndexes;
	/** Stores the indexes of the Questions, with content search result */
	private ArrayList<Integer> questionContentIndexes;
	/**
	 * Stores the indexes of the comments, and at index=0 the index of the
	 * related question
	 */
	private ArrayList<ArrayList<Integer>> questionCommentIndexes;
	/** Stores the indexes of the Answers, with content search result */
	private ArrayList<Integer> answerContentIndexes;
	/**
	 * Stores the indexes of the comments, and at index=0 the index of the
	 * related answer
	 */
	private ArrayList<ArrayList<Integer>> answerCommentIndexes;

	public Search(String text) {
		this.text = text;
		n = 50;
		this.manager = UserQuestionAnswerManager.getInstance();
		questionTagIndexes = new ArrayList<Integer>();
		questionContentIndexes = new ArrayList<Integer>();
		questionCommentIndexes = new ArrayList<ArrayList<Integer>>();
		answerContentIndexes = new ArrayList<Integer>();
		answerCommentIndexes = new ArrayList<ArrayList<Integer>>();

		// Fill in ArrayLists with ArrayLists, create 2D ArrayLists
		for (int i = 0; i < n; i++) {
			questionCommentIndexes.add(new ArrayList<Integer>());
		}
		for (int i = 0; i < n; i++) {
			answerCommentIndexes.add(new ArrayList<Integer>());
		}
	}

	public void searchQuestionTags() {
		boolean addQuestionOnlyOnce = true;
		for (int i = 0; i < manager.getQuestions().size(); i++) {
			for (int j = 0; j < manager.getQuestions().get(i).getTags().size(); j++) {
				if (manager.getQuestions().get(i).getTags().get(j)
						.contains(text)) {
					if (addQuestionOnlyOnce) {
						questionTagIndexes.add(i);
						addQuestionOnlyOnce = false;
					}
				}
			}
		}
		addQuestionOnlyOnce = true;
	}

	public void searchQuestionContent() {
		for (int i = 0; i < manager.getQuestions().size(); i++) {
			if (manager.getQuestions().get(i).getContent().contains(text)) {
				questionContentIndexes.add(i);
			}
		}
	}

	@Unused
	// TODO Will be done by next week
	public void searchQuestionComments() {
		int index = 0;
		boolean addQuestionIndex = true;
		boolean incrementIndex = false;
		for (int i = 0; i < manager.getQuestions().size(); i++) {
			for (int j = 0; j < manager.getQuestions().get(i).getTags().size(); j++) {
				if (manager.getQuestions().get(i).getTags().get(j)
						.contains(text)) {
					if (addQuestionIndex) {
						questionCommentIndexes.get(index).add(i);
						addQuestionIndex = false;
					} else {
						questionCommentIndexes.get(index).add(j);
					}
					incrementIndex = true;
				}
			}
			addQuestionIndex = true;
			if (incrementIndex) {
				index++;
				incrementIndex = false;
			}
		}
	}

	public void searchAnswerContent() {
		for (int i = 0; i < manager.getAnswers().size(); i++) {
			if (manager.getAnswers().get(i).getContent().contains(text)) {
				answerContentIndexes.add(i);
			}
		}
	}

	@Unused
	// TODO Will be done by next week
	public void searchAnswersComments() {
	}

	/** Getters and Setters */
	public ArrayList<Integer> getQuestionTagIndexes() {
		return questionTagIndexes;
	}

	public ArrayList<Integer> getQuestionContentIndexes() {
		return questionContentIndexes;
	}

	public ArrayList<ArrayList<Integer>> getQuestionCommentIndexes() {
		return questionCommentIndexes;
	}

	public ArrayList<Integer> getAnswerContentIndexes() {
		return answerContentIndexes;
	}

	public ArrayList<ArrayList<Integer>> getAnswerCommentIndexes() {
		return answerCommentIndexes;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
