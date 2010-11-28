package models.comparators;

import java.util.Comparator;
import java.util.Date;

import models.Question;

/**
 * Compares two questions by their last-changed date.
 */
public class ChangedDateComparator implements Comparator<Question> {

	public final int compare(Question a, Question b) {
		
		Date valueA = a.getLastChangedDate();
		Date valueB = b.getLastChangedDate();

		return valueA.compareTo(valueB);
	}

}