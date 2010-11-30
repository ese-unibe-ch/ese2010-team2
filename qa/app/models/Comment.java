package models;

import java.util.ArrayList;
import java.util.Date;

/**
 * The Class Comment delivers functionality to comment other posts like answers
 * and question.
 */
public class Comment extends Post {

	private Post commentedPost;
	private static DbManager manager = DbManager.getInstance();

	/**
	 * Instantiates a new comment.
	 * 
	 * @param owner
	 *            - the commenting user.
	 * @param commentedPost
	 *            - the post which is commented.
	 * @param content
	 *            - the content of the comment.
	 */
	public Comment(User owner, Post commentedPost, String content) {
		this.owner = owner;
		this.commentedPost = commentedPost;
		this.content = content;
		this.date = new Date();
		oldVersions = new ArrayList<Post>();
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
		this.setEditor(uname);
		this.content = content;
	}
	
	public void setLastChanged(Date date){
		this.getCommentedPost().setLastChanged(date);
	}

	public int likes() {
		return manager.getLikes(this.id).size();
	}

	public boolean isLiked(String username) {
		User user = manager.getUserByName(username);
		return user != null && manager.getLike(user, id) != null;
	}
}
