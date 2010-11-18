package models;

/**
 * The Class Like describes the relation between a user and a comment. If this
 * relation exists, the user likes the given comment.
 */
public class Like {

	private Comment comment;
	private User user;

	public Like(User user, Comment comment) {

		this.user = user;
		this.comment = comment;

	}

	public Comment getComment() {
		return comment;
	}

	public User getUser() {
		return user;
	}

}
