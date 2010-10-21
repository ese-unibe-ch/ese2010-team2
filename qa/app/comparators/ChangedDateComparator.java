package comparators;

import java.util.Comparator;
import java.util.Date;

import models.Question;

/**
 * The Class DateComparator implements Comparator and only overrides the method
 * compare().
 */
public class ChangedDateComparator implements Comparator {

	/**
	 * Compares two objects of the type votable. @see
	 * java.util.Comparator#compare(Object, Object)
	 */
	public final int compare(Object aGiven, Object bGiven) {
		Question a = (Question) aGiven;
		Question b = (Question) bGiven;

		Date valueA = a.getLastChangedDate();
		Date valueB = b.getLastChangedDate();

		return valueA.compareTo(valueB);
	}

}