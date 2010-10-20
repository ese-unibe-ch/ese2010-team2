package models;

import java.util.ArrayList;

public class Search {
	private String query;
	private DbManager manager;

	/**
	 * Determines the number of questions, answer and comments in DbMangers
	 * ArrayLists
	 */
	private int numberOfQuestions;
	private int numberOfAnswers;
	private int numberOfComments;

	/** Store the results of the search */
	private ArrayList<Question> questionTagResults;
	private ArrayList<Question> questionContentResults;
	private ArrayList<Answer> answerContentResults;
	private ArrayList<Comment> commentResults;

	public Search(String query) {
		this.query = query;
		this.query = this.query.toLowerCase();
		this.manager = DbManager.getInstance();

		questionTagResults = new ArrayList<Question>();
		questionContentResults = new ArrayList<Question>();
		answerContentResults = new ArrayList<Answer>();
		commentResults = new ArrayList<Comment>();

		numberOfQuestions = manager.getQuestions().size();
		numberOfAnswers = manager.getAnswers().size();
		numberOfComments = manager.getComments().size();
	}

	/** Search through all question tags for search query matches */
	public void searchQuestionTags() {
		// Prevents that if a search query matches two different tags the
		// question will be added twice.
		boolean addQuestionOnlyOnce = true;

		for (int i = 0; i < numberOfQuestions; i++) {

			Question curQuestion = manager.getQuestions().get(i);
			int numberOfTags = curQuestion.getTags().size();

			for (int j = 0; j < numberOfTags; j++) {

				String curTag = curQuestion.getTagByIndex(j);
				curTag = curTag.toLowerCase();

				if (curTag.contains(query)) {
					if (addQuestionOnlyOnce) {
						questionTagResults.add(curQuestion);
						addQuestionOnlyOnce = false;
					}
				}
			}
		}
		addQuestionOnlyOnce = true;
	}

	/** Search through all question contents for search query matches */
	public void searchQuestionContent() {
		for (int i = 0; i < numberOfQuestions; i++) {

			Question curQuestion = manager.getQuestions().get(i);
			String curContent = curQuestion.getContent();
			curContent = curContent.toLowerCase();

			if (curContent.contains(query)) {
				questionContentResults.add(curQuestion);
			}
		}
	}

	/** Search through all comments for matches */
	public void searchComments() {
		for (int i = 0; i < numberOfComments; i++) {

			Comment curComment = manager.getComments().get(i);
			String curContent = curComment.getContent();
			curContent = curContent.toLowerCase();

			if (curContent.contains(query)) {
				commentResults.add(curComment);
			}
		}
	}

	/** Search through all answer contents for matches */
	public void searchAnswerContent() {
		for (int i = 0; i < numberOfAnswers; i++) {

			Answer curAnswer = manager.getAnswers().get(i);
			String curContent = curAnswer.getContent();
			curContent = curContent.toLowerCase();

			if (curContent.contains(query)) {
				answerContentResults.add(curAnswer);
			}
		}
	}

	/** Getters */
	public ArrayList<Question> getQuestionTagsResults() {
		return questionTagResults;
	}

	public ArrayList<Question> getQuestionContentResults() {
		return questionContentResults;
	}

	public ArrayList<Answer> getAnswerContentResults() {
		return answerContentResults;
	}

	public ArrayList<Comment> getCommentResults() {
		return commentResults;
	}
}
