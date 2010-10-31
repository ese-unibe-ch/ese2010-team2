package models;

public class Notification {
	
	private String message;
	private User notifiedUser;
	private Question changedQuestion;
	
	public Notification(String message, User notifiedUser, Question changedQuestion) {
		this.message = message;
		this.notifiedUser = notifiedUser;
		this.changedQuestion = changedQuestion;
		this.notifiedUser.addNotification(this);
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setNotifiedUser(User notifiedUser) {
		this.notifiedUser = notifiedUser;
	}

	public User getNotifiedUser() {
		return notifiedUser;
	}

	public void setChangedQuestion(Question changedQuestion) {
		this.changedQuestion = changedQuestion;
	}

	public Question getChangedQuestion() {
		return changedQuestion;
	}
}
