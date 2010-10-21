package comparators;

import java.sql.Timestamp;
import java.util.Comparator;

import models.Post;

/**
 * The Class DateComparator implements Comparator and only overrides the method
 * compare().
 */
public class DateComparator implements Comparator {

	/**
	 * Compares two objects of the type votable. @see
	 * java.util.Comparator#compare(Object, Object)
	 */
	public final int compare(Object aGiven, Object bGiven) {
		Post a = (Post) aGiven;
		Post b = (Post) bGiven;

		Timestamp valueA = a.getDate();
		Timestamp valueB = b.getDate();

		return valueA.compareTo(valueB);
	}

}