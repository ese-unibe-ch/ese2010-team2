package models;

import java.util.ArrayList;

public class SearchResult {
	private int radiusTagCount;
	private int radiusContentCount;
	private int totalRadiusCount;
	private boolean hasABestAnswer;
	private int totalScore;
	private Question question;
	private ArrayList<Answer> answers;
	private ArrayList<Comment> comments;

	public SearchResult() {
	}


	/** Getters */
	public Question getQuestion() {
		return question;
	}

	public int getTotalRadiusCount() {
		int total = radiusTagCount + radiusContentCount;
		return total;
	}

	public ArrayList<Answer> getAnswers() {
		return answers;
	}

	public ArrayList<Comment> getComments() {
		return comments;
	}
	public int getRadiusContentCount() {
		return radiusContentCount;
	}

	public int getRadiusTagCount() {
		return radiusTagCount;
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

	public void setRadiusTagCount(int radiusTagCount) {
		this.radiusTagCount = radiusTagCount;
	}

	public void setRadiusContentCount(int radiusContentCount) {
		this.radiusContentCount = radiusContentCount;
	}

	public void setHasABestAnswer(boolean hasABestAnswer) {
		this.hasABestAnswer = hasABestAnswer;
	}
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
}
