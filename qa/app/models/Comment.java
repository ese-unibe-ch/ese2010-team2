package models;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class Comment {

	private User owner;
	private Post commentedVotable;
	private String content;
	private Date timestamp;
	private int id;

	private Calendar calendar = Calendar.getInstance();

	/** The application-manager. */
	private static DbManager manager = DbManager
			.getInstance();

	public Comment(User owner, Post commentedVotable, String content) {
		this.owner = owner;
		this.commentedVotable = commentedVotable;
		this.content = content;
		this.timestamp = new Date();
		commentedVotable.setLastChanged(new Date());
		owner.addActivity("Commented post <" + commentedVotable.getContent()
				+ "> by writing: <" + content + ">");
		manager.addComment(this);
	}

	/** Getters */
	public User getOwner() {
		return owner;
	}

	public Post getCommentedVotable() {
		return commentedVotable;
	}

	public String getContent() {
		return content;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public int getId() {
		return id;
	}

	/** Setters */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setCommentedVotable(Post commentedVotable) {
		this.commentedVotable = commentedVotable;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setId(int id) {
		this.id = id;
	}
}
