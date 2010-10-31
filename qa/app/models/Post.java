package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.petebevin.markdown.MarkdownProcessor;

/**
 * The Class Votable delivers functionality for all objects that are votable
 * (e.g. answers and questions).
 */
public abstract class Post {

	protected int id;
	protected String content;
	protected User owner;
	protected int score = 0;
	protected ArrayList<Post> oldVersions= new ArrayList<Post>();
	protected ArrayList<User> editedBy= new ArrayList<User>();
 
	protected static DbManager manager = DbManager.getInstance();
	protected Calendar calendar = Calendar.getInstance();
	protected Date date;
	/** All users that already voted for the question. */
	private ArrayList<User> userVotedForPost = new ArrayList<User>();
	protected static MarkdownProcessor markdownProcessor = new MarkdownProcessor();

	/**
	 * Vote for a votable.
	 * 
	 * @param vote2
	 *            - The vote you want to add. This is an String-object
	 *            containing an integer number.
	 */
	public void vote(int vote) {
		//int vote = Integer.parseInt(vote2);
		score = score + vote;
		this.setLastChanged(new Date());
		manager.updateReputation(this.getOwner());
	}

	/**
	 * @return parsed markdown string, so either plain text or HTML.
	 */
	public String getHtml() {
		return markdownProcessor.markdown(content);
	}

	/**
	 * Checks if the user with the id #uid is the owner of the votable. 
	 * 
	 * @param uid
	 *            numeric user id as string
	 * @return true, if successful and false if either the uid isn't the owner of the post or if the uid can't be parsed to an integer value.
	 */
	public boolean ownerIs(String uid) {
		
		try {
			return owner.getId() == Integer.parseInt(uid);
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Checks if the user with the id #uid is the owner of the votable.
	 * 
	 * @param uid
	 *            the uid
	 * @return true, if successful
	 */
	public boolean ownerIs(int uid) {
		return owner.getId() == uid;
	}

	public String toString() {
		return content + "\n by " + owner;
	}

	/** Getter methods */
	public int getId() {
		return id;
	}

	public int getScore() {
		return score;
	}

	public Date getDate() {
		return date;
	}
	
	/**
	 * Gives an approximative measure of the amount of time passed since
	 * this post has been created. 
	 * 
	 * The measure is given using the largest time unit where the amount 
	 * is >= 1. I.e. if it was posted 9 months and 3 weeks ago, the measure will 
	 * be "about 9 months ago". 1 week, 5 days => "about 1 week ago".   
	 * 
	 * @return String representing the time difference
	 */
	public String getTimePassedSincePosting() {
		final long SECONDS_IN_A_DAY = 60*60*24;
		long posted = date.getTime();
		long now = new Date().getTime();
		long diff = now - posted;
		diff /= 1000; // convert to seconds
		
		StringBuffer s = new StringBuffer();
		
		s.append("about ");
		
		if ( diff < 60 ) {
			s.append(diff + " seconds ");
		} else if ( diff > 60 && diff < 60 * 60 ) {
			long minutes = diff / 60;
			s.append(minutes);
			s.append(minutes > 1 ? " minutes " : " minute ");
		} else if ( diff >= 60 * 60 && diff < SECONDS_IN_A_DAY ) {
			long hours = diff / (60*60);
			s.append(hours);
			s.append(hours > 1 ? " hours " : " hour ");
		} else if ( diff >= SECONDS_IN_A_DAY && diff < SECONDS_IN_A_DAY * 7 ) {
			long days = diff / SECONDS_IN_A_DAY;
			s.append(days);
			s.append(days > 1 ? " days " : " day ");
		} else if ( diff >= SECONDS_IN_A_DAY * 7 && diff < SECONDS_IN_A_DAY * 30 ) {
			long weeks = diff / (SECONDS_IN_A_DAY * 7);
			s.append(weeks);
			s.append(weeks > 1 ? " weeks " : " week ");
		} else if ( diff >= SECONDS_IN_A_DAY * 30 && diff < SECONDS_IN_A_DAY * 365 ) {
			long months = diff / (SECONDS_IN_A_DAY * 30);
			s.append(months);
			s.append(months > 1 ? " months " : " month ");
		} else {
			long years = diff / (SECONDS_IN_A_DAY * 365);
			s.append(years);
			s.append(years > 1 ? " years " : " year ");
		}
		
		s.append("ago");
		return s.toString();
	}

	public String getContent() {
		return content;
	}

	public User getOwner() {
		return owner;
	}
	
	public ArrayList<Post> getOldVersions(){
		return this.oldVersions;
	}
	
	public ArrayList<User> getEditors(){
		return this.editedBy;
	}

	/** Setter methods */
	protected void setContent(String content, String uname) {
		this.editedBy.add(manager.getUserByName(uname));
		this.content = content;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDate(Date date) {
		this.date = date; 
	}

	/**
	 * Invokes the method setLastChangedDate of the question the post relates to
	 * (also itself) to set the date of the last change.
	 * 
	 * @param date
	 *            - the date when the question was last changed.
	 */
	public void setLastChanged(Date date) {
		if (this instanceof Question) {
			manager.getQuestionById(this.id).setLastChangedDate(date);
		} else if (this instanceof Answer) {
			int questionId = manager.getAnswerById(id).getQuestionId();
			manager.getQuestionById(questionId).setLastChangedDate(date);
		}
	}

	/**
	 * Checks if a certain user already voted for this question.
	 * 
	 * @param user
	 *            - The user you want to check if he has alrady voted for this
	 *            question.
	 * @return - true if the user already voted for this question or false if he
	 *         didn't.
	 */
	public boolean checkUserVotedForPost(User user) {
		for (int i = 0; i < userVotedForPost.size(); i++) {
			if (user.getName().equals(userVotedForPost.get(i).getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a user to the list of all users who already voted for this question.
	 * 
	 * @param user
	 *            - that voted for the question.
	 */
	public void userVotedForPost(User user) {
		userVotedForPost.add(user);
	}
}