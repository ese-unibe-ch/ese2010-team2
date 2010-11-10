package models.algorithms;

import java.util.ArrayList;

import models.Answer;
import models.Comment;
import models.DbManager;
import models.Question;
import models.SearchResult;

/**
 * This class creates the SearchResult composite based on the results the Search
 * class delivers.
 */
public class SearchResultAssembler {
	private DbManager manager;

	/** ArrayList for the results from the search class */
	private ArrayList<Answer> answerResults;
	private ArrayList<Comment> commentResults;
	private ArrayList<Question> questionResults;

	/** ArrayList for the assembled results */
	private ArrayList<SearchResult> searchResults;


	public SearchResultAssembler(ArrayList<Answer> answerResults,
			ArrayList<Comment> commentResults, ArrayList<Question> questionResults) {

		this.answerResults = answerResults;
		this.commentResults = commentResults;
		this.questionResults = questionResults;

		searchResults = new ArrayList<SearchResult>();
		this.manager = DbManager.getInstance();

		// Initialize the assembling
		assembleBasedOnQuestion();
		assembleBasedOnAnswer();
		assembleBasedOnComment();
	}

	/** Assembles a SearchResult based on the search matches in the questions */
	private void assembleBasedOnQuestion() {
		for (int j = 0; j < questionResults.size(); j++) {
			Question curQuestion = questionResults.get(j);
			avoidDuplicatedSearchResults(curQuestion);
		}
	}

	/** Assembles a SearchResult based on the search matches in the answers */
	private void assembleBasedOnAnswer() {
		for (int i = 0; i < answerResults.size(); i++) {
			Answer curAnswer = answerResults.get(i);
			int questionId = curAnswer.getQuestionId();

			// Get question which belongs to answer
			Question curQuestion = manager.getQuestionById(questionId);

			avoidDuplicatedSearchResults(curQuestion);
		}

	}

	/** Assembles a SearchResult based on the search matches in the comments */
	private void assembleBasedOnComment() {
		for (int i = 0; i < commentResults.size(); i++) {
			Comment curComment = commentResults.get(i);
			if (curComment.getCommentedPost() instanceof Question) {

				// Get question which belongs to comment
				Question curQuestion = (Question) curComment.getCommentedPost();

				avoidDuplicatedSearchResults(curQuestion);

			} else {
				Answer curAnswer = (Answer) curComment.getCommentedPost();
				int questionId = curAnswer.getQuestionId();

				// Get question which belongs to answer
				Question curQuestion = manager.getQuestionById(questionId);

				avoidDuplicatedSearchResults(curQuestion);
			}
		}
	}

	/**
	 * This helper method, avoids duplicated SearchResults. Basically it goes
	 * through Question, Answer, Comment ArrayLists which were delivered from
	 * Search class and checks if there is an answer's or comment's questionId
	 * that is already in a SearchResult composite and if true deletes those
	 * answer's or comment's.
	 */
	private void avoidDuplicatedSearchResults(Question curQuestion) {
		SearchResult result = new SearchResult();
		result.setQuestion(curQuestion);

		// Determine answers which belong to question
		result.setAnswers(manager.getAllAnswersByQuestionId(curQuestion
				.getId()));

		// Determine Comments who belong to Answers
		for (int i = 0; i < result.getAnswers().size(); i++) {
			result.setComments(result.getAnswers().get(i).getComments());
		}

		// Determine comments who belong to question
		result.setComments(curQuestion.getComments());

		// Avoid duplicated SearchResults because of Answers
		for (int k = 0; k < answerResults.size(); k++) {
			Answer curAnswer1 = answerResults.get(k);
			if (curAnswer1.getQuestionId() == curQuestion.getId()) {
				answerResults.remove(k);
			}
		}

		// Avoid duplicated SearchResults because of comments who belong to
		// questions
		for (int j = 0; j < commentResults.size(); j++) {
			Comment curComment1 = commentResults.get(j);
			if (curComment1.getCommentedPost().getId() == curQuestion
					.getId()) {
				commentResults.remove(j);
			}
		}

		// Avoid duplicated SearchResults because of comment who belong to
		// answers
		for (int j = 0; j < commentResults.size(); j++) {
			Comment curComment1 = commentResults.get(j);
			for (int y = 0; y < result.getAnswers().size(); y++) {
				Answer curAnswer = result.getAnswers().get(y);
				if (curComment1.getCommentedPost().getId() == curAnswer.getId()) {
					commentResults.remove(j);
				}
			}
		}
		searchResults.add(result);
	}

	/** Getters */
	public ArrayList<SearchResult> getSearchResults() {
		return searchResults;
	}

	// Will be used for testing only.
	public ArrayList<Answer> getAnswerResults() {
		return answerResults;
	}

	// Will be used for testing only.
	public ArrayList<Comment> getCommentResults() {
		return commentResults;
	}

	// Will be used for testing only.
	public ArrayList<Question> getQuestionResults() {
		return questionResults;
	}
}
