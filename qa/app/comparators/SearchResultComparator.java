package comparators;

import java.util.Comparator;

import models.SearchResult;

/**
 * Compares two SearchResults. First Order: The total count of the search query
 * in the in the ambient text. Second Order: The Total Score.
 */
public class SearchResultComparator implements Comparator<SearchResult> {

	public final int compare(SearchResult a, SearchResult b) {
		if (a.getTotalCount() < b.getTotalCount()) {
			return 1;
		} else if (a.getTotalCount() > b.getTotalCount()) {
			return -1;
		} else {
			if (a.getTotalScore() < b.getTotalScore()) {
				return 1;
			} else if (a.getTotalScore() > b.getTotalScore()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}