package models.comparators;

import java.util.Comparator;

import models.Post;

/**
 * Compares two posts by their score.
 * 
 * Note: Causes posts with low scores to appear at the beginning of the list.
 * 
 * @see Collections.reverseOrder
 */
public class ScoreComparator implements Comparator<Post> {

	public final int compare(Post a, Post b) {
		int scoreA = a.getScore();
		int scoreB = b.getScore();

		if (scoreA == scoreB)
			return 0;
		else if (scoreA < scoreB)
			return -1;
		else
			return 1;
	}
}
