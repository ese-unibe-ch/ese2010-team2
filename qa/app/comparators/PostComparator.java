package comparators;

import java.util.Comparator;
import java.util.Date;

import models.Post;

/**
 * The Class VotableComparator implements the Comparator-class and adapts it for
 * votables.
 */
public class PostComparator implements Comparator<Post> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public final int compare(Post a, Post b) {
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
	public final int compareByDate(Post a, Post b) {

		Date valueA = a.getDate();
		Date valueB = b.getDate();

		return valueA.compareTo(valueB);
	}

}
