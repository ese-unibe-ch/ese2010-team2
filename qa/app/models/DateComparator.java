package models;

import java.sql.Timestamp;
import java.util.Comparator;

public class DateComparator implements Comparator {

	public final int compare(Object aGiven, Object bGiven){
		Votable a=(Votable) aGiven;
		Votable b=(Votable) bGiven;
		
		Timestamp valueA=a.getDate();
		Timestamp valueB=b.getDate();
		
		return valueA.compareTo(valueB);
	}
	
}