package models;

import java.sql.Timestamp;
import java.util.ArrayList;
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
		oldVersions= new ArrayList<Post>();
		commentedPost.setLastChanged(new Date());
		owner.addActivity("Commented post <" + commentedPost.getContent()
				+ "> by writing: <" + content + ">");
		manager.addComment(this);
	}

	/** Getters */
	public Post getCommentedPost() {
		return commentedPost;
	}

	/** Setters */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setCommentedPost(Post commentedPost) {
		this.commentedPost = commentedPost;
	}
	
	public void setContent(String content, String uname) {
		this.editedBy.add(manager.getUserByName(uname));
		this.content = content;
	}

}
