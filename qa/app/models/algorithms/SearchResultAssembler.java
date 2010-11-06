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
	private ArrayList<Answer> answerContentResults;
	private ArrayList<Comment> commentResults;
	private ArrayList<Question> questions;

	/** ArrayList for the assembled results */
	private ArrayList<SearchResult> searchResults;

	public SearchResultAssembler(ArrayList<Answer> answerContentResults,
			ArrayList<Comment> commentResults,
			ArrayList<Question> questions, String query) {

		this.answerContentResults = answerContentResults;
		this.commentResults = commentResults;
		this.questions = questions;

		searchResults = new ArrayList<SearchResult>();
		this.manager = DbManager.getInstance();

		// initialize the assembling
		assembleBasedOnQuestion();
		assembleBasedOnAnswer();
		assembleBasedOnComment();
	}

	/** Assembles a SearchResult based on the search matches in the questions */
	private void assembleBasedOnQuestion() {
		for (int j = 0; j < questions.size(); j++) {
			Question curQuestion = questions.get(j);
			avoidDuplicatedSearchResults(curQuestion);
		}
	}

	/** Assembles a SearchResult based on the search matches in the answers */
	private void assembleBasedOnAnswer() {
		for (int i = 0; i < answerContentResults.size(); i++) {
			Answer curAnswer = answerContentResults.get(i);
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
	 * through all ArrayLists which were delivered from Search class and checks
	 * if there is answer's or comment's questionId that is already in a
	 * SearchResult composite.
	 */
	private void avoidDuplicatedSearchResults(Question curQuestion) {
		SearchResult result = new SearchResult();
		result.setQuestion(curQuestion);

		// Determine answers which belong to question
		result.setAnswers(manager.getAllAnswersByQuestionId(curQuestion
				.getId()));

		// Avoid duplicated SearchResults because of Answers
		for (int k = 0; k < answerContentResults.size(); k++) {
			Answer curAnswer1 = answerContentResults.get(k);
			if (curAnswer1.getQuestionId() == curQuestion.getId()) {
				answerContentResults.remove(k);
			}
		}

		// Determine comments who belong to question
		result.setComments(curQuestion.getComments());

		// Avoid duplicated SearchResults because of comments
		for (int j = 0; j < commentResults.size(); j++) {
			Comment curComment1 = commentResults.get(j);
			if (curComment1.getCommentedPost().getId() == curQuestion
					.getId()) {
				commentResults.remove(j);
			}
		}
		searchResults.add(result);
	}

	/** Getters */
	public ArrayList<SearchResult> getSearchResults() {
		return searchResults;
	}
}
