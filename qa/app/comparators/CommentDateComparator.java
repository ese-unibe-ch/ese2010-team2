package comparators;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

import models.Comment;

/**
 * The Class CommentDateComparator implements Comparator and only overrides the
 * method compare().
 */
public class CommentDateComparator implements Comparator<Comment> {

	/**
	 * Compares two objects of the type comment. @see
	 * java.util.Comparator#compare(Object, Object)
	 */
	public final int compare(Comment a, Comment b) {

		Date valueA = a.getTimestamp();
		Date valueB = b.getTimestamp();

		return valueA.compareTo(valueB);
	}

}