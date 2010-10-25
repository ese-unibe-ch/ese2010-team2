package models;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Comment extends Post {

	private Post commentedPost;

	/** The application-manager. */
	private static DbManager manager = DbManager
			.getInstance();

	public Comment(User owner, Post commentedPost, String content) {
		this.owner = owner;
		this.commentedPost = commentedPost;
		this.content = content;
		this.date = new Date();
		commentedPost.setLastChanged(new Date());
		owner.addActivity("Commented post <" + commentedPost.getContent()
				+ "> by writing: <" + content + ">");
		manager.addComment(this);
	}

	/** Getters */
	public Post getCommentedVotable() {
		return commentedPost;
	}

	/** Setters */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setCommentedVotable(Post commentedPost) {
		this.commentedPost = commentedPost;
	}

}
