package models;

public class Warning {
	
	private Post inappropriatePost;
	
	public Warning(Post inappropriatePost) {
		this.setInappropriatePost(inappropriatePost);
	}

	public void setInappropriatePost(Post inappropriatePost) {
		this.inappropriatePost = inappropriatePost;
	}

	public Post getInappropriatePost() {
		return inappropriatePost;
	}
}
