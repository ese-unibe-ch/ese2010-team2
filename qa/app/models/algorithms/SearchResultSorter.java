package models.algorithms;

import java.util.ArrayList;
import java.util.Collections;

import models.Answer;
import models.Comment;
import models.Question;
import models.SearchResult;

import org.apache.commons.codec.language.Soundex;

import comparators.SearchResultComparator;

/**
 * This class gatters the information needed for sorting, like counting how many
 * times a search query appears in the tags. Furthermore the class sorts the
 * search results in the way they appear to the user as smart. See the
 * SearchResultComparator for the sorting rules.
 */
public class SearchResultSorter {
	/** The sorted searchResults */
	private ArrayList<SearchResult> searchResults;

	/**
	 * Will be used for distinction between counting based on sentences or
	 * counting based on soundex codes.
	 */
	private boolean doASoundexBasedCount;

	/**
	 * The soundex algorithm from:
	 * 
	 * @package org.apache.commons.codec.language.Soundex
	 */
	private Soundex soundexAlgorithm;

	/** ArrayList with the soundexCodes from SearchQueryParser */
	private ArrayList<String> soundexCodes;

	/** ArrayList with the sentences from SearchQueryParser */
	private ArrayList<String> sentences;

	public SearchResultSorter(ArrayList<SearchResult> searchResults,
			ArrayList<String> soundexCodesOfQuery,
			ArrayList<String> sentencesOfQuery) {

		this.searchResults = searchResults;
		this.soundexCodes = soundexCodesOfQuery;
		this.sentences = sentencesOfQuery;
		doASoundexBasedCount = false;
		soundexAlgorithm = new Soundex();

		initSorting();
	}

	/**
	 * Go two times through all search results, first time count based on the
	 * soundex codes the second time based on sentences.
	 */
	public void initSorting() {

		doASoundexBasedCount = true;
		for (int i = 0; i < soundexCodes.size(); i++) {
			String query = soundexCodes.get(i);
			countTagMatches(query);
			countContentMatches(query);
		}
		
		doASoundexBasedCount = false;
		for (int i = 0; i < sentences.size(); i++) {
			String query = sentences.get(i);
			countContentMatches(query);
		}
		
		checkIfHasABestAnswer();
		summarizeScores();
		sort();
	}

	/**
	 * Goes through all composits and counts the tags in a composite which match
	 * the search query. Every tag that matches the query will count +2.
	 */
	private void countTagMatches(String query) {
		for (int i = 0; i < searchResults.size(); i++) {
			int tagCount = 0;
			Question curQuestion = searchResults.get(i).getQuestion();
			int numberOfTags = curQuestion.getTags().size();

			for (int j = 0; j < numberOfTags; j++) {
				String curTag = curQuestion.getTagByIndex(j);
				try {
					curTag = soundexAlgorithm.encode(curTag);
				} catch (IllegalArgumentException e) {
				}

				if (curTag.contains(query)) {
					tagCount = tagCount + 2;
				}
			}
			searchResults.get(i).setTotalCount(tagCount);
		}

	}

	/**
	 * Goes through all composits and counts the search query matches in the
	 * composits contents (Question-, Answer-, Comment-Content). Every match
	 * counts +1.
	 */
	private void countContentMatches(String query) {
		for (int i = 0; i < searchResults.size(); i++) {
			int contentCount = 0;
			Question curQuestion = searchResults.get(i).getQuestion();
			String[] splitedQuestionContent = curQuestion.getContent().split("\\s");

			// Go through question content word by word count every match.
			if (doASoundexBasedCount) {
				for (int x = 0; x < splitedQuestionContent.length; x++) {
					try {
						if (soundexAlgorithm.encode(splitedQuestionContent[x])
								.contains(query)) {
							contentCount++;
						}
					} catch (IllegalArgumentException e) {
					}
				}
			}
			// If we have a sentence as query
			else if (curQuestion.getContent().toLowerCase().contains(query)) {
				contentCount++;
			}

			// Go Through all answers belonging to question
			for (int j = 0; j < searchResults.get(i).getAnswers().size(); j++) {
				Answer curAnswer = searchResults.get(i).getAnswers().get(j);
				String[] splitedAnswerContent = curAnswer.getContent().split("\\s");

				if (doASoundexBasedCount) {
					for (int x = 0; x < splitedAnswerContent.length; x++) {
						try {
							if (soundexAlgorithm
									.encode(splitedAnswerContent[x]).contains(
											query)) {
								if (curAnswer.isBestAnswer()) {
									contentCount = contentCount + 5;
								} else {
									contentCount++;
								}
							}
						} catch (IllegalArgumentException e) {
						}
					}
				}
				// If we have a sentence as query
				else if (curAnswer.getContent().toLowerCase().contains(query)) {
					if (curAnswer.isBestAnswer()) {
						contentCount = contentCount + 5;
					} else {
						contentCount++;
					}
				}
			}

			// Go through all comments
			for (int k = 0; k < searchResults.get(i).getComments().size(); k++) {
				Comment curComment = searchResults.get(i).getComments().get(k);
				String[] splitedCommentContent = curComment.getContent().split("\\s");

				if (doASoundexBasedCount) {

					for (int x = 0; x < splitedCommentContent.length; x++) {
						try {
							if (soundexAlgorithm.encode(
									splitedCommentContent[x]).contains(query)) {
								contentCount++;
							}
						} catch (IllegalArgumentException e) {
						}
					}
				}
				// If we have a sentence as query
				else if (curComment.getContent().toLowerCase().contains(query)) {
					contentCount++;
				}
			}
			searchResults.get(i).setTotalCount(contentCount);
		}
	}

	/**
	 * Checks if a composite has a answer which is selected as "best answer". A
	 * best answer counts +5.
	 */
	private void checkIfHasABestAnswer() {
		for (int i = 0; i < searchResults.size(); i++) {
			Question curQuestion = searchResults.get(i).getQuestion();

			if (curQuestion.hasBestAnswer()) {
				searchResults.get(i).setHasABestAnswer(true);
				searchResults.get(i).setTotalScore(5);
			}
		}
	}
	
	/** Summarizes all Scores (Question, Answer) in a composite. */
	private void summarizeScores() {
		for (int i = 0; i < searchResults.size(); i++) {
			int totalScore = 0;
			Question curQuestion = searchResults.get(i).getQuestion();
			totalScore = totalScore + curQuestion.getScore();

			for (int j = 0; j < searchResults.get(i).getAnswers().size(); j++) {
				Answer curAnswer = searchResults.get(i).getAnswers().get(j);
				totalScore = totalScore + curAnswer.getScore();
			}

			searchResults.get(i).setTotalScore(totalScore);
		}
	}

	/**
	 * Sort the SearchResult composites after the rules in
	 * SearchResultComparator.
	 */
	private void sort() {
		Collections.sort(searchResults, new SearchResultComparator());
	}

	/** Getters */
	public ArrayList<SearchResult> getSearchResults() {
		return searchResults;
	}

}
