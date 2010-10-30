package models.algorithms;

import java.util.ArrayList;
import java.util.Collections;

import models.Answer;
import models.Comment;
import models.Question;
import models.SearchResult;

import comparators.SearchResultComparator;

/**
 * This class sorts the search results in way they appear to the user as "smart"
 * searched
 */
public class SearchResultSorter {
	/** The sorted searchResults */
	private ArrayList<SearchResult> searchResults;

	/** The original search query, will be used for sorting */
	private String query;

	public SearchResultSorter(ArrayList<SearchResult> searchResults,
			String query) {
		this.searchResults = searchResults;
		this.query = query;

		// Initialize sorting
		countTagMatches();
		countContentMatches();
		checkIfHasABestAnswer();
		countTotalScore();
		sort();
	}

	/**
	 * Goes through all composits and counts the tags in a composite which match
	 * the search query. Every tag that matches the query will count +2.
	 */
	private void countTagMatches() {
		for (int i = 0; i < searchResults.size(); i++) {
			int tagCount = 0;
			Question curQuestion = searchResults.get(i).getQuestion();
			int numberOfTags = curQuestion.getTags().size();

			for (int j = 0; j < numberOfTags; j++) {
				String curTag = curQuestion.getTagByIndex(j);
				curTag = curTag.toLowerCase();

				if (curTag.contains(query)) {
					tagCount = tagCount + 2;
				}
			}
			searchResults.get(i).setTagCount(tagCount);
		}

	}

	/**
	 * Goes through all composits and counts the search query matches in the
	 * composits contents (Question-, Answer-, Comment-Content). Every match
	 * counts +1.
	 */
	private void countContentMatches(){
		for (int i = 0; i < searchResults.size(); i++) {
			int contentCount = 0;
			Question curQuestion = searchResults.get(i).getQuestion();
			String[] splitedQuestionContent = curQuestion.getContent().split("\\s");

			// Go through question content word by word count every match.
			for (int x = 0; x < splitedQuestionContent.length; x++) {
				if (splitedQuestionContent[x].contains(query)) {
					contentCount++;
				}
			}
			// If we have a sentence as query
			if (curQuestion.getContent().contains(query) && contentCount == 0) {
				contentCount++;
			}

			// Go Through all answers belonging to question
			for (int j = 0; j < searchResults.get(i).getAnswers().size(); j++) {
				Answer curAnswer = searchResults.get(i).getAnswers().get(j);
				String[] splitedAnswerContent = curAnswer.getContent().split("\\s");

				// Will be used in case query is a sentence
				int contentCountCheck = contentCount;

				// Go through answer content word by word count every match.
				for (int x = 0; x < splitedAnswerContent.length; x++) {
					if (splitedAnswerContent[x].contains(query)) {
						contentCount++;
					}
				}

				// If we have a sentence as query
				if (curAnswer.getContent().contains(query)
						&& contentCount == contentCountCheck) {
					contentCount++;
				}
			}

			// Go through all comments
			for (int k = 0; k < searchResults.get(i).getComments().size(); k++) {
				Comment curComment = searchResults.get(i).getComments().get(k);
				String[] splitedCommentContent = curComment.getContent().split("\\s");

				// Will be used in case query is a sentence
				int contentCountCheck = contentCount;

				// Go through word by word count every match with search query
				for (int x = 0; x < splitedCommentContent.length; x++) {
					if (splitedCommentContent[x].contains(query)) {
						contentCount++;
					}
				}

				// If we have a sentence as query
				if (curComment.getContent().contains(query)
						&& contentCountCheck == contentCount) {
					contentCount++;
				}
			}
			searchResults.get(i).setContentCount(contentCount);
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

				int actualScore = searchResults.get(i).getTotalScore();
				searchResults.get(i).setTotalScore(actualScore + 5);
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
