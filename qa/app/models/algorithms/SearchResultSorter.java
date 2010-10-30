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
		for (int i = 0; i < searchResults.size(); i++) {
			int tagCount = 0;

			Question curQuestion = searchResults.get(i).getQuestion();
			int numberOfTags = curQuestion.getTags().size();

			for (int j = 0; j < numberOfTags; j++) {

				String curTag = curQuestion.getTagByIndex(j);
				curTag = curTag.toLowerCase();

				if (curTag.contains(query)) {
					// Tags Count Double
					tagCount = tagCount + 2;
				}
			}
			searchResults.get(i).setRadiusTagCount(tagCount);
		}

	}

	private void countContentHits(){
		for (int i = 0; i < searchResults.size(); i++) {
			int contentCount = 0;

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
			if (curQuestion.getContent().contains(query) && contentCount == 0) {
				contentCount++;
			}

			int contentCountBackup = contentCount;

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
				if (curAnswer.getContent().contains(query)
						&& contentCount == contentCountBackup) {
					contentCount++;
				}
			}

			int contentCountBackup2 = contentCount;
			// Go through all Comments
			for (int k = 0; k < searchResults.get(i).getComments().size(); k++) {
				Comment curComment = searchResults.get(i).getComments().get(k);
				String[] result2 = curComment.getContent().split("\\s");
				// Go through word by word count every match with search query
				for (int x = 0; x < result2.length; x++) {
					if (result2[x].contains(query)) {
						contentCount++;
					}
					// System.out.println(result[x]);
				}
				// If we have a sentence as query
				if (curComment.getContent().contains(query)
						&& contentCountBackup2 == contentCount) {
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
				// If there is a best answer Score will be increment plus 5
				searchResults.get(i).setTotalScore(
						searchResults.get(i).getTotalScore() + 5);
			}
		}
	}
	
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
	
	private void sortAfterTotalCount() {
		Collections.sort(searchResults, new SearchResultComparator());
	}

	public ArrayList<SearchResult> getSearchResults() {
		return searchResults;
	}

}
