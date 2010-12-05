package models;

public class Warning {
	
	private Post inappropriatePost;
	private int warningCounter;
	
	public Warning(Post inappropriatePost) {
		this.inappropriatePost = inappropriatePost;
		this.warningCounter = 1;
	}

	public void incrementCounter() {
		this.warningCounter++;
	}

	public void setInappropriatePost(Post inappropriatePost) {
		this.inappropriatePost = inappropriatePost;
	}

	public Post getInappropriatePost() {
		return inappropriatePost;
	}

	public void setWarningCounter(int warningCounter) throws Exception {
		if (warningCounter < 1)
			throw new Exception("counter can't be less than 1");
		this.warningCounter = warningCounter;
	}

	public int getWarningCounter() {
		return warningCounter;
	}
}
