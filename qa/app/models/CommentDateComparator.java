package models;

import java.sql.Timestamp;
import java.util.Comparator;

/**
 * The Class CommentDateComparator implements Comparator and only overrides the
 * method compare().
 */
public class CommentDateComparator implements Comparator {

	/**
	 * Compares two objects of the type comment. @see
	 * java.util.Comparator#compare(Object, Object)
	 */
	public final int compare(Object aGiven, Object bGiven) {
		Comment a = (Comment) aGiven;
		Comment b = (Comment) bGiven;

		Timestamp valueA = a.getTimestamp();
		Timestamp valueB = b.getTimestamp();

		return valueA.compareTo(valueB);
	}

}