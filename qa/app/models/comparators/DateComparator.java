package models.comparators;

import java.util.Comparator;
import java.util.Date;

import models.Post;

/**
 * Compares two posts by their date.
 */
public class DateComparator implements Comparator<Post> {

	public final int compare(Post a, Post b) {

		Date valueA = a.getDate();
		Date valueB = b.getDate();

		return valueA.compareTo(valueB);
	}
}