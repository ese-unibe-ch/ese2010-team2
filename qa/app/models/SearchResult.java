package models;

import java.util.ArrayList;

/**
 * This class is the Result of a full text and tag search in Questions, Answers
 * and Comments. The class is equal to what you see when you click on a question
 * in the UI. It is a composite of Question, Answer and Comment classes. (I know
 * it isn't really a composite like the one in the composite pattern.)
 */
public class SearchResult {
	private boolean hasABestAnswer;

	/** Holds the value how many tags in the composite match the search query */
	private int tagCount;

	/**
	 * Holds the value how many times the search query is in the composits
	 * contents
	 */
	private int contentCount;

	/** The value of the tagCount + contentCount */
	private int totalCount;

	/** The score of the question, all answers and comments together */
	private int totalScore;

	/** The components in the composite */
	private Question question;
	private ArrayList<Answer> answers;
	private ArrayList<Comment> comments;


	/** Getters */
	public Question getQuestion() {
		return question;
	}

	public int getTotalCount() {
		int total = tagCount + contentCount;
		return total;
	}

	public ArrayList<Answer> getAnswers() {
		return answers;
	}

	public ArrayList<Comment> getComments() {
		return comments;
	}
	public int getContentCount() {
		return contentCount;
	}

	public int getTagCount() {
		return tagCount;
	}

	public boolean getHasABestAnswer() {
		return hasABestAnswer;
	}

	public int getTotalScore() {
		return totalScore;
	}

	/** Setters */
	public void setAnswers(ArrayList<Answer> answers) {
		this.answers = answers;
	}

	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public void setTagCount(int tagCount) {
		this.tagCount = tagCount;
	}

	public void setContentCount(int contentCount) {
		this.contentCount = contentCount;
	}

	public void setHasABestAnswer(boolean hasABestAnswer) {
		this.hasABestAnswer = hasABestAnswer;
	}
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
}
