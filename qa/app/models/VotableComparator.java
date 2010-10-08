package models;

import java.sql.Timestamp;
import java.util.Comparator;

public class VotableComparator implements Comparator {

	public final int compare(Object aGiven, Object bGiven) {
		Votable a = (Votable) aGiven;
		Votable b = (Votable) bGiven;

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
	
	public final int compareByDate(Object aGiven, Object bGiven){
		Votable a=(Votable) aGiven;
		Votable b=(Votable) bGiven;
		
		Timestamp valueA=a.getDate();
		Timestamp valueB=b.getDate();
		
		return valueA.compareTo(valueB);
	}
	
	

}
