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
 * This class sorts the search results in way they appear to the user as "smart"
 * searched
 */
public class SearchResultSorter {
	/** The sorted searchResults */
	private ArrayList<SearchResult> searchResults;
	private boolean doASoundexBasedSort;
	private Soundex soundex;
	private ArrayList<String> queryWordsSoundex;
	private ArrayList<String> querySentences;

	public SearchResultSorter(ArrayList<SearchResult> searchResults,
			ArrayList<String> queryWordsSoundex,
			ArrayList<String> querySentences) {
		this.searchResults = searchResults;
		this.queryWordsSoundex = queryWordsSoundex;
		this.querySentences = querySentences;
		doASoundexBasedSort = false;
		soundex = new Soundex();

		initSorting();
	}

	public void initSorting() {
		doASoundexBasedSort = true;
		for (int i = 0; i < queryWordsSoundex.size(); i++) {
			String query = queryWordsSoundex.get(i);
			countTagMatches(query);
			countContentMatches(query);
		}
		
		doASoundexBasedSort = false;
		for (int i = 0; i < querySentences.size(); i++) {
			String query = querySentences.get(i);
			countContentMatches(query);
		}
		
		checkIfHasABestAnswer();
		countTotalScore();
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
				curTag = soundex.encode(curTag);

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
			if (doASoundexBasedSort) {

				for (int x = 0; x < splitedQuestionContent.length; x++) {
					if (soundex.encode(splitedQuestionContent[x]).contains(
							query)) {
						contentCount++;
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

				if (doASoundexBasedSort) {
					for (int x = 0; x < splitedAnswerContent.length; x++) {
						if (soundex.encode(splitedAnswerContent[x]).contains(
								query)) {
							contentCount++;
						}
					}
				}
				// If we have a sentence as query
				else if (curAnswer.getContent().toLowerCase().contains(query)) {
					contentCount++;
				}
			}

			// Go through all comments
			for (int k = 0; k < searchResults.get(i).getComments().size(); k++) {
				Comment curComment = searchResults.get(i).getComments().get(k);
				String[] splitedCommentContent = curComment.getContent().split("\\s");

				if (doASoundexBasedSort) {

					for (int x = 0; x < splitedCommentContent.length; x++) {
						if (soundex.encode(splitedCommentContent[x]).contains(
								query)) {
							contentCount++;
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
	 * best answer counts +5
	 */
	private void checkIfHasABestAnswer() {
		for (int i = 0; i < searchResults.size(); i++) {
			Question curQuestion = searchResults.get(i).getQuestion();
			if (curQuestion.hasBestAnswer()) {
				searchResults.get(i).setHasABestAnswer(true);

				searchResults.get(i).setTotalCount(5);
			}
		}
	}
	
	/** Counts all Scores (Question, Answer) in a composite */
	private void countTotalScore() {
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
