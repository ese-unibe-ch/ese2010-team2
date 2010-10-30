package models.algorithms;

import java.util.ArrayList;
import java.util.Collections;

import models.Answer;
import models.Comment;
import models.Question;
import models.SearchResult;

import comparators.SearchResultComparator;

public class SearchResultSorter {
	private ArrayList<SearchResult> searchResults;

	private ArrayList<SearchResult> searchResultsSorted;
	private String query;

	public SearchResultSorter(ArrayList<SearchResult> searchResults,
			String query) {
		this.searchResults = searchResults;
		this.query = query;

		// Initialize Sorting
		countTags();
		countContentHits();
		checkIfHasABestAnswer();
		countTotalScore();
		sortAfterTotalCount();
	}


	private void countTags() {
		int tagCount = 0;
		for (int i = 0; i < searchResults.size(); i++) {
			Question curQuestion = searchResults.get(i).getQuestion();
			int numberOfTags = curQuestion.getTags().size();

			for (int j = 0; j < numberOfTags; j++) {

				String curTag = curQuestion.getTagByIndex(j);
				curTag = curTag.toLowerCase();

				if (curTag.contains(query)) {
					tagCount++;
				}
			}
			searchResults.get(i).setRadiusTagCount(tagCount);
		}

	}

	private void countContentHits(){
		int contentCount = 0;
		for (int i = 0; i < searchResults.size(); i++) {
			// Go trough questions
			Question curQuestion = searchResults.get(i).getQuestion();
			String[] result = curQuestion.getContent().split("\\s");
			// Go trhough word by word count every match with search query
			for (int x = 0; x < result.length; x++) {
				if (result[x].contains(query)) {
					contentCount++;
				}
				// System.out.println(result[x]);
			}
			// If we have a sentence as query
			if (curQuestion.getContent().contains(query)) {
				contentCount++;
			}

			// Go Through all Answers
			for (int j = 0; j < searchResults.get(i).getAnswers().size(); j++) {
				Answer curAnswer = searchResults.get(i).getAnswers().get(j);
				String[] result1 = curAnswer.getContent().split("\\s");
				// Go trhough word by word count every match with search query
				for (int x = 0; x < result1.length; x++) {
					if (result1[x].contains(query)) {
						contentCount++;
					}
					// System.out.println(result[x]);
				}
				// If we have a sentence as query
				if (curAnswer.getContent().contains(query)) {
					contentCount++;
				}
			}

			// Go Through all Comments
			for (int k = 0; k < searchResults.get(i).getComments().size(); k++) {
				Comment curComment = searchResults.get(i).getComments().get(k);
				String[] result2 = curComment.getContent().split("\\s");
				// Go trhough word by word count every match with search query
				for (int x = 0; x < result2.length; x++) {
					if (result2[x].contains(query)) {
						contentCount++;
					}
					// System.out.println(result[x]);
				}
				// If we have a sentence as query
				if (curComment.getContent().contains(query)) {
					contentCount++;
				}
			}
			searchResults.get(i).setRadiusContentCount(contentCount);
		}
	}

	private void checkIfHasABestAnswer() {
		for (int i = 0; i < searchResults.size(); i++) {
			Question curQuestion = searchResults.get(i).getQuestion();
			if (curQuestion.hasBestAnswer()) {
				searchResults.get(i).setHasABestAnswer(true);
			}
		}
	}
	
	private void countTotalScore() {
		int totalScore = 0;
		for (int i = 0; i < searchResults.size(); i++) {
			Question curQuestion = searchResults.get(i).getQuestion();
			totalScore = totalScore + curQuestion.getScore();

			for (int j = 0; j < searchResults.get(i).getAnswers().size(); j++) {
				Answer curAnswer = searchResults.get(i).getAnswers().get(j);
				totalScore = totalScore + curAnswer.getScore();
			}

		}
	}
	
	private void sortAfterTotalCount() {
		Collections.sort(searchResults, new SearchResultComparator());
	}

	private void sortInBestAnswer() {
		// TODO
	}

	public ArrayList<SearchResult> getSearchResults() {
		return searchResults;
	}

}
