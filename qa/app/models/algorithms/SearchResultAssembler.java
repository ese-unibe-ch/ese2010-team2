package models.algorithms;

import java.util.ArrayList;

import models.Answer;
import models.Comment;
import models.DbManager;
import models.Question;
import models.SearchResult;

public class SearchResultAssembler {
	private ArrayList<Answer> answerContentResults;
	private ArrayList<Comment> commentResults;
	private ArrayList<Question> mergedQuestion;
	private ArrayList<SearchResult> searchResults;

	private DbManager manager;

	public SearchResultAssembler(ArrayList<Answer> answerContentResults,
			ArrayList<Comment> commentResults,
			ArrayList<Question> mergedQuestion, String query) {

		this.manager = DbManager.getInstance();
		this.answerContentResults = answerContentResults;
		this.commentResults = commentResults;
		this.mergedQuestion = mergedQuestion;
		searchResults = new ArrayList<SearchResult>();

		// initialize the assembling
		assembleBasedOnQuestion();
		assembleBasedOnAnswer();
		assembleBasedOnComment();
	}

	private void assembleBasedOnQuestion() {

		for (int j = 0; j < mergedQuestion.size(); j++) {
			Question curQuestion = mergedQuestion.get(j);

			createSearchResultAndAvoidDuplication(curQuestion);
		}
	}

	private void assembleBasedOnAnswer() {
		for (int i = 0; i < answerContentResults.size(); i++) {
			Answer curAnswer = answerContentResults.get(i);
			int questionId = curAnswer.getQuestionId();

			// Get Question which belong to Answer
			Question curQuestion = manager.getQuestionById(questionId);

			createSearchResultAndAvoidDuplication(curQuestion);

			assert (answerContentResults.size() == 0);
			System.out.println("This should be 0 "
					+ answerContentResults.size());

		}

	}

	private void assembleBasedOnComment() {
		for (int i = 0; i < commentResults.size(); i++) {
			Comment curComment = commentResults.get(i);

			if (curComment.getCommentedPost() instanceof Question) {
				// Get Question who belong to Comment
				Question curQuestion = (Question) curComment.getCommentedPost();

				createSearchResultAndAvoidDuplication(curQuestion);

			} else {
				Answer curAnswer = (Answer) curComment.getCommentedPost();
				int questionId = curAnswer.getQuestionId();

				// Get Question which belong to Answer
				Question curQuestion = manager.getQuestionById(questionId);

				createSearchResultAndAvoidDuplication(curQuestion);
			}
			// assert (commentResults.size() == 0);

		}
	}

	private void createSearchResultAndAvoidDuplication(Question curQuestion) {

		SearchResult result = new SearchResult();
		result.setQuestion(curQuestion);

		// Determine Answers which belong to Question
		result.setAnswers(manager.getAllAnswersByQuestionId(curQuestion
				.getId()));

		// Avoid duplicated Search Results
		for (int k = 0; k < answerContentResults.size(); k++) {
			Answer curAnswer1 = answerContentResults.get(k);
			if (curAnswer1.getQuestionId() == curQuestion.getId()) {
				answerContentResults.remove(k);
			}
		}

		// Determine Comments who belong to Question
		result.setComments(curQuestion.getComments());

		// Avoid duplicated Search Results
		for (int j = 0; j < commentResults.size(); j++) {
			Comment curComment1 = commentResults.get(j);
			if (curComment1.getCommentedPost().getId() == curQuestion
					.getId()) {
				commentResults.remove(j);
			}
		}
		searchResults.add(result);
	}

	public ArrayList<SearchResult> getSearchResults() {
		return searchResults;
	}
}
