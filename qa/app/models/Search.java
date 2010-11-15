package models;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.codec.language.Soundex;

/**
 * This class implements the feature of fulltext and tag search for Question,
 * Answer, Comments.
 */
public class Search {
	private DbManager manager;

	/** The from the SearchQueryParser made soundex Codes for the query. */
	private ArrayList<String> soundexCodes;

	/** The form the SearchQueryParser made sentences. */
	private ArrayList<String> sentences;

	/**
	 * Used for the distinction between a Search to do with soundex codes or
	 * with full sentences.
	 */
	private boolean doASoundexSearch;

	/**
	 * The soundex algorithm from:
	 * 
	 * @package org.apache.commons.codec.language.Soundex
	 */
	private Soundex soundexAlgorithm;

	/**
	 * Determines the number of questions, answer and comments in DbMangers
	 * ArrayLists.
	 */
	private int numberOfQuestions;
	private int numberOfAnswers;
	private int numberOfComments;

	private String searchType;

	/** Store the results of the search, grouped after their type. */
	private ArrayList<Answer> answerResults;
	private ArrayList<Comment> commentResults;
	private ArrayList<Question> questionResults;

	public Search(ArrayList<String> soundexCodes, ArrayList<String> sentences,
			String searchType) {
		this.soundexCodes = soundexCodes;
		this.sentences = sentences;
		this.searchType = searchType;
		this.manager = DbManager.getInstance();

		doASoundexSearch = false;
		soundexAlgorithm = new Soundex();

		answerResults = new ArrayList<Answer>();
		commentResults = new ArrayList<Comment>();
		questionResults = new ArrayList<Question>();

		numberOfQuestions = manager.getQuestions().size();
		numberOfAnswers = manager.getAnswers().size();
		numberOfComments = manager.getComments().size();

		doTheSearchForEveryQuery();
	}

	/**
	 * Does a search through all Questions, Answers and Comments the first time
	 * with the soundex codes, the second time with complete sentences.
	 */
	private void doTheSearchForEveryQuery() {

		doASoundexSearch = true;
		for (int i = 0; i < soundexCodes.size(); i++) {
			String query = soundexCodes.get(i);
			if (searchType == "similarUserByTag" || searchType == "search") {
				searchQuestionTags(query);
			}
			if (searchType == "similarUserByContent" || searchType == "search")
				searchQuestionContent(query);
			searchAnswerContent(query);
			searchComments(query);
		}

		doASoundexSearch = false;
		for (int i = 0; i < sentences.size(); i++) {
			String query = sentences.get(i);
			if (searchType == "similarUserByContent" || searchType == "search") {
				searchQuestionContent(query);
				searchAnswerContent(query);
				searchComments(query);
			}
		}
		removeDuplicatedQuestions();
	}

	/** Search through all questions tags for search query matches. */
	public void searchQuestionTags(String query) {
		// Prevents that if a search query matches two different tags the
		// question will be added twice.
		boolean addQuestionOnlyOnce = true;

		for (int i = 0; i < numberOfQuestions; i++) {

			Question curQuestion = manager.getQuestions().get(i);
			int numberOfTags = curQuestion.getTags().size();

			for (int j = 0; j < numberOfTags; j++) {
				String curTag = curQuestion.getTagByIndex(j);
				curTag = curTag.toLowerCase();
				try {
					curTag = soundexAlgorithm.encode(curTag);
				} catch (IllegalArgumentException e) {
				}

				if (curTag.contains(query)) {
					if (addQuestionOnlyOnce) {
						questionResults.add(curQuestion);
						addQuestionOnlyOnce = false;
					}
				}
			}
			addQuestionOnlyOnce = true;
		}
	}

	/** Search through all questions content for search query matches. */
	public void searchQuestionContent(String query) {
		for (int i = 0; i < numberOfQuestions; i++) {
			Question curQuestion = manager.getQuestions().get(i);
			String curContent = curQuestion.getContent();
			curContent = curContent.toLowerCase();

			if (doASoundexSearch) {
				String[] curContentArray = curQuestion.getContent()
						.split("\\s");
				for (int x = 0; x < curContentArray.length; x++) {
					try {
						if (soundexAlgorithm.encode(curContentArray[x])
								.contains(query)) {
							questionResults.add(curQuestion);
						}
					} catch (IllegalArgumentException e) {
					}
				}

			} else if (curContent.contains(query)) {
				questionResults.add(curQuestion);
			}
		}
	}

	/** Search through all answer contents for matches */
	public void searchAnswerContent(String query) {
		for (int i = 0; i < numberOfAnswers; i++) {

			Answer curAnswer = manager.getAnswers().get(i);
			String curContent = curAnswer.getContent();

			curContent = curContent.toLowerCase();

			if (doASoundexSearch) {
				String[] curContentArray = curAnswer.getContent().split("\\s");
				for (int x = 0; x < curContentArray.length; x++) {
					try {
						if (soundexAlgorithm.encode(curContentArray[x])
								.contains(query)) {
							answerResults.add(curAnswer);
						}
					} catch (IllegalArgumentException e) {
					}
				}
			} else if (curContent.contains(query)) {
				answerResults.add(curAnswer);
			}
		}
	}

	/** Search through all comments content for matches */
	public void searchComments(String query) {
		for (int i = 0; i < numberOfComments; i++) {

			Comment curComment = manager.getComments().get(i);
			String curContent = curComment.getContent();
			curContent = curContent.toLowerCase();

			if (doASoundexSearch) {
				String[] curContentArray = curComment.getContent().split("\\s");
				for (int x = 0; x < curContentArray.length; x++) {
					try {
						if (soundexAlgorithm.encode(curContentArray[x])
								.contains(query)) {
							commentResults.add(curComment);
						}
					} catch (IllegalArgumentException e) {
					}
				}
			} else if (curContent.contains(query)) {
				commentResults.add(curComment);
			}
		}
	}

	/**
	 * Because of tag and content search in questions there may be question
	 * duplicates in "questions" ArrayList, this method deletes all duplicates.
	 */
	public void removeDuplicatedQuestions() {
		HashSet h = new HashSet(questionResults);
		questionResults.clear();
		questionResults.addAll(h);
	}

	/** Getters */
	public ArrayList<Question> getQuestionResults() {
		return questionResults;
	}

	public ArrayList<Answer> getAnswerResults() {
		return answerResults;
	}

	public ArrayList<Comment> getCommentResults() {
		return commentResults;
	}
}
