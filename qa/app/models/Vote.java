package models;

import java.util.Date;

public class Vote {
	protected int vote;
	protected Post post;
	protected User user;
	protected Date voteSetTime;
	protected int voteToAddToScore[] = new int[1];

	public Vote(Post post, int vote, User user) {
		this.post = post;
		this.vote = vote;
		this.user = user;
		post.votes.add(this);
		voteToAddToScore[0] = vote;
		voteSetTime = new Date();
	}

	/**
	 * Checks if a vote is changeable
	 * 
	 * @return true if the vote can be changed, false otherwise
	 */
	public boolean voteChangeable() {
		long then;
		long now = new Date().getTime();

		if (voteSetTime == null) {
			then = now;
		} else {
			then = voteSetTime.getTime();
		}

		long diff = now - then;

		if ((diff / (1000 * 60)) < 1) {
			return true;
		} else {
			return false;
		}
	}

	/** Getters */
	public int getVote() {
		return this.vote;
	}

	public User getUser() {
		return this.user;
	}

	public int getVoteToAddToScore() {
		return this.voteToAddToScore[0];
	}

	public Date getvoteSetTime() {
		return this.voteSetTime;
	}

	public Post getPost() {
		return this.post;
	}

	/** Setters */
	public void setVote(int value) {
		this.vote = value;
		this.voteToAddToScore[0] = value;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
