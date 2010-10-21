package comparators;

import java.sql.Timestamp;
import java.util.Comparator;

import models.Post;

/**
 * The Class VotableComparator implements the Comparator-class and adapts it for
 * votables.
 */
public class PostComparator implements Comparator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public final int compare(Object aGiven, Object bGiven) {
		Post a = (Post) aGiven;
		Post b = (Post) bGiven;

		int valueA = a.getScore();
		int valueB = b.getScore();

		if (valueA < valueB) {
			return 1;
		} else if (valueA == valueB) {
			return 0;
		} else if (valueA > valueB) {
			return -1;
		}
		return (Integer) null;

	}

	/**
	 * Compare two votables by their creation-date.
	 * 
	 * @param aGiven
	 *            the a given
	 * @param bGiven
	 *            the b given
	 * @return - a negative integer, zero, or a positive integer as the date of
	 *         the first object is less than, equal to, or greater than the date
	 *         of the second.
	 */
	public final int compareByDate(Object aGiven, Object bGiven) {
		Post a = (Post) aGiven;
		Post b = (Post) bGiven;

		Timestamp valueA = a.getDate();
		Timestamp valueB = b.getDate();

		return valueA.compareTo(valueB);
	}

}
