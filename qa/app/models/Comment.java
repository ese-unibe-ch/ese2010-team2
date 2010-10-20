package models;

import java.sql.Timestamp;
import java.util.Calendar;

public class Comment {

	private User owner;
	private Post commentedVotable;
	private String content;
	private Timestamp timestamp;
	private int id;

	private Calendar calendar = Calendar.getInstance();

	private static int comment_id;

	/** The application-manager. */
	private static DbManager manager = DbManager
			.getInstance();

	public Comment(User owner, Post commentedVotable, String content) {
		this.owner = owner;
		this.commentedVotable = commentedVotable;
		this.content = content;
		this.timestamp = new Timestamp(calendar.getTime().getTime());
		this.id = comment_id;
		comment_id++;
		manager.getComments().add(this);
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getOwner() {
		return owner;
	}

	public void setCommentedVotable(Post commentedVotable) {
		this.commentedVotable = commentedVotable;
	}

	public Post getCommentedVotable() {
		return commentedVotable;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
