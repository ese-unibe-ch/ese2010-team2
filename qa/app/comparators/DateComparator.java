package comparators;

import java.util.Comparator;
import java.util.Date;

import models.Post;

/**
 * The Class DateComparator implements Comparator and only overrides the method
 * compare().
 */
public class DateComparator implements Comparator<Post> {

	/**
	 * Compares two objects of the type votable. @see
	 * java.util.Comparator#compare(Object, Object)
	 */
	public final int compare(Post a, Post b) {

		Date valueA = a.getDate();
		Date valueB = b.getDate();

		return valueA.compareTo(valueB);
	}

}