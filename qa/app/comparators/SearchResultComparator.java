package comparators;

import java.util.Comparator;

import models.SearchResult;

/**
 * Compares two posts by their date.
 */
public class SearchResultComparator implements Comparator<SearchResult> {

	public final int compare(SearchResult a, SearchResult b) {
		if (a.getTotalRadiusCount() < b.getTotalRadiusCount()) {
			return 1;
		} else if (a.getTotalRadiusCount() > b.getTotalRadiusCount()) {
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