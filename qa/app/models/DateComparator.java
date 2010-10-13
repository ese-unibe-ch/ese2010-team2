package models;

import java.sql.Timestamp;
import java.util.Comparator;

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
		Votable a = (Votable) aGiven;
		Votable b = (Votable) bGiven;

		Timestamp valueA = a.getDate();
		Timestamp valueB = b.getDate();

		return valueA.compareTo(valueB);
	}

}