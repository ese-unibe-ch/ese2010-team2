package models;

/**
 * This class represents a waring an admin gets, when a post is reported as
 * inappropriate
 */
public class Warning implements Comparable<Warning>{
	
	DbManager manager = DbManager.getInstance();
	private Post inappropriatePost;
	private int warningCounter;
	private int id;
	
	public Warning(Post inappropriatePost) {
		this.inappropriatePost = inappropriatePost;
		this.warningCounter = 1;
		manager.addWarning(this);
	}

	public void incrementCounter() {
		this.warningCounter++;
	}

	public int compareTo(Warning comparedWarning) {
		return comparedWarning.getWarningCounter() - this.getWarningCounter();
	}

	/** Getters */
	public Post getInappropriatePost() {
		return inappropriatePost;
	}

	public int getId() {
		return id;
	}

	public int getWarningCounter() {
		return warningCounter;
	}

	/** Setters */
	public void setWarningCounter(int warningCounter) throws Exception {
		if (warningCounter < 1)
			throw new Exception("counter can't be less than 1");
		this.warningCounter = warningCounter;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInappropriatePost(Post inappropriatePost) {
		this.inappropriatePost = inappropriatePost;
	}
}
