package models;

/**
 * The Class Notifications delivers functionality to 
 * notify an user about a change.
 */
public class Notification {
	
	private String message;
	private User notifiedUser;
	private Question changedQuestion;
	
	/**
	 * Initialize a new notification.
	 * 
	 * @param message
	 * 				- the message the notified user gets.
	 * 
	 * @param notifiedUser
	 * 				- the user which is notified
	 * 
	 * @param changedQuestion
	 * 				- the question that changed.
	 */
	public Notification(String message, User notifiedUser, Question changedQuestion) {
		this.message = message;
		this.notifiedUser = notifiedUser;
		this.changedQuestion = changedQuestion;
		this.notifiedUser.addNotification(this);
	}

	/** Getters */
	public String getMessage() {
		return message;
	}
	
	public User getNotifiedUser() {
		return notifiedUser;
	}
	
	public Question getChangedQuestion() {
		return changedQuestion;
	}
	
	/** Setters */
	public void setMessage(String message) {
		this.message = message;
	}

	public void setNotifiedUser(User notifiedUser) {
		this.notifiedUser = notifiedUser;
	}

	public void setChangedQuestion(Question changedQuestion) {
		this.changedQuestion = changedQuestion;
	}
}
